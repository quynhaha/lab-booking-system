import './StudentDashboard.css';

type Lab = {
  id: string;
  name: string;
  building: string;
  room: string;
  capacity: number;
  description: string;
  available: boolean;
};

const sampleLabs: Lab[] = [
  {
    id: 'lab-a',
    name: 'Computer Lab A',
    building: 'Engineering Building',
    room: 'Floor 2, Room 201',
    capacity: 30,
    description: 'High-performance computers for programming and software development',
    available: true,
  },
  {
    id: 'lab-b',
    name: 'Physics Lab',
    building: 'Science Building',
    room: 'Floor 1, Room 105',
    capacity: 20,
    description: 'Equipped with modern physics experiment apparatus',
    available: true,
  },
  {
    id: 'lab-c',
    name: 'Chemistry Lab',
    building: 'Science Building',
    room: 'Floor 3, Room 302',
    capacity: 25,
    description: 'Fully equipped chemistry laboratory with fume hoods',
    available: true,
  },
  {
    id: 'lab-d',
    name: 'Computer Lab B',
    building: 'Engineering Building',
    room: 'Floor 3, Room 305',
    capacity: 25,
    description: 'Advanced computing lab with latest software',
    available: false,
  },
];

export default function StudentDashboard({ onLogout, userName }: { onLogout: () => void; userName?: string }) {
  return (
    <div className="sd-page">
      <header className="sd-topbar">
        <div className="sd-left">
          <img src="https://iconape.com/wp-content/png_logo_vector/fpt-university-logo.png" alt="FPT University" className="sd-logo-img" />
          <div className="sd-title">University Labs</div>
        </div>
        <nav className="sd-nav">
          <button className="nav-btn active">Dashboard</button>
          <button className="nav-btn">My Bookings</button>
          <button className="nav-btn">Profile</button>
        </nav>
        <div className="sd-right">
          <div className="welcome">Welcome, <strong>{userName || 'Student'}</strong></div>
          <button className="logout" onClick={onLogout}>Logout</button>
        </div>
      </header>

      <main className="sd-main">
        <section className="sd-header">
          <h2>Available Labs</h2>
          <p className="sd-sub">Browse and book laboratory spaces for your studies</p>
        </section>

        <section className="sd-controls">
          <input className="sd-search" placeholder="Search labs by name or description..." />
          <div className="sd-filters">
            <select>
              <option>All Buildings</option>
            </select>
            <select>
              <option>All Floors</option>
            </select>
          </div>
        </section>

        <section className="sd-grid">
          {sampleLabs.map((lab) => (
            <div key={lab.id} className={`sd-card ${lab.available ? '' : 'unavailable'}`}>
              <div className="sd-card-head">
                <div>
                  <div className="sd-lab-name">{lab.name}</div>
                  <div className="sd-lab-loc">{lab.building} - {lab.room}</div>
                </div>
                <div className={`sd-badge ${lab.available ? 'avail' : 'unavail'}`}>{lab.available ? 'Available' : 'Unavailable'}</div>
              </div>
              <p className="sd-desc">{lab.description}</p>
              <div className="sd-meta">Capacity: {lab.capacity} people</div>
              <div className="sd-actions">
                {lab.available && <button className="btn-outline">Book Lab</button>}
              </div>
            </div>
          ))}
        </section>
      </main>
    </div>
  );
}
