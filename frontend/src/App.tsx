import { Navigate, Route, Routes } from 'react-router-dom';
import { AppShell } from './components/AppShell';
import { useAuth } from './contexts/AuthContext';
import { AdminPage } from './pages/AdminPage';
import { AuthPage } from './pages/AuthPage';
import { DashboardPage } from './pages/DashboardPage';
import { TransactionsPage } from './pages/TransactionsPage';
import { TransferPage } from './pages/TransferPage';

const Protected = ({ children }: { children: JSX.Element }) => {
  const { session } = useAuth();
  if (!session) {
    return <Navigate to="/login" replace />;
  }
  return children;
};

const AdminProtected = ({ children }: { children: JSX.Element }) => {
  const { session } = useAuth();
  if (!session) {
    return <Navigate to="/login" replace />;
  }
  if (session.role !== 'ADMIN') {
    return <Navigate to="/dashboard" replace />;
  }
  return children;
};

export const App = () => (
  <Routes>
    <Route path="/login" element={<AuthPage />} />
    <Route path="/signup" element={<AuthPage />} />
    <Route
      path="/"
      element={
        <Protected>
          <AppShell />
        </Protected>
      }
    >
      <Route index element={<Navigate to="/dashboard" replace />} />
      <Route path="dashboard" element={<DashboardPage />} />
      <Route path="transfer" element={<TransferPage />} />
      <Route path="transactions" element={<TransactionsPage />} />
      <Route
        path="admin"
        element={
          <AdminProtected>
            <AdminPage />
          </AdminProtected>
        }
      />
    </Route>
    <Route path="*" element={<Navigate to="/dashboard" replace />} />
  </Routes>
);
