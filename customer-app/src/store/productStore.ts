import { defineStore } from 'pinia'
import { productAPI } from '../services/api'

interface Product {
  id: number
  productNo: string
  name: string
  price: number
  description: string
  imageUrl: string
  categoryId: number
  status: number
  createdAt: string
  updatedAt: string
}

interface Category {
  id: number
  name: string
  description: string
}

interface ProductState {
  products: Product[]
  categories: Category[]
  currentProduct: Product | null
  loading: boolean
  error: string | null
}

export const useProductStore = defineStore('product', {
  state: (): ProductState => ({
    products: [],
    categories: [],
    currentProduct: null,
    loading: false,
    error: null
  }),

  getters: {
    allProducts: (state) => state.products,
    activeProducts: (state) => state.products.filter(product => product.status === 1),
    allCategories: (state) => state.categories,
    currentProductDetails: (state) => state.currentProduct
  },

  actions: {
    async fetchProducts() {
      this.loading = true
      this.error = null
      try {
        const response = await productAPI.getProducts({ page: 1, size: 100 })
        this.products = response.data || []
        return this.products
      } catch (error: any) {
        this.error = error.response?.data?.message || '获取商品列表失败'
        console.error('API call failed:', error)
        throw error
      } finally {
        this.loading = false
      }
    },

    async fetchProductById(id: number) {
      this.loading = true
      this.error = null
      try {
        const response = await productAPI.getProductDetail(id)
        this.currentProduct = response.data
        return this.currentProduct
      } catch (error: any) {
        this.error = error.response?.data?.message || '获取商品详情失败'
        console.error('API call failed:', error)
        throw error
      } finally {
        this.loading = false
      }
    },

    async fetchCategories() {
      this.loading = true
      this.error = null
      try {
        const response = await productAPI.getCategories()
        this.categories = response.data || []
        return this.categories
      } catch (error: any) {
        this.error = error.response?.data?.message || '获取分类失败'
        console.error('API call failed:', error)
        throw error
      } finally {
        this.loading = false
      }
    },

    async fetchProductsByCategory(categoryId: number) {
      this.loading = true
      this.error = null
      try {
        const response = await productAPI.getProducts({ categoryId, page: 1, size: 100 })
        this.products = response.data || []
        return this.products
      } catch (error: any) {
        this.error = error.response?.data?.message || '按分类获取商品失败'
        console.error('API call failed:', error)
        throw error
      } finally {
        this.loading = false
      }
    }
  }
})