import { useCallback, useEffect, useState } from 'react';
import { getCart, addToCart, setQty, setSaved } from '../api/cartApi.js';
export default function useCart(){ const [cart,setCart]=useState(null); const refresh=useCallback(()=>getCart().then(setCart),[]); useEffect(()=>{refresh().catch(()=>{})},[refresh]); return {cart,refresh,add:(productId,variantId,qty=1)=>addToCart(productId,variantId,qty).then(refresh),setQty:(id,qty)=>setQty(id,qty).then(setCart),setSaved:(id,saved)=>setSaved(id,saved).then(setCart)}; }
