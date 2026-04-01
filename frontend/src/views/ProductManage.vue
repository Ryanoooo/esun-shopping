<template>
  <div>
    <h1 class="page-title">📦 商品管理</h1>

    <div class="card">
      <h2 style="font-size:1.1rem; margin-bottom:1rem">新增商品</h2>
      <div class="alert alert-success" v-if="successMsg">{{ successMsg }}</div>
      <div class="alert alert-error"   v-if="errorMsg">{{ errorMsg }}</div>

      <div style="display:grid; grid-template-columns:1fr 1fr; gap:1rem">
        <div class="form-group">
          <label>商品編號 *</label>
          <input v-model="form.productId" placeholder="例：P005" />
        </div>
        <div class="form-group">
          <label>商品名稱 *</label>
          <input v-model="form.productName" placeholder="例：藍牙耳機" />
        </div>
        <div class="form-group">
          <label>售價 *</label>
          <input v-model.number="form.price" type="number" placeholder="例：1500" />
        </div>
        <div class="form-group">
          <label>庫存數量 *</label>
          <input v-model.number="form.quantity" type="number" placeholder="例：30" />
        </div>
      </div>

      <button class="btn btn-primary" @click="handleAddProduct" :disabled="loading">
        {{ loading ? '新增中...' : '新增商品' }}
      </button>
    </div>

    <div class="card">
      <h2 style="font-size:1.1rem; margin-bottom:1rem">
        目前有庫存的商品
        <button class="btn btn-primary"
                style="margin-left:1rem; font-size:0.8rem"
                @click="loadProducts">重新整理</button>
      </h2>

      <div v-if="products.length === 0" style="color:#999; text-align:center; padding:2rem">
        目前沒有有庫存的商品
      </div>

      <table v-else>
        <thead>
          <tr>
            <th>商品編號</th><th>商品名稱</th><th>售價</th><th>庫存</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="p in products" :key="p.productId">
            <td>{{ p.productId }}</td>
            <td>{{ p.productName }}</td>
            <td>NT$ {{ p.price.toLocaleString() }}</td>
            <td>{{ p.quantity }}</td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getProductsInStock, addProduct } from '../api/productApi'

const products   = ref([])
const form       = ref({ productId: '', productName: '', price: null, quantity: null })
const loading    = ref(false)
const successMsg = ref('')
const errorMsg   = ref('')

const loadProducts = async () => {
  try {
    const res = await getProductsInStock()
    products.value = res.data.data
  } catch (e) {
    console.error('載入商品失敗', e)
  }
}

const handleAddProduct = async () => {
  if (!form.value.productId || !form.value.productName ||
      form.value.price === null || form.value.quantity === null) {
    errorMsg.value = '請填寫所有必填欄位'
    return
  }
  loading.value = true
  successMsg.value = ''
  errorMsg.value = ''
  try {
    await addProduct(form.value)
    successMsg.value = `商品「${form.value.productName}」新增成功！`
    form.value = { productId: '', productName: '', price: null, quantity: null }
    await loadProducts()
  } catch (e) {
    errorMsg.value = e.response?.data?.message || '新增失敗'
  } finally {
    loading.value = false
  }
}

onMounted(loadProducts)
</script>