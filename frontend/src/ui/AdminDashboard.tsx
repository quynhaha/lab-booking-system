import { useState, useEffect } from 'react';
import './AdminDashboard.css';

const apiBaseUrl = (import.meta as any).env.VITE_API_BASE_URL || 'http://localhost:8080';

interface User {
  id: number;
  fullName: string;
  email: string;
  role: string;
  studentId?: string;
  faculty?: string;
  status: boolean;
}

interface Lab {
  id: number;
  name: string;
  building: string;
  room: string;
  location?: string;
  capacity: number;
  description: string;
  status: string;
  equipment?: string[];
}

interface DashboardStats {
  totalLabs: number;
  availableLabs: number;
  totalBookings: number;
  pendingBookings: number;
  totalCapacity: number;
}

interface AdminDashboardProps {
  onLogout: () => void;
  user: User;
  token: string;
}

export default function AdminDashboard({ onLogout, user, token }: AdminDashboardProps) {
  const [labs, setLabs] = useState<Lab[]>([]);
  const [stats, setStats] = useState<DashboardStats>({
    totalLabs: 0,
    availableLabs: 0,
    totalBookings: 0,
    pendingBookings: 0,
    totalCapacity: 0,
  });
  const [loading, setLoading] = useState(true);
  const [activeTab, setActiveTab] = useState<'labs' | 'bookings' | 'events'>('labs');
  const [showAddModal, setShowAddModal] = useState(false);
  const [showDetailModal, setShowDetailModal] = useState(false);
  const [selectedLab, setSelectedLab] = useState<Lab | null>(null);
  const [bookings, setBookings] = useState<any[]>([]);
  const [loadingBookings, setLoadingBookings] = useState(false);
  const [showDeleteConfirm, setShowDeleteConfirm] = useState(false);
  const [labToDelete, setLabToDelete] = useState<Lab | null>(null);
  const [showRejectModal, setShowRejectModal] = useState(false);
  const [bookingToReject, setBookingToReject] = useState<any | null>(null);
  const [rejectReason, setRejectReason] = useState('');
  const [newLab, setNewLab] = useState({
    labCode: '',
    name: '',
    description: '',
    location: '',
    capacity: 0,
    status: 'AVAILABLE',
    facilities: '',
  });
  
  // Events state
  const [events, setEvents] = useState<any[]>([]);
  const [loadingEvents, setLoadingEvents] = useState(false);
  const [showAddEventModal, setShowAddEventModal] = useState(false);
  const [showRejectEventModal, setShowRejectEventModal] = useState(false);
  const [eventToReject, setEventToReject] = useState<any | null>(null);
  const [rejectEventReason, setRejectEventReason] = useState('');
  const [newEvent, setNewEvent] = useState({
    title: '',
    description: '',
    labId: 0,
  });

  useEffect(() => {
    fetchDashboardData();
  }, []);

  useEffect(() => {
    if (activeTab === 'bookings') {
      fetchBookings();
    } else if (activeTab === 'events') {
      fetchEvents();
    }
  }, [activeTab]);

  const fetchDashboardData = async () => {
    setLoading(true);
    try {
      // Fetch labs
      const labsRes = await fetch(`${apiBaseUrl}/api/v1/labs`, {
        headers: {
          'Authorization': `Bearer ${token}`,
        },
      });
      
      if (labsRes.ok) {
        const labsData = await labsRes.json();
        setLabs(labsData);
        
        // Calculate stats from labs
        const totalCapacity = labsData.reduce((sum: number, lab: Lab) => sum + lab.capacity, 0);
        const availableLabs = labsData.filter((lab: Lab) => lab.status === 'AVAILABLE').length;
        
        setStats(prev => ({
          ...prev,
          totalLabs: labsData.length,
          availableLabs,
          totalCapacity,
        }));
      }

      // Fetch dashboard stats if available
      try {
        const statsRes = await fetch(`${apiBaseUrl}/api/v1/admin/dashboard`, {
          headers: {
            'Authorization': `Bearer ${token}`,
          },
        });
        
        if (statsRes.ok) {
          const statsData = await statsRes.json();
          setStats(prev => ({
            ...prev,
            totalBookings: statsData.totalBookings || 0,
            pendingBookings: statsData.pendingBookings || 0,
          }));
        }
      } catch (error) {
        console.log('Dashboard stats not available:', error);
      }
      
    } catch (error) {
      console.error('Error fetching dashboard data:', error);
    } finally {
      setLoading(false);
    }
  };

  const getStatusBadgeClass = (status: string) => {
    switch (status) {
      case 'AVAILABLE':
        return 'avail';
      case 'OCCUPIED':
      case 'MAINTENANCE':
        return 'unavail';
      default:
        return 'avail';
    }
  };

  const getStatusText = (status: string) => {
    switch (status) {
      case 'AVAILABLE':
        return 'Available';
      case 'OCCUPIED':
        return 'Occupied';
      case 'MAINTENANCE':
        return 'Maintenance';
      default:
        return status;
    }
  };

  const handleCreateLab = async () => {
    try {
      const response = await fetch(`${apiBaseUrl}/api/v1/labs`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`,
        },
        body: JSON.stringify(newLab),
      });

      if (!response.ok) {
        throw new Error('Failed to create lab');
      }

      // Reset form and close modal
      setNewLab({
        labCode: '',
        name: '',
        description: '',
        location: '',
        capacity: 0,
        status: 'AVAILABLE',
        facilities: '',
      });
      setShowAddModal(false);

      // Refresh labs list
      fetchDashboardData();
      alert('Lab created successfully!');
    } catch (error) {
      console.error('Error creating lab:', error);
      alert('Failed to create lab. Please try again.');
    }
  };

  const fetchBookings = async () => {
    setLoadingBookings(true);
    try {
      const response = await fetch(`${apiBaseUrl}/api/v1/bookings`, {
        headers: {
          'Authorization': `Bearer ${token}`,
        },
      });

      if (response.ok) {
        const data = await response.json();
        setBookings(data);
      }
    } catch (error) {
      console.error('Error fetching bookings:', error);
    } finally {
      setLoadingBookings(false);
    }
  };

  const handleViewDetails = (lab: Lab) => {
    setSelectedLab(lab);
    setShowDetailModal(true);
  };

  const handleDeleteLab = (lab: Lab) => {
    setLabToDelete(lab);
    setShowDeleteConfirm(true);
  };

  const confirmDeleteLab = async () => {
    if (!labToDelete) return;

    try {
      const response = await fetch(`${apiBaseUrl}/api/v1/labs/${labToDelete.id}`, {
        method: 'DELETE',
        headers: {
          'Authorization': `Bearer ${token}`,
        },
      });

      if (!response.ok) {
        throw new Error('Failed to delete lab');
      }

      setShowDeleteConfirm(false);
      setLabToDelete(null);
      fetchDashboardData();
      alert('Lab deleted successfully!');
    } catch (error) {
      console.error('Error deleting lab:', error);
      alert('Failed to delete lab. Please try again.');
    }
  };

  const handleApproveBooking = async (bookingId: number) => {
    try {
      const response = await fetch(`${apiBaseUrl}/api/v1/bookings/${bookingId}/approve`, {
        method: 'PUT',
        headers: {
          'Authorization': `Bearer ${token}`,
        },
      });

      if (!response.ok) {
        throw new Error('Failed to approve booking');
      }

      alert('Booking approved successfully!');
      fetchBookings(); // Refresh bookings list
    } catch (error) {
      console.error('Error approving booking:', error);
      alert('Failed to approve booking. Please try again.');
    }
  };

  const handleRejectBooking = (booking: any) => {
    setBookingToReject(booking);
    setShowRejectModal(true);
  };

  const confirmRejectBooking = async () => {
    if (!bookingToReject || !rejectReason.trim()) {
      alert('Please provide a reason for rejection');
      return;
    }

    try {
      const response = await fetch(
        `${apiBaseUrl}/api/v1/bookings/${bookingToReject.id}/reject?reason=${encodeURIComponent(rejectReason)}`,
        {
          method: 'PUT',
          headers: {
            'Authorization': `Bearer ${token}`,
          },
        }
      );

      if (!response.ok) {
        throw new Error('Failed to reject booking');
      }

      alert('Booking rejected successfully!');
      setShowRejectModal(false);
      setBookingToReject(null);
      setRejectReason('');
      fetchBookings(); // Refresh bookings list
    } catch (error) {
      console.error('Error rejecting booking:', error);
      alert('Failed to reject booking. Please try again.');
    }
  };

  // Events functions
  const fetchEvents = async () => {
    setLoadingEvents(true);
    try {
      const response = await fetch(`${apiBaseUrl}/api/events`, {
        headers: {
          'Authorization': `Bearer ${token}`,
        },
      });

      if (response.ok) {
        const data = await response.json();
        setEvents(data);
      }
    } catch (error) {
      console.error('Error fetching events:', error);
    } finally {
      setLoadingEvents(false);
    }
  };

  const handleApproveEvent = async (eventId: number) => {
    try {
      const response = await fetch(`${apiBaseUrl}/api/events/${eventId}/approve`, {
        method: 'PUT',
        headers: {
          'Authorization': `Bearer ${token}`,
        },
      });

      if (!response.ok) {
        throw new Error('Failed to approve event');
      }

      alert('Event approved successfully!');
      fetchEvents(); // Refresh events list
    } catch (error) {
      console.error('Error approving event:', error);
      alert('Failed to approve event. Please try again.');
    }
  };

  const handleRejectEvent = (event: any) => {
    setEventToReject(event);
    setShowRejectEventModal(true);
  };

  const confirmRejectEvent = async () => {
    if (!eventToReject) {
      return;
    }

    try {
      const reason = rejectEventReason.trim() || 'Rejected by admin';
      const response = await fetch(
        `${apiBaseUrl}/api/events/${eventToReject.id}/reject?reason=${encodeURIComponent(reason)}`,
        {
          method: 'PUT',
          headers: {
            'Authorization': `Bearer ${token}`,
          },
        }
      );

      if (!response.ok) {
        throw new Error('Failed to reject event');
      }

      alert('Event rejected successfully!');
      setShowRejectEventModal(false);
      setEventToReject(null);
      setRejectEventReason('');
      fetchEvents(); // Refresh events list
    } catch (error) {
      console.error('Error rejecting event:', error);
      alert('Failed to reject event. Please try again.');
    }
  };

  const handleCreateEvent = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!newEvent.title.trim()) {
      alert('Please fill in event title');
      return;
    }

    try {
      const response = await fetch(`${apiBaseUrl}/api/events`, {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          title: newEvent.title,
          description: newEvent.description || null,
          labId: newEvent.labId && newEvent.labId > 0 ? newEvent.labId : null,
          startTime: null,
          endTime: null,
        }),
      });

      if (!response.ok) {
        const errorData = await response.json().catch(() => ({}));
        throw new Error(errorData.message || 'Failed to create event');
      }

      alert('Event created successfully!');
      setShowAddEventModal(false);
      setNewEvent({
        title: '',
        description: '',
        labId: 0,
        startTime: '',
        endTime: '',
      });
      fetchEvents(); // Refresh events list
    } catch (error: any) {
      console.error('Error creating event:', error);
      alert(error.message || 'Failed to create event. Please try again.');
    }
  };

  return (
    <div className="ad-page">
      <header className="ad-topbar">
        <div className="ad-left">
          <img 
            src="https://iconape.com/wp-content/png_logo_vector/fpt-university-logo.png" 
            alt="FPT University" 
            className="ad-logo-img" 
          />
          <div className="ad-title">University Labs</div>
        </div>
        <nav className="ad-nav">
          <button 
            className={`ad-nav-btn ${activeTab === 'labs' ? 'active' : ''}`}
            onClick={() => setActiveTab('labs')}
          >
            Labs
          </button>
          <button 
            className={`ad-nav-btn ${activeTab === 'bookings' ? 'active' : ''}`}
            onClick={() => setActiveTab('bookings')}
          >
            All Bookings
          </button>
          <button 
            className={`ad-nav-btn ${activeTab === 'events' ? 'active' : ''}`}
            onClick={() => setActiveTab('events')}
          >
            Events
          </button>
        </nav>
        <div className="ad-right">
          <div className="ad-welcome">
            Welcome, <strong>{user.fullName}</strong>
          </div>
          <button className="ad-logout" onClick={onLogout}>Logout</button>
        </div>
      </header>

      <main className="ad-main">
        <section className="ad-header">
          <h2>Admin Dashboard</h2>
          <p className="ad-sub">Manage laboratories, events, and bookings</p>

          <div className="ad-stats">
            <div className="stat card-blue">
              <div className="stat-label">Total Labs</div>
              <div className="stat-value">{loading ? '...' : stats.totalLabs}</div>
            </div>
            <div className="stat card-green">
              <div className="stat-label">Available Labs</div>
              <div className="stat-value">{loading ? '...' : stats.availableLabs}</div>
            </div>
            <div className="stat card-purple">
              <div className="stat-label">Total Bookings</div>
              <div className="stat-value">{loading ? '...' : stats.totalBookings}</div>
            </div>
            <div className="stat card-orange">
              <div className="stat-label">Total Capacity</div>
              <div className="stat-value">{loading ? '...' : stats.totalCapacity}</div>
            </div>
          </div>
        </section>

        <section className="ad-actions">
          <div className="ad-tabs">
            <button 
              className={`tab ${activeTab === 'labs' ? 'active' : ''}`}
              onClick={() => setActiveTab('labs')}
            >
              Labs
            </button>
            <button 
              className={`tab ${activeTab === 'bookings' ? 'active' : ''}`}
              onClick={() => setActiveTab('bookings')}
            >
              Bookings
            </button>
            <button 
              className={`tab ${activeTab === 'events' ? 'active' : ''}`}
              onClick={() => setActiveTab('events')}
            >
              Events
            </button>
          </div>
          <div className="ad-add">
            {activeTab === 'labs' && (
              <button className="btn-add" onClick={() => setShowAddModal(true)}>
                + Add Lab
              </button>
            )}
            {activeTab === 'events' && (
              <button className="btn-add" onClick={() => setShowAddEventModal(true)}>
                + Create Event
              </button>
            )}
          </div>
        </section>

        {activeTab === 'labs' && (
        <section className="ad-grid">
            {loading ? (
              <div className="loading-message">Loading labs...</div>
            ) : labs.length === 0 ? (
              <div className="empty-message">No labs found. Click "+ Add Lab" to create one.</div>
            ) : (
              labs.map((lab) => (
                <div 
                  key={lab.id} 
                  className={`ad-card ${lab.status !== 'AVAILABLE' ? 'unavailable' : ''}`}
                >
              <div className="ad-card-head">
                <div>
                  <div className="ad-lab-name">{lab.name}</div>
                  <div className="ad-lab-loc">{lab.building} - {lab.room}</div>
                </div>
                    <div className={`ad-badge ${getStatusBadgeClass(lab.status)}`}>
                      {getStatusText(lab.status)}
                    </div>
              </div>
              <p className="ad-desc">{lab.description}</p>
              <div className="ad-meta">Capacity: {lab.capacity} people</div>
                  {lab.equipment && lab.equipment.length > 0 && (
                    <div className="ad-equipment">
                      Equipment:{' '}
                      {lab.equipment.map((eq, idx) => (
                        <span key={idx} className="chip">{eq}</span>
                      ))}
                    </div>
                  )}
              <div className="ad-actions-row">
                    <button 
                      className="btn-view"
                      onClick={() => handleViewDetails(lab)}
                    >
                      View Details
                    </button>
                    <button 
                      className="btn-delete"
                      onClick={() => handleDeleteLab(lab)}
                    >
                      Delete
                    </button>
                  </div>
                </div>
              ))
            )}
          </section>
        )}

        {activeTab === 'bookings' && (
          <section className="ad-grid">
            {loadingBookings ? (
              <div className="loading-message">Loading bookings...</div>
            ) : bookings.length === 0 ? (
              <div className="empty-message">No bookings found.</div>
            ) : (
              bookings.map((booking) => (
                <div key={booking.id} className="ad-card">
                  <div className="ad-card-head">
                    <div>
                      <div className="ad-lab-name">{booking.title}</div>
                      <div className="ad-lab-loc">{booking.labName}</div>
                    </div>
                    <div className={`ad-badge ${booking.status === 'APPROVED' ? 'avail' : booking.status === 'PENDING' ? 'pending' : 'unavail'}`}>
                      {booking.status}
                    </div>
                  </div>
                  <p className="ad-desc">{booking.description || 'No description'}</p>
                  <div className="ad-meta">
                    <div>👤 {booking.userName}</div>
                    <div>📧 {booking.userEmail}</div>
                    <div className="ad-time-highlight">
                      <span className="ad-time-icon">📅</span>
                      <span className="ad-time-text">{new Date(booking.startTime).toLocaleString('vi-VN')}</span>
                    </div>
                    <div className="ad-time-highlight">
                      <span className="ad-time-icon">🕒</span>
                      <span className="ad-time-text">{new Date(booking.endTime).toLocaleString('vi-VN')}</span>
                    </div>
                    <div>👥 {booking.participantsCount} participants</div>
                  </div>
                  {booking.status === 'PENDING' && (
                    <div className="ad-actions-row">
                      <button 
                        className="btn-approve"
                        onClick={() => handleApproveBooking(booking.id)}
                      >
                        ✓ Approve
                      </button>
                      <button 
                        className="btn-reject"
                        onClick={() => handleRejectBooking(booking)}
                      >
                        ✗ Reject
                      </button>
                    </div>
                  )}
                </div>
              ))
            )}
          </section>
        )}


        {activeTab === 'events' && (
          <section className="ad-grid">
            {loadingEvents ? (
              <div className="loading-message">Loading events...</div>
            ) : events.length === 0 ? (
              <div className="empty-message">No events found. Click "+ Create Event" to create one.</div>
            ) : (
              events.map((event) => (
                <div key={event.id} className="ad-card">
                  <div className="ad-card-head">
                    <div>
                      <div className="ad-lab-name">{event.title}</div>
                      <div className="ad-lab-loc">{event.labName || `Lab ID: ${event.labId}`}</div>
                    </div>
                    <div className={`ad-badge ${
                      event.status === 'PENDING' ? 'pending' : 
                      event.status === 'APPROVED' ? 'avail' : 
                      'unavail'
                    }`}>
                      {event.status}
                    </div>
                  </div>
                  {event.description && (
                    <p className="ad-desc">{event.description}</p>
                  )}
                  <div className="ad-meta">
                    {event.startTime && event.endTime && (
                      <>
                        <div>Start: {new Date(event.startTime).toLocaleString()}</div>
                        <div>End: {new Date(event.endTime).toLocaleString()}</div>
                      </>
                    )}
                    {(!event.startTime || !event.endTime) && (
                      <div style={{ color: '#6b7280', fontStyle: 'italic' }}>
                        Chưa có thời gian (sẽ được đặt khi teacher book lab)
                      </div>
                    )}
                    {event.userFullName && (
                      <div>Created by: {event.userFullName}</div>
                    )}
                  </div>
                  {event.status === 'PENDING' && (
                    <div className="ad-actions-row">
                      <button 
                        className="btn-view"
                        onClick={() => handleApproveEvent(event.id)}
                        style={{ backgroundColor: '#10b981', color: 'white', border: 'none' }}
                      >
                        ✓ Approve
                      </button>
                      <button 
                        className="btn-delete"
                        onClick={() => handleRejectEvent(event)}
                      >
                        ✗ Reject
                      </button>
                    </div>
                  )}
                </div>
              ))
            )}
          </section>
        )}
      </main>

      {/* View Details Modal */}
      {showDetailModal && selectedLab && (
        <div className="modal-overlay" onClick={() => setShowDetailModal(false)}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h3>{selectedLab.name}</h3>
              <button className="modal-close" onClick={() => setShowDetailModal(false)}>×</button>
            </div>
            <div className="modal-body">
              <div className="detail-section">
                <div className="detail-row">
                  <div className="detail-label">Lab ID:</div>
                  <div className="detail-value">#{selectedLab.id}</div>
                </div>
                <div className="detail-row">
                  <div className="detail-label">Lab Name:</div>
                  <div className="detail-value">{selectedLab.name}</div>
                </div>
                <div className="detail-row">
                  <div className="detail-label">Location:</div>
                  <div className="detail-value">{selectedLab.location || 'N/A'}</div>
                </div>
                <div className="detail-row">
                  <div className="detail-label">Capacity:</div>
                  <div className="detail-value">{selectedLab.capacity} people</div>
                </div>
                <div className="detail-row">
                  <div className="detail-label">Status:</div>
                  <div className="detail-value">
                    <span className={`ad-badge ${getStatusBadgeClass(selectedLab.status)}`}>
                      {getStatusText(selectedLab.status)}
                    </span>
                  </div>
                </div>
                {selectedLab.description && (
                  <div className="detail-row">
                    <div className="detail-label">Description:</div>
                    <div className="detail-value">{selectedLab.description}</div>
                  </div>
                )}
                {selectedLab.equipment && selectedLab.equipment.length > 0 && (
                  <div className="detail-row">
                    <div className="detail-label">Equipment:</div>
                    <div className="detail-value">
                      {selectedLab.equipment.map((eq, idx) => (
                        <span key={idx} className="chip">{eq}</span>
                      ))}
                    </div>
                  </div>
                )}
              </div>
            </div>
            <div className="modal-footer">
              <button className="btn-cancel" onClick={() => setShowDetailModal(false)}>
                Close
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Delete Confirmation Modal */}
      {showDeleteConfirm && labToDelete && (
        <div className="modal-overlay" onClick={() => setShowDeleteConfirm(false)}>
          <div className="modal-content modal-small" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h3>⚠️ Delete Lab</h3>
              <button className="modal-close" onClick={() => setShowDeleteConfirm(false)}>×</button>
            </div>
            <div className="modal-body">
              <p>Are you sure you want to delete <strong>{labToDelete.name}</strong>?</p>
              <p className="warning-text">This action cannot be undone. All associated bookings will be affected.</p>
            </div>
            <div className="modal-footer">
              <button className="btn-cancel" onClick={() => setShowDeleteConfirm(false)}>
                Cancel
              </button>
              <button className="btn-delete-confirm" onClick={confirmDeleteLab}>
                Delete Lab
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Reject Booking Modal */}
      {showRejectModal && bookingToReject && (
        <div className="modal-overlay" onClick={() => setShowRejectModal(false)}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h3>⚠️ Reject Booking</h3>
              <button className="modal-close" onClick={() => setShowRejectModal(false)}>×</button>
            </div>
            <div className="modal-body">
              <p>Are you sure you want to reject booking <strong>{bookingToReject.title}</strong>?</p>
              <p className="warning-text">The user will be notified via email.</p>
              <div className="form-group" style={{ marginTop: '16px' }}>
                <label>Rejection Reason *</label>
                <textarea
                  placeholder="Please provide a reason for rejection..."
                  value={rejectReason}
                  onChange={(e) => setRejectReason(e.target.value)}
                  rows={4}
                  style={{ width: '100%', padding: '8px', borderRadius: '4px', border: '1px solid #ccc' }}
                />
              </div>
            </div>
            <div className="modal-footer">
              <button className="btn-cancel" onClick={() => {
                setShowRejectModal(false);
                setRejectReason('');
              }}>
                Cancel
              </button>
              <button className="btn-delete-confirm" onClick={confirmRejectBooking}>
                Reject Booking
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Add Lab Modal */}
      {showAddModal && (
        <div className="modal-overlay" onClick={() => setShowAddModal(false)}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h3>Add New Lab</h3>
              <button className="modal-close" onClick={() => setShowAddModal(false)}>×</button>
            </div>
            <div className="modal-body">
              <div className="form-group">
                <label>Lab Code *</label>
                <input
                  type="text"
                  placeholder="e.g., LAB001"
                  value={newLab.labCode}
                  onChange={(e) => setNewLab({...newLab, labCode: e.target.value})}
                />
              </div>
              <div className="form-group">
                <label>Lab Name *</label>
                <input
                  type="text"
                  placeholder="e.g., Computer Lab A"
                  value={newLab.name}
                  onChange={(e) => setNewLab({...newLab, name: e.target.value})}
                />
              </div>
              <div className="form-group">
                <label>Location *</label>
                <input
                  type="text"
                  placeholder="e.g., Engineering Building - Floor 2, Room 201"
                  value={newLab.location}
                  onChange={(e) => setNewLab({...newLab, location: e.target.value})}
                />
              </div>
              <div className="form-row">
                <div className="form-group">
                  <label>Capacity *</label>
                  <input
                    type="number"
                    placeholder="e.g., 30"
                    value={newLab.capacity || ''}
                    onChange={(e) => setNewLab({...newLab, capacity: parseInt(e.target.value) || 0})}
                  />
                </div>
                <div className="form-group">
                  <label>Status</label>
                  <select
                    value={newLab.status}
                    onChange={(e) => setNewLab({...newLab, status: e.target.value})}
                  >
                    <option value="AVAILABLE">Available</option>
                    <option value="MAINTENANCE">Maintenance</option>
                    <option value="UNAVAILABLE">Unavailable</option>
                  </select>
                </div>
              </div>
              <div className="form-group">
                <label>Description</label>
                <textarea
                  placeholder="Describe the lab facilities and purpose..."
                  value={newLab.description}
                  onChange={(e) => setNewLab({...newLab, description: e.target.value})}
                  rows={3}
                />
              </div>
              <div className="form-group">
                <label>Facilities/Equipment</label>
                <input
                  type="text"
                  placeholder="e.g., Computers, Projector, Whiteboard"
                  value={newLab.facilities}
                  onChange={(e) => setNewLab({...newLab, facilities: e.target.value})}
                />
              </div>
            </div>
            <div className="modal-footer">
              <button className="btn-cancel" onClick={() => setShowAddModal(false)}>
                Cancel
              </button>
              <button 
                className="btn-submit" 
                onClick={handleCreateLab}
                disabled={!newLab.labCode || !newLab.name || !newLab.location || !newLab.capacity}
              >
                Create Lab
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Reject Event Modal */}
      {showRejectEventModal && eventToReject && (
        <div className="modal-overlay" onClick={() => setShowRejectEventModal(false)}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h3>⚠️ Reject Event</h3>
              <button className="modal-close" onClick={() => setShowRejectEventModal(false)}>×</button>
            </div>
            <div className="modal-body">
              <p>Are you sure you want to reject event <strong>{eventToReject.title}</strong>?</p>
              <p className="warning-text">The teacher will be notified.</p>
              <div className="form-group" style={{ marginTop: '16px' }}>
                <label>Rejection Reason *</label>
                <textarea
                  placeholder="Please provide a reason for rejection..."
                  value={rejectEventReason}
                  onChange={(e) => setRejectEventReason(e.target.value)}
                  rows={4}
                  style={{ width: '100%', padding: '8px', borderRadius: '4px', border: '1px solid #ccc' }}
                />
              </div>
            </div>
            <div className="modal-footer">
              <button 
                className="btn-cancel" 
                onClick={() => {
                  setShowRejectEventModal(false);
                  setRejectEventReason('');
                }}
              >
                Cancel
              </button>
              <button 
                className="btn-delete-confirm" 
                onClick={confirmRejectEvent}
              >
                Reject Event
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Create Event Modal */}
      {showAddEventModal && (
        <div className="modal-overlay" onClick={() => setShowAddEventModal(false)}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h3>Create New Event</h3>
              <button className="modal-close" onClick={() => setShowAddEventModal(false)}>×</button>
            </div>
            <form onSubmit={handleCreateEvent}>
              <div className="modal-body">
                <div className="form-group">
                  <label>Event Title *</label>
                  <input
                    type="text"
                    placeholder="e.g., Java Programming Workshop"
                    value={newEvent.title}
                    onChange={(e) => setNewEvent({...newEvent, title: e.target.value})}
                    required
                  />
                </div>
                <div className="form-group">
                  <label>Description</label>
                  <textarea
                    placeholder="Describe the event..."
                    value={newEvent.description}
                    onChange={(e) => setNewEvent({...newEvent, description: e.target.value})}
                    rows={3}
                  />
                </div>
                <div className="form-group">
                  <label>Lab (Optional)</label>
                  <select
                    value={newEvent.labId || 0}
                    onChange={(e) => setNewEvent({...newEvent, labId: parseInt(e.target.value) || 0})}
                  >
                    <option value={0}>No lab assigned (can assign later)</option>
                    {labs.map((lab) => (
                      <option key={lab.id} value={lab.id}>
                        {lab.name} - {lab.building} {lab.room}
                      </option>
                    ))}
                  </select>
                  <small style={{color: '#666', fontSize: '12px'}}>
                    Note: Time will be set when teacher books a lab for this event
                  </small>
                </div>
              </div>
              <div className="modal-footer">
                <button 
                  type="button"
                  className="btn-cancel" 
                  onClick={() => {
                    setShowAddEventModal(false);
                    setNewEvent({
                      title: '',
                      description: '',
                      labId: 0,
                    });
                  }}
                >
                  Cancel
                </button>
                <button 
                  type="submit"
                  className="btn-submit" 
                  disabled={!newEvent.title.trim()}
                >
                  Create Event
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
