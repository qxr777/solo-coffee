import { defineStore } from 'pinia'
import { authAPI } from '../services/api'

interface User {
  id: number
  name: string
  phone: string
  email: string
  memberLevelId: number
  points: number
}

interface AuthState {
  user: User | null
  token: string | null
  loading: boolean
  error: string | null
}

export const useAuthStore = defineStore('auth', {
  state: (): AuthState => ({
    user: null,
    token: localStorage.getItem('token'),
    loading: false,
    error: null
  }),

  getters: {
    isAuthenticated: (state) => !!state.token,
    currentUser: (state) => state.user,
    userPoints: (state) => state.user?.points || 0,
    userLevel: (state) => state.user?.memberLevelId || 0
  },

  actions: {
    async login(phone: string, password: string) {
      this.loading = true
      this.error = null
      try {
        const response: any = await authAPI.login({ phone, password })
        this.token = response.token
        this.user = response.user
        localStorage.setItem('token', response.token)
        return response
      } catch (error: any) {
        this.error = error.response?.data?.message || '手机号或密码错误'
        console.error('API call failed:', error)
        throw error
      } finally {
        this.loading = false
      }
    },

    async register(name: string, phone: string, email: string, password: string) {
      this.loading = true
      this.error = null
      try {
        const response: any = await authAPI.register({ name, phone, email, password })
        this.token = response.token
        this.user = response.user
        localStorage.setItem('token', response.token)
        return response
      } catch (error: any) {
        this.error = error.response?.data?.message || '注册失败'
        throw error
      } finally {
        this.loading = false
      }
    },

    async logout() {
      try {
        await authAPI.logout()
      } catch (error) {
        console.error('登出失败:', error)
      } finally {
        this.user = null
        this.token = null
        localStorage.removeItem('token')
      }
    },

    async fetchUserProfile() {
      if (!this.token) return
      this.loading = true
      try {
        const response: any = await authAPI.getProfile()
        this.user = response
        return response
      } catch (error) {
        console.error('获取用户信息失败:', error)
        this.logout()
        throw error
      } finally {
        this.loading = false
      }
    },

    async sendSmsCode(phone: string, type: number) {
      this.loading = true
      try {
        await authAPI.sendSms({ phone, type })
        return true
      } catch (error: any) {
        this.error = error.response?.data?.message || '发送验证码失败'
        throw error
      } finally {
        this.loading = false
      }
    },

    async smsLogin(phone: string, verificationCode: string) {
      this.loading = true
      this.error = null
      try {
        const response: any = await authAPI.smsLogin({ phone, verificationCode })
        this.token = response.token
        this.user = response.user
        localStorage.setItem('token', response.token)
        return response
      } catch (error: any) {
        this.error = error.response?.data?.message || '验证码登录失败'
        throw error
      } finally {
        this.loading = false
      }
    },

    async oauthLogin(provider: string, code: string) {
      this.loading = true
      this.error = null
      try {
        const response: any = await authAPI.oauthLogin(provider, code)
        this.token = response.token
        this.user = response.user
        localStorage.setItem('token', response.token)
        return response
      } catch (error: any) {
        this.error = error.response?.data?.message || '第三方登录失败'
        throw error
      } finally {
        this.loading = false
      }
    },

    async forgotPassword(phone: string, verificationCode: string, newPassword: string) {
      this.loading = true
      this.error = null
      try {
        const response: any = await authAPI.forgotPassword({ phone, verificationCode, newPassword })
        return response
      } catch (error: any) {
        this.error = error.response?.data?.message || '密码重置失败'
        throw error
      } finally {
        this.loading = false
      }
    },

    async resetPassword(oldPassword: string, newPassword: string) {
      this.loading = true
      this.error = null
      try {
        const response: any = await authAPI.resetPassword({ oldPassword, newPassword })
        return response
      } catch (error: any) {
        this.error = error.response?.data?.message || '密码修改失败'
        throw error
      } finally {
        this.loading = false
      }
    }
  }
})