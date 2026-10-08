import { useEffect, useRef } from 'react';

const STORES=[
 ['Urban Loom','Women','https://images.unsplash.com/photo-1490481651871-ab68de25d43d?auto=format&fit=crop&w=500&q=82'],
 ['StyleNest','Fashion','https://images.unsplash.com/photo-1483985988355-763728e1935b?auto=format&fit=crop&w=500&q=82'],
 ['FootStreet','Footwear','https://images.unsplash.com/photo-1542291026-7eec264c27ff?auto=format&fit=crop&w=500&q=82'],
 ['GlowHub','Beauty','https://images.unsplash.com/photo-1596462502278-27bfdc403348?auto=format&fit=crop&w=500&q=82'],
 ['HomeAura','Home','https://images.unsplash.com/photo-1555041469-a586c61ea9bc?auto=format&fit=crop&w=500&q=82'],
 ['KidsJoy','Kids','https://images.unsplash.com/photo-1519457431-44ccd64a579b?auto=format&fit=crop&w=500&q=82'],
 ['JewelBox','Jewellery','https://images.unsplash.com/photo-1515562141207-7a88fb7ce338?auto=format&fit=crop&w=500&q=82'],
 ['TrendKart','Everyday','https://images.unsplash.com/photo-1529139574466-a303027c1d8b?auto=format&fit=crop&w=500&q=82'],
 ['Urban Loom','Women','https://images.unsplash.com/photo-1490481651871-ab68de25d43d?auto=format&fit=crop&w=500&q=82'],
 ['StyleNest','Fashion','https://images.unsplash.com/photo-1483985988355-763728e1935b?auto=format&fit=crop&w=500&q=82'],
];
export default function PopularStores(){const ref=useRef(null);useEffect(()=>{const el=ref.current;if(!el)return;let x=0;const id=setInterval(()=>{x+=1;if(x>=el.scrollWidth/2)x=0;el.scrollLeft=x},35);return()=>clearInterval(id)},[]);return <section className="max-w-7xl mx-auto px-4 lg:px-6 py-6"><div className="flex items-end justify-between mb-4"><div><p className="text-xs uppercase tracking-[.18em] text-brand font-bold">Shop by store</p><h2 className="text-2xl font-extrabold text-slate-900 mt-1">Popular stores</h2></div><span className="text-xs text-slate-400">Swipe to explore</span></div><div ref={ref} className="flex gap-4 overflow-hidden scroll-smooth pb-2" style={{scrollbarWidth:'none'}}>{STORES.map(([name,cat,img],i)=><a href={`/search?q=${encodeURIComponent(name)}`} key={`${name}-${i}`} className="shrink-0 w-36 sm:w-44 bg-white border border-slate-100 rounded-2xl p-3 hover:shadow-md transition"><div className="w-16 h-16 mx-auto rounded-full overflow-hidden ring-4 ring-slate-50"><img src={img} alt={name} className="w-full h-full object-cover"/></div><p className="font-bold text-center text-sm mt-3 truncate">{name}</p><p className="text-xs text-slate-400 text-center">{cat}</p></a>)}</div></section>}
