import { FormEvent, useState } from 'react';
import { transferApi } from '../api/client';

export const TransferPage = () => {
  const [fromAccountId, setFromAccountId] = useState('');
  const [toAccountId, setToAccountId] = useState('');
  const [amountInr, setAmountInr] = useState('');
  const [narrative, setNarrative] = useState('');
  const [message, setMessage] = useState('');

  const onSubmit = async (event: FormEvent) => {
    event.preventDefault();
    setMessage('');
    try {
      await transferApi.create({
        fromAccountId,
        toAccountId,
        amountPaise: Math.round(Number(amountInr) * 100),
        narrative
      });
      setMessage('Transfer request accepted and sent for async processing.');
      setNarrative('');
      setAmountInr('');
    } catch {
      setMessage('Transfer failed. Check account IDs, balance, and retry.');
    }
  };

  return (
    <section className="panel narrow">
      <h3>Initiate transfer</h3>
      <form className="form-grid" onSubmit={onSubmit}>
        <label>
          From account ID
          <input value={fromAccountId} onChange={(e) => setFromAccountId(e.target.value)} required />
        </label>
        <label>
          To account ID
          <input value={toAccountId} onChange={(e) => setToAccountId(e.target.value)} required />
        </label>
        <label>
          Amount (INR)
          <input value={amountInr} onChange={(e) => setAmountInr(e.target.value)} type="number" min="1" step="0.01" required />
        </label>
        <label>
          Narrative
          <input value={narrative} onChange={(e) => setNarrative(e.target.value)} required />
        </label>
        <button type="submit">Submit transfer</button>
      </form>
      {message ? <p className="info">{message}</p> : null}
    </section>
  );
};
