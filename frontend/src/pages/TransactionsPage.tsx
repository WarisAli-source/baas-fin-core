import { FormEvent, useState } from 'react';
import { statementApi } from '../api/client';
import { TransactionTable } from '../components/TransactionTable';
import { StatementLine } from '../types/domain';

const sampleRows: StatementLine[] = [
  {
    id: 'demo-1',
    accountId: 'sample',
    direction: 'DEBIT',
    amountPaise: 125000,
    narrative: 'Vendor settlement',
    createdAt: new Date(Date.now() - 3600000).toISOString()
  },
  {
    id: 'demo-2',
    accountId: 'sample',
    direction: 'CREDIT',
    amountPaise: 270000,
    narrative: 'Salary credit',
    createdAt: new Date(Date.now() - 7200000).toISOString()
  }
];

export const TransactionsPage = () => {
  const [accountId, setAccountId] = useState('');
  const [rows, setRows] = useState<StatementLine[]>([]);
  const [message, setMessage] = useState('Enter account ID to load real statement lines.');

  const load = async (event: FormEvent) => {
    event.preventDefault();
    try {
      const response = await statementApi.byAccount(accountId);
      setRows(response);
      setMessage(response.length ? 'Live statement loaded.' : 'No lines found for this account.');
    } catch {
      setRows(sampleRows);
      setMessage('Statement API unavailable. Displaying demo transactions for preview.');
    }
  };

  return (
    <section className="stack">
      <section className="panel narrow">
        <h3>Past transactions</h3>
        <form className="form-inline" onSubmit={load}>
          <input value={accountId} onChange={(e) => setAccountId(e.target.value)} placeholder="Account ID" required />
          <button type="submit">Load statement</button>
        </form>
        <p className="caption">{message}</p>
      </section>
      <section className="panel">
        <TransactionTable rows={rows} />
      </section>
    </section>
  );
};
