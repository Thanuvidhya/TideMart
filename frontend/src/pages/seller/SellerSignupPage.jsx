import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { sellerSignup } from '../../api/sellerApi.js';
import Navbar from '../../components/common/Navbar.jsx';

export default function SellerSignupPage() {
  const nav = useNavigate();
  const [f, setF] = useState({ businessName: '', gstNo: '', panNo: '', bankAccount: '', ifsc: '' });
  const [msg, setMsg] = useState('');
  const [done, setDone] = useState(false);
  const [busy, setBusy] = useState(false);
  const set = (k) => (e) => setF({ ...f, [k]: e.target.value });
  const go = async () => {
    setMsg(''); setBusy(true);
    try {
      const s = await sellerSignup(f);
      setDone(true);
      setMsg(s.status === 'APPROVED' ? 'Seller account created successfully. You can now sell products on TideMart.' : 'Seller account submitted for approval.');
    } catch (e) { setMsg(e.response?.data?.message || 'Could not create seller account'); }
    finally { setBusy(false); }
  };
  const field = 'w-full border border-slate-200 rounded-xl px-3 py-3 mb-3 outline-none focus:ring-2 focus:ring-brand/20 focus:border-brand';
  return <>
    <Navbar />
    <main className="min-h-screen bg-slate-50 py-8 px-4">
      <div className="max-w-4xl mx-auto grid md:grid-cols-[.85fr_1.15fr] gap-6">
        <section className="rounded-3xl bg-gradient-to-br from-brand-dark to-brand text-white p-7 md:p-9">
          <p className="text-sm font-bold opacity-80">SELL ON TIDEMART</p>
          <h1 className="text-3xl font-extrabold mt-2">Turn your products into a business.</h1>
          <p className="mt-3 text-sm leading-6 opacity-90">Create a seller account, add your catalogue, manage stock and receive orders — in the same simple flow inspired by modern marketplace seller apps.</p>
          <div className="mt-7 space-y-3 text-sm"><div>✓ Your own shop name</div><div>✓ Add products, prices and stock</div><div>✓ Seller dashboard and order management</div><div>✓ You can also become a reseller with the same login</div></div>
          <div className="mt-8 pt-5 border-t border-white/20 text-sm">Already a seller? <Link className="font-bold underline" to="/seller">Open Seller Panel</Link></div>
        </section>
        <section className="bg-white rounded-3xl border border-slate-100 shadow-sm p-6 md:p-8">
          <div className="mb-5"><h2 className="text-2xl font-extrabold text-slate-900">Create seller account</h2><p className="text-sm text-slate-500 mt-1">Use your shop and payout details to start selling.</p></div>
          {!done ? <>
            <label className="text-xs font-bold text-slate-600">SHOP / BUSINESS NAME *</label>
            <input className={field} placeholder="Example: Urban Loom" value={f.businessName} onChange={set('businessName')} />
            <label className="text-xs font-bold text-slate-600">GST NUMBER <span className="font-normal">(optional)</span></label>
            <input className={field} placeholder="GSTIN if you have one" value={f.gstNo} onChange={set('gstNo')} />
            <label className="text-xs font-bold text-slate-600">PAN NUMBER *</label>
            <input className={field} placeholder="ABCDE1234F" value={f.panNo} onChange={set('panNo')} />
            <div className="grid sm:grid-cols-2 gap-3"><div><label className="text-xs font-bold text-slate-600">BANK ACCOUNT *</label><input className={field} placeholder="Account number" value={f.bankAccount} onChange={set('bankAccount')} /></div><div><label className="text-xs font-bold text-slate-600">IFSC *</label><input className={field} placeholder="SBIN0000001" value={f.ifsc} onChange={set('ifsc')} /></div></div>
            <button disabled={busy} className="w-full bg-brand-accent text-white py-3 rounded-xl font-bold disabled:opacity-60" onClick={go}>{busy ? 'Creating seller account…' : 'Create Seller Account'}</button>
            {msg && <p className="mt-3 text-sm text-red-600">{msg}</p>}
          </> : <div className="bg-green-50 border border-green-100 rounded-2xl p-5"><p className="text-green-700 font-bold">✓ {msg}</p><p className="text-sm text-slate-600 mt-2">Your account can have both Seller and Reseller roles. If the seller option does not appear immediately, log out and log in once to refresh your roles.</p><div className="flex gap-3 mt-4"><button onClick={() => nav('/seller')} className="bg-brand text-white px-4 py-2 rounded-xl font-bold">Open Seller Panel</button><Link to="/reseller/signup" className="border px-4 py-2 rounded-xl font-bold">Become Reseller</Link></div></div>}
        </section>
      </div>
    </main>
  </>;
}
