<template>
  <div class="login-wrapper">
    <div class="login-card">
      <div class="login-header">
        <div class="login-logo">🛒</div>
        <h1>玉山電商購物中心</h1>
        <p>{{ isRegister ? '建立新帳號' : '請登入您的帳號' }}</p>
      </div>

      <div class="alert alert-success" v-if="successMsg">{{ successMsg }}</div>
      <div class="alert alert-error"   v-if="errorMsg">{{ errorMsg }}</div>

      <!-- 註冊額外欄位 -->
      <template v-if="isRegister">
        <div class="form-group">
          <label>姓名 *</label>
          <input v-model="form.memberName" placeholder="請輸入姓名" />
        </div>
        <div class="form-group">
          <label>手機</label>
          <input v-model="form.phone" placeholder="例：0912-345-678" />
        </div>
      </template>

      <div class="form-group">
        <label>Email *</label>
        <input v-model="form.email" type="email" placeholder="請輸入 Email" />
      </div>
      <div class="form-group">
        <label>密碼 *</label>
        <input v-model="form.password" type="password" placeholder="請輸入密碼（至少6碼）"
               @keyup.enter="isRegister ? handleRegister() : handleLogin()" />
      </div>

      <button class="btn btn-primary btn-block" :disabled="loading"
              @click="isRegister ? handleRegister() : handleLogin()">
        {{ loading ? (isRegister ? '註冊中...' : '登入中...') : (isRegister ? '註冊' : '登入') }}
      </button>

      <div class="toggle-hint">
        <span v-if="isRegister">已有帳號？</span>
        <span v-else>還沒有帳號？</span>
        <a href="#" @click.prevent="switchMode">{{ isRegister ? '前往登入' : '立即註冊' }}</a>
      </div>

      <div class="admin-hint" v-if="!isRegister">
        <p>管理員帳號：admin@esun.com　密碼：admin1234</p>
        <p>測試會員：member1@esun.com　密碼：member1234</p>
        <p class="hint-indent"> member2@esun.com　密碼：member1234</p>
        <p class="hint-indent"> member3@esun.com　密碼：member1234</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { login as loginApi, register as registerApi } from '../api/authApi'
import { useAuth } from '../store/auth'

const router     = useRouter()
const { login }  = useAuth()

const isRegister = ref(false)
const form       = ref({ memberName: '', email: '', password: '', phone: '' })
const loading    = ref(false)
const successMsg = ref('')
const errorMsg   = ref('')

const switchMode = () => {
  isRegister.value = !isRegister.value
  errorMsg.value   = ''
  successMsg.value = ''
  form.value = { memberName: '', email: '', password: '', phone: '' }
}

const handleLogin = async () => {
  if (!form.value.email || !form.value.password) {
    errorMsg.value = '請填寫 Email 與密碼'
    return
  }
  loading.value  = true
  errorMsg.value = ''
  try {
    const res = await loginApi({ email: form.value.email, password: form.value.password })
    login(res.data.data)
    if (res.data.data.role === 'ADMIN') {
      router.push('/admin/orders')
    } else {
      router.push('/orders')
    }
  } catch (e) {
    errorMsg.value = e.response?.data?.message || '登入失敗，請確認帳號密碼'
  } finally {
    loading.value = false
  }
}

const handleRegister = async () => {
  if (!form.value.memberName || !form.value.email || !form.value.password) {
    errorMsg.value = '請填寫姓名、Email 與密碼'
    return
  }
  loading.value  = true
  errorMsg.value = ''
  try {
    const res = await registerApi(form.value)
    login(res.data.data)
    router.push('/orders')
  } catch (e) {
    errorMsg.value = e.response?.data?.message || '註冊失敗，請確認 Email 是否已被使用'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-wrapper {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f0f4f8;
}
.login-card {
  background: white;
  border-radius: 12px;
  padding: 2.5rem;
  width: 100%;
  max-width: 420px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.12);
}
.login-header {
  text-align: center;
  margin-bottom: 2rem;
}
.login-logo { font-size: 3rem; margin-bottom: 0.5rem; }
.login-header h1 { font-size: 1.4rem; color: #003087; margin-bottom: 0.3rem; }
.login-header p  { color: #888; font-size: 0.9rem; }
.btn-block { width: 100%; padding: 0.75rem; font-size: 1rem; margin-top: 0.5rem; }
.toggle-hint {
  text-align: center;
  margin-top: 1.2rem;
  font-size: 0.88rem;
  color: #666;
}
.toggle-hint a { color: #003087; text-decoration: none; font-weight: 600; margin-left: 0.3rem; }
.toggle-hint a:hover { text-decoration: underline; }
.admin-hint {
  margin-top: 1.2rem;
  padding: 0.7rem 1rem;
  background: #f8f9fa;
  border-radius: 6px;
  font-size: 0.78rem;
  color: #888;
}
.admin-hint p { margin: 0.15rem 0; }
.hint-indent  { padding-left: 4.5em; }
</style>
