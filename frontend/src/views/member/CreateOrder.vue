<template>
  <div>
    <h1 class="page-title">🛍️ 建立訂單</h1>
    <div class="alert alert-success" v-if="successMsg">{{ successMsg }}</div>
    <div class="alert alert-error"   v-if="errorMsg">{{ errorMsg }}</div>

    <div class="card">
      <h2 class="section-title">選擇商品</h2>
      <div class="empty-hint" v-if="products.length === 0">目前沒有有庫存的商品</div>
      <table v-else>
        <thead>
          <tr>
            <th style="width:50px">選擇</th>
            <th>商品編號</th><th>商品名稱</th>
            <th>單價</th><th>庫存</th>
            <th style="width:120px">購買數量</th>
            <th>小計</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="p in products" :key="p.productId">
            <td><input type="checkbox" :value="p.productId" v-model="selectedIds" /></td>
            <td>{{ p.productId }}</td>
            <td>{{ p.productName }}</td>
            <td>NT$ {{ p.price.toLocaleString() }}</td>
            <td>{{ p.quantity }}</td>
            <td>
              <input type="number" min="1" :max="p.quantity"
                     v-model.number="quantities[p.productId]"
                     :disabled="!selectedIds.includes(p.productId)"
                     class="qty-input" />
            </td>
            <td>
              <span v-if="selectedIds.includes(p.productId)">
                NT$ {{ (p.price * (quantities[p.productId] || 1)).toLocaleString() }}
              </span>
              <span v-else style="color:#ccc">-</span>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <div class="card summary-bar">
      <div class="order-total">
        訂單總金額：<strong>NT$ {{ totalAmount.toLocaleString() }}</strong>
      </div>
      <button class="btn btn-success" @click="handleCreateOrder"
              :disabled="loading || selectedIds.length === 0">
        {{ loading ? '建立中...' : '✅ 建立訂單' }}
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getProductsInStock } from '../../api/productApi'
import { createOrder } from '../../api/orderApi'

const products    = ref([])
const selectedIds = ref([])
const quantities  = ref({})
const loading     = ref(false)
const successMsg  = ref('')
const errorMsg    = ref('')

const loadProducts = async () => {
  const res = await getProductsInStock()
  products.value = res.data.data
  products.value.forEach(p => { quantities.value[p.productId] = 1 })
}

const totalAmount = computed(() =>
  products.value
    .filter(p => selectedIds.value.includes(p.productId))
    .reduce((sum, p) => sum + p.price * (quantities.value[p.productId] || 1), 0)
)

const handleCreateOrder = async () => {
  loading.value  = true
  successMsg.value = ''
  errorMsg.value   = ''
  try {
    const items = selectedIds.value.map(id => ({
      productId: id,
      quantity: quantities.value[id] || 1
    }))
    const res = await createOrder({ items })
    successMsg.value = `訂單建立成功！訂單編號：${res.data.data.orderId}`
    selectedIds.value = []
    await loadProducts()
  } catch (e) {
    errorMsg.value = e.response?.data?.message || '建立訂單失敗'
  } finally {
    loading.value = false
  }
}

onMounted(loadProducts)
</script>

<style scoped>
.section-title { font-size: 1.1rem; margin-bottom: 1rem; }
.qty-input { width: 70px; padding: 0.3rem; border: 1px solid #ddd; border-radius: 4px; }
.summary-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.order-total { font-size: 1.1rem; color: #003087; }
.empty-hint { color: #999; text-align: center; padding: 2rem; }
</style>
