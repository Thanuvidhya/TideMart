import { useEffect, useState } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { addToCart } from '../../api/cartApi.js';
import { advanceOrder, cancelOrder, getOrder, requestReturn } from '../../api/orderApi.js';
import Navbar from '../../components/common/Navbar.jsx';

const FLOW = ['PLACED', 'PACKED', 'SHIPPED', 'OUT_FOR_DELIVERY', 'DELIVERED'];

export default function OrderDetailPage() {
  const { id } = useParams();
  const nav = useNavigate();
  const [d, setD] = useState(null);
  const [err, setErr] = useState('');
  useEffect(() => { getOrder(id).then(setD); }, [id]);
  const act = (fn) => fn(id).then(setD).catch((e) => setErr(e.response?.data?.message || 'Failed'));
  if (!d) return <Navbar />;
  const o = d.order, step = FLOW.indexOf(o.status), cancelled = o.status === 'CANCELLED';
  const ret = (i) => {
    const reason = window.prompt('Reason for return (wrong size, damaged, not as shown)');
    if (reason) requestReturn({ orderId: o.id, orderItemId: i.itemId, type: 'RETURN', reason }).then(() => setErr('Return requested. See My returns.')).catch((e) => setErr(e.response?.data?.message || 'Failed'));
  };
  return (
    <>
      <Navbar />
      <main className="max-w-3xl mx-auto p-4">
        <h1 className="text-2xl font-bold">Order {o.orderNo}</h1>
        <p className="text-sm text-slate-500 mb-4">{o.paymentMethod} | payment {o.paymentStatus.toLowerCase()} | total Rs {o.total}</p>
        {cancelled ? <p className="text-red-600 font-semibold mb-4">This order was cancelled.</p> : (
          <div className="flex justify-between mb-6">
            {FLOW.map((s, k) => (
              <div key={s} className="flex-1 text-center text-xs">
                <div className={`mx-auto w-5 h-5 rounded-full border-4 ${k <= step ? 'bg-green-600 border-green-600' : 'border-slate-300'}`} />
                <div className={k <= step ? 'font-semibold' : 'text-slate-400'}>{s.replaceAll('_', ' ')}</div>
              </div>
            ))}
          </div>
        )}
        {d.items.map((i) => <div key={i.itemId} className="bg-white border rounded-2xl p-3 mb-3 flex gap-3 items-center"><Link to={`/product/${i.productId}`} className="w-24 h-24 rounded-xl overflow-hidden bg-slate-100 shrink-0"><img src={i.imageUrl||'/products/floral-cotton-kurti.svg'} alt={i.name} className="w-full h-full object-cover"/></Link><div className="min-w-0 flex-1"><Link to={`/product/${i.productId}`} className="font-semibold hover:text-brand">{i.name}</Link><p className="text-sm text-slate-500">Size: {i.size || 'Free'} · Qty: {i.qty}</p><p className="font-bold mt-1">Rs {i.unitPrice * i.qty}</p><div className="mt-2">{o.status === 'DELIVERED' && <button className="text-sm text-brand" onClick={() => ret(i)}>Return</button>}</div></div></div>)}
        <p className="text-sm mt-3">Deliver to: {o.shipName}, {o.shipLine1}, {o.shipCity} {o.shipPincode}</p>
        <div className="flex gap-2 mt-4 flex-wrap">
          <button className="px-4 py-2 border rounded-lg" onClick={() => Promise.all(d.items.map((i) => addToCart(i.productId, i.variantId, i.qty))).then(() => nav('/cart')).catch((e) => setErr(e.response?.data?.message || 'Could not reorder'))}>Reorder</button>
          <Link className="px-4 py-2 border rounded-lg" to={`/orders/${id}/invoice`}>Invoice</Link>
          {['PLACED', 'PACKED'].includes(o.status) && <button className="px-4 py-2 border rounded-lg text-red-600" onClick={() => act(cancelOrder)}>Cancel order</button>}
          {step >= 0 && step < 4 && <button className="px-4 py-2 border rounded-lg" onClick={() => act(advanceOrder)}>Next status (demo)</button>}
        </div>
        {err && <p className="text-red-600 mt-2 text-sm">{err}</p>}
        <h2 className="font-semibold mt-6 mb-2">History</h2>
        {d.history.map((h) => <div key={h.id} className="text-sm text-slate-600">{h.createdAt.slice(0, 16).replace('T', ' ')} - {h.status.replaceAll('_', ' ')}</div>)}
      </main>
    </>
  );
}
