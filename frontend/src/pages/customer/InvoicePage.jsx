import { useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import { getOrder } from '../../api/orderApi.js';

export default function InvoicePage() {
  const { id } = useParams();
  const [d, setD] = useState(null);
  useEffect(() => { getOrder(id).then(setD); }, [id]);
  if (!d) return null;
  const o = d.order;
  return (
    <main className="max-w-2xl mx-auto p-6 bg-white">
      <div className="flex justify-between items-center print:hidden mb-4"><Link to={`/orders/${id}`} className="text-brand text-sm">Back</Link><button className="px-3 py-1 border rounded" onClick={() => window.print()}>Print or save as PDF</button></div>
      <img src="/tidemart-logo.svg" alt="Tidemart" className="h-12 mb-2" />
      <h1 className="text-xl font-bold">Invoice {o.orderNo}</h1>
      <p className="text-sm text-slate-500 mb-4">Date {o.createdAt.slice(0, 10)} | {o.paymentMethod} | payment {o.paymentStatus.toLowerCase()}</p>
      <p className="text-sm mb-4"><b>Bill to:</b> {o.shipName}, {o.shipLine1}, {o.shipCity}, {o.shipState} {o.shipPincode}</p>
      <table className="w-full text-sm mb-4"><thead><tr className="border-b text-left"><th>Item</th><th>Qty</th><th className="text-right">Price</th><th className="text-right">Amount</th></tr></thead>
        <tbody>{d.items.map((i) => <tr key={i.itemId} className="border-b"><td>{i.name} ({i.size})</td><td>{i.qty}</td><td className="text-right">Rs {i.unitPrice}</td><td className="text-right">Rs {i.unitPrice * i.qty}</td></tr>)}</tbody></table>
      <div className="text-sm text-right">
        <div>Items: Rs {o.subtotal}</div>{o.discount > 0 && <div>Discount: - Rs {o.discount}</div>}<div>Delivery: Rs {o.deliveryFee}</div>{o.codFee > 0 && <div>COD fee: Rs {o.codFee}</div>}
        <div className="font-bold text-base mt-1">Total: Rs {o.total}</div>
      </div>
      <p className="text-xs text-slate-500 mt-6">Prices include applicable taxes. This is a demo invoice from a practice project.</p>
    </main>
  );
}
