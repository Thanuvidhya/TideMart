import { useEffect, useState } from 'react';
import { getLabel, rejectOrder, sellerOrders, setOrderStatus } from '../../api/sellerApi.js';
import SellerLayout from '../../layouts/SellerLayout.jsx';

const NEXT = { PLACED: 'PACKED', PACKED: 'SHIPPED' };

export default function SellerOrdersPage() {
  const [list, setList] = useState([]);
  const [label, setLabel] = useState(null);
  const [err, setErr] = useState('');
  const load = () => sellerOrders().then(setList);
  useEffect(() => { load(); }, []);
  const move = (o) => setOrderStatus(o.orderId, NEXT[o.status]).then(load).catch((e) => setErr(e.response?.data?.message || 'Failed'));
  return (
    <SellerLayout title="Orders to ship">
      {!list.length && <p className="text-slate-500">No orders yet.</p>}
      {err && <p className="text-red-600 text-sm mb-2">{err}</p>}
      {list.map((o, i) => (
        <div key={i} className="bg-white border rounded-xl p-3 mb-2">
          <div className="flex justify-between"><b>{o.orderNo}</b><span className="text-sm">{o.status.replaceAll('_', ' ')}</span></div>
          <div className="text-sm">{o.product} (size {o.size}) x{o.qty} | Rs {o.unitPrice * o.qty} | {o.paymentMethod}</div>
          <div className="text-sm text-slate-500">To {o.shipName}, {o.shipCity} | {o.createdAt}</div>
          <div className="flex gap-2 mt-2">
            {NEXT[o.status] && <button className="px-3 py-1 border rounded-lg text-sm" onClick={() => move(o)}>Mark {NEXT[o.status].toLowerCase()}</button>}
            {o.status === 'PLACED' && <button className="px-3 py-1 border rounded-lg text-sm text-red-600" onClick={() => { const r = window.prompt('Reason for rejecting'); if (r) rejectOrder(o.orderId, r).then(load).catch((e) => setErr(e.response?.data?.message || 'Failed')); }}>Reject</button>}
            <button className="px-3 py-1 border rounded-lg text-sm" onClick={() => getLabel(o.orderId).then(setLabel)}>Shipping label</button>
          </div>
        </div>
      ))}
      {label && (
        <div className="border-2 border-dashed rounded-xl p-4 mt-4 bg-white text-sm">
          <b>{label.orderNo}</b> | {label.paymentMethod}{label.collectOnDelivery > 0 && ` | Collect Rs ${label.collectOnDelivery}`}<br />
          <b>To:</b> {label.shipTo}, {label.phone}<br />{label.address}<br />
          <b>From:</b> {label.sentBy}, {label.pickup}<br /><b>Items:</b> {label.items.join(', ')}<br />
          <button className="mt-3 px-3 py-1 border rounded-lg" onClick={() => window.print()}>Print</button>
        </div>
      )}
    </SellerLayout>
  );
}
