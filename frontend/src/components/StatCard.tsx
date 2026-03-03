type Props = {
  label: string;
  value: string;
  helper?: string;
};

export const StatCard = ({ label, value, helper }: Props) => (
  <article className="stat-card">
    <p>{label}</p>
    <h3>{value}</h3>
    {helper ? <small>{helper}</small> : null}
  </article>
);
