import api from './axiosClient';

const unwrap = (p) => p.then((r) => r.data.data);

export const getWishlist = () => unwrap(api.get('/wishlist'));
export const toggleWishlist = (productId) => unwrap(api.post(`/wishlist/${productId}`));
