<template>
  <div>
    <nav class="navbar">
      <div class="navbar-brand">🛒 玉山電商購物中心</div>
      <div class="navbar-links">
        <RouterLink to="/products">商品管理</RouterLink>
        <RouterLink to="/orders/create">建立訂單</RouterLink>
      </div>
    </nav>
    <main class="main-content">
      <RouterView />
    </main>
  </div>
</template>

<script setup>
import { RouterLink, RouterView } from 'vue-router'
</script>

<style>
* { margin: 0; padding: 0; box-sizing: border-box; }
body { font-family: Arial, sans-serif; background-color: #f5f5f5; }

.navbar {
  background-color: #003087;
  color: white;
  padding: 1rem 2rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.navbar-brand { font-size: 1.2rem; font-weight: bold; }
.navbar-links { display: flex; gap: 1.5rem; }
.navbar-links a {
  color: white; text-decoration: none;
  padding: 0.4rem 0.8rem; border-radius: 4px;
}
.navbar-links a:hover,
.navbar-links a.router-link-active { background-color: rgba(255,255,255,0.2); }

.main-content { padding: 2rem; max-width: 1000px; margin: 0 auto; }

.card {
  background: white; border-radius: 8px;
  padding: 1.5rem; box-shadow: 0 2px 8px rgba(0,0,0,0.1);
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
.form-group label { display: block; margin-bottom: 0.4rem; font-weight: 500; }
.form-group input {
  width: 100%; padding: 0.5rem 0.8rem;
  border: 1px solid #ddd; border-radius: 4px; font-size: 0.9rem;
}
.form-group input:focus { outline: none; border-color: #003087; }
</style>