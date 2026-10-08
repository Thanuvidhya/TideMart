import { useState } from 'react';
import { Link } from 'react-router-dom';
import { resellerSignup } from '../../api/resellerApi.js';
import Navbar from '../../components/common/Navbar.jsx';

export default function ResellerSignupPage() {
  const [f, setF] = useState({ upiId: '', bankAccount: '', ifsc: '' });
  const [msg, setMsg] = useState('');
  const [done, setDone] = useState(false);
  const set = (k) => (e) => setF({ ...f, [k]: e.target.value });
  const go = () => resellerSignup(f).then(() => { setDone(true); setMsg('You are a reseller now. Log out and log in again to open the reseller panel.'); }).catch((e) => setMsg(e.response?.data?.message || 'Could not sign up'));
  const field = 'w-full border rounded-lg px-3 py-2 mb-2';
  return (
    <>
      <Navbar />
      <main className="max-w-md mx-auto p-4">
        <h1 className="text-2xl font-bold mb-1">Become a reseller</h1>
        <p className="text-sm text-slate-500 mb-3">Pick products, set your margin, share on WhatsApp and earn on every delivered order.</p>
        <input className={field} placeholder="UPI ID (for payouts)" value={f.upiId} onChange={set('upiId')} />
        <input className={field} placeholder="Bank account (optional)" value={f.bankAccount} onChange={set('bankAccount')} />
        <input className={field} placeholder="IFSC (optional)" value={f.ifsc} onChange={set('ifsc')} />
        {!done && <button className="w-full bg-brand-accent text-white py-2 rounded-lg font-semibold" onClick={go}>Start reselling</button>}
        {msg && <p className="mt-3 text-sm">{msg}</p>}
        {done && <Link className="text-brand text-sm" to="/login">Go to login</Link>}
      </main>
    </>
  );
}
