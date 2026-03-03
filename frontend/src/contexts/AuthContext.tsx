import { createContext, PropsWithChildren, useContext, useMemo, useState } from 'react';
import { authApi, setAuthToken } from '../api/client';
import { UserSession } from '../types/domain';

type AuthContextValue = {
  session: UserSession | null;
  login: (email: string, password: string) => Promise<void>;
  signup: (email: string, password: string) => Promise<void>;
  logout: () => void;
};

const STORAGE_KEY = 'baas-fin-session';
const AuthContext = createContext<AuthContextValue | null>(null);

const normalizeRole = (email: string): 'ADMIN' | 'USER' => (email.includes('admin') ? 'ADMIN' : 'USER');

export const AuthProvider = ({ children }: PropsWithChildren) => {
  const [session, setSession] = useState<UserSession | null>(() => {
    const raw = localStorage.getItem(STORAGE_KEY);
    if (!raw) {
      return null;
    }
    const parsed = JSON.parse(raw) as UserSession;
    setAuthToken(parsed.token);
    return parsed;
  });

  const persist = (next: UserSession | null) => {
    if (next) {
      localStorage.setItem(STORAGE_KEY, JSON.stringify(next));
      setAuthToken(next.token);
    } else {
      localStorage.removeItem(STORAGE_KEY);
      setAuthToken(undefined);
    }
    setSession(next);
  };

  const login = async (email: string, password: string) => {
    const response = await authApi.login(email, password);
    persist({
      token: response.token,
      email,
      role: response.role ?? normalizeRole(email)
    });
  };

  const signup = async (email: string, password: string) => {
    const response = await authApi.signup(email, password);
    persist({
      token: response.token,
      email,
      role: response.role ?? normalizeRole(email)
    });
  };

  const logout = () => {
    persist(null);
  };

  const value = useMemo(() => ({ session, login, signup, logout }), [session]);

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};

export const useAuth = (): AuthContextValue => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used inside AuthProvider');
  }
  return context;
};
