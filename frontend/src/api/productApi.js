import api from './axiosClient';

const unwrap = (p) => p.then((r) => r.data.data);

export const getHome = () => unwrap(api.get('/home'));
export const getCategories = () => unwrap(api.get('/categories'));
export const listProducts = (params) => unwrap(api.get('/products', { params }));
export const getProduct = (id) => unwrap(api.get(`/products/${id}`));
export const suggest = (q) => unwrap(api.get('/products/suggest', { params: { q } }));
export const saveSearch = (term) => unwrap(api.post('/search/history', null, { params: { term } }));
export const getTrendingSearches = () => unwrap(api.get('/search/trending'));
export const markViewed = (id) => unwrap(api.post(`/recently-viewed/${id}`));
export const getRecentlyViewed = () => unwrap(api.get('/recently-viewed'));
export const getQuestions = (id) => unwrap(api.get(`/products/${id}/questions`));
export const askQuestion = (id, question) => unwrap(api.post(`/products/${id}/questions`, { question }));
export const answerQuestion = (id, answer) => unwrap(api.post(`/questions/${id}/answers`, { answer }));
export const notifyMe = (id) => unwrap(api.post(`/products/${id}/alert`));
export const getPolicy = (slug) => unwrap(api.get(`/policies/${slug}`));
export const getSizeChart = (id) => unwrap(api.get(`/products/${id}/size-chart`));
