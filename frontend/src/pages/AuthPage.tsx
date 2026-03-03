import { FormEvent, useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';

export const AuthPage = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const { login, signup } = useAuth();
  const isSignup = location.pathname.includes('signup');

  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const onSubmit = async (event: FormEvent) => {
    event.preventDefault();
    setError('');
    setLoading(true);
    try {
      if (isSignup) {
        await signup(email, password);
      } else {
        await login(email, password);
      }
      navigate('/dashboard');
    } catch {
      setError('Could not authenticate. Please verify credentials and retry.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-screen">
      <section className="hero">
        <h1>BaaS Fin Core</h1>
        <p>Production-grade digital banking console for users and administrators.</p>
        <ul>
          <li>Secure onboarding with JWT authentication</li>
          <li>Live transfers, balances and statement visibility</li>
          <li>Admin analytics, ledger health and audit readiness</li>
        </ul>
      </section>
      <form className="auth-card" onSubmit={onSubmit}>
        <h2>{isSignup ? 'Create account' : 'Sign in'}</h2>
        <label>
          Work email
          <input value={email} onChange={(e) => setEmail(e.target.value)} type="email" required />
        </label>
        <label>
          Password
          <input
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            type="password"
            minLength={8}
            required
          />
        </label>
        {error ? <p className="error">{error}</p> : null}
        <button disabled={loading} type="submit">{loading ? 'Please wait...' : isSignup ? 'Sign up' : 'Sign in'}</button>
        <p>
          {isSignup ? 'Already have an account?' : 'New to platform?'}{' '}
          <Link to={isSignup ? '/login' : '/signup'}>{isSignup ? 'Login' : 'Create account'}</Link>
        </p>
      </form>
    </div>
  );
};
