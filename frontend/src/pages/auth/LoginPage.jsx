import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { sendOtp, customerLogin, register } from '../../api/authApi.js';
import useAuth from '../../hooks/useAuth.js';

const field = 'w-full border rounded-lg px-3 py-2 mb-3';
const primary = 'w-full bg-brand-accent text-white py-2 rounded-lg font-semibold';

export default function LoginPage() {
  const nav = useNavigate();
  const { signIn } = useAuth();
  const [tab, setTab] = useState('otp');
  const [isNew, setIsNew] = useState(false);
  const [f, setF] = useState({ phone: '', name: '', email: '', password: '' });
  const [err, setErr] = useState('');
  const set = (k) => (e) => setF({ ...f, [k]: e.target.value });
  const run = async (fn) => {
    setErr('');
    try { await fn(); } catch (e) { setErr(e.response?.data?.message || 'Something went wrong'); }
  };
  const byOtp = () => run(async () => {
    const d = await sendOtp(f.phone);
    nav('/verify', { state: { phone: f.phone, demoOtp: d.demoOtp } });
  });
  const byEmail = () => run(async () => {
    signIn(isNew ? await register(f) : await customerLogin(f));
    nav('/');
  });
  return (
    <div className="min-h-screen flex items-center justify-center bg-slate-50 p-4">
      <div className="bg-white rounded-2xl shadow p-6 w-full max-w-sm">
        <img src="/tidemart-logo.svg" alt="Tidemart" className="w-40 mx-auto mb-4" />
        <div className="flex gap-2 mb-4">
          {['otp', 'email'].map((t) => (
            <button key={t} onClick={() => setTab(t)} className={`flex-1 py-2 rounded-lg border ${tab === t ? 'bg-brand text-white' : ''}`}>
              {t === 'otp' ? 'Phone OTP' : 'Email'}
            </button>
          ))}
        </div>
        {tab === 'otp' ? (
          <>
            <input className={field} placeholder="10-digit phone number" value={f.phone} onChange={set('phone')} />
            <button className={primary} onClick={byOtp}>Send OTP</button>
          </>
        ) : (
          <>
            {isNew && <input className={field} placeholder="Name" value={f.name} onChange={set('name')} />}
            <input className={field} placeholder="Email" value={f.email} onChange={set('email')} />
            <input className={field} type="password" placeholder="Password (min 6)" value={f.password} onChange={set('password')} />
            <button className={primary} onClick={byEmail}>{isNew ? 'Create account' : 'Log in'}</button>
            <button className="w-full mt-2 text-sm text-brand" onClick={() => setIsNew(!isNew)}>
              {isNew ? 'I already have an account' : 'Create an account'}
            </button>
          </>
        )}
        {err && <p className="text-red-600 mt-3 text-sm">{err}</p>}
      </div>
    </div>
  );
}
