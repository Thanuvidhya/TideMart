import api from './axiosClient';export const applyCoupon=(code)=>api.get('/cart',{params:{coupon:code}}).then(r=>r.data.data);
