import api from './index'

export const getMyOrders      = ()                      => api.get('/orders')
export const createOrder      = (orderData)             => api.post('/orders', orderData)
export const getAllOrders      = ()                      => api.get('/admin/orders')
export const updatePayStatus  = (orderId, payStatus)    => api.patch(`/admin/orders/${orderId}/pay-status`, null, { params: { payStatus } })
