import { defineStore } from 'pinia'
import { recommendAPI } from '../services/api'

interface Product {
  id: number
  name: string
  description: string
  price: number
  image: string
  categoryId: number
  categoryName: string
  rating: number
  reviewCount: number
  isHot: boolean
  isNew: boolean
  ingredients: string[]
  customizationOptions: any[]
}

interface Recommendation {
  product: Product
  reason: string
  score: number
  tags: string[]
}

interface RecommendationState {
  recommendations: Recommendation[]
  promotionRecommendations: any[]
  productCombinations: any[]
  loading: boolean
  error: string | null
}

export const useRecommendationStore = defineStore('recommendation', {
  state: (): RecommendationState => ({
    recommendations: [],
    promotionRecommendations: [],
    productCombinations: [],
    loading: false,
    error: null
  }),

  getters: {
    personalizedRecommendations: (state) => state.recommendations,
    allPromotionRecommendations: (state) => state.promotionRecommendations,
    allProductCombinations: (state) => state.productCombinations,
    isLoading: (state) => state.loading,
    getError: (state) => state.error
  },

  actions: {
    async getPersonalizedRecommendations(params?: { customerId?: number; limit?: number; storeId?: number }) {
      this.loading = true
      this.error = null
      try {
        const response = await recommendAPI.getRecommendations(params || {})
        this.recommendations = Array.isArray(response) ? response : []
        return this.recommendations
      } catch (error: any) {
        this.error = error.response?.data?.message || '获取推荐失败'
        // 如果API失败，使用模拟数据
        this.useMockRecommendations()
        throw error
      } finally {
        this.loading = false
      }
    },

    async getPromotionRecommendations(params?: { customerId?: number; limit?: number; storeId?: number }) {
      this.loading = true
      this.error = null
      try {
        const response = await recommendAPI.getPromotionRecommendations(params || {})
        this.promotionRecommendations = Array.isArray(response) ? response : []
        return this.promotionRecommendations
      } catch (error: any) {
        this.error = error.response?.data?.message || '获取促销推荐失败'
        // 如果API失败，使用模拟数据
        this.useMockPromotions()
        throw error
      } finally {
        this.loading = false
      }
    },

    async getProductCombinations(params?: { customerId?: number; limit?: number; storeId?: number }) {
      this.loading = true
      this.error = null
      try {
        const response = await recommendAPI.getProductCombinations(params || {})
        this.productCombinations = Array.isArray(response) ? response : []
        return this.productCombinations
      } catch (error: any) {
        this.error = error.response?.data?.message || '获取产品组合推荐失败'
        // 如果API失败，使用模拟数据
        this.useMockCombinations()
        throw error
      } finally {
        this.loading = false
      }
    },

    async submitRecommendationFeedback(feedbackData: { userId: number; productId: number; feedbackType: string; score?: number; comment?: string }) {
      try {
        await recommendAPI.submitFeedback(feedbackData)
        return true
      } catch (error: any) {
        this.error = error.response?.data?.message || '提交反馈失败'
        throw error
      }
    },

    // 模拟数据
    useMockRecommendations() {
      this.recommendations = [
        {
          product: {
            id: 1,
            name: '经典美式咖啡',
            description: '醇厚的经典美式咖啡，口感平衡',
            price: 28,
            image: 'https://neeko-copilot.bytedance.net/api/text2image?prompt=classic%20americano%20coffee%20in%20a%20modern%20cup%20with%20steam&size=512x512',
            categoryId: 1,
            categoryName: '经典咖啡',
            rating: 4.8,
            reviewCount: 1250,
            isHot: true,
            isNew: false,
            ingredients: ['精选咖啡豆', '纯净水'],
            customizationOptions: []
          },
          reason: '根据您的历史偏好推荐',
          score: 0.95,
          tags: ['热门', '经典']
        },
        {
          product: {
            id: 2,
            name: '拿铁咖啡',
            description: '丝滑的牛奶与浓缩咖啡的完美融合',
            price: 32,
            image: 'https://neeko-copilot.bytedance.net/api/text2image?prompt=latte%20coffee%20with%20milk%20foam%20art&size=512x512',
            categoryId: 1,
            categoryName: '经典咖啡',
            rating: 4.9,
            reviewCount: 2100,
            isHot: true,
            isNew: false,
            ingredients: ['精选咖啡豆', '鲜牛奶'],
            customizationOptions: []
          },
          reason: '本周最受欢迎的饮品',
          score: 0.92,
          tags: ['热门', '畅销']
        },
        {
          product: {
            id: 3,
            name: '香草拿铁',
            description: '香草风味与咖啡的完美结合',
            price: 36,
            image: 'https://neeko-copilot.bytedance.net/api/text2image?prompt=vanilla%20latte%20coffee%20in%20a%20tall%20glass&size=512x512',
            categoryId: 2,
            categoryName: '风味咖啡',
            rating: 4.7,
            reviewCount: 980,
            isHot: false,
            isNew: true,
            ingredients: ['精选咖啡豆', '鲜牛奶', '香草糖浆'],
            customizationOptions: []
          },
          reason: '新品尝鲜',
          score: 0.88,
          tags: ['新品', '风味']
        },
        {
          product: {
            id: 4,
            name: '焦糖玛奇朵',
            description: '焦糖的甜蜜与咖啡的苦涩交织',
            price: 38,
            image: 'https://neeko-copilot.bytedance.net/api/text2image?prompt=caramel%20macchiato%20with%20caramel%20drizzle&size=512x512',
            categoryId: 2,
            categoryName: '风味咖啡',
            rating: 4.6,
            reviewCount: 850,
            isHot: true,
            isNew: false,
            ingredients: ['精选咖啡豆', '鲜牛奶', '焦糖糖浆'],
            customizationOptions: []
          },
          reason: '适合您的口味偏好',
          score: 0.85,
          tags: ['风味', '畅销']
        }
      ]
    },

    useMockPromotions() {
      this.promotionRecommendations = [
        {
          id: 1,
          title: '新用户专享',
          description: '首单立减10元',
          discount: 10,
          startTime: new Date().toISOString(),
          endTime: new Date(Date.now() + 7 * 24 * 60 * 60 * 1000).toISOString(),
          image: 'https://neeko-copilot.bytedance.net/api/text2image?prompt=coffee%20promotion%20banner%20new%20user%20discount&size=1024x512'
        },
        {
          id: 2,
          title: '周三咖啡日',
          description: '全场咖啡8.5折',
          discount: 0.85,
          startTime: new Date().toISOString(),
          endTime: new Date(Date.now() + 1 * 24 * 60 * 60 * 1000).toISOString(),
          image: 'https://neeko-copilot.bytedance.net/api/text2image?prompt=coffee%20wednesday%20promotion%20banner&size=1024x512'
        }
      ]
    },

    useMockCombinations() {
      this.productCombinations = [
        {
          id: 1,
          name: '早餐组合',
          products: [
            {
              id: 1,
              name: '经典美式咖啡',
              price: 28
            },
            {
              id: 5,
              name: '牛角面包',
              price: 18
            }
          ],
          totalPrice: 46,
          discountPrice: 40,
          saving: 6,
          image: 'https://neeko-copilot.bytedance.net/api/text2image?prompt=coffee%20and%20croissant%20breakfast%20combo&size=512x512'
        },
        {
          id: 2,
          name: '下午茶组合',
          products: [
            {
              id: 3,
              name: '香草拿铁',
              price: 36
            },
            {
              id: 6,
              name: '蓝莓蛋糕',
              price: 22
            }
          ],
          totalPrice: 58,
          discountPrice: 50,
          saving: 8,
          image: 'https://neeko-copilot.bytedance.net/api/text2image?prompt=coffee%20and%20blueberry%20cake%20afternoon%20tea&size=512x512'
        }
      ]
    }
  }
})
