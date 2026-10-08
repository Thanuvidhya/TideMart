import { useState } from 'react';
import { getSizeChart } from '../../api/productApi.js';

export default function SizeChartModal({ productId }) {
  const [rows, setRows] = useState(null);
  const open = () => getSizeChart(productId).then((d) => setRows(d.chart ? d.chart.split(';').map((r) => r.split(',')) : []));
  return (
    <>
      <button className="text-sm text-brand underline" onClick={open}>Size chart</button>
      {rows && (
        <div className="fixed inset-0 z-20 bg-black/50 grid place-items-center p-4" onClick={() => setRows(null)}>
          <div className="bg-white rounded-2xl p-4 max-w-md w-full" onClick={(e) => e.stopPropagation()}>
            <h3 className="font-bold mb-2">Size chart</h3>
            {rows.length ? (
              <table className="w-full text-sm"><tbody>{rows.map((r, i) => <tr key={i} className={i === 0 ? 'font-bold border-b' : 'border-b'}>{r.map((c, k) => <td key={k} className="py-1 pr-3">{c}</td>)}</tr>)}</tbody></table>
            ) : <p className="text-sm text-slate-500">The seller has not added a size chart yet.</p>}
            <button className="mt-3 text-sm text-brand" onClick={() => setRows(null)}>Close</button>
          </div>
        </div>
      )}
    </>
  );
}
