import { describe, it, expect, beforeEach, vi } from 'vitest'
import { createPinia, setActivePinia } from 'pinia'
import axios from 'axios'
import { useProductStore } from '../store/productStore'

// Mock axios
vi.mock('axios')
const mockedAxios = axios as any

describe('ProductStore', () => {
  beforeEach(() => {
    // Create a fresh Pinia instance for each test
    const pinia = createPinia()
    setActivePinia(pinia)
    
    // Clear all mocks
    vi.clearAllMocks()
  })

  describe('state', () => {
    it('should initialize with empty products array', () => {
      const productStore = useProductStore()
      expect(productStore.products).toEqual([])
    })

    it('should initialize with empty categories array', () => {
      const productStore = useProductStore()
      expect(productStore.categories).toEqual([])
    })

    it('should initialize with null currentProduct', () => {
      const productStore = useProductStore()
      expect(productStore.currentProduct).toBeNull()
    })

    it('should initialize with loading false', () => {
      const productStore = useProductStore()
      expect(productStore.loading).toBe(false)
    })

    it('should initialize with error null', () => {
      const productStore = useProductStore()
      expect(productStore.error).toBeNull()
    })
  })

  describe('getters', () => {
    it('allProducts should return all products', () => {
      const productStore = useProductStore()
      const mockProducts = [
        { id: 1, productNo: 'P001', name: 'Americano', price: 3.50, description: '', imageUrl: '', categoryId: 1, status: 1, createdAt: '', updatedAt: '' },
        { id: 2, productNo: 'P002', name: 'Latte', price: 4.75, description: '', imageUrl: '', categoryId: 1, status: 1, createdAt: '', updatedAt: '' }
      ]
      productStore.products = mockProducts
      
      expect(productStore.allProducts).toEqual(mockProducts)
    })

    it('activeProducts should return only active products', () => {
      const productStore = useProductStore()
      const mockProducts = [
        { id: 1, productNo: 'P001', name: 'Americano', price: 3.50, description: '', imageUrl: '', categoryId: 1, status: 1, createdAt: '', updatedAt: '' },
        { id: 2, productNo: 'P002', name: 'Latte', price: 4.75, description: '', imageUrl: '', categoryId: 1, status: 0, createdAt: '', updatedAt: '' },
        { id: 3, productNo: 'P003', name: 'Cappuccino', price: 4.50, description: '', imageUrl: '', categoryId: 1, status: 1, createdAt: '', updatedAt: '' }
      ]
      productStore.products = mockProducts
      
      expect(productStore.activeProducts).toEqual([mockProducts[0], mockProducts[2]])
    })

    it('allCategories should return all categories', () => {
      const productStore = useProductStore()
      const mockCategories = [
        { id: 1, name: 'Coffee', description: 'Hot and cold coffee drinks' },
        { id: 2, name: 'Pastries', description: 'Fresh baked goods' }
      ]
      productStore.categories = mockCategories
      
      expect(productStore.allCategories).toEqual(mockCategories)
    })

    it('currentProductDetails should return currentProduct', () => {
      const productStore = useProductStore()
      const mockProduct = { id: 1, productNo: 'P001', name: 'Americano', price: 3.50, description: '', imageUrl: '', categoryId: 1, status: 1, createdAt: '', updatedAt: '' }
      productStore.currentProduct = mockProduct
      
      expect(productStore.currentProductDetails).toEqual(mockProduct)
    })
  })

  describe('actions', () => {
    it('fetchProducts should fetch products from API and update state', async () => {
      const productStore = useProductStore()
      const mockProducts = [
        { id: 1, productNo: 'P001', name: 'Americano', price: 3.50, description: '', imageUrl: '', categoryId: 1, status: 1, createdAt: '', updatedAt: '' }
      ]
      
      mockedAxios.get.mockResolvedValueOnce({ data: mockProducts })
      
      await productStore.fetchProducts()
      
      expect(mockedAxios.get).toHaveBeenCalledWith('/api/v1/products')
      expect(productStore.products).toEqual(mockProducts)
      expect(productStore.loading).toBe(false)
      expect(productStore.error).toBeNull()
    })

    it('fetchProducts should use mock data when API call fails', async () => {
      const productStore = useProductStore()
      
      mockedAxios.get.mockRejectedValueOnce(new Error('API Error'))
      
      await productStore.fetchProducts()
      
      expect(mockedAxios.get).toHaveBeenCalledWith('/api/v1/products')
      expect(productStore.products).toHaveLength(8) // 8 mock products in the store
      expect(productStore.loading).toBe(false)
      expect(productStore.error).toBeNull()
    })

    it('fetchProductById should fetch product by ID from API and update currentProduct', async () => {
      const productStore = useProductStore()
      const mockProduct = { id: 1, productNo: 'P001', name: 'Americano', price: 3.50, description: '', imageUrl: '', categoryId: 1, status: 1, createdAt: '', updatedAt: '' }
      
      mockedAxios.get.mockResolvedValueOnce({ data: mockProduct })
      
      await productStore.fetchProductById(1)
      
      expect(mockedAxios.get).toHaveBeenCalledWith('/api/v1/products/1')
      expect(productStore.currentProduct).toEqual(mockProduct)
      expect(productStore.loading).toBe(false)
      expect(productStore.error).toBeNull()
    })

    it('fetchProductById should use mock data when API call fails', async () => {
      const productStore = useProductStore()
      
      mockedAxios.get.mockRejectedValueOnce(new Error('API Error'))
      
      await productStore.fetchProductById(1)
      
      expect(mockedAxios.get).toHaveBeenCalledWith('/api/v1/products/1')
      expect(productStore.currentProduct).toBeDefined()
      expect(productStore.currentProduct?.id).toBe(1)
      expect(productStore.loading).toBe(false)
      expect(productStore.error).toBeNull()
    })

    it('fetchProductById should return first mock product when ID not found', async () => {
      const productStore = useProductStore()
      
      mockedAxios.get.mockRejectedValueOnce(new Error('API Error'))
      
      await productStore.fetchProductById(999) // Non-existent ID
      
      expect(productStore.currentProduct).toBeDefined()
      expect(productStore.currentProduct?.id).toBe(1) // First mock product
    })

    it('fetchCategories should fetch categories from API and update state', async () => {
      const productStore = useProductStore()
      const mockCategories = [
        { id: 1, name: 'Coffee', description: 'Hot and cold coffee drinks' },
        { id: 2, name: 'Pastries', description: 'Fresh baked goods' }
      ]
      
      mockedAxios.get.mockResolvedValueOnce({ data: mockCategories })
      
      await productStore.fetchCategories()
      
      expect(mockedAxios.get).toHaveBeenCalledWith('/api/v1/categories')
      expect(productStore.categories).toEqual(mockCategories)
      expect(productStore.loading).toBe(false)
      expect(productStore.error).toBeNull()
    })

    it('fetchCategories should use mock data when API call fails', async () => {
      const productStore = useProductStore()
      
      mockedAxios.get.mockRejectedValueOnce(new Error('API Error'))
      
      await productStore.fetchCategories()
      
      expect(mockedAxios.get).toHaveBeenCalledWith('/api/v1/categories')
      expect(productStore.categories).toHaveLength(4) // 4 mock categories in the store
      expect(productStore.loading).toBe(false)
      expect(productStore.error).toBeNull()
    })

    it('fetchProductsByCategory should fetch products by category from API and update state', async () => {
      const productStore = useProductStore()
      const mockProducts = [
        { id: 1, productNo: 'P001', name: 'Americano', price: 3.50, description: '', imageUrl: '', categoryId: 1, status: 1, createdAt: '', updatedAt: '' }
      ]
      
      mockedAxios.get.mockResolvedValueOnce({ data: mockProducts })
      
      await productStore.fetchProductsByCategory(1)
      
      expect(mockedAxios.get).toHaveBeenCalledWith('/api/v1/products/category/1')
      expect(productStore.products).toEqual(mockProducts)
      expect(productStore.loading).toBe(false)
      expect(productStore.error).toBeNull()
    })

    it('fetchProductsByCategory should use filtered mock data when API call fails', async () => {
      const productStore = useProductStore()
      
      mockedAxios.get.mockRejectedValueOnce(new Error('API Error'))
      
      await productStore.fetchProductsByCategory(1)
      
      expect(mockedAxios.get).toHaveBeenCalledWith('/api/v1/products/category/1')
      // Should return only coffee products (categoryId: 1) - 5 products in mock data
      expect(productStore.products).toHaveLength(5)
      expect(productStore.products.every(p => p.categoryId === 1)).toBe(true)
      expect(productStore.loading).toBe(false)
      expect(productStore.error).toBeNull()
    })

    it('fetchProductsByCategory should handle non-existent category with empty array', async () => {
      const productStore = useProductStore()
      
      mockedAxios.get.mockRejectedValueOnce(new Error('API Error'))
      
      await productStore.fetchProductsByCategory(999) // Non-existent category
      
      expect(productStore.products).toEqual([])
      expect(productStore.loading).toBe(false)
      expect(productStore.error).toBeNull()
    })
  })
})
