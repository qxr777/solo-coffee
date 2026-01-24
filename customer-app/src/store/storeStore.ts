import { defineStore } from 'pinia'
import { storeAPI } from '../services/api'

interface Store {
  id: number
  name: string
  address: string
  phone: string
  latitude: number
  longitude: number
  distance?: number
  rating: number
  reviewCount: number
  openingHours: string
  isFavorite: boolean
  isOpen: boolean
  features: string[]
  image: string
}

interface StoreState {
  stores: Store[]
  nearbyStores: Store[]
  favoriteStores: Store[]
  selectedStore: Store | null
  loading: boolean
  error: string | null
  currentLocation: {
    latitude: number
    longitude: number
  } | null
}

export const useStoreStore = defineStore('store', {
  state: (): StoreState => ({
    stores: [],
    nearbyStores: [],
    favoriteStores: [],
    selectedStore: null,
    loading: false,
    error: null,
    currentLocation: null
  }),

  getters: {
    getStores: (state) => state.stores,
    getAllNearbyStores: (state) => state.nearbyStores,
    getAllFavoriteStores: (state) => state.favoriteStores,
    getSelectedStore: (state) => state.selectedStore,
    isLoading: (state) => state.loading,
    getError: (state) => state.error,
    getCurrentLocation: (state) => state.currentLocation
  },

  actions: {
    async getNearbyStores(params?: { latitude?: number; longitude?: number; radius?: number; page?: number; size?: number }) {
      this.loading = true
      this.error = null
      try {
        // 如果没有提供位置，尝试获取当前位置
        if (!params?.latitude || !params?.longitude) {
          const location = await this.getCurrentPosition()
          params = {
            latitude: location.latitude || 0,
            longitude: location.longitude || 0,
            radius: params?.radius || 10000000,
            page: params?.page,
            size: params?.size
          }
        }

        const requestParams = params || { latitude: 0, longitude: 0, radius: 10000000 }
        const response = await storeAPI.getNearbyStores(requestParams)
        this.nearbyStores = response.data?.records || []
        return this.nearbyStores
      } catch (error: any) {
        this.error = error.response?.data?.message || '获取附近门店失败'
        console.error('API call failed:', error)
        throw error
      } finally {
        this.loading = false
      }
    },

    async searchStores(params: { keyword: string; latitude?: number; longitude?: number; page?: number; size?: number }) {
      this.loading = true
      this.error = null
      try {
        // 如果没有提供位置，尝试获取当前位置
        if (!params.latitude || !params.longitude) {
          const location = await this.getCurrentPosition()
          params = {
            ...params,
            latitude: location.latitude,
            longitude: location.longitude
          }
        }

        const response = await storeAPI.searchStores(params)
        this.stores = response.data?.records || []
        return this.stores
      } catch (error: any) {
        this.error = error.response?.data?.message || '搜索门店失败'
        console.error('API call failed:', error)
        throw error
      } finally {
        this.loading = false
      }
    },

    async getStoreDetail(storeId: number) {
      this.loading = true
      this.error = null
      try {
        const response = await storeAPI.getStoreDetail(storeId)
        this.selectedStore = response.data
        return this.selectedStore
      } catch (error: any) {
        this.error = error.response?.data?.message || '获取门店详情失败'
        console.error('API call failed:', error)
        throw error
      } finally {
        this.loading = false
      }
    },

    async toggleFavorite(storeId: number, isFavorite: boolean) {
      this.loading = true
      this.error = null
      try {
        await storeAPI.toggleFavorite(storeId, isFavorite)

        // 更新本地状态
        const store = this.stores.find(s => s.id === storeId)
        if (store) {
          store.isFavorite = isFavorite
        }

        const nearbyStore = this.nearbyStores.find(s => s.id === storeId)
        if (nearbyStore) {
          nearbyStore.isFavorite = isFavorite
        }

        const favoriteStore = this.favoriteStores.find(s => s.id === storeId)
        if (isFavorite && !favoriteStore) {
          const storeToAdd = store || nearbyStore
          if (storeToAdd) {
            this.favoriteStores.push(storeToAdd)
          }
        } else if (!isFavorite && favoriteStore) {
          this.favoriteStores = this.favoriteStores.filter(s => s.id !== storeId)
        }

        return true
      } catch (error: any) {
        this.error = error.response?.data?.message || '操作失败'
        throw error
      } finally {
        this.loading = false
      }
    },

    async getFavoriteStores(params?: { page?: number; size?: number }) {
      this.loading = true
      this.error = null
      try {
        const response = await storeAPI.getFavoriteStores(params || {})
        this.favoriteStores = response.data?.records || []
        return this.favoriteStores
      } catch (error: any) {
        this.error = error.response?.data?.message || '获取收藏门店失败'
        console.error('API call failed:', error)
        throw error
      } finally {
        this.loading = false
      }
    },

    selectStore(store: Store) {
      this.selectedStore = store
      // 保存到localStorage
      localStorage.setItem('selected_store', JSON.stringify(store))
    },

    loadSelectedStore() {
      const storedStore = localStorage.getItem('selected_store')
      if (storedStore) {
        try {
          this.selectedStore = JSON.parse(storedStore)
        } catch (error) {
          console.error('解析选中门店失败:', error)
          localStorage.removeItem('selected_store')
        }
      }
    },

    async getCurrentPosition(): Promise<{ latitude: number; longitude: number }> {
      if (this.currentLocation) {
        return this.currentLocation
      }

      return new Promise((resolve, _reject) => {
        if (navigator.geolocation) {
          navigator.geolocation.getCurrentPosition(
            (position) => {
              const location = {
                latitude: position.coords.latitude,
                longitude: position.coords.longitude
              }
              this.currentLocation = location
              resolve(location)
            },
            (error) => {
              console.error('获取位置失败:', error)
              // 使用默认位置（例如公司总部位置）
              const defaultLocation = {
                latitude: 39.9042,
                longitude: 116.4074
              }
              this.currentLocation = defaultLocation
              resolve(defaultLocation)
            }
          )
        } else {
          // 浏览器不支持地理位置
          const defaultLocation = {
            latitude: 39.9042,
            longitude: 116.4074
          }
          this.currentLocation = defaultLocation
          resolve(defaultLocation)
        }
      })
    }
  }
})
