import { describe, it, expect, beforeEach, vi } from 'vitest'
import { useUserStore } from '../../src/stores/userStore'
import { createPinia, setActivePinia } from 'pinia'

// Mock localStorage
const mockLocalStorage = {
  getItem: vi.fn().mockReturnValue(null),
  setItem: vi.fn(),
  removeItem: vi.fn(),
  clear: vi.fn()
}

Object.defineProperty(window, 'localStorage', {
  value: mockLocalStorage
})

describe('UserStore', () => {
  let store: ReturnType<typeof useUserStore>

  beforeEach(() => {
    // Reset mock localStorage
    vi.clearAllMocks()
    
    // Create and set active pinia
    const pinia = createPinia()
    setActivePinia(pinia)
    
    // Initialize store
    store = useUserStore()
  })

  describe('state', () => {
    it('should initialize with null user and token', () => {
      expect(store.user).toBeNull()
      expect(store.token).toBeNull()
      expect(store.isAuthenticated).toBe(false)
      expect(store.loading).toBe(false)
      expect(store.error).toBeNull()
    })

    it('should initialize with values from localStorage when available', () => {
      const mockToken = 'test-token'
      
      // Reset mock and set specific implementation
      mockLocalStorage.getItem.mockReset()
      mockLocalStorage.getItem.mockImplementation((key) => {
        if (key === 'token') return mockToken
        return null
      })

      // Create new pinia and store
      const pinia = createPinia()
      setActivePinia(pinia)
      const newStore = useUserStore()
      
      expect(newStore.token).toBe(mockToken)
      expect(newStore.user).toBeNull() // user is always initialized to null
      expect(newStore.isAuthenticated).toBe(true)
    })
  })

  describe('getters', () => {
    it('should return correct values from getters', () => {
      const mockUser = { id: 1, name: 'Test User', phone: '13800138000', role: 'admin' }
      const mockToken = 'test-token'
      
      store.user = mockUser
      store.token = mockToken
      store.isAuthenticated = true
      store.loading = true
      store.error = 'Test error'
      
      expect(store.getUser).toEqual(mockUser)
      expect(store.getToken).toBe(mockToken)
      expect(store.getIsAuthenticated).toBe(true)
      expect(store.getLoading).toBe(true)
      expect(store.getError).toBe('Test error')
    })
  })

  describe('actions', () => {
    describe('login', () => {
      it('should login successfully with correct credentials', async () => {
        const username = '13800138000'
        const password = 'password123'
        
        const result = await store.login(username, password)
        
        expect(result).toHaveProperty('token')
        expect(result).toHaveProperty('user')
        expect(result.user.phone).toBe('13800138000')
        expect(result.user.role).toBe('admin')
        
        expect(store.token).toBe(result.token)
        expect(store.user).toEqual(result.user)
        expect(store.isAuthenticated).toBe(true)
        expect(store.loading).toBe(false)
        expect(store.error).toBeNull()
        
        expect(mockLocalStorage.setItem).toHaveBeenCalledWith('token', result.token)
        expect(mockLocalStorage.setItem).toHaveBeenCalledWith('user', JSON.stringify(result.user))
      })

      it('should handle login failure', async () => {
        // Mock the promise to reject
        vi.spyOn(global, 'setTimeout').mockImplementation((fn) => {
          setTimeout(() => {
            throw new Error('Login failed')
          }, 1000)
          return 0 as unknown as NodeJS.Timeout
        })
        
        await expect(store.login('wrong', 'wrong')).rejects.toThrow()
        expect(store.error).toBe('登录失败，请检查用户名和密码')
        expect(store.loading).toBe(false)
      })
    })

    describe('logout', () => {
      it('should logout successfully', async () => {
        // Set initial state
        store.token = 'test-token'
        store.user = { id: 1, name: 'Test User', phone: '13800138000', role: 'admin' }
        store.isAuthenticated = true
        
        await store.logout()
        
        expect(store.token).toBeNull()
        expect(store.user).toBeNull()
        expect(store.isAuthenticated).toBe(false)
        
        expect(mockLocalStorage.removeItem).toHaveBeenCalledWith('token')
        expect(mockLocalStorage.removeItem).toHaveBeenCalledWith('user')
      })
    })

    describe('clearError', () => {
      it('should clear error message', () => {
        store.error = 'Test error'
        store.clearError()
        expect(store.error).toBeNull()
      })
    })
  })
})
