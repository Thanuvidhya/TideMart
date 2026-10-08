import { useEffect, useState } from 'react';
import { sellerAnalytics } from '../../api/sellerApi.js';
import SellerLayout from '../../layouts/SellerLayout.jsx';

export default function SellerAnalyticsPage() {
  const [rows, setRows] = useState([]);
  useEffect(() => { sellerAnalytics().then(setRows); }, []);
  const max = Math.max(1, ...rows.map((r) => r.revenue));
  return (
    <SellerLayout title="Sales analytics">
      <p className="text-sm text-slate-500 mb-3">Units and sales by product from orders that are not cancelled.</p>
      {!rows.length && <p className="text-slate-500">No sales yet.</p>}
      {rows.map((r) => (
        <div key={r.product} className="mb-3 text-sm">
          <div className="flex justify-between"><span>{r.product}</span><span>{r.units} units | Rs {r.revenue}</span></div>
          <div className="h-2 bg-slate-200 rounded"><div className="h-2 bg-brand rounded" style={{ width: `${(r.revenue / max) * 100}%` }} /></div>
        </div>
      ))}
    </SellerLayout>
  );
}
