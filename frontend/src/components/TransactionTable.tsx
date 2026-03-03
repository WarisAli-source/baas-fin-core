import { StatementLine } from '../types/domain';

const formatCurrency = (paise: number) =>
  new Intl.NumberFormat('en-IN', { style: 'currency', currency: 'INR' }).format(paise / 100);

export const TransactionTable = ({ rows }: { rows: StatementLine[] }) => (
  <div className="table-wrap">
    <table>
      <thead>
        <tr>
          <th>Time</th>
          <th>Direction</th>
          <th>Narrative</th>
          <th>Amount</th>
        </tr>
      </thead>
      <tbody>
        {rows.map((row) => (
          <tr key={row.id}>
            <td>{new Date(row.createdAt).toLocaleString()}</td>
            <td>
              <span className={row.direction === 'CREDIT' ? 'pill credit' : 'pill debit'}>{row.direction}</span>
            </td>
            <td>{row.narrative ?? '-'}</td>
            <td>{formatCurrency(row.amountPaise)}</td>
          </tr>
        ))}
      </tbody>
    </table>
  </div>
);
