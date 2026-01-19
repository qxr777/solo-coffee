import { describe, it, expect, beforeEach, vi } from 'vitest'
import { createPinia, setActivePinia } from 'pinia'
import { recommendAPI } from '../services/api'
import { useRecommendationStore } from '../store/recommendationStore'

// Mock the recommendAPI
vi.mock('../services/api', () => ({
  recommendAPI: {
    getRecommendations: vi.fn(),
    getPromotionRecommendations: vi.fn(),
    getProductCombinations: vi.fn(),
    submitFeedback: vi.fn()
  }
}))

const mockedRecommendAPI = recommendAPI as any

describe('RecommendationStore', () => {
  beforeEach(() => {
    // Create a fresh Pinia instance for each test
    const pinia = createPinia()
    setActivePinia(pinia)
    
    // Clear all mocks
    vi.clearAllMocks()
  })

  describe('state', () => {
    it('should initialize with empty recommendations array', () => {
      const recommendationStore = useRecommendationStore()
      expect(recommendationStore.recommendations).toEqual([])
    })

    it('should initialize with empty promotionRecommendations array', () => {
      const recommendationStore = useRecommendationStore()
      expect(recommendationStore.$state.promotionRecommendations).toEqual([])
    })

    it('should initialize with empty productCombinations array', () => {
      const recommendationStore = useRecommendationStore()
      expect(recommendationStore.$state.productCombinations).toEqual([])
    })

    it('should initialize with loading false', () => {
      const recommendationStore = useRecommendationStore()
      expect(recommendationStore.loading).toBe(false)
    })

    it('should initialize with error null', () => {
      const recommendationStore = useRecommendationStore()
      expect(recommendationStore.error).toBeNull()
    })
  })

  describe('getters', () => {
    it('personalizedRecommendations should return recommendations state', () => {
      const recommendationStore = useRecommendationStore()
      const mockRecommendations = [{ product: { id: 1, name: 'Test Product' }, reason: 'Test', score: 0.9, tags: ['test'] } as any]
      recommendationStore.recommendations = mockRecommendations
      
      expect(recommendationStore.personalizedRecommendations).toEqual(mockRecommendations)
    })

    it('allPromotionRecommendations should return promotionRecommendations state', () => {
      const recommendationStore = useRecommendationStore()
      const mockPromotions = [{ id: 1, title: 'Test Promotion' } as any]
      recommendationStore.$state.promotionRecommendations = mockPromotions
      
      expect(recommendationStore.allPromotionRecommendations).toEqual(mockPromotions)
    })

    it('allProductCombinations should return productCombinations state', () => {
      const recommendationStore = useRecommendationStore()
      const mockCombinations = [{ id: 1, name: 'Test Combo' } as any]
      recommendationStore.$state.productCombinations = mockCombinations
      
      expect(recommendationStore.allProductCombinations).toEqual(mockCombinations)
    })

    it('isLoading should return loading state', () => {
      const recommendationStore = useRecommendationStore()
      recommendationStore.loading = true
      
      expect(recommendationStore.isLoading).toBe(true)
      
      recommendationStore.loading = false
      expect(recommendationStore.isLoading).toBe(false)
    })

    it('getError should return error state', () => {
      const recommendationStore = useRecommendationStore()
      const errorMessage = 'Test Error'
      recommendationStore.error = errorMessage
      
      expect(recommendationStore.getError).toBe(errorMessage)
      
      recommendationStore.error = null
      expect(recommendationStore.getError).toBeNull()
    })
  })

  describe('actions', () => {
    describe('getPersonalizedRecommendations', () => {
      it('should fetch recommendations from API and update state', async () => {
        const recommendationStore = useRecommendationStore()
        const mockRecommendations = [{ product: { id: 1, name: 'Test Product' }, reason: 'Test', score: 0.9, tags: ['test'] } as any]
        
        mockedRecommendAPI.getRecommendations.mockResolvedValueOnce(mockRecommendations)
        
        const result = await recommendationStore.getPersonalizedRecommendations({ limit: 5 })
        
        expect(mockedRecommendAPI.getRecommendations).toHaveBeenCalledWith({ limit: 5 })
        expect(recommendationStore.recommendations).toEqual(mockRecommendations)
        expect(result).toEqual(mockRecommendations)
        expect(recommendationStore.loading).toBe(false)
        expect(recommendationStore.error).toBeNull()
      })

      it('should use mock recommendations when API call fails', async () => {
        const recommendationStore = useRecommendationStore()
        
        mockedRecommendAPI.getRecommendations.mockRejectedValueOnce(new Error('API Error'))
        
        await expect(recommendationStore.getPersonalizedRecommendations()).rejects.toThrow('API Error')
        
        expect(mockedRecommendAPI.getRecommendations).toHaveBeenCalledWith({})
        expect(recommendationStore.recommendations).toHaveLength(4) // 4 mock recommendations
        expect(recommendationStore.loading).toBe(false)
        expect(recommendationStore.error).toBe('获取推荐失败')
      })

      it('should handle empty API response', async () => {
        const recommendationStore = useRecommendationStore()
        
        mockedRecommendAPI.getRecommendations.mockResolvedValueOnce([])
        
        const result = await recommendationStore.getPersonalizedRecommendations()
        
        expect(recommendationStore.recommendations).toEqual([])
        expect(result).toEqual([])
      })
    })

    describe('getPromotionRecommendations', () => {
      it('should fetch promotion recommendations from API and update state', async () => {
        const recommendationStore = useRecommendationStore()
        const mockPromotions = [{ id: 1, title: 'Test Promotion' } as any]
        
        mockedRecommendAPI.getPromotionRecommendations.mockResolvedValueOnce(mockPromotions)
        
        const result = await recommendationStore.getPromotionRecommendations({ limit: 2 })
        
        expect(mockedRecommendAPI.getPromotionRecommendations).toHaveBeenCalledWith({ limit: 2 })
        expect(recommendationStore.$state.promotionRecommendations).toEqual(mockPromotions)
        expect(result).toEqual(mockPromotions)
        expect(recommendationStore.loading).toBe(false)
        expect(recommendationStore.error).toBeNull()
      })

      it('should use mock promotions when API call fails', async () => {
        const recommendationStore = useRecommendationStore()
        
        mockedRecommendAPI.getPromotionRecommendations.mockRejectedValueOnce(new Error('API Error'))
        
        await expect(recommendationStore.getPromotionRecommendations()).rejects.toThrow('API Error')
        
        expect(mockedRecommendAPI.getPromotionRecommendations).toHaveBeenCalledWith({})
        expect(recommendationStore.$state.promotionRecommendations).toHaveLength(2) // 2 mock promotions
        expect(recommendationStore.loading).toBe(false)
        expect(recommendationStore.error).toBe('获取促销推荐失败')
      })
    })

    describe('getProductCombinations', () => {
      it('should fetch product combinations from API and update state', async () => {
        const recommendationStore = useRecommendationStore()
        const mockCombinations = [{ id: 1, name: 'Test Combo' } as any]
        
        mockedRecommendAPI.getProductCombinations.mockResolvedValueOnce(mockCombinations)
        
        const result = await recommendationStore.getProductCombinations({ limit: 3 })
        
        expect(mockedRecommendAPI.getProductCombinations).toHaveBeenCalledWith({ limit: 3 })
        expect(recommendationStore.$state.productCombinations).toEqual(mockCombinations)
        expect(result).toEqual(mockCombinations)
        expect(recommendationStore.loading).toBe(false)
        expect(recommendationStore.error).toBeNull()
      })

      it('should use mock combinations when API call fails', async () => {
        const recommendationStore = useRecommendationStore()
        
        mockedRecommendAPI.getProductCombinations.mockRejectedValueOnce(new Error('API Error'))
        
        await expect(recommendationStore.getProductCombinations()).rejects.toThrow('API Error')
        
        expect(mockedRecommendAPI.getProductCombinations).toHaveBeenCalledWith({})
        expect(recommendationStore.$state.productCombinations).toHaveLength(2) // 2 mock combinations
        expect(recommendationStore.loading).toBe(false)
        expect(recommendationStore.error).toBe('获取产品组合推荐失败')
      })
    })

    describe('submitRecommendationFeedback', () => {
      it('should submit feedback successfully', async () => {
        const recommendationStore = useRecommendationStore()
        const mockFeedback = { userId: 1, productId: 2, feedbackType: 'like', score: 5 }
        
        mockedRecommendAPI.submitFeedback.mockResolvedValueOnce(undefined)
        
        const result = await recommendationStore.submitRecommendationFeedback(mockFeedback)
        
        expect(mockedRecommendAPI.submitFeedback).toHaveBeenCalledWith(mockFeedback)
        expect(result).toBe(true)
        expect(recommendationStore.error).toBeNull()
      })

      it('should handle feedback submission failure', async () => {
        const recommendationStore = useRecommendationStore()
        const mockFeedback = { userId: 1, productId: 2, feedbackType: 'dislike' }
        
        mockedRecommendAPI.submitFeedback.mockRejectedValueOnce(new Error('API Error'))
        
        await expect(recommendationStore.submitRecommendationFeedback(mockFeedback)).rejects.toThrow('API Error')
        
        expect(mockedRecommendAPI.submitFeedback).toHaveBeenCalledWith(mockFeedback)
        expect(recommendationStore.error).toBe('提交反馈失败')
      })
    })

    describe('mock data methods', () => {
      it('useMockRecommendations should set mock recommendation data', () => {
        const recommendationStore = useRecommendationStore()
        
        recommendationStore.useMockRecommendations()
        
        expect(recommendationStore.recommendations).toHaveLength(4)
        expect(recommendationStore.recommendations[0].product.name).toBe('经典美式咖啡')
        expect(recommendationStore.recommendations[1].product.name).toBe('拿铁咖啡')
      })

      it('useMockPromotions should set mock promotion data', () => {
        const recommendationStore = useRecommendationStore()
        
        recommendationStore.useMockPromotions()
        
        expect(recommendationStore.$state.promotionRecommendations).toHaveLength(2)
        expect(recommendationStore.$state.promotionRecommendations[0].title).toBe('新用户专享')
        expect(recommendationStore.$state.promotionRecommendations[1].title).toBe('周三咖啡日')
      })

      it('useMockCombinations should set mock product combination data', () => {
        const recommendationStore = useRecommendationStore()
        
        recommendationStore.useMockCombinations()
        
        expect(recommendationStore.$state.productCombinations).toHaveLength(2)
        expect(recommendationStore.$state.productCombinations[0].name).toBe('早餐组合')
        expect(recommendationStore.$state.productCombinations[1].name).toBe('下午茶组合')
      })
    })
  })
})
