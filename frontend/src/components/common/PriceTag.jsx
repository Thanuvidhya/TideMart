export default function PriceTag({price,mrp}){return <div className="flex items-baseline gap-2"><b className="text-xl">₹{price}</b>{mrp>price&&<s className="text-xs text-slate-400">₹{mrp}</s>}</div>}
