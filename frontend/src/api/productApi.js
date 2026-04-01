import axios from 'axios'

const api = axios.create({
  baseURL: 'http://localhost:8080/api/v1'
})

export const getProductsInStock = () => api.get('/products/in-stock')
export const addProduct = (product) => api.post('/products', product)