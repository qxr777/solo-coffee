
import { defineStore } from 'pinia'

interface User {
  id: number
  name: string
  phone: string
  role: string
}

interface UserState {
  user: User | null
  token: string | null
  isAuthenticated: boolean
  loading: boolean
  error: string | null
}

export const useUserStore = defineStore('user', {
  state: (): UserState => ({
    user: null,
    token: localStorage.getItem('token'),
    isAuthenticated: localStorage.getItem('token') !== null,
    loading: false,
    error: null
  }),

  getters: {
    getUser: (state) => state.user,
    getToken: (state) => state.token,
    getIsAuthenticated: (state) => state.isAuthenticated,
    getLoading: (state) => state.loading,
    getError: (state) => state.error
  },

  actions: {
    async login(username: string, password: string) {
      this.loading = true
      this.error = null

      try {
        // 模拟登录请求
        // 实际项目中应该调用真实的登录API
        await new Promise(resolve => setTimeout(resolve, 1000))
        
        // 模拟登录成功响应
        const mockResponse = {
          token: 'mock-jwt-token-' + Date.now(),
          user: {
            id: 1,
            name: '管理员',
            phone: '13800138000',
            role: 'admin'
          }
        }

        this.token = mockResponse.token
        this.user = mockResponse.user
        this.isAuthenticated = true

        localStorage.setItem('token', mockResponse.token)
        localStorage.setItem('user', JSON.stringify(mockResponse.user))

        return mockResponse
      } catch (error) {
        this.error = '登录失败，请检查用户名和密码'
        throw error
      } finally {
        this.loading = false
      }
    },

    async logout() {
      this.token = null
      this.user = null
      this.isAuthenticated = false

      localStorage.removeItem('token')
      localStorage.removeItem('user')
    },

    clearError() {
      this.error = null
    }
  }
})
