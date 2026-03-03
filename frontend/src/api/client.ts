import axios from 'axios';
import {
  Account,
  AuditRecord,
  AuthResponse,
  MetricsOverview,
  StatementLine,
  TransferRequest
} from '../types/domain';

const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080',
  timeout: 12000
});

export const setAuthToken = (token?: string): void => {
  if (token) {
    api.defaults.headers.common.Authorization = `Bearer ${token}`;
  } else {
    delete api.defaults.headers.common.Authorization;
  }
};

export const authApi = {
  signup: async (email: string, password: string): Promise<AuthResponse> => {
    const response = await api.post('/api/v1/auth/signup', { email, password });
    return response.data;
  },
  login: async (email: string, password: string): Promise<AuthResponse> => {
    const response = await api.post('/api/v1/auth/login', { email, password });
    return response.data;
  }
};

export const accountApi = {
  list: async (): Promise<Account[]> => {
    const response = await api.get('/api/v1/accounts');
    return response.data;
  },
  create: async (): Promise<Account> => {
    const response = await api.post('/api/v1/accounts');
    return response.data;
  }
};

export const transferApi = {
  create: async (transfer: TransferRequest): Promise<void> => {
    await api.post('/api/v1/transfers', transfer, {
      headers: {
        'Idempotency-Key': crypto.randomUUID()
      }
    });
  }
};

export const statementApi = {
  byAccount: async (accountId: string): Promise<StatementLine[]> => {
    const response = await api.get(`/api/v1/statements/${accountId}`);
    return response.data;
  }
};

export const adminApi = {
  metrics: async (): Promise<MetricsOverview> => {
    const response = await api.get('/api/v1/metrics/overview');
    return response.data;
  },
  recentAudit: async (): Promise<AuditRecord[]> => {
    const response = await api.get('/api/v1/audit/recent');
    return response.data;
  }
};
