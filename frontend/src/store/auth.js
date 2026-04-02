import { reactive } from 'vue'

const state = reactive({
  token:      localStorage.getItem('token')      || null,
  memberId:   localStorage.getItem('memberId')   || null,
  memberName: localStorage.getItem('memberName') || null,
  role:       localStorage.getItem('role')       || null,
})

export function useAuth() {
  const login = (data) => {
    state.token      = data.token
    state.memberId   = data.memberId
    state.memberName = data.memberName
    state.role       = data.role
    localStorage.setItem('token',      data.token)
    localStorage.setItem('memberId',   data.memberId)
    localStorage.setItem('memberName', data.memberName)
    localStorage.setItem('role',       data.role)
  }

  const logout = () => {
    state.token      = null
    state.memberId   = null
    state.memberName = null
    state.role       = null
    localStorage.removeItem('token')
    localStorage.removeItem('memberId')
    localStorage.removeItem('memberName')
    localStorage.removeItem('role')
  }

  return { state, login, logout }
}
