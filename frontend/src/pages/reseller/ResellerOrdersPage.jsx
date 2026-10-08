import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { resellerOrders } from '../../api/resellerApi.js';
import ResellerLayout from '../../layouts/ResellerLayout.jsx';

export default function ResellerOrdersPage() {
  const [rows, setRows] = useState([]);
  useEffect(() => { resellerOrders().then(setRows); }, []);
  return (
    <ResellerLayout title="Customer orders">
      {!rows.length && <p className="text-slate-500">No orders yet.</p>}
      {rows.map((o) => (
        <Link key={o.id} to={`/orders/${o.id}`} className="block bg-white border rounded-xl p-3 mb-2">
          <div className="flex justify-between"><b>{o.orderNo}</b><span className="text-sm">{o.status.replaceAll('_', ' ')}</span></div>
          <div className="text-sm text-slate-500">{o.customer}, {o.city} | {o.createdAt} | Rs {o.total} | your margin <b>Rs {o.margin}</b></div>
        </Link>
      ))}
    </ResellerLayout>
  );
}
