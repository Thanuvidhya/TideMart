import { useEffect, useState } from 'react';
import { getProduct, listProducts } from '../../api/productApi.js';
import { placeCustomerOrder } from '../../api/resellerApi.js';
import ResellerLayout from '../../layouts/ResellerLayout.jsx';

const field = 'w-full border rounded-lg px-3 py-2 mb-2';

export default function ResellerPlaceOrderPage() {
  const [products, setProducts] = useState([]);
  const [pid, setPid] = useState('');
  const [detail, setDetail] = useState(null);
  const [vid, setVid] = useState('');
  const [qty, setQty] = useState(1);
  const [lines, setLines] = useState([]);
  const [c, setC] = useState({ customerName: '', customerPhone: '', line1: '', city: '', state: '', pincode: '', paymentMethod: 'COD' });
  const [msg, setMsg] = useState('');
  useEffect(() => { listProducts({ size: 50 }).then((p) => setProducts(p.content)); }, []);
  useEffect(() => { setVid(''); if (pid) getProduct(pid).then(setDetail); }, [pid]);
  const set = (k) => (e) => setC({ ...c, [k]: e.target.value });
  const addLine = () => {
    const v = (detail?.variants || []).find((x) => String(x.id) === String(vid));
    if (!v) return setMsg('Choose a size');
    setLines([...lines, { productId: detail.product.id, variantId: v.id, qty: +qty, label: `${detail.product.name} (${v.size}) x${qty}` }]);
    setMsg('');
  };
  const submit = () => placeCustomerOrder({ ...c, items: lines.map(({ productId, variantId, qty }) => ({ productId, variantId, qty })) })
    .then((o) => { setMsg(`Order ${o.orderNo} placed. Customer pays Rs ${o.total}.`); setLines([]); })
    .catch((e) => setMsg(e.response?.data?.message || 'Could not place order'));
  return (
    <ResellerLayout title="Place an order for a customer">
      <div className="flex gap-2 flex-wrap mb-2">
        <select className="border rounded-lg px-2 py-2 flex-1" value={pid} onChange={(e) => setPid(e.target.value)}><option value="">Choose product</option>{products.map((p) => <option key={p.id} value={p.id}>{p.name}</option>)}</select>
        <select className="border rounded-lg px-2 py-2" value={vid} onChange={(e) => setVid(e.target.value)}><option value="">Size</option>{detail?.variants.map((v) => <option key={v.id} value={v.id}>{v.size} ({v.stock})</option>)}</select>
        <input type="number" min="1" className="border rounded-lg px-2 py-2 w-20" value={qty} onChange={(e) => setQty(e.target.value)} />
        <button className="px-4 border rounded-lg" onClick={addLine}>Add</button>
      </div>
      {lines.map((l, i) => <div key={i} className="text-sm mb-1">{l.label}</div>)}
      <h2 className="font-semibold mt-4 mb-2">Customer details</h2>
      <input className={field} placeholder="Customer name" value={c.customerName} onChange={set('customerName')} />
      <input className={field} placeholder="Phone (10 digits)" value={c.customerPhone} onChange={set('customerPhone')} />
      <input className={field} placeholder="Address" value={c.line1} onChange={set('line1')} />
      <div className="flex gap-2"><input className={field} placeholder="City" value={c.city} onChange={set('city')} /><input className={field} placeholder="State" value={c.state} onChange={set('state')} /><input className={field} placeholder="Pincode (try 560001)" value={c.pincode} onChange={set('pincode')} /></div>
      <select className={field} value={c.paymentMethod} onChange={set('paymentMethod')}><option value="COD">Cash on delivery</option><option value="UPI">UPI (demo)</option></select>
      <button disabled={!lines.length} className="w-full bg-brand-accent text-white py-2 rounded-lg font-semibold disabled:opacity-40" onClick={submit}>Place order</button>
      {msg && <p className="mt-3 text-sm">{msg}</p>}
    </ResellerLayout>
  );
}
