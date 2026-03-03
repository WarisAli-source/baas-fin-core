export type AuthResponse = {
  token: string;
  role?: 'USER' | 'ADMIN';
};

export type UserSession = {
  token: string;
  email: string;
  role: 'USER' | 'ADMIN';
};

export type Account = {
  id: string;
  status: string;
  currentBalancePaise?: number;
  availableBalancePaise?: number;
};

export type TransferRequest = {
  fromAccountId: string;
  toAccountId: string;
  amountPaise: number;
  narrative: string;
};

export type StatementLine = {
  id: string;
  accountId: string;
  direction: 'DEBIT' | 'CREDIT';
  amountPaise: number;
  narrative?: string;
  createdAt: string;
};

export type MetricsOverview = {
  totalTransfers: number;
  successTransfers: number;
  failedTransfers: number;
  totalVolumePaise: number;
  avgAmountPaise: number;
  activeAccounts?: number;
};

export type AuditRecord = {
  id: string;
  eventType: string;
  aggregateId: string;
  payload: string;
  createdAt: string;
};
