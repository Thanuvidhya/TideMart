import { useEffect, useState } from 'react';
import { resellerCustomers } from '../../api/resellerApi.js';
import ResellerLayout from '../../layouts/ResellerLayout.jsx';

export default function ResellerCustomersPage() {
  const [rows, setRows] = useState([]);
  useEffect(() => { resellerCustomers().then(setRows); }, []);
  return (
    <ResellerLayout title="My customers">
      {!rows.length && <p className="text-slate-500">Customers are saved here when you place an order for them.</p>}
      {rows.map((c) => <div key={c.id} className="bg-white border rounded-xl p-3 mb-2 text-sm"><b>{c.name}</b> | {c.phone}<br />{c.address}</div>)}
    </ResellerLayout>
  );
}
