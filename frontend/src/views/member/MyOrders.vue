<template>
  <div>
    <h1 class="page-title">📋 我的訂單</h1>

    <div class="alert alert-error" v-if="errorMsg">{{ errorMsg }}</div>

    <div class="empty-hint" v-if="orders.length === 0 && !errorMsg">目前沒有任何訂單</div>

    <div class="order-card" v-for="order in orders" :key="order.orderId">
      <div class="order-header">
        <div class="order-meta">
          <span class="order-id">訂單編號：{{ order.orderId }}</span>
          <span class="order-date">{{ order.createdAt }}</span>
        </div>
        <div class="order-right">
          <span class="badge" :class="order.payStatus === 1 ? 'badge-paid' : 'badge-unpaid'">
            {{ order.payStatus === 1 ? '已付款' : '未付款' }}
          </span>
          <span class="order-total">NT$ {{ order.totalPrice?.toLocaleString() }}</span>
        </div>
      </div>

      <table class="items-table" v-if="order.items && order.items.length > 0">
        <thead>
          <tr>
            <th>商品編號</th><th>商品名稱</th><th>數量</th>
            <th>單價</th><th>小計</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in order.items" :key="item.orderItemSn">
            <td>{{ item.productId }}</td>
            <td>{{ item.productName }}</td>
            <td>{{ item.quantity }}</td>
            <td>NT$ {{ item.standPrice?.toLocaleString() }}</td>
            <td>NT$ {{ item.itemPrice?.toLocaleString() }}</td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getMyOrders } from '../../api/orderApi'

const orders   = ref([])
const errorMsg = ref('')

const loadOrders = async () => {
  try {
    const res = await getMyOrders()
    orders.value = res.data.data
  } catch (e) {
    errorMsg.value = '載入訂單失敗'
  }
}

onMounted(loadOrders)
</script>

<style scoped>
.order-card {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
  margin-bottom: 1.2rem;
  overflow: hidden;
}
.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem 1.5rem;
  background: #f8f9fa;
  border-bottom: 1px solid #eee;
  flex-wrap: wrap;
  gap: 0.5rem;
}
.order-meta { display: flex; flex-direction: column; gap: 0.2rem; }
.order-id   { font-weight: 600; color: #003087; font-size: 0.95rem; }
.order-date { font-size: 0.82rem; color: #999; }
.order-right { display: flex; align-items: center; gap: 1rem; }
.order-total { font-size: 1.1rem; font-weight: 700; color: #003087; }
.badge {
  padding: 0.25rem 0.7rem;
  border-radius: 20px;
  font-size: 0.82rem;
  font-weight: 600;
}
.badge-paid   { background: #d4edda; color: #155724; }
.badge-unpaid { background: #fff3cd; color: #856404; }
.items-table { width: 100%; border-collapse: collapse; }
.items-table th,
.items-table td { padding: 0.6rem 1.5rem; text-align: left; border-bottom: 1px solid #f0f0f0; font-size: 0.9rem; }
.items-table th { background: #fafafa; color: #666; font-weight: 600; }
.empty-hint { color: #999; text-align: center; padding: 3rem; }
</style>
