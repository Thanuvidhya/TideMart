import { useEffect, useState } from 'react';
import { advanceReturn, listReturns } from '../../api/orderApi.js';
import Navbar from '../../components/common/Navbar.jsx';

export default function ReturnsPage() {
  const [list, setList] = useState([]);
  const load = () => listReturns().then(setList);
  useEffect(() => { load(); }, []);
  return (
    <>
      <Navbar />
      <main className="max-w-2xl mx-auto p-4">
        <h1 className="text-2xl font-bold mb-3">My returns</h1>
        {!list.length && <p className="text-slate-500">No returns yet. Open a delivered order to request one.</p>}
        {list.map((r) => (
          <div key={r.id} className="bg-white border rounded-xl p-3 mb-2">
            <div className="flex justify-between"><b>{r.type} #{r.id}</b><span className="text-sm">{r.status.replaceAll('_', ' ')}</span></div>
            <div className="text-sm text-slate-500">Reason: {r.reason}</div>
            {r.status === 'REFUNDED' ? <div className="text-sm text-green-700">Refund added to your wallet.</div> : <button className="mt-2 px-3 py-1 border rounded-lg text-sm" onClick={() => advanceReturn(r.id).then(load)}>Next status (demo)</button>}
          </div>
        ))}
      </main>
    </>
  );
}
