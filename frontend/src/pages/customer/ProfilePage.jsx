import { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { deleteMe, getMe, updateMe } from '../../api/userApi.js';
import useAuth from '../../hooks/useAuth.js';

export default function ProfilePage() {
  const nav = useNavigate();
  const { signOut } = useAuth();
  const [u, setU] = useState({ name: '', email: '', language: 'en' });
  const [msg, setMsg] = useState('');
  useEffect(() => { getMe().then((d) => setU({ ...d, name: d.name || '', email: d.email || '' })); }, []);
  const set = (k) => (e) => setU({ ...u, [k]: e.target.value });
  const save = async () => {
    try { await updateMe({ name: u.name, email: u.email, language: u.language }); setMsg('Saved'); }
    catch (e) { setMsg(e.response?.data?.message || 'Could not save'); }
  };
  const remove = async () => {
    if (window.confirm('Delete your account? This cannot be undone.')) { await deleteMe(); signOut(); nav('/login'); }
  };
  const field = 'w-full border rounded-lg px-3 py-2 mb-3';
  return (
    <div className="max-w-md mx-auto p-6">
      <Link to="/" className="text-brand text-sm">Back</Link>
      <h1 className="text-2xl font-bold my-3">My profile</h1>
      <p className="text-sm text-slate-500 mb-3">Phone: {u.phone || 'not added'} | Referral code: {u.referralCode}</p>
      <input className={field} placeholder="Name" value={u.name} onChange={set('name')} />
      <input className={field} placeholder="Email" value={u.email} onChange={set('email')} />
      <select className={field} value={u.language} onChange={set('language')}>
        <option value="en">English</option><option value="ta">Tamil</option><option value="hi">Hindi</option>
      </select>
      <button className="w-full bg-brand text-white py-2 rounded-lg" onClick={save}>Save</button>
      {msg && <p className="mt-2 text-sm">{msg}</p>}
      <button className="w-full mt-8 text-red-600 text-sm" onClick={remove}>Delete my account</button>
    </div>
  );
}
