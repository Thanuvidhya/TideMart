import { useEffect, useState } from 'react';
import { getLeaderboard } from '../../api/resellerApi.js';
import ResellerLayout from '../../layouts/ResellerLayout.jsx';

const COLORS = { Bronze: 'bg-orange-200 text-orange-900', Silver: 'bg-slate-200 text-slate-800', Gold: 'bg-yellow-300 text-yellow-900' };

export default function ResellerLeaderboardPage() {
  const [d, setD] = useState(null);
  useEffect(() => { getLeaderboard().then(setD); }, []);
  if (!d) return <ResellerLayout title="Leaderboard" />;
  return (
    <ResellerLayout title="Level and leaderboard">
      <div className="bg-white border rounded-2xl p-4 mb-4">
        <span className={`px-3 py-1 rounded-full font-bold ${COLORS[d.level]}`}>{d.level}</span>
        <p className="text-sm mt-2">{d.deliveredOrders} delivered orders. {d.nextLevelAt ? `Reach ${d.nextLevelAt} delivered orders for the next level.` : 'You have the top level.'}</p>
      </div>
      <h2 className="font-semibold mb-2">Top resellers</h2>
      {d.top.map((t, i) => <div key={t.resellerId} className={`flex justify-between border rounded-xl p-3 mb-2 ${t.resellerId === d.myResellerId ? 'bg-orange-50 border-brand-accent' : 'bg-white'}`}><span>{i + 1}. {t.name}</span><span>Rs {t.earned} | {t.orders} orders</span></div>)}
    </ResellerLayout>
  );
}
