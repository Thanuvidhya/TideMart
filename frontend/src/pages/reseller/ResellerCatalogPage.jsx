import { useEffect, useState } from 'react';
import { getCatalog, getShare, saveMargin } from '../../api/resellerApi.js';
import ResellerLayout from '../../layouts/ResellerLayout.jsx';

export default function ResellerCatalogPage() {
  const [q, setQ] = useState('');
  const [rows, setRows] = useState([]);
  const [sel, setSel] = useState([]);
  const [msg, setMsg] = useState('');
  const load = () => getCatalog({ q }).then((p) => setRows(p.content));
  useEffect(() => { load(); }, []);
  const toggle = (id) => setSel(sel.includes(id) ? sel.filter((x) => x !== id) : [...sel, id]);
  const margin = (id, v) => setRows(rows.map((r) => (r.card.id === id ? { ...r, margin: +v, customerPrice: r.card.price + +v } : r)));
  const save = (r) => saveMargin(r.card.id, r.margin).then(() => setMsg(`Margin saved for ${r.card.name}`)).catch((e) => setMsg(e.response?.data?.message || 'Failed'));
  const share = () => getShare(sel).then((d) => window.open(d.whatsappUrl, '_blank'));
  const poster = (r) => {
    const c = document.createElement('canvas'); c.width = 800; c.height = 800;
    const x = c.getContext('2d'); const g = x.createLinearGradient(0, 0, 800, 800);
    g.addColorStop(0, '#0E7C95'); g.addColorStop(1, '#164E63'); x.fillStyle = g; x.fillRect(0, 0, 800, 800);
    x.fillStyle = '#fff'; x.font = 'bold 54px sans-serif'; x.fillText(r.card.name.slice(0, 22), 50, 300);
    x.fillStyle = '#FDBA74'; x.font = 'bold 110px sans-serif'; x.fillText(`Rs ${r.customerPrice}`, 50, 450);
    x.fillStyle = '#fff'; x.font = '36px sans-serif'; x.fillText('Order on Tidemart today', 50, 540); x.fillText('Free delivery above Rs 499', 50, 590);
    const a = document.createElement('a'); a.href = c.toDataURL('image/png'); a.download = `poster-${r.card.id}.png`; a.click();
  };
  return (
    <ResellerLayout title="Catalogue">
      <div className="flex gap-2 mb-3">
        <input className="flex-1 border rounded-lg px-3 py-2" placeholder="Search products" value={q} onChange={(e) => setQ(e.target.value)} onKeyDown={(e) => e.key === 'Enter' && load()} />
        <button className="px-4 border rounded-lg" onClick={load}>Search</button>
        <button disabled={!sel.length} className="px-4 bg-green-600 text-white rounded-lg disabled:opacity-40" onClick={share}>Share {sel.length || ''} on WhatsApp</button>
      </div>
      {msg && <p className="text-sm text-green-700 mb-2">{msg}</p>}
      {rows.map((r) => (
        <div key={r.card.id} className="bg-white border rounded-xl p-3 mb-2 flex items-center gap-3">
          <input type="checkbox" checked={sel.includes(r.card.id)} onChange={() => toggle(r.card.id)} />
          <div className="flex-1"><b>{r.card.name}</b><div className="text-sm text-slate-500">Supplier price Rs {r.card.price}</div></div>
          <label className="text-sm">Margin Rs <input type="number" className="w-20 border rounded px-2 py-1" value={r.margin} onChange={(e) => margin(r.card.id, e.target.value)} onBlur={() => save(r)} /></label>
          <div className="text-sm text-right">Customer pays<br /><b>Rs {r.customerPrice}</b></div>
          <button className="px-2 py-1 border rounded text-xs" onClick={() => poster(r)}>Poster</button>
        </div>
      ))}
    </ResellerLayout>
  );
}
