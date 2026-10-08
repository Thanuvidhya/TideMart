import { useEffect, useState } from 'react';
import { getResellerMe, resellerEarnings, savePayoutDetails } from '../../api/resellerApi.js';
import ResellerLayout from '../../layouts/ResellerLayout.jsx';

export default function ResellerEarningsPage() {
  const [e, setE] = useState(null);
  const [p, setP] = useState({ upiId: '', bankAccount: '', ifsc: '' });
  const [msg, setMsg] = useState('');
  useEffect(() => { resellerEarnings().then(setE); getResellerMe().then((r) => setP({ upiId: r.upiId || '', bankAccount: r.bankAccount || '', ifsc: r.ifsc || '' })); }, []);
  const set = (k) => (ev) => setP({ ...p, [k]: ev.target.value });
  const Card = ({ k, v }) => <div className="bg-white border rounded-2xl p-4"><div className="text-sm text-slate-500">{k}</div><div className="text-2xl font-extrabold">{v}</div></div>;
  return (
    <ResellerLayout title="Earnings">
      {e && <div className="grid grid-cols-3 gap-3 mb-5"><Card k="Earned (delivered)" v={`Rs ${e.earned}`} /><Card k="Pending" v={`Rs ${e.pending}`} /><Card k="Delivered orders" v={e.deliveredOrders} /></div>}
      <p className="text-sm text-slate-500 mb-4">Margin counts once an order is delivered. Returned items are not counted. Payouts are processed by the admin in a later phase.</p>
      <h2 className="font-semibold mb-2">Payout details</h2>
      {['upiId', 'bankAccount', 'ifsc'].map((k) => <input key={k} className="w-full border rounded-lg px-3 py-2 mb-2" placeholder={k} value={p[k]} onChange={set(k)} />)}
      <button className="px-4 py-2 bg-brand text-white rounded-lg" onClick={() => savePayoutDetails(p).then(() => setMsg('Saved'))}>Save</button>
      {msg && <span className="ml-3 text-sm">{msg}</span>}
    </ResellerLayout>
  );
}
