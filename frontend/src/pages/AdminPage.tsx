import { useEffect, useMemo, useState } from 'react';
import { Area, AreaChart, CartesianGrid, ResponsiveContainer, Tooltip, XAxis, YAxis } from 'recharts';
import { adminApi } from '../api/client';
import { StatCard } from '../components/StatCard';
import { AuditRecord, MetricsOverview } from '../types/domain';

const formatCurrency = (paise: number) =>
  new Intl.NumberFormat('en-IN', { style: 'currency', currency: 'INR' }).format(paise / 100);

const demoMetrics: MetricsOverview = {
  totalTransfers: 1820,
  successTransfers: 1756,
  failedTransfers: 64,
  totalVolumePaise: 987000000,
  avgAmountPaise: 542300,
  activeAccounts: 212
};

export const AdminPage = () => {
  const [metrics, setMetrics] = useState<MetricsOverview>(demoMetrics);
  const [auditRows, setAuditRows] = useState<AuditRecord[]>([]);
  const [message, setMessage] = useState('Live admin data loaded.');

  useEffect(() => {
    Promise.all([adminApi.metrics(), adminApi.recentAudit()])
      .then(([m, a]) => {
        setMetrics({ ...demoMetrics, ...m });
        setAuditRows(a);
      })
      .catch(() => {
        setMessage('Admin APIs unavailable. Displaying portfolio analytics preview.');
        setAuditRows([]);
      });
  }, []);

  const trend = useMemo(
    () => [
      { slot: '09:00', tx: Math.round(metrics.totalTransfers * 0.12) },
      { slot: '11:00', tx: Math.round(metrics.totalTransfers * 0.24) },
      { slot: '13:00', tx: Math.round(metrics.totalTransfers * 0.48) },
      { slot: '15:00', tx: Math.round(metrics.totalTransfers * 0.63) },
      { slot: '17:00', tx: Math.round(metrics.totalTransfers * 0.87) },
      { slot: '19:00', tx: metrics.totalTransfers }
    ],
    [metrics.totalTransfers]
  );

  return (
    <section className="stack">
      <p className="caption">{message}</p>
      <div className="grid cols-4">
        <StatCard label="Transfers" value={String(metrics.totalTransfers)} />
        <StatCard label="Success" value={String(metrics.successTransfers)} />
        <StatCard label="Failed" value={String(metrics.failedTransfers)} />
        <StatCard label="Volume" value={formatCurrency(metrics.totalVolumePaise)} />
      </div>
      <section className="panel">
        <h3>Transfer throughput trend</h3>
        <div style={{ height: 260 }}>
          <ResponsiveContainer>
            <AreaChart data={trend}>
              <defs>
                <linearGradient id="txColor" x1="0" y1="0" x2="0" y2="1">
                  <stop offset="5%" stopColor="#67e8f9" stopOpacity={0.9} />
                  <stop offset="95%" stopColor="#67e8f9" stopOpacity={0.05} />
                </linearGradient>
              </defs>
              <CartesianGrid strokeDasharray="3 3" stroke="#1f2a44" />
              <XAxis dataKey="slot" stroke="#9fb0d0" />
              <YAxis stroke="#9fb0d0" />
              <Tooltip />
              <Area type="monotone" dataKey="tx" stroke="#67e8f9" fill="url(#txColor)" />
            </AreaChart>
          </ResponsiveContainer>
        </div>
      </section>
      <section className="panel">
        <h3>Recent audit trail</h3>
        {auditRows.length === 0 ? (
          <p>No live audit rows returned. Ensure admin role authorization is configured in backend.</p>
        ) : (
          <div className="table-wrap">
            <table>
              <thead>
                <tr>
                  <th>Time</th>
                  <th>Event type</th>
                  <th>Aggregate ID</th>
                </tr>
              </thead>
              <tbody>
                {auditRows.map((row) => (
                  <tr key={row.id}>
                    <td>{new Date(row.createdAt).toLocaleString()}</td>
                    <td>{row.eventType}</td>
                    <td>{row.aggregateId}</td>
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
