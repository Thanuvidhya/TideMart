import { useEffect, useState } from 'react';
import { applyReferral, getReferrals } from '../../api/referralApi.js';
import Navbar from '../../components/common/Navbar.jsx';

export default function ReferralPage() {
  const [d, setD] = useState(null);
  const [code, setCode] = useState('');
  const [msg, setMsg] = useState('');
  useEffect(() => { getReferrals().then(setD); }, []);
  const apply = () => applyReferral(code).then((v) => { setD(v); setMsg('Code applied. Rs 50 added to your wallet.'); }).catch((e) => setMsg(e.response?.data?.message || 'Failed'));
  return (
    <>
      <Navbar />
      <main className="max-w-md mx-auto p-4">
        <h1 className="text-2xl font-bold mb-3">Refer a friend</h1>
        {d && (
          <div className="rounded-2xl bg-gradient-to-r from-brand to-brand-dark text-white p-5 mb-4">
            <div className="text-sm opacity-80">Your code</div><div className="text-3xl font-extrabold tracking-widest">{d.code}</div>
            <div className="text-sm mt-2">You and your friend each get Rs 50. Earned so far: Rs {d.earned} from {d.referrals.length} friends.</div>
          </div>
        )}
        <h2 className="font-semibold mb-2">Have a friend's code?</h2>
        <div className="flex gap-2"><input className="flex-1 border rounded-lg px-3 py-2" value={code} onChange={(e) => setCode(e.target.value)} placeholder="Enter code" /><button className="px-4 bg-brand text-white rounded-lg" onClick={apply}>Apply</button></div>
        {msg && <p className="mt-2 text-sm">{msg}</p>}
      </main>
    </>
  );
}
