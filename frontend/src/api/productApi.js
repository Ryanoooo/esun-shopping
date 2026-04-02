import api from './index'

export const getProductsInStock = ()        => api.get('/products/in-stock')
export const addProduct         = (product) => api.post('/products', product)
