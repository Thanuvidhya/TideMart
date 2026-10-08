import api from './axiosClient';

const unwrap = (p) => p.then((r) => r.data.data);

export const getNotifications = () => unwrap(api.get('/notifications'));
export const readAllNotifications = () => unwrap(api.post('/notifications/read-all'));
export const getFaqs = () => unwrap(api.get('/faqs'));
export const createTicket = (body) => unwrap(api.post('/support/tickets', body));
export const listTickets = () => unwrap(api.get('/support/tickets'));
export const getTicket = (id) => unwrap(api.get(`/support/tickets/${id}`));
export const replyTicket = (id, message) => unwrap(api.post(`/support/tickets/${id}/messages`, { message }));
export const getChat = () => unwrap(api.get('/support/chat'));
export const sendChat = (message) => unwrap(api.post('/support/chat', { message }));
export const getReviews = (productId) => unwrap(api.get(`/products/${productId}/reviews`));
export const addReview = (body) => unwrap(api.post('/reviews', body));
