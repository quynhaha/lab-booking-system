import { useState, useEffect } from 'react';
import './Login.css';
import AdminDashboard from './AdminDashboard';

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

interface LoginResponse {
  token: string;
  loginMethod: string;
  userType: string;
  message: string;
}

export const App: React.FC = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState<string | null>(null);
  const [authenticated, setAuthenticated] = useState(false);
  const [user, setUser] = useState<User | null>(null);
  const [token, setToken] = useState<string | null>(null);

  // Check for existing token on mount
  useEffect(() => {
    const storedToken = localStorage.getItem('jwt_token');
    if (storedToken) {
      verifyToken(storedToken);
    }
  }, []);

  const verifyToken = async (jwtToken: string) => {
    try {
      const res = await fetch(`${apiBaseUrl}/api/auth/me`, {
        headers: {
          'Authorization': `Bearer ${jwtToken}`,
        },
      });
      if (res.ok) {
        const userData = await res.json();
        // Only allow ADMIN role for Web Admin Dashboard
        if (userData.role === 'ADMIN') {
          setUser(userData);
          setToken(jwtToken);
          setAuthenticated(true);
        } else {
          // Not admin, clear token
          localStorage.removeItem('jwt_token');
          setMessage('Access denied: Admin role required');
        }
      } else {
        // Token invalid, clear it
        localStorage.removeItem('jwt_token');
      }
    } catch (error) {
      console.error('Token verification failed:', error);
      localStorage.removeItem('jwt_token');
    }
  };

  const submit = async (e?: React.FormEvent) => {
    e?.preventDefault();
    setMessage(null);
    
    if (!email || !password) {
      setMessage('Vui lòng nhập email và mật khẩu.');
      return;
    }

    setLoading(true);
    
    try {
      // Use FPT SSO login endpoint
      const res = await fetch(`${apiBaseUrl}/api/auth/login/fpt-sso`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email, password }),
      });

      if (!res.ok) {
        const errorData = await res.json().catch(() => null);
        throw new Error(errorData?.message || `Login failed: ${res.status}`);
      }

      const data: LoginResponse = await res.json();
      
      // Save token
      localStorage.setItem('jwt_token', data.token);
      setToken(data.token);

      // Fetch user info
      const userRes = await fetch(`${apiBaseUrl}/api/auth/me`, {
        headers: {
          'Authorization': `Bearer ${data.token}`,
        },
      });

      if (!userRes.ok) {
        throw new Error('Failed to fetch user info');
      }

      const userData: User = await userRes.json();

      // Check if user is admin
      if (userData.role !== 'ADMIN') {
        setMessage('Access denied: Admin role required for Web Admin Dashboard');
        localStorage.removeItem('jwt_token');
        return;
      }

      setUser(userData);
      setAuthenticated(true);
      setMessage('Đăng nhập thành công!');
      
    } catch (err: any) {
      setMessage(err?.message || 'Lỗi khi đăng nhập. Vui lòng kiểm tra lại thông tin.');
      console.error('Login error:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleLogout = () => {
    localStorage.removeItem('jwt_token');
    setAuthenticated(false);
    setUser(null);
    setToken(null);
    setMessage(null);
  };

  if (authenticated && user && user.role === 'ADMIN') {
    return <AdminDashboard onLogout={handleLogout} user={user} token={token!} />;
  }

  return (
    <div className="login-page">
      <div className="login-brand">
        <img 
          src="https://iconape.com/wp-content/png_logo_vector/fpt-university-logo.png" 
          alt="FPT University" 
          className="logo-img" 
        />
        <h1>Lab Booking System</h1>
        <p className="sub">Admin Dashboard</p>
      </div>

      <form className="login-card" onSubmit={submit}>
        <h2>Admin Login</h2>
        <p className="hint">Enter your admin credentials to access the dashboard</p>

        <label className="label">Email</label>
        <input
          className="input"
          type="email"
          placeholder="admin@fpt.edu.vn"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          disabled={loading}
        />

        <label className="label">Password</label>
        <input
          className="input"
          type="password"
          placeholder="Enter your password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          disabled={loading}
        />

        <button className="btn primary" type="submit" disabled={loading}>
          {loading ? 'Signing in...' : 'Sign In'}
        </button>

        {message && (
          <div className={`message ${authenticated ? 'success' : ''}`}>
            {message}
          </div>
        )}
      </form>
    </div>
  );
};
