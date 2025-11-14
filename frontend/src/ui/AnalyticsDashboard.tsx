import React, { useEffect, useState } from "react";
import { Line, Pie, Bar } from "react-chartjs-2";
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  BarElement,
  ArcElement,
  PointElement,
  LineElement,
  Tooltip,
  Legend
} from "chart.js";

ChartJS.register(
  CategoryScale,
  LinearScale,
  BarElement,
  ArcElement,
  PointElement,
  LineElement,
  Tooltip,
  Legend
);

// 🟩 THÊM TYPE Ở ĐÂY — BẮT BUỘC
type BookingStatus = "APPROVED" | "REJECTED" | "PENDING";

const apiBaseUrl =
  (import.meta as any).env.VITE_API_BASE_URL || "http://localhost:8080";

export default function AnalyticsDashboard({ token }: { token: string }) {
  const [bookings, setBookings] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchBookings();
  }, []);

  const fetchBookings = async () => {
    try {
      const res = await fetch(`${apiBaseUrl}/api/v1/bookings`, {
        headers: { Authorization: `Bearer ${token}` }
      });

      if (res.ok) {
        const data = await res.json();
        setBookings(data);
      }
    } catch (e) {
      console.error("Error fetching bookings", e);
    } finally {
      setLoading(false);
    }
  };

  if (loading) return <p>Loading analytics...</p>;
  if (bookings.length === 0) return <p>No data available</p>;

  // -------------------------------------------------------
  // 1️⃣ KPI: TODAY / WEEK / MONTH
  // -------------------------------------------------------
  const now = new Date();
  const todayStr = now.toISOString().split("T")[0];

  const totalToday = bookings.filter((b) =>
    b.createdAt.startsWith(todayStr)
  ).length;

  const weekAgo = new Date(now);
  weekAgo.setDate(now.getDate() - 7);

  const totalWeek = bookings.filter(
    (b) => new Date(b.createdAt) >= weekAgo
  ).length;

  const monthAgo = new Date(now);
  monthAgo.setMonth(now.getMonth() - 1);

  const totalMonth = bookings.filter(
    (b) => new Date(b.createdAt) >= monthAgo
  ).length;

  // -------------------------------------------------------
  // 2️⃣ SUCCESS VS CANCEL
  // -------------------------------------------------------
  const statusCount: Record<BookingStatus, number> = {
    APPROVED: 0,
    REJECTED: 0,
    PENDING: 0
  };

  bookings.forEach((b) => {
    const s = b.status as BookingStatus; // ép kiểu an toàn
    if (statusCount[s] !== undefined) statusCount[s]++;
  });

  const successCancelData = {
    labels: ["Approved", "Rejected"],
    datasets: [
      {
        data: [statusCount.APPROVED, statusCount.REJECTED],
        backgroundColor: ["#10b981", "#ef4444"]
      }
    ]
  };

  // -------------------------------------------------------
  // 3️⃣ MOST USED LAB
  // -------------------------------------------------------
  const labUsage: Record<string, number> = {};

  bookings.forEach((b) => {
    if (!labUsage[b.labName]) labUsage[b.labName] = 0;
    labUsage[b.labName]++;
  });

  const labRanking = {
    labels: Object.keys(labUsage),
    datasets: [
      {
        label: "Bookings",
        data: Object.values(labUsage),
        backgroundColor: "#6366f1"
      }
    ]
  };

  // -------------------------------------------------------
  // 4️⃣ BOOKINGS OVER TIME (LINE)
  // -------------------------------------------------------
  const dateCount: Record<string, number> = {};

  bookings.forEach((b) => {
    const d = b.createdAt.split("T")[0];
    if (!dateCount[d]) dateCount[d] = 0;
    dateCount[d]++;
  });

  const lineData = {
    labels: Object.keys(dateCount),
    datasets: [
      {
        label: "Bookings per day",
        data: Object.values(dateCount),
        borderColor: "#3b82f6",
        backgroundColor: "rgba(59,130,246,0.3)"
      }
    ]
  };

  // -------------------------------------------------------
  // 5️⃣ PEAK HOURS
  // -------------------------------------------------------
  const hourCount: Record<string, number> = {};

  bookings.forEach((b) => {
    const hour = new Date(b.startTime).getHours();
    if (!hourCount[hour]) hourCount[hour] = 0;
    hourCount[hour]++;
  });

  const hourData = {
    labels: Object.keys(hourCount).map((h) => `${h}:00`),
    datasets: [
      {
        label: "Bookings",
        data: Object.values(hourCount),
        backgroundColor: "#f59e0b"
      }
    ]
  };

  // -------------------------------------------------------
  // 6️⃣ CANCEL REASON
  // -------------------------------------------------------
  const reasonCount: Record<string, number> = {};

  bookings.forEach((b) => {
    if (b.cancellationReason) {
      if (!reasonCount[b.cancellationReason])
        reasonCount[b.cancellationReason] = 0;
      reasonCount[b.cancellationReason]++;
    }
  });

  const cancelReasonData = {
    labels: Object.keys(reasonCount),
    datasets: [
      {
        label: "Count",
        data: Object.values(reasonCount),
        backgroundColor: ["#ef4444", "#f87171", "#fecaca"]
      }
    ]
  };

  return (
    <div style={{ padding: "20px" }}>
      <h2 style={{ marginBottom: "20px" }}>📊 Analytics Dashboard</h2>

      {/* KPI CARDS */}
      <div className="kpi-container">
        <div className="kpi-card">Today: {totalToday}</div>
        <div className="kpi-card">This Week: {totalWeek}</div>
        <div className="kpi-card">This Month: {totalMonth}</div>
        <div className="kpi-card">
          Success Rate: {statusCount.APPROVED} ✔ / {statusCount.REJECTED} ✘
        </div>
      </div>

      {/* CHARTS */}
      <div className="chart-grid">

        <div className="chart-card">
          <h3>Bookings Over Time</h3>
          <Line data={lineData} />
        </div>

        <div className="chart-card">
          <h3>Success vs Cancel</h3>
          <Pie data={successCancelData} />
        </div>

        <div className="chart-card">
          <h3>Lab Usage Ranking</h3>
          <Bar data={labRanking} />
        </div>

        <div className="chart-card">
          <h3>Peak Hours</h3>
          <Bar data={hourData} />
        </div>

        <div className="chart-card">
          <h3>Cancellation Reasons</h3>
          <Pie data={cancelReasonData} />
        </div>

      </div>
    </div>
  );
}
