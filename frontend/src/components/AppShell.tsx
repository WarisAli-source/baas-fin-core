import { NavLink, Outlet } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';

const links = [
  { to: '/dashboard', label: 'Dashboard' },
  { to: '/transfer', label: 'Transfers' },
  { to: '/transactions', label: 'Transactions' },
  { to: '/admin', label: 'Admin Analytics' }
];

export const AppShell = () => {
  const { session, logout } = useAuth();
  return (
    <div className="layout">
      <aside className="sidebar">
        <h1>BaaS Core</h1>
        <p className="caption">Modern banking operations console</p>
        <nav>
          {links
            .filter((link) => (link.to === '/admin' ? session?.role === 'ADMIN' : true))
            .map((link) => (
              <NavLink key={link.to} to={link.to} className={({ isActive }) => (isActive ? 'active' : '')}>
                {link.label}
              </NavLink>
            ))}
        </nav>
        <button onClick={logout} className="ghost">Sign out</button>
      </aside>
      <main className="content">
        <header>
          <div>
            <h2>Welcome back</h2>
            <p>{session?.email}</p>
          </div>
          <span className="badge">{session?.role}</span>
        </header>
        <Outlet />
      </main>
    </div>
  );
};
