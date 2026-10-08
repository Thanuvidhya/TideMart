import { useEffect, useState } from 'react';
import { useSearchParams } from 'react-router-dom';
import { adminCreate, adminList, adminStats, adminUpdate, assignDelivery, deliverOrder, failDelivery, returnStatus, sellerDecision, adminFraud, ticketReply, ticketView } from '../../api/adminApi.js';
import Navbar from '../../components/common/Navbar.jsx';

const ORDER = ['PACKED', 'SHIPPED', 'OUT_FOR_DELIVERY', 'DELIVERED', 'CANCELLED'];
const TABS = [
  { k: 'users', label: 'Users', acts: [['Block', { status: 'BLOCKED' }], ['Unblock', { status: 'ACTIVE' }]] },
  { k: 'sellers', label: 'Sellers', seller: true },
  { k: 'products', label: 'Products', acts: [['Approve', { status: 'APPROVED' }], ['Reject', { status: 'REJECTED' }], ['Hide', { status: 'HIDDEN' }]] },
  { k: 'orders', label: 'Orders', acts: ORDER.map((s) => [s.replaceAll('_', ' '), { status: s }]) },
  { k: 'return_requests', label: 'Returns', ret: true },
  { k: 'categories', label: 'Categories', form: ['name', 'slug'], acts: [['Hide', { active: false }], ['Show', { active: true }]] },
  { k: 'coupons', label: 'Coupons', form: ['code', 'type', 'value', 'max_discount', 'min_order', 'valid_from', 'valid_till', 'usage_limit', 'active'], acts: [['Disable', { active: false }], ['Enable', { active: true }]] },
  { k: 'commission_rules', label: 'Commission', form: ['category_id', 'percent'] },
  { k: 'shipping_rules', label: 'Shipping', form: ['min_order', 'max_order', 'fee', 'active'] },
  { k: 'tax_settings', label: 'Tax', form: ['name', 'percent'] },
  { k: 'payouts', label: 'Payouts', form: ['party_type', 'party_id', 'amount', 'method', 'status'] },
  { k: 'banners', label: 'Banners', form: ['title', 'image_url', 'link', 'sort_order', 'active'] },
  { k: 'policy_pages', label: 'Policies', form: ['slug', 'title', 'content'] },
  { k: 'delivery_partners', label: 'Delivery partners', form: ['name', 'phone', 'status'] },
  { k: 'delivery', label: 'Delivery run', delivery: true },
  { k: 'cod_collections', label: 'COD cash', acts: [['Mark settled', { status: 'SETTLED' }]] },
  { k: 'return_pickups', label: 'Return pickups', form: ['return_id', 'partner_id', 'scheduled_on', 'status'] },
  { k: 'delivery_attempts', label: 'Delivery attempts' },
  { k: 'flash_sales', label: 'Flash sales', form: ['product_id', 'sale_price', 'starts_at', 'ends_at'] },
  { k: 'product_promotions', label: 'Promotions' },
  { k: 'fraud', label: 'Fraud flags' },
  { k: 'audit_log', label: 'Audit log' },
  { k: 'support_tickets', label: 'Tickets', ticket: true, acts: [['In progress', { status: 'IN_PROGRESS' }], ['Close', { status: 'CLOSED' }]] },
  { k: 'faqs', label: 'FAQ', form: ['question', 'answer', 'sort_order'] },
];

export default function AdminDashboardPage() {
  const [params] = useSearchParams();
  const [tab, setTab] = useState(params.get('tab') || 'dashboard');
  const [stats, setStats] = useState(null);
  const [rows, setRows] = useState([]);
  const [form, setForm] = useState({});
  const [msg, setMsg] = useState('');
  const t = TABS.find((x) => x.k === tab);
  const load = () => (tab === 'dashboard' ? adminStats().then(setStats) : (tab === 'fraud' ? adminFraud() : adminList(tab === 'delivery' ? 'orders' : tab)).then((r) => setRows(tab === 'delivery' ? r.filter((o) => ['SHIPPED', 'OUT_FOR_DELIVERY'].includes(o.status)) : r))).catch((e) => setMsg(e.response?.data?.message || 'Admin access needed'));
  useEffect(() => { setMsg(''); setForm({}); load(); }, [tab]);
  const run = (p) => p.then(() => { setMsg('Done'); load(); }).catch((e) => setMsg(e.response?.data?.message || 'Failed'));
  const cols = rows[0] ? Object.keys(rows[0]) : [];
  const Btn = ({ children, onClick }) => <button className="px-2 py-1 border rounded text-xs mr-1 mb-1" onClick={onClick}>{children}</button>;
  return (
    <>
      <Navbar />
      <main className="max-w-6xl mx-auto p-4">
        <h1 className="text-2xl font-bold mb-3">Admin panel</h1>
        <div className="flex gap-2 flex-wrap mb-4">
          {[{ k: 'dashboard', label: 'Dashboard' }, ...TABS].map((x) => <button key={x.k} onClick={() => setTab(x.k)} className={`px-3 py-1 rounded-full border text-sm ${tab === x.k ? 'bg-brand text-white' : 'bg-white'}`}>{x.label}</button>)}
        </div>
        {msg && <p className="text-sm mb-2">{msg}</p>}
        {tab === 'dashboard' && stats && (
          <>
            <div className="grid grid-cols-2 md:grid-cols-4 gap-3 mb-4">
              {[['Users', stats.users], ['Sellers', stats.sellers], ['Sellers to approve', stats.pendingSellers], ['Products to approve', stats.pendingProducts], ['Orders', stats.orders], ['Open returns', stats.openReturns], ['Sales', `Rs ${stats.sales}`]].map(([k, v]) => (
                <div key={k} className="bg-white border rounded-2xl p-4"><div className="text-sm text-slate-500">{k}</div><div className="text-2xl font-extrabold">{v}</div></div>
              ))}
            </div>
            <h2 className="font-semibold mb-2">Orders by status</h2>
            {stats.ordersByStatus.map((s) => <div key={s.status} className="text-sm">{s.status.replaceAll('_', ' ')}: <b>{s.count}</b></div>)}
            <h2 className="font-semibold mt-5 mb-2">Sales, last 7 days</h2>
            {stats.dailySales.map((d) => <div key={d.day} className="text-sm flex items-center gap-2"><span className="w-24">{String(d.day)}</span><div className="h-2 bg-brand rounded" style={{ width: `${Math.min(100, (d.sales / Math.max(...stats.dailySales.map((x) => x.sales))) * 100)}%` }} /><span>Rs {d.sales} ({d.orders} orders)</span></div>)}
            <h2 className="font-semibold mt-5 mb-2">Top products by units</h2>
            {stats.topProducts.map((p) => <div key={p.name} className="text-sm">{p.name}: <b>{p.units}</b></div>)}
          </>
        )}
        {t?.form && (
          <div className="flex gap-2 flex-wrap mb-4 bg-white border rounded-xl p-3">
            {t.form.map((k) => <input key={k} className="border rounded px-2 py-1 text-sm" placeholder={k} value={form[k] ?? ''} onChange={(e) => setForm({ ...form, [k]: e.target.value })} />)}
            <button className="px-3 py-1 bg-brand-accent text-white rounded text-sm" onClick={() => run(adminCreate(tab, form))}>Add</button>
          </div>
        )}
        {t && (
          <div className="overflow-x-auto bg-white border rounded-xl">
            <table className="w-full text-sm">
              <thead><tr>{cols.map((c) => <th key={c} className="text-left p-2 border-b">{c}</th>)}<th className="p-2 border-b">Actions</th></tr></thead>
              <tbody>
                {rows.map((r) => (
                  <tr key={r.id} className="border-b">
                    {cols.map((c) => <td key={c} className="p-2 max-w-xs truncate">{String(r[c] ?? '')}</td>)}
                    <td className="p-2">
                      {t.acts?.map(([label, body]) => <Btn key={label} onClick={() => run(adminUpdate(tab, r.id, body))}>{label}</Btn>)}
                      {t.seller && <><Btn onClick={() => run(sellerDecision(r.id, true))}>Approve</Btn><Btn onClick={() => run(sellerDecision(r.id, false))}>Reject</Btn></>}
                      {t.ret && ['APPROVED', 'PICKED_UP', 'REFUNDED', 'REJECTED'].map((s) => <Btn key={s} onClick={() => run(returnStatus(r.id, s))}>{s.replaceAll('_', ' ')}</Btn>)}
                      {t.ticket && (<>
                        <Btn onClick={() => ticketView(r.id).then((m) => window.alert(m.map((x) => x.message).join('\n') || 'No messages'))}>View</Btn>
                        <Btn onClick={() => { const m = window.prompt('Your reply'); if (m) run(ticketReply(r.id, m)); }}>Reply</Btn>
                      </>)}
                      {t.delivery && (<>
                        <Btn onClick={() => { const p = window.prompt('Delivery partner ID (add partners in the Delivery partners tab)'); if (p) assignDelivery(r.id, p).then((d) => { setMsg(`Assigned. Demo OTP: ${d.demoOtp}`); load(); }).catch((e) => setMsg(e.response?.data?.message || 'Failed')); }}>Assign partner</Btn>
                        <Btn onClick={() => { const o = window.prompt('Customer OTP'); if (o) run(deliverOrder(r.id, o)); }}>Deliver with OTP</Btn>
                        <Btn onClick={() => { const n = window.prompt('Why did it fail?', 'Customer not available'); failDelivery(r.id, n).then((d) => { setMsg(d.result); load(); }); }}>Failed attempt</Btn>
                      </>)}
                      {t.form && !t.acts && <Btn onClick={() => { const v = window.prompt('Edit as JSON, e.g. {"percent": 12}'); if (v) run(adminUpdate(tab, r.id, JSON.parse(v))); }}>Edit</Btn>}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </main>
    </>
  );
}
