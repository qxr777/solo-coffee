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
        const response: any = await recommendAPI.getRecommendations(params || {})
        this.recommendations = Array.isArray(response.data) ? response.data : []
        return this.recommendations
      } catch (error: any) {
        this.error = error.response?.data?.message || '获取推荐失败'
        console.error('API call failed:', error)
        throw error
      } finally {
        this.loading = false
      }
    },

    async getPromotionRecommendations(params?: { customerId?: number; limit?: number; storeId?: number }) {
      this.loading = true
      this.error = null
      try {
        const response: any = await recommendAPI.getPromotionRecommendations(params || {})
        this.promotionRecommendations = Array.isArray(response.data) ? response.data : []
        return this.promotionRecommendations
      } catch (error: any) {
        this.error = error.response?.data?.message || '获取促销推荐失败'
        console.error('API call failed:', error)
        throw error
      } finally {
        this.loading = false
      }
    },

    async getProductCombinations(params?: { customerId?: number; limit?: number; storeId?: number }) {
      this.loading = true
      this.error = null
      try {
        const response: any = await recommendAPI.getProductCombinations(params || {})
        this.productCombinations = Array.isArray(response.data) ? response.data : []
        return this.productCombinations
      } catch (error: any) {
        this.error = error.response?.data?.message || '获取产品组合推荐失败'
        console.error('API call failed:', error)
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
    }
  }
})
