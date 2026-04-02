<template>
  <div>
    <nav class="navbar" v-if="auth.state.token">
      <div class="navbar-brand">🛒 玉山電商購物中心</div>

      <div class="navbar-links">
        <!-- Admin 選單 -->
        <template v-if="auth.state.role === 'ADMIN'">
          <RouterLink to="/admin/products">商品管理</RouterLink>
          <RouterLink to="/admin/orders">所有訂單</RouterLink>
        </template>
        <!-- Member 選單 -->
        <template v-else>
          <RouterLink to="/orders">我的訂單</RouterLink>
          <RouterLink to="/orders/create">建立訂單</RouterLink>
        </template>
      </div>

      <div class="navbar-user">
        <span class="user-info">
          {{ auth.state.memberName }}
          <span class="role-badge" :class="auth.state.role === 'ADMIN' ? 'role-admin' : 'role-member'">
            {{ auth.state.role === 'ADMIN' ? '管理員' : '會員' }}
          </span>
        </span>
        <button class="btn-logout" @click="handleLogout">登出</button>
      </div>
    </nav>

    <main :class="auth.state.token ? 'main-content' : ''">
      <RouterView />
    </main>
  </div>
</template>

<script setup>
import { RouterLink, RouterView, useRouter } from 'vue-router'
import { useAuth } from './store/auth'

const auth   = useAuth()
const router = useRouter()

const handleLogout = () => {
  auth.logout()
  router.push('/login')
}
</script>

<style>
* { margin: 0; padding: 0; box-sizing: border-box; }
body { font-family: Arial, sans-serif; background-color: #f5f5f5; }

.navbar {
  background-color: #003087;
  color: white;
  padding: 0.8rem 2rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 1rem;
}
.navbar-brand { font-size: 1.1rem; font-weight: bold; white-space: nowrap; }

.navbar-links { display: flex; gap: 1rem; }
.navbar-links a {
  color: white; text-decoration: none;
  padding: 0.4rem 0.8rem; border-radius: 4px; font-size: 0.9rem;
}
.navbar-links a:hover,
.navbar-links a.router-link-active { background-color: rgba(255,255,255,0.2); }

.navbar-user { display: flex; align-items: center; gap: 0.8rem; white-space: nowrap; }
.user-info { font-size: 0.9rem; display: flex; align-items: center; gap: 0.5rem; }
.role-badge {
  font-size: 0.75rem; padding: 0.15rem 0.5rem;
  border-radius: 10px; font-weight: 600;
}
.role-admin  { background: #ffc107; color: #333; }
.role-member { background: rgba(255,255,255,0.25); color: white; }

.btn-logout {
  background: rgba(255,255,255,0.15);
  border: 1px solid rgba(255,255,255,0.4);
  color: white; padding: 0.3rem 0.8rem;
  border-radius: 4px; cursor: pointer; font-size: 0.85rem;
}
.btn-logout:hover { background: rgba(255,255,255,0.25); }

.main-content { padding: 2rem; max-width: 1100px; margin: 0 auto; }

/* 共用元件 */
.card {
  background: white; border-radius: 8px;
  padding: 1.5rem; box-shadow: 0 2px 8px rgba(0,0,0,0.08);
  margin-bottom: 1.5rem;
}
.page-title { font-size: 1.4rem; font-weight: bold; color: #003087; margin-bottom: 1.5rem; }
.btn {
  padding: 0.5rem 1.2rem; border: none;
  border-radius: 4px; cursor: pointer; font-size: 0.9rem;
}
.btn-primary { background-color: #003087; color: white; }
.btn-primary:hover { background-color: #002070; }
.btn-success { background-color: #28a745; color: white; }
.btn-success:hover { background-color: #218838; }
.btn:disabled { background-color: #ccc; cursor: not-allowed; }

.alert { padding: 0.8rem 1rem; border-radius: 4px; margin-bottom: 1rem; }
.alert-success { background-color: #d4edda; color: #155724; }
.alert-error   { background-color: #f8d7da; color: #721c24; }

table { width: 100%; border-collapse: collapse; }
th, td { padding: 0.7rem 1rem; text-align: left; border-bottom: 1px solid #eee; }
th { background-color: #f8f9fa; font-weight: 600; color: #555; }

.form-group { margin-bottom: 1rem; }
.form-group label { display: block; margin-bottom: 0.4rem; font-weight: 500; font-size: 0.9rem; }
.form-group input {
  width: 100%; padding: 0.5rem 0.8rem;
  border: 1px solid #ddd; border-radius: 4px; font-size: 0.9rem;
}
.form-group input:focus { outline: none; border-color: #003087; }
</style>
