import { createContext,useContext,useEffect,useState } from 'react';
export const LocationCtx=createContext(null);
export function LocationProvider({children}){
 const [value,setValue]=useState(()=>({pincode:localStorage.getItem('tidemart_pin')||'',addressId:localStorage.getItem('tidemart_addr')||null}));
 const setLocation=(v)=>{const x={...value,...v}; if(x.pincode) localStorage.setItem('tidemart_pin',x.pincode); else localStorage.removeItem('tidemart_pin'); if(x.addressId) localStorage.setItem('tidemart_addr',String(x.addressId)); else localStorage.removeItem('tidemart_addr'); setValue(x); window.dispatchEvent(new CustomEvent('tidemart-location',{detail:x}));};
 useEffect(()=>{const h=e=>setValue(e.detail||{}); window.addEventListener('tidemart-location',h); return()=>window.removeEventListener('tidemart-location',h)},[]);
 return <LocationCtx.Provider value={{...value,setLocation}}>{children}</LocationCtx.Provider>
}
export default LocationProvider; export const useLocationContext=()=>useContext(LocationCtx);
