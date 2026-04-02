import { createRouter, createWebHistory } from 'vue-router'
import Login          from '../views/Login.vue'
import ProductManage  from '../views/admin/ProductManage.vue'
import AdminOrders    from '../views/admin/AdminOrders.vue'
import MyOrders       from '../views/member/MyOrders.vue'
import CreateOrder    from '../views/member/CreateOrder.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/login', name: 'login', component: Login },
    {
      path: '/admin/products',
      name: 'adminProducts',
      component: ProductManage,
      meta: { requiresAuth: true, requiresAdmin: true }
    },
    {
      path: '/admin/orders',
      name: 'adminOrders',
      component: AdminOrders,
      meta: { requiresAuth: true, requiresAdmin: true }
    },
    {
      path: '/orders',
      name: 'myOrders',
      component: MyOrders,
      meta: { requiresAuth: true }
    },
    {
      path: '/orders/create',
      name: 'createOrder',
      component: CreateOrder,
      meta: { requiresAuth: true }
    },
    { path: '/', redirect: '/login' },
    { path: '/:pathMatch(.*)*', redirect: '/login' }
  ]
})

router.beforeEach((to, _from, next) => {
  const token = localStorage.getItem('token')
  const role  = localStorage.getItem('role')

  if (to.meta.requiresAuth && !token) {
    next('/login')
  } else if (to.meta.requiresAdmin && role !== 'ADMIN') {
    next('/orders')
  } else if (to.name === 'login' && token) {
    next(role === 'ADMIN' ? '/admin/orders' : '/orders')
  } else {
    next()
  }
})

export default router
