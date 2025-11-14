import { useState } from 'react';
import './ReportGeneration.css';

const apiBaseUrl = 'https://lab-booking-system-on5y.onrender.com';
interface ReportGenerationProps {
  token: string;
}

export default function ReportGeneration({ token }: ReportGenerationProps) {
  const [startDate, setStartDate] = useState('');
  const [endDate, setEndDate] = useState('');
  const [loading, setLoading] = useState<string | null>(null);

  // Quick date presets
  const handlePreset = (preset: 'today' | 'thisWeek' | 'thisMonth') => {
    const today = new Date();
    
    switch (preset) {
      case 'today':
        setStartDate(formatDate(today));
        setEndDate(formatDate(today));
        break;
      case 'thisWeek':
        const firstDayOfWeek = new Date(today);
        firstDayOfWeek.setDate(today.getDate() - today.getDay());
        setStartDate(formatDate(firstDayOfWeek));
        setEndDate(formatDate(today));
        break;
      case 'thisMonth':
        const firstDayOfMonth = new Date(today.getFullYear(), today.getMonth(), 1);
        setStartDate(formatDate(firstDayOfMonth));
        setEndDate(formatDate(today));
        break;
    }
  };

  const formatDate = (date: Date): string => {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  };

  const validateDates = (): boolean => {
    if (!startDate || !endDate) {
      alert('Vui lòng chọn ngày bắt đầu và ngày kết thúc');
      return false;
    }
    if (new Date(startDate) > new Date(endDate)) {
      alert('Ngày bắt đầu phải trước ngày kết thúc');
      return false;
    }
    return true;
  };

  const handleDownloadReport = async (
    reportType: 'bookings' | 'penalties' | 'lab-utilization' | 'comprehensive',
    format: 'pdf' | 'excel' | 'csv'
  ) => {
    if (!validateDates()) return;

    const loadingKey = `${reportType}-${format}`;
    setLoading(loadingKey);

    try {
      const url = `${apiBaseUrl}/api/v1/admin/reports/${reportType}?startDate=${startDate}&endDate=${endDate}&format=${format}`;
      
      const response = await fetch(url, {
        method: 'GET',
        headers: {
          'Authorization': `Bearer ${token}`,
        },
      });

      if (!response.ok) {
        throw new Error('Failed to generate report');
      }

      // Get filename from Content-Disposition header or create default
      const contentDisposition = response.headers.get('Content-Disposition');
      let filename = `${reportType}-report-${startDate}-${endDate}.${format === 'excel' ? 'xlsx' : format}`;
      
      if (contentDisposition) {
        const filenameMatch = contentDisposition.match(/filename="?(.+)"?/);
        if (filenameMatch) {
          filename = filenameMatch[1];
        }
      }

      // Download file
      const blob = await response.blob();
      const downloadUrl = window.URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.href = downloadUrl;
      link.download = filename;
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
      window.URL.revokeObjectURL(downloadUrl);

      alert('Báo cáo đã được tải xuống thành công!');
    } catch (error) {
      console.error('Error downloading report:', error);
      alert('Không thể tải xuống báo cáo. Vui lòng thử lại.');
    } finally {
      setLoading(null);
    }
  };

  return (
    <div className="report-gen-container">
      <div className="report-gen-header">
        <h1>📊 Kết xuất Báo cáo</h1>
        <p className="report-gen-subtitle">
          Xuất các báo cáo chi tiết về hoạt động hệ thống phòng lab
        </p>
      </div>

      {/* Common Filters Section */}
      <div className="report-filters">
        <h2>🗓️ Khu vực Bộ lọc Chung</h2>
        <p className="filter-description">
          Chọn khoảng thời gian để áp dụng cho tất cả các báo cáo bên dưới
        </p>

        <div className="filter-content">
          <div className="date-inputs">
            <div className="date-group">
              <label htmlFor="startDate">Ngày bắt đầu</label>
              <input
                id="startDate"
                type="date"
                value={startDate}
                onChange={(e) => setStartDate(e.target.value)}
                className="date-input"
              />
            </div>
            <div className="date-separator">→</div>
            <div className="date-group">
              <label htmlFor="endDate">Ngày kết thúc</label>
              <input
                id="endDate"
                type="date"
                value={endDate}
                onChange={(e) => setEndDate(e.target.value)}
                className="date-input"
              />
            </div>
          </div>

          <div className="date-presets">
            <span className="preset-label">Chọn nhanh:</span>
            <button
              className="preset-btn"
              onClick={() => handlePreset('today')}
            >
              Hôm nay
            </button>
            <button
              className="preset-btn"
              onClick={() => handlePreset('thisWeek')}
            >
              Tuần này
            </button>
            <button
              className="preset-btn"
              onClick={() => handlePreset('thisMonth')}
            >
              Tháng này
            </button>
          </div>
        </div>
      </div>

      {/* Reports List Section */}
      <div className="reports-list">
        <h2>🗂️ Danh sách Báo cáo</h2>

        <div className="reports-grid">
          {/* Card 1: Bookings Report */}
          <div className="report-card">
            <div className="report-card-header">
              <div className="report-icon">📅</div>
              <h3>Báo cáo Đặt phòng</h3>
            </div>
            <p className="report-description">
              Xuất file chi tiết toàn bộ các lượt đặt phòng (thành công, bị hủy, chờ duyệt) 
              trong khoảng thời gian đã chọn.
            </p>
            <div className="report-actions">
              <button
                className="export-btn btn-pdf"
                onClick={() => handleDownloadReport('bookings', 'pdf')}
                disabled={loading === 'bookings-pdf'}
              >
                {loading === 'bookings-pdf' ? '⏳ Đang xuất...' : '📄 Xuất PDF'}
              </button>
              <button
                className="export-btn btn-excel"
                onClick={() => handleDownloadReport('bookings', 'excel')}
                disabled={loading === 'bookings-excel'}
              >
                {loading === 'bookings-excel' ? '⏳ Đang xuất...' : '📊 Xuất Excel'}
              </button>
              <button
                className="export-btn btn-csv"
                onClick={() => handleDownloadReport('bookings', 'csv')}
                disabled={loading === 'bookings-csv'}
              >
                {loading === 'bookings-csv' ? '⏳ Đang xuất...' : '📋 Xuất CSV'}
              </button>
            </div>
          </div>

          {/* Card 2: Penalties Report */}
          <div className="report-card">
            <div className="report-card-header">
              <div className="report-icon">⚠️</div>
              <h3>Báo cáo Vi phạm & Phạt</h3>
            </div>
            <p className="report-description">
              Xuất file thống kê các vi phạm, lý do phạt, và tổng số tiền phạt thu được 
              trong khoảng thời gian đã chọn.
            </p>
            <div className="report-actions">
              <button
                className="export-btn btn-pdf"
                onClick={() => handleDownloadReport('penalties', 'pdf')}
                disabled={loading === 'penalties-pdf'}
              >
                {loading === 'penalties-pdf' ? '⏳ Đang xuất...' : '📄 Xuất PDF'}
              </button>
              <button
                className="export-btn btn-excel"
                onClick={() => handleDownloadReport('penalties', 'excel')}
                disabled={loading === 'penalties-excel'}
              >
                {loading === 'penalties-excel' ? '⏳ Đang xuất...' : '📊 Xuất Excel'}
              </button>
              <button
                className="export-btn btn-csv"
                onClick={() => handleDownloadReport('penalties', 'csv')}
                disabled={loading === 'penalties-csv'}
              >
                {loading === 'penalties-csv' ? '⏳ Đang xuất...' : '📋 Xuất CSV'}
              </button>
            </div>
          </div>

          {/* Card 3: Lab Utilization Report */}
          <div className="report-card">
            <div className="report-card-header">
              <div className="report-icon">📈</div>
              <h3>Báo cáo Hiệu suất Lab</h3>
            </div>
            <p className="report-description">
              Xuất file phân tích chi tiết về tần suất sử dụng, số giờ hoạt động, 
              và tỷ lệ lấp đầy của từng phòng lab.
            </p>
            <div className="report-actions">
              <button
                className="export-btn btn-pdf"
                onClick={() => handleDownloadReport('lab-utilization', 'pdf')}
                disabled={loading === 'lab-utilization-pdf'}
              >
                {loading === 'lab-utilization-pdf' ? '⏳ Đang xuất...' : '📄 Xuất PDF'}
              </button>
              <button
                className="export-btn btn-excel"
                onClick={() => handleDownloadReport('lab-utilization', 'excel')}
                disabled={loading === 'lab-utilization-excel'}
              >
                {loading === 'lab-utilization-excel' ? '⏳ Đang xuất...' : '📊 Xuất Excel'}
              </button>
              <button
                className="export-btn btn-csv"
                onClick={() => handleDownloadReport('lab-utilization', 'csv')}
                disabled={loading === 'lab-utilization-csv'}
              >
                {loading === 'lab-utilization-csv' ? '⏳ Đang xuất...' : '📋 Xuất CSV'}
              </button>
            </div>
          </div>

          {/* Card 4: Comprehensive Report */}
          <div className="report-card report-card-featured">
            <div className="report-card-header">
              <div className="report-icon">📑</div>
              <h3>Báo cáo Tổng hợp</h3>
              <span className="featured-badge">Đầy đủ nhất</span>
            </div>
            <p className="report-description">
              Xuất một file tổng hợp bao gồm tất cả các số liệu: đặt phòng, vi phạm, 
              tài chính, và hiệu suất sử dụng.
            </p>
            <div className="report-actions">
              <button
                className="export-btn btn-pdf"
                onClick={() => handleDownloadReport('comprehensive', 'pdf')}
                disabled={loading === 'comprehensive-pdf'}
              >
                {loading === 'comprehensive-pdf' ? '⏳ Đang xuất...' : '📄 Xuất PDF'}
              </button>
              <button
                className="export-btn btn-excel"
                onClick={() => handleDownloadReport('comprehensive', 'excel')}
                disabled={loading === 'comprehensive-excel'}
              >
                {loading === 'comprehensive-excel' ? '⏳ Đang xuất...' : '📊 Xuất Excel'}
              </button>
              <button
                className="export-btn btn-csv"
                onClick={() => handleDownloadReport('comprehensive', 'csv')}
                disabled={loading === 'comprehensive-csv'}
              >
                {loading === 'comprehensive-csv' ? '⏳ Đang xuất...' : '📋 Xuất CSV'}
              </button>
            </div>
          </div>
        </div>
      </div>

      {/* Help Section */}
      <div className="report-help">
        <h3>💡 Hướng dẫn sử dụng</h3>
        <ol>
          <li>Chọn khoảng thời gian bằng cách nhập ngày bắt đầu và ngày kết thúc, hoặc dùng các nút chọn nhanh.</li>
          <li>Chọn loại báo cáo bạn muốn xuất từ danh sách bên dưới.</li>
          <li>Nhấp vào định dạng file mong muốn (PDF, Excel, hoặc CSV).</li>
          <li>File báo cáo sẽ tự động được tải xuống về máy của bạn.</li>
        </ol>
      </div>
    </div>
  );
}
