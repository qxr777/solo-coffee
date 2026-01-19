import { describe, it, expect, beforeEach, vi, afterEach } from 'vitest'
import { createPinia, setActivePinia } from 'pinia'
import { storeAPI } from '../services/api'
import { useStoreStore } from '../store/storeStore'

// Mock the storeAPI
vi.mock('../services/api', () => ({
  storeAPI: {
    getNearbyStores: vi.fn(),
    searchStores: vi.fn(),
    getStoreDetail: vi.fn(),
    toggleFavorite: vi.fn(),
    getFavoriteStores: vi.fn()
  }
}))

const mockedStoreAPI = storeAPI as any

describe('StoreStore', () => {
  // Mock localStorage
  const localStorageMock = {
    getItem: vi.fn(),
    setItem: vi.fn(),
    removeItem: vi.fn(),
    clear: vi.fn()
  }
  
  // Mock navigator.geolocation
  const mockGeolocation = {
    getCurrentPosition: vi.fn()
  }
  
  beforeEach(() => {
    // Create a fresh Pinia instance for each test
    const pinia = createPinia()
    setActivePinia(pinia)
    
    // Mock localStorage
    Object.defineProperty(window, 'localStorage', {
      value: localStorageMock
    })
    
    // Mock navigator.geolocation
    Object.defineProperty(navigator, 'geolocation', {
      value: mockGeolocation
    })
    
    // Clear all mocks
    vi.clearAllMocks()
  })
  
  afterEach(() => {
    // Restore original implementations
    vi.restoreAllMocks()
  })

  describe('state', () => {
    it('should initialize with empty stores array', () => {
      const storeStore = useStoreStore()
      expect(storeStore.stores).toEqual([])
    })

    it('should initialize with empty nearbyStores array', () => {
      const storeStore = useStoreStore()
      expect(storeStore.nearbyStores).toEqual([])
    })

    it('should initialize with empty favoriteStores array', () => {
      const storeStore = useStoreStore()
      expect(storeStore.favoriteStores).toEqual([])
    })

    it('should initialize with null selectedStore', () => {
      const storeStore = useStoreStore()
      expect(storeStore.$state.selectedStore).toBeNull()
    })

    it('should initialize with loading false', () => {
      const storeStore = useStoreStore()
      expect(storeStore.$state.loading).toBe(false)
    })

    it('should initialize with error null', () => {
      const storeStore = useStoreStore()
      expect(storeStore.$state.error).toBeNull()
    })

    it('should initialize with null currentLocation', () => {
      const storeStore = useStoreStore()
      expect(storeStore.$state.currentLocation).toBeNull()
    })
  })

  describe('getters', () => {
    it('getStores should return stores state', () => {
      const storeStore = useStoreStore()
      const mockStores = [{ id: 1, name: 'Test Store' } as any]
      storeStore.stores = mockStores
      
      expect(storeStore.getStores).toEqual(mockStores)
    })

    it('getAllNearbyStores should return nearbyStores state', () => {
      const storeStore = useStoreStore()
      const mockNearbyStores = [{ id: 1, name: 'Test Store' } as any]
      storeStore.$state.nearbyStores = mockNearbyStores
      
      expect(storeStore.getAllNearbyStores).toEqual(mockNearbyStores)
    })

    it('getAllFavoriteStores should return favoriteStores state', () => {
      const storeStore = useStoreStore()
      const mockFavoriteStores = [{ id: 1, name: 'Test Store' } as any]
      storeStore.$state.favoriteStores = mockFavoriteStores
      
      expect(storeStore.getAllFavoriteStores).toEqual(mockFavoriteStores)
    })

    it('getSelectedStore should return selectedStore state', () => {
      const storeStore = useStoreStore()
      const mockSelectedStore = { id: 1, name: 'Test Store' } as any
      storeStore.$state.selectedStore = mockSelectedStore
      
      expect(storeStore.getSelectedStore).toEqual(mockSelectedStore)
    })

    it('isLoading should return loading state', () => {
      const storeStore = useStoreStore()
      storeStore.$state.loading = true
      
      expect(storeStore.isLoading).toBe(true)
      
      storeStore.$state.loading = false
      expect(storeStore.isLoading).toBe(false)
    })

    it('getError should return error state', () => {
      const storeStore = useStoreStore()
      const errorMessage = 'Test Error'
      storeStore.$state.error = errorMessage
      
      expect(storeStore.getError).toBe(errorMessage)
      
      storeStore.$state.error = null
      expect(storeStore.getError).toBeNull()
    })

    it('getCurrentLocation should return currentLocation state', () => {
      const storeStore = useStoreStore()
      const mockLocation = { latitude: 123.456, longitude: 78.901 }
      storeStore.$state.currentLocation = mockLocation
      
      expect(storeStore.getCurrentLocation).toEqual(mockLocation)
      
      storeStore.$state.currentLocation = null
      expect(storeStore.getCurrentLocation).toBeNull()
    })
  })

  describe('actions', () => {
    describe('getNearbyStores', () => {
      it('should fetch nearby stores from API with provided location', async () => {
        const storeStore = useStoreStore()
        const mockStores = [{ id: 1, name: 'Test Store' } as any]
        
        mockedStoreAPI.getNearbyStores.mockResolvedValueOnce({ data: mockStores })
        
        const result = await storeStore.getNearbyStores({ latitude: 123.456, longitude: 78.901, radius: 5 })
        
        expect(mockedStoreAPI.getNearbyStores).toHaveBeenCalledWith({ latitude: 123.456, longitude: 78.901, radius: 5 })
        expect(storeStore.nearbyStores).toEqual(mockStores)
        expect(result).toEqual(mockStores)
        expect(storeStore.loading).toBe(false)
        expect(storeStore.error).toBeNull()
      })

      it('should use mock stores when API call fails', async () => {
        const storeStore = useStoreStore()
        
        // Mock geolocation to return default location
        mockGeolocation.getCurrentPosition.mockImplementation((success) => {
          success({ coords: { latitude: 39.9042, longitude: 116.4074 } } as GeolocationPosition)
        })
        
        mockedStoreAPI.getNearbyStores.mockRejectedValueOnce(new Error('API Error'))
        
        await expect(storeStore.getNearbyStores()).rejects.toThrow('API Error')
        
        expect(storeStore.nearbyStores).toHaveLength(4) // 4 mock nearby stores
        expect(storeStore.stores).toHaveLength(4) // Stores should be set to nearby stores
        expect(storeStore.loading).toBe(false)
        expect(storeStore.error).toBe('获取附近门店失败')
      })
    })

    describe('searchStores', () => {
      it('should search stores from API with provided keyword and location', async () => {
        const storeStore = useStoreStore()
        const mockStores = [{ id: 1, name: 'Test Store' } as any]
        
        mockedStoreAPI.searchStores.mockResolvedValueOnce({ data: mockStores })
        
        const result = await storeStore.searchStores({ keyword: 'coffee', latitude: 123.456, longitude: 78.901 })
        
        expect(mockedStoreAPI.searchStores).toHaveBeenCalledWith({ keyword: 'coffee', latitude: 123.456, longitude: 78.901 })
        expect(storeStore.stores).toEqual(mockStores)
        expect(result).toEqual(mockStores)
        expect(storeStore.loading).toBe(false)
        expect(storeStore.error).toBeNull()
      })

      it('should use mock search results when API call fails', async () => {
        const storeStore = useStoreStore()
        
        // Mock geolocation to return default location
        mockGeolocation.getCurrentPosition.mockImplementation((success) => {
          success({ coords: { latitude: 39.9042, longitude: 116.4074 } } as GeolocationPosition)
        })
        
        mockedStoreAPI.searchStores.mockRejectedValueOnce(new Error('API Error'))
        
        await expect(storeStore.searchStores({ keyword: '国贸' })).rejects.toThrow('API Error')
        
        expect(storeStore.stores).toHaveLength(2) // 2 mock stores matching '国贸'
        expect(storeStore.loading).toBe(false)
        expect(storeStore.error).toBe('搜索门店失败')
      })
    })

    describe('getStoreDetail', () => {
      it('should fetch store detail from API', async () => {
        const storeStore = useStoreStore()
        const mockStore = { id: 1, name: 'Test Store' } as any
        
        mockedStoreAPI.getStoreDetail.mockResolvedValueOnce({ data: mockStore })
        
        const result = await storeStore.getStoreDetail(1)
        
        expect(mockedStoreAPI.getStoreDetail).toHaveBeenCalledWith(1)
        expect(storeStore.$state.selectedStore).toEqual(mockStore)
        expect(result).toEqual(mockStore)
        expect(storeStore.$state.loading).toBe(false)
        expect(storeStore.$state.error).toBeNull()
      })

      it('should use mock store detail when API call fails', async () => {
        const storeStore = useStoreStore()
        
        mockedStoreAPI.getStoreDetail.mockRejectedValueOnce(new Error('API Error'))
        
        await expect(storeStore.getStoreDetail(1)).rejects.toThrow('API Error')
        
        expect(storeStore.$state.selectedStore).toBeDefined()
        expect(storeStore.$state.selectedStore?.id).toBe(1)
        expect(storeStore.$state.selectedStore?.name).toBe('Solo Coffee 国贸店')
        expect(storeStore.$state.loading).toBe(false)
        expect(storeStore.$state.error).toBe('获取门店详情失败')
      })
    })

    describe('toggleFavorite', () => {
      it('should toggle favorite status successfully', async () => {
        const storeStore = useStoreStore()
        const mockStore = {
          id: 1, 
          name: 'Test Store', 
          isFavorite: false,
          address: 'Test Address',
          phone: '123456789',
          latitude: 0,
          longitude: 0,
          rating: 4.5,
          reviewCount: 100,
          openingHours: '08:00-22:00',
          isOpen: true,
          features: [],
          image: ''
        } as any
        
        storeStore.$state.stores = [mockStore]
        storeStore.$state.nearbyStores = [mockStore]
        
        mockedStoreAPI.toggleFavorite.mockResolvedValueOnce(undefined)
        
        const result = await storeStore.toggleFavorite(1, true)
        
        expect(mockedStoreAPI.toggleFavorite).toHaveBeenCalledWith(1, true)
        expect(storeStore.$state.stores[0].isFavorite).toBe(true)
        expect(storeStore.$state.nearbyStores[0].isFavorite).toBe(true)
        expect(storeStore.$state.favoriteStores).toHaveLength(1)
        expect(result).toBe(true)
        expect(storeStore.$state.loading).toBe(false)
        expect(storeStore.$state.error).toBeNull()
      })

      it('should remove from favorites when toggling to false', async () => {
        const storeStore = useStoreStore()
        const mockStore = {
          id: 1, 
          name: 'Test Store', 
          isFavorite: true,
          address: 'Test Address',
          phone: '123456789',
          latitude: 0,
          longitude: 0,
          rating: 4.5,
          reviewCount: 100,
          openingHours: '08:00-22:00',
          isOpen: true,
          features: [],
          image: ''
        } as any
        
        storeStore.$state.stores = [mockStore]
        storeStore.$state.nearbyStores = [mockStore]
        storeStore.$state.favoriteStores = [mockStore]
        
        mockedStoreAPI.toggleFavorite.mockResolvedValueOnce(undefined)
        
        const result = await storeStore.toggleFavorite(1, false)
        
        expect(storeStore.$state.stores[0].isFavorite).toBe(false)
        expect(storeStore.$state.nearbyStores[0].isFavorite).toBe(false)
        expect(storeStore.$state.favoriteStores).toHaveLength(0)
        expect(result).toBe(true)
      })

      it('should handle toggle favorite failure', async () => {
        const storeStore = useStoreStore()
        
        mockedStoreAPI.toggleFavorite.mockRejectedValueOnce(new Error('API Error'))
        
        await expect(storeStore.toggleFavorite(1, true)).rejects.toThrow('API Error')
        
        expect(storeStore.$state.loading).toBe(false)
        expect(storeStore.$state.error).toBe('操作失败')
      })
    })

    describe('getFavoriteStores', () => {
      it('should fetch favorite stores from API', async () => {
        const storeStore = useStoreStore()
        const mockStores = [{ id: 1, name: 'Test Store' } as any]
        
        mockedStoreAPI.getFavoriteStores.mockResolvedValueOnce({ data: mockStores })
        
        const result = await storeStore.getFavoriteStores({ page: 1, size: 10 })
        
        expect(mockedStoreAPI.getFavoriteStores).toHaveBeenCalledWith({ page: 1, size: 10 })
        expect(storeStore.$state.favoriteStores).toEqual(mockStores)
        expect(result).toEqual(mockStores)
        expect(storeStore.$state.loading).toBe(false)
        expect(storeStore.$state.error).toBeNull()
      })

      it('should use mock favorite stores when API call fails', async () => {
        const storeStore = useStoreStore()
        
        mockedStoreAPI.getFavoriteStores.mockRejectedValueOnce(new Error('API Error'))
        
        await expect(storeStore.getFavoriteStores()).rejects.toThrow('API Error')
        
        expect(storeStore.$state.favoriteStores).toHaveLength(1) // 1 mock favorite store
        expect(storeStore.$state.loading).toBe(false)
        expect(storeStore.$state.error).toBe('获取收藏门店失败')
      })
    })

    describe('selectStore and loadSelectedStore', () => {
      it('should select store and save to localStorage', () => {
        const storeStore = useStoreStore()
        const mockStore = {
          id: 1, 
          name: 'Test Store',
          address: 'Test Address',
          phone: '123456789',
          latitude: 0,
          longitude: 0,
          rating: 4.5,
          reviewCount: 100,
          openingHours: '08:00-22:00',
          isOpen: true,
          features: [],
          image: ''
        } as any
        
        storeStore.selectStore(mockStore)
        
        expect(localStorageMock.setItem).toHaveBeenCalledWith('selected_store', JSON.stringify(mockStore))
        expect(storeStore.$state.selectedStore).toEqual(mockStore)
      })

      it('should load selected store from localStorage', () => {
        const storeStore = useStoreStore()
        const mockStore = {
          id: 1, 
          name: 'Test Store',
          address: 'Test Address',
          phone: '123456789',
          latitude: 0,
          longitude: 0,
          rating: 4.5,
          reviewCount: 100,
          openingHours: '08:00-22:00',
          isOpen: true,
          features: [],
          image: ''
        } as any
        
        localStorageMock.getItem.mockReturnValueOnce(JSON.stringify(mockStore))
        
        storeStore.loadSelectedStore()
        
        expect(localStorageMock.getItem).toHaveBeenCalledWith('selected_store')
        expect(storeStore.$state.selectedStore).toEqual(mockStore)
      })

      it('should handle invalid localStorage data', () => {
        const storeStore = useStoreStore()
        
        localStorageMock.getItem.mockReturnValueOnce('invalid-json')
        
        storeStore.loadSelectedStore()
        
        expect(localStorageMock.getItem).toHaveBeenCalledWith('selected_store')
        expect(localStorageMock.removeItem).toHaveBeenCalledWith('selected_store')
        expect(storeStore.$state.selectedStore).toBeNull()
      })
    })

    describe('getCurrentPosition', () => {
      it('should return existing currentLocation if available', async () => {
        const storeStore = useStoreStore()
        const mockLocation = { latitude: 123.456, longitude: 78.901 }
        storeStore.$state.currentLocation = mockLocation
        
        const result = await storeStore.getCurrentPosition()
        
        expect(result).toEqual(mockLocation)
        // Should not call geolocation if currentLocation is already set
        expect(mockGeolocation.getCurrentPosition).not.toHaveBeenCalled()
      })

      it('should return geolocation result when available', async () => {
        const storeStore = useStoreStore()
        const mockLocation = { latitude: 123.456, longitude: 78.901 }
        
        mockGeolocation.getCurrentPosition.mockImplementation((success) => {
          success({ coords: mockLocation } as GeolocationPosition)
        })
        
        const result = await storeStore.getCurrentPosition()
        
        expect(mockGeolocation.getCurrentPosition).toHaveBeenCalled()
        expect(result).toEqual(mockLocation)
        expect(storeStore.$state.currentLocation).toEqual(mockLocation)
      })

      it('should return default location when geolocation fails', async () => {
        const storeStore = useStoreStore()
        const defaultLocation = { latitude: 39.9042, longitude: 116.4074 }
        
        mockGeolocation.getCurrentPosition.mockImplementation((_, error) => {
          error({ code: 1, message: 'User denied geolocation' } as GeolocationPositionError)
        })
        
        const result = await storeStore.getCurrentPosition()
        
        expect(mockGeolocation.getCurrentPosition).toHaveBeenCalled()
        expect(result).toEqual(defaultLocation)
        expect(storeStore.$state.currentLocation).toEqual(defaultLocation)
      })
    })

    describe('mock data methods', () => {
      it('useMockNearbyStores should set mock nearby stores', () => {
        const storeStore = useStoreStore()
        
        storeStore.useMockNearbyStores()
        
        expect(storeStore.nearbyStores).toHaveLength(4)
        expect(storeStore.stores).toHaveLength(4)
        expect(storeStore.nearbyStores[0].name).toBe('Solo Coffee 国贸店')
        expect(storeStore.nearbyStores[1].name).toBe('Solo Coffee 三里屯店')
      })

      it('useMockSearchStores should set filtered mock stores', () => {
        const storeStore = useStoreStore()
        
        storeStore.useMockSearchStores('国贸')
        
        expect(storeStore.stores).toHaveLength(2)
        expect(storeStore.stores[0].name).toBe('Solo Coffee 国贸店')
        expect(storeStore.stores[1].name).toBe('Solo Coffee 国贸二店')
      })

      it('useMockSearchStores should return empty array for no matches', () => {
        const storeStore = useStoreStore()
        
        storeStore.useMockSearchStores('NonExistentKeyword')
        
        expect(storeStore.stores).toEqual([])
      })

      it('useMockStoreDetail should set mock store detail', () => {
        const storeStore = useStoreStore()
        
        storeStore.useMockStoreDetail(1)
        
        expect(storeStore.$state.selectedStore).toBeDefined()
        expect(storeStore.$state.selectedStore?.id).toBe(1)
        expect(storeStore.$state.selectedStore?.name).toBe('Solo Coffee 国贸店')
      })

      it('useMockFavoriteStores should set mock favorite stores', () => {
        const storeStore = useStoreStore()
        
        storeStore.useMockFavoriteStores()
        
        expect(storeStore.favoriteStores).toHaveLength(1)
        expect(storeStore.favoriteStores[0].name).toBe('Solo Coffee 国贸店')
        expect(storeStore.favoriteStores[0].isFavorite).toBe(true)
      })
    })
  })
})
