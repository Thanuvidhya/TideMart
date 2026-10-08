import { useEffect, useState } from 'react';
import { getWallet } from '../../api/walletApi.js';
import Navbar from '../../components/common/Navbar.jsx';

export default function WalletPage() {
  const [w, setW] = useState({ balance: 0, transactions: [] });
  useEffect(() => { getWallet().then(setW); }, []);
  return (
    <>
      <Navbar />
      <main className="max-w-xl mx-auto p-4">
        <h1 className="text-2xl font-bold mb-3">My wallet</h1>
        <div className="rounded-2xl bg-gradient-to-r from-brand to-brand-dark text-white p-6 mb-4"><div className="text-sm opacity-80">Balance</div><div className="text-3xl font-extrabold">Rs {w.balance}</div></div>
        {!w.transactions.length && <p className="text-slate-500">No transactions yet. Refunds are added here.</p>}
        {w.transactions.map((t) => (
          <div key={t.id} className="bg-white border rounded-xl p-3 mb-2 flex justify-between"><span>{t.reason}<br /><small className="text-slate-500">{t.createdAt.slice(0, 10)}</small></span><b className={t.type === 'DEBIT' ? 'text-red-600' : 'text-green-700'}>{t.type === 'DEBIT' ? '-' : '+'} Rs {t.amount}</b></div>
        ))}
      </main>
    </>
  );
}
