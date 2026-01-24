import { describe, it, expect, vi, beforeEach } from 'vitest'
import { useAuthStore } from '../store/authStore'
import { useAuthStore } from '../store/authStore'
import { createPinia, setActivePinia } from 'pinia'

// Mock localStorage
const localStorageMock = (() => {
  let store: Record<string, string> = {}
  return {
    getItem: (key: string) => store[key] || null,
    setItem: (key: string, value: string) => {
      store[key] = value.toString()
    },
    clear: () => {
      store = {}
    },
    removeItem: (key: string) => {
      delete store[key]
    }
  }
})()

global.localStorage = localStorageMock as any

// Mock the entire services/api.ts module using vi.hoisted to avoid reference error
vi.mock('../services/api', () => {
  const mockAuthAPI = {
    login: vi.fn(),
    register: vi.fn(),
    logout: vi.fn(),
    sendSms: vi.fn(),
    smsLogin: vi.fn(),
    oauthLogin: vi.fn(),
    getProfile: vi.fn(),
    forgotPassword: vi.fn(),
    resetPassword: vi.fn()
  }

    // Expose mockAuthAPI to be used in tests
    ; (global as any).mockAuthAPI = mockAuthAPI

  return {
    __esModule: true,
    default: vi.fn(),
    authAPI: mockAuthAPI
  }
})

describe('AuthStore', () => {
  let authStore: ReturnType<typeof useAuthStore>

  let mockAuthAPI: any

  beforeEach(() => {
    // Get mockAuthAPI from global scope
    mockAuthAPI = (global as any).mockAuthAPI
    // Clear localStorage before each test
    localStorage.clear()
    // Reset all mock functions
    Object.values(mockAuthAPI).forEach((fn: any) => fn.mockClear())
    // Create and set active Pinia instance
    const pinia = createPinia()
    setActivePinia(pinia)
    // Create a new store instance
    authStore = useAuthStore()
  })

  describe('login', () => {
    it('should login successfully with test account', async () => {
      const phone = '13800138000'
      const password = '123456'
      const mockResponse = {
        data: {
          token: 'mock-token',
          user: {
            id: 1,
            name: '测试用户',
            phone: '13800138000',
            role: 'customer'
          }
        }
      }
      mockAuthAPI.login.mockResolvedValue(mockResponse)

      // Call login method
      const result = await authStore.login(phone, password)

      // Verify the result
      expect(result).toBeDefined()
      expect(result.token).toBe('mock-token')
      expect(result.user).toBeDefined()
      expect(authStore.token).toBe('mock-token')
      expect(authStore.user).toEqual(result.user)
      expect(localStorage.getItem('token')).toBe('mock-token')
    })

    it('should handle login failure with wrong credentials', async () => {
      const phone = 'wrong-phone'
      const password = 'wrong-password'
      const errorResponse = {
        response: {
          data: {
            message: '手机号或密码错误'
          }
        }
      }
      mockAuthAPI.login.mockRejectedValue(errorResponse)

      // Call login method and expect rejection
      await expect(authStore.login(phone, password)).rejects.toThrow()
      expect(authStore.token).toBe(null)
      expect(authStore.user).toBe(null)
      expect(localStorage.getItem('token')).toBe(null)
      expect(authStore.error).toBe('手机号或密码错误')
    })
  })

  describe('register', () => {
    it('should register successfully', async () => {
      // Mock successful register response
      const mockResponse = {
        data: {
          token: 'mock-token',
          user: {
            id: 1,
            name: 'John Doe',
            email: 'john@example.com',
            phone: '123-456-7890',
            memberLevelId: 1,
            points: 0
          }
        }
      }
      mockAuthAPI.register.mockResolvedValue(mockResponse)

      // Call register method
      const result = await authStore.register('John Doe', '123-456-7890', 'john@example.com', 'password123')

      // Verify the result
      expect(result).toEqual(mockResponse.data)
      expect(authStore.token).toBe('mock-token')
      expect(authStore.user).toEqual(mockResponse.data.user)
      expect(localStorage.getItem('token')).toBe('mock-token')
      expect(mockAuthAPI.register).toHaveBeenCalledWith({
        name: 'John Doe',
        phone: '123-456-7890',
        email: 'john@example.com',
        password: 'password123'
      })
    })
  })

  describe('logout', () => {
    it('should logout successfully', async () => {
      // Mock successful logout response
      mockAuthAPI.logout.mockResolvedValue({})

      // Set initial state
      authStore.token = 'mock-token'
      authStore.user = {
        id: 1,
        name: 'John Doe',
        email: 'john@example.com',
        phone: '123-456-7890',
        memberLevelId: 2,
        points: 1250
      }
      localStorage.setItem('token', 'mock-token')

      // Call logout method with await
      await authStore.logout()

      // Verify the state
      expect(authStore.token).toBe(null)
      expect(authStore.user).toBe(null)
      expect(localStorage.getItem('token')).toBe(null)
      expect(mockAuthAPI.logout).toHaveBeenCalled()
    })
  })

  describe('getters', () => {
    it('should return correct isAuthenticated value', () => {
      // Test when not authenticated
      expect(authStore.isAuthenticated).toBe(false)

      // Test when authenticated
      authStore.token = 'mock-token'
      expect(authStore.isAuthenticated).toBe(true)
    })

    it('should return correct user points', () => {
      // Test when no user
      expect(authStore.userPoints).toBe(0)

      // Test when user has points
      authStore.user = {
        id: 1,
        name: 'John Doe',
        email: 'john@example.com',
        phone: '123-456-7890',
        memberLevelId: 2,
        points: 1250
      }
      expect(authStore.userPoints).toBe(1250)
    })
  })
})