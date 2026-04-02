<template>
  <div>
    <h1 class="page-title">📋 所有訂單</h1>

    <div class="alert alert-error" v-if="errorMsg">{{ errorMsg }}</div>
    <div class="empty-hint" v-if="groupedMembers.length === 0 && !errorMsg">目前沒有任何訂單</div>

    <!-- 依會員分組 -->
    <div class="member-block" v-for="group in groupedMembers" :key="group.memberId">

      <!-- 會員標題 -->
      <div class="member-header">
        <span class="member-name">{{ group.memberName }}</span>
        <span class="member-id">（{{ group.memberId }}）</span>
        <span class="member-order-count">共 {{ group.orders.length }} 筆訂單</span>
      </div>

      <!-- 訂單明細整合表（同商品合併加總） -->
      <div class="section-label">訂單明細</div>
      <table class="detail-table">
        <thead>
          <tr>
            <th>流水號</th>
            <th>商品編號</th>
            <th>商品名稱</th>
            <th>數量</th>
            <th>單價</th>
            <th>小計</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(item, idx) in group.mergedItems" :key="item.productId">
            <td>{{ idx + 1 }}</td>
            <td>{{ item.productId }}</td>
            <td>{{ item.productName }}</td>
            <td>{{ item.quantity }}</td>
            <td>NT$ {{ item.standPrice?.toLocaleString() }}</td>
            <td>NT$ {{ item.itemPrice?.toLocaleString() }}</td>
          </tr>
        </tbody>
      </table>

      <!-- 各訂單付款狀態 -->
      <div class="section-label">訂單付款狀態</div>
      <table class="status-table">
        <thead>
          <tr>
            <th>訂單編號</th>
            <th>建立時間</th>
            <th>訂單總金額</th>
            <th>付款狀態</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="order in group.orders" :key="order.orderId"
              :class="{ 'row-paid': order.payStatus === 1 }">
            <td class="order-id-cell">{{ order.orderId }}</td>
            <td>{{ order.createdAt }}</td>
            <td>NT$ {{ order.totalPrice?.toLocaleString() }}</td>
            <td>
              <span class="badge" :class="order.payStatus === 1 ? 'badge-paid' : 'badge-unpaid'">
                {{ order.payStatus === 1 ? '已付款' : '未付款' }}
              </span>
            </td>
            <td>
              <button class="btn-toggle"
                      :class="order.payStatus === 1 ? 'btn-set-unpaid' : 'btn-set-paid'"
                      :disabled="order.updating"
                      @click="togglePayStatus(order)">
                {{ order.updating ? '更新中...' : (order.payStatus === 1 ? '標記未付款' : '標記已付款') }}
              </button>
            </td>
          </tr>
        </tbody>
      </table>

      <!-- 所有訂單合計 -->
      <div class="member-total">
        所有訂單合計：<span>NT$ {{ group.totalAmount.toLocaleString() }}</span>
      </div>

    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getAllOrders, updatePayStatus } from '../../api/orderApi'

const orders   = ref([])
const errorMsg = ref('')

const loadOrders = async () => {
  try {
    const res = await getAllOrders()
    orders.value = (res.data.data || []).map(o => ({ ...o, updating: false }))
  } catch (e) {
    errorMsg.value = '載入訂單失敗'
  }
}

// 依會員 ID 分組，合併相同商品並計算合計金額
const groupedMembers = computed(() => {
  const map = new Map()
  for (const order of orders.value) {
    if (!map.has(order.memberId)) {
      map.set(order.memberId, {
        memberId:   order.memberId,
        memberName: order.memberName,
        orders:     [],
      })
    }
    map.get(order.memberId).orders.push(order)
  }

  return Array.from(map.values()).map(group => {
    // 相同 productId 的品項合併加總
    const itemMap = new Map()
    for (const order of group.orders) {
      for (const item of (order.items || [])) {
        if (itemMap.has(item.productId)) {
          const existing = itemMap.get(item.productId)
          existing.quantity  += item.quantity
          existing.itemPrice += item.itemPrice
        } else {
          itemMap.set(item.productId, { ...item })
        }
      }
    }

    // 所有訂單總金額
    const totalAmount = group.orders.reduce((sum, o) => sum + (o.totalPrice || 0), 0)

    return { ...group, mergedItems: Array.from(itemMap.values()), totalAmount }
  })
})

const togglePayStatus = async (order) => {
  order.updating = true
  errorMsg.value = ''
  try {
    const newStatus = order.payStatus === 1 ? 0 : 1
    await updatePayStatus(order.orderId, newStatus)
    order.payStatus = newStatus
  } catch (e) {
    errorMsg.value = '更新付款狀態失敗'
  } finally {
    order.updating = false
  }
}

onMounted(loadOrders)
</script>

<style scoped>
.member-block {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
  margin-bottom: 1.8rem;
  overflow: hidden;
}
.member-header {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 1rem 1.5rem;
  background: #003087;
  color: white;
}
.member-name        { font-size: 1rem; font-weight: 700; }
.member-id          { font-size: 0.88rem; opacity: 0.8; }
.member-order-count { margin-left: auto; font-size: 0.82rem; opacity: 0.75; }

.section-label {
  padding: 0.5rem 1.5rem;
  font-size: 0.82rem;
  font-weight: 700;
  color: #555;
  background: #f0f4f8;
  border-top: 1px solid #e0e0e0;
  border-bottom: 1px solid #e0e0e0;
  letter-spacing: 0.03em;
}

.detail-table,
.status-table { width: 100%; border-collapse: collapse; }

.detail-table th,
.detail-table td,
.status-table th,
.status-table td {
  padding: 0.6rem 1.2rem;
  text-align: left;
  border-bottom: 1px solid #f0f0f0;
  font-size: 0.88rem;
}
.detail-table th,
.status-table th { background: #fafafa; color: #666; font-weight: 600; }

/* 已付款列 → 灰底 */
.row-paid td { background-color: #f0f0f0 !important; color: #999; }

.order-id-cell { font-size: 0.78rem; color: #aaa; }

.badge {
  display: inline-block;
  padding: 0.2rem 0.65rem;
  border-radius: 20px;
  font-size: 0.8rem;
  font-weight: 600;
}
.badge-paid   { background: #d4edda; color: #155724; }
.badge-unpaid { background: #fff3cd; color: #856404; }

.btn-toggle {
  padding: 0.3rem 0.9rem;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.82rem;
  font-weight: 600;
}
.btn-set-paid        { background: #28a745; color: white; }
.btn-set-paid:hover  { background: #218838; }
.btn-set-unpaid      { background: #6c757d; color: white; }
.btn-set-unpaid:hover { background: #5a6268; }
.btn-toggle:disabled { background: #ccc; cursor: not-allowed; }

.empty-hint { color: #999; text-align: center; padding: 3rem; }

.member-total {
  padding: 0.75rem 1.5rem;
  text-align: right;
  font-size: 0.95rem;
  font-weight: 700;
  color: #003087;
  background: #f0f4f8;
  border-top: 2px solid #003087;
}
.member-total span { font-size: 1.05rem; }
</style>
