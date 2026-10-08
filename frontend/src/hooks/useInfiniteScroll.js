import { useEffect } from 'react';
export default function useInfiniteScroll(ref, onLoad, enabled=true){ useEffect(()=>{const el=ref?.current;if(!el||!enabled)return;const io=new IntersectionObserver(es=>es.forEach(e=>e.isIntersecting&&onLoad()),{rootMargin:'300px'});io.observe(el);return()=>io.disconnect()},[ref,onLoad,enabled]); }
