import { createRouter, createWebHistory } from 'vue-router'
import ProductManage from '../views/ProductManage.vue'
import CreateOrder from '../views/CreateOrder.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', redirect: '/products' },
    { path: '/products', name: 'products', component: ProductManage },
    { path: '/orders/create', name: 'createOrder', component: CreateOrder }
  ]
})

export default router