import { useEffect, useMemo, useState } from 'react';
import { accountApi } from '../api/client';
import { StatCard } from '../components/StatCard';
import { Account } from '../types/domain';

const formatCurrency = (paise: number) =>
  new Intl.NumberFormat('en-IN', { style: 'currency', currency: 'INR' }).format(paise / 100);

export const DashboardPage = () => {
  const [accounts, setAccounts] = useState<Account[]>([]);

  useEffect(() => {
    accountApi
      .list()
      .then(setAccounts)
      .catch(() => {
        setAccounts([]);
      });
  }, []);

  const totalBalance = useMemo(
    () => accounts.reduce((sum, account) => sum + (account.currentBalancePaise ?? 0), 0),
    [accounts]
  );

  return (
    <section className="stack">
      <div className="grid cols-3">
        <StatCard label="Total accounts" value={String(accounts.length)} />
        <StatCard label="Portfolio balance" value={formatCurrency(totalBalance)} helper="Across visible accounts" />
        <StatCard
          label="Active accounts"
          value={String(accounts.filter((a) => a.status === 'ACTIVE').length)}
          helper="Operational accounts"
        />
      </div>
      <section className="panel">
        <h3>Your accounts</h3>
        {accounts.length === 0 ? (
          <p>No accounts found yet. Create your first account from API or onboarding flow.</p>
        ) : (
          <div className="table-wrap">
            <table>
              <thead>
                <tr>
                  <th>Account ID</th>
                  <th>Status</th>
                  <th>Current</th>
                  <th>Available</th>
                </tr>
              </thead>
              <tbody>
                {accounts.map((account) => (
                  <tr key={account.id}>
                    <td>{account.id}</td>
                    <td>{account.status}</td>
                    <td>{formatCurrency(account.currentBalancePaise ?? 0)}</td>
                    <td>{formatCurrency(account.availableBalancePaise ?? 0)}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </section>
    </section>
  );
};
