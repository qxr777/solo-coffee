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
            radius: params?.radius,
            page: params?.page,
            size: params?.size
          }
        }

        const requestParams = params || { latitude: 0, longitude: 0 }
        const response = await storeAPI.getNearbyStores(requestParams)
        this.nearbyStores = response.data || []
        return this.nearbyStores
      } catch (error: any) {
        this.error = error.response?.data?.message || '获取附近门店失败'
        // 如果API失败，使用模拟数据
        this.useMockNearbyStores()
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
        this.stores = response.data || []
        return this.stores
      } catch (error: any) {
        this.error = error.response?.data?.message || '搜索门店失败'
        // 如果API失败，使用模拟数据
        this.useMockSearchStores(params.keyword)
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
        // 如果API失败，使用模拟数据
        this.useMockStoreDetail(storeId)
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
        this.favoriteStores = response.data || []
        return this.favoriteStores
      } catch (error: any) {
        this.error = error.response?.data?.message || '获取收藏门店失败'
        // 如果API失败，使用模拟数据
        this.useMockFavoriteStores()
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
    },

    // 模拟数据
    useMockNearbyStores() {
      this.nearbyStores = [
        {
          id: 1,
          name: 'Solo Coffee 国贸店',
          address: '北京市朝阳区建国门外大街1号国贸中心1楼',
          phone: '010-88888888',
          latitude: 39.9075,
          longitude: 116.4668,
          distance: 0.5,
          rating: 4.8,
          reviewCount: 1250,
          openingHours: '08:00-22:00',
          isFavorite: true,
          isOpen: true,
          features: ['Wi-Fi', '座位', '外卖', '停车'],
          image: 'https://neeko-copilot.bytedance.net/api/text2image?prompt=modern%20coffee%20shop%20exterior%20with%20glass%20windows%20and%20cozy%20interior&size=512x512'
        },
        {
          id: 2,
          name: 'Solo Coffee 三里屯店',
          address: '北京市朝阳区三里屯太古里北区NLG-29',
          phone: '010-99999999',
          latitude: 39.9342,
          longitude: 116.4536,
          distance: 1.2,
          rating: 4.9,
          reviewCount: 2100,
          openingHours: '08:00-23:00',
          isFavorite: false,
          isOpen: true,
          features: ['Wi-Fi', '座位', '外卖', '停车场'],
          image: 'https://neeko-copilot.bytedance.net/api/text2image?prompt=trendy%20coffee%20shop%20in%20shopping%20district%20with%20modern%20design&size=512x512'
        },
        {
          id: 3,
          name: 'Solo Coffee 望京店',
          address: '北京市朝阳区望京SOHO T1 C座101',
          phone: '010-77777777',
          latitude: 39.9988,
          longitude: 116.4753,
          distance: 2.5,
          rating: 4.7,
          reviewCount: 850,
          openingHours: '07:30-21:00',
          isFavorite: false,
          isOpen: true,
          features: ['Wi-Fi', '座位', '外卖'],
          image: 'https://neeko-copilot.bytedance.net/api/text2image?prompt=coffee%20shop%20in%20business%20district%20with%20industrial%20design&size=512x512'
        },
        {
          id: 4,
          name: 'Solo Coffee 中关村店',
          address: '北京市海淀区中关村大街1号',
          phone: '010-66666666',
          latitude: 39.9847,
          longitude: 116.3055,
          distance: 5.0,
          rating: 4.6,
          reviewCount: 1500,
          openingHours: '08:00-22:00',
          isFavorite: false,
          isOpen: false,
          features: ['Wi-Fi', '座位', '外卖', '停车场'],
          image: 'https://neeko-copilot.bytedance.net/api/text2image?prompt=coffee%20shop%20near%20university%20campus%20with%20study%20areas&size=512x512'
        }
      ]
      this.stores = this.nearbyStores
    },

    useMockSearchStores(keyword: string) {
      this.stores = [
        {
          id: 1,
          name: 'Solo Coffee 国贸店',
          address: '北京市朝阳区建国门外大街1号国贸中心1楼',
          phone: '010-88888888',
          latitude: 39.9075,
          longitude: 116.4668,
          rating: 4.8,
          reviewCount: 1250,
          openingHours: '08:00-22:00',
          isFavorite: true,
          isOpen: true,
          features: ['Wi-Fi', '座位', '外卖', '停车'],
          image: 'https://neeko-copilot.bytedance.net/api/text2image?prompt=modern%20coffee%20shop%20exterior%20with%20glass%20windows%20and%20cozy%20interior&size=512x512'
        },
        {
          id: 5,
          name: 'Solo Coffee 国贸二店',
          address: '北京市朝阳区建国门外大街1号国贸中心3楼',
          phone: '010-88888889',
          latitude: 39.9076,
          longitude: 116.4669,
          rating: 4.7,
          reviewCount: 650,
          openingHours: '08:00-22:00',
          isFavorite: false,
          isOpen: true,
          features: ['Wi-Fi', '座位', '外卖'],
          image: 'https://neeko-copilot.bytedance.net/api/text2image?prompt=coffee%20shop%20inside%20shopping%20mall%20with%20modern%20design&size=512x512'
        }
      ].filter(store => store.name.includes(keyword) || store.address.includes(keyword))
    },

    useMockStoreDetail(storeId: number) {
      this.selectedStore = {
        id: storeId,
        name: 'Solo Coffee 国贸店',
        address: '北京市朝阳区建国门外大街1号国贸中心1楼',
        phone: '010-88888888',
        latitude: 39.9075,
        longitude: 116.4668,
        rating: 4.8,
        reviewCount: 1250,
        openingHours: '08:00-22:00',
        isFavorite: true,
        isOpen: true,
        features: ['Wi-Fi', '座位', '外卖', '停车'],
        image: 'https://neeko-copilot.bytedance.net/api/text2image?prompt=modern%20coffee%20shop%20exterior%20with%20glass%20windows%20and%20cozy%20interior&size=512x512'
      }
    },

    useMockFavoriteStores() {
      this.favoriteStores = [
        {
          id: 1,
          name: 'Solo Coffee 国贸店',
          address: '北京市朝阳区建国门外大街1号国贸中心1楼',
          phone: '010-88888888',
          latitude: 39.9075,
          longitude: 116.4668,
          rating: 4.8,
          reviewCount: 1250,
          openingHours: '08:00-22:00',
          isFavorite: true,
          isOpen: true,
          features: ['Wi-Fi', '座位', '外卖', '停车'],
          image: 'https://neeko-copilot.bytedance.net/api/text2image?prompt=modern%20coffee%20shop%20exterior%20with%20glass%20windows%20and%20cozy%20interior&size=512x512'
        }
      ]
    }
  }
})
