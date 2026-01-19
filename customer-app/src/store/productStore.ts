import { defineStore } from 'pinia'
import axios from 'axios'

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
        const response = await axios.get('/api/v1/products')
        this.products = response.data
        return response.data
      } catch (error: any) {
        // If API call fails, use mock data
        console.log('API call failed, using mock products')
        const mockProducts: Product[] = [
          { 
            id: 1, 
            productNo: 'P001', 
            name: 'Americano', 
            price: 3.50, 
            description: 'Classic black coffee made with espresso and hot water', 
            imageUrl: 'https://images.unsplash.com/photo-1509042239860-f550ce710b93?ixlib=rb-1.2.1&auto=format&fit=crop&w=500&q=60', 
            categoryId: 1, 
            status: 1, 
            createdAt: '2023-01-01T00:00:00Z', 
            updatedAt: '2023-01-01T00:00:00Z' 
          },
          { 
            id: 2, 
            productNo: 'P002', 
            name: 'Latte', 
            price: 4.75, 
            description: 'Espresso with steamed milk and a small layer of foam', 
            imageUrl: 'https://images.unsplash.com/photo-1541167760496-1628856ab772?ixlib=rb-1.2.1&auto=format&fit=crop&w=500&q=60', 
            categoryId: 1, 
            status: 1, 
            createdAt: '2023-01-01T00:00:00Z', 
            updatedAt: '2023-01-01T00:00:00Z' 
          },
          { 
            id: 3, 
            productNo: 'P003', 
            name: 'Cappuccino', 
            price: 4.50, 
            description: 'Equal parts espresso, steamed milk, and foam', 
            imageUrl: 'https://images.unsplash.com/photo-1559056199-5f2371125432?ixlib=rb-1.2.1&auto=format&fit=crop&w=500&q=60', 
            categoryId: 1, 
            status: 1, 
            createdAt: '2023-01-01T00:00:00Z', 
            updatedAt: '2023-01-01T00:00:00Z' 
          },
          { 
            id: 4, 
            productNo: 'P004', 
            name: 'Croissant', 
            price: 2.50, 
            description: 'Buttery, flaky pastry', 
            imageUrl: 'https://images.unsplash.com/photo-1555244162-803834f7003b?ixlib=rb-1.2.1&auto=format&fit=crop&w=500&q=60', 
            categoryId: 2, 
            status: 1, 
            createdAt: '2023-01-01T00:00:00Z', 
            updatedAt: '2023-01-01T00:00:00Z' 
          },
          { 
            id: 5, 
            productNo: 'P005', 
            name: 'Blueberry Muffin', 
            price: 3.25, 
            description: 'Fresh blueberry muffin with a crumb topping', 
            imageUrl: 'https://images.unsplash.com/photo-1590845908939-57f40a875779?ixlib=rb-1.2.1&auto=format&fit=crop&w=500&q=60', 
            categoryId: 2, 
            status: 1, 
            createdAt: '2023-01-01T00:00:00Z', 
            updatedAt: '2023-01-01T00:00:00Z' 
          },
          { 
            id: 6, 
            productNo: 'P006', 
            name: 'Vanilla Latte', 
            price: 4.75, 
            description: 'Latte with vanilla syrup', 
            imageUrl: 'https://images.unsplash.com/photo-1537725525291-634ef8e08313?ixlib=rb-1.2.1&auto=format&fit=crop&w=500&q=60', 
            categoryId: 1, 
            status: 1, 
            createdAt: '2023-01-01T00:00:00Z', 
            updatedAt: '2023-01-01T00:00:00Z' 
          },
          { 
            id: 7, 
            productNo: 'P007', 
            name: 'Caramel Macchiato', 
            price: 4.95, 
            description: 'Espresso with steamed milk, caramel syrup, and foam', 
            imageUrl: 'https://images.unsplash.com/photo-1579145893815-1a104d82c26d?ixlib=rb-1.2.1&auto=format&fit=crop&w=500&q=60', 
            categoryId: 1, 
            status: 1, 
            createdAt: '2023-01-01T00:00:00Z', 
            updatedAt: '2023-01-01T00:00:00Z' 
          },
          { 
            id: 8, 
            productNo: 'P008', 
            name: 'Chocolate Croissant', 
            price: 3.00, 
            description: 'Croissant filled with chocolate', 
            imageUrl: 'https://images.unsplash.com/photo-1586495777744-4413f21062fa?ixlib=rb-1.2.1&auto=format&fit=crop&w=500&q=60', 
            categoryId: 2, 
            status: 1, 
            createdAt: '2023-01-01T00:00:00Z', 
            updatedAt: '2023-01-01T00:00:00Z' 
          }
        ]
        this.products = mockProducts
        this.error = null
        return mockProducts
      } finally {
        this.loading = false
      }
    },

    async fetchProductById(id: number) {
      this.loading = true
      this.error = null
      try {
        const response = await axios.get(`/api/v1/products/${id}`)
        this.currentProduct = response.data
        return response.data
      } catch (error: any) {
        // If API call fails, use mock data
        console.log('API call failed, using mock data for product', id)
        const mockProducts: Product[] = [
          { 
            id: 1, 
            productNo: 'P001', 
            name: 'Americano', 
            price: 3.50, 
            description: 'Classic black coffee made with espresso and hot water', 
            imageUrl: 'https://images.unsplash.com/photo-1509042239860-f550ce710b93?ixlib=rb-1.2.1&auto=format&fit=crop&w=500&q=60', 
            categoryId: 1, 
            status: 1, 
            createdAt: '2023-01-01T00:00:00Z', 
            updatedAt: '2023-01-01T00:00:00Z' 
          },
          { 
            id: 2, 
            productNo: 'P002', 
            name: 'Latte', 
            price: 4.75, 
            description: 'Espresso with steamed milk and a small layer of foam', 
            imageUrl: 'https://images.unsplash.com/photo-1541167760496-1628856ab772?ixlib=rb-1.2.1&auto=format&fit=crop&w=500&q=60', 
            categoryId: 1, 
            status: 1, 
            createdAt: '2023-01-01T00:00:00Z', 
            updatedAt: '2023-01-01T00:00:00Z' 
          },
          { 
            id: 3, 
            productNo: 'P003', 
            name: 'Cappuccino', 
            price: 4.50, 
            description: 'Equal parts espresso, steamed milk, and foam', 
            imageUrl: 'https://images.unsplash.com/photo-1559056199-5f2371125432?ixlib=rb-1.2.1&auto=format&fit=crop&w=500&q=60', 
            categoryId: 1, 
            status: 1, 
            createdAt: '2023-01-01T00:00:00Z', 
            updatedAt: '2023-01-01T00:00:00Z' 
          },
          { 
            id: 4, 
            productNo: 'P004', 
            name: 'Croissant', 
            price: 2.50, 
            description: 'Buttery, flaky pastry', 
            imageUrl: 'https://images.unsplash.com/photo-1555244162-803834f7003b?ixlib=rb-1.2.1&auto=format&fit=crop&w=500&q=60', 
            categoryId: 2, 
            status: 1, 
            createdAt: '2023-01-01T00:00:00Z', 
            updatedAt: '2023-01-01T00:00:00Z' 
          },
          { 
            id: 5, 
            productNo: 'P005', 
            name: 'Blueberry Muffin', 
            price: 3.25, 
            description: 'Fresh blueberry muffin with a crumb topping', 
            imageUrl: 'https://images.unsplash.com/photo-1590845908939-57f40a875779?ixlib=rb-1.2.1&auto=format&fit=crop&w=500&q=60', 
            categoryId: 2, 
            status: 1, 
            createdAt: '2023-01-01T00:00:00Z', 
            updatedAt: '2023-01-01T00:00:00Z' 
          },
          { 
            id: 6, 
            productNo: 'P006', 
            name: 'Vanilla Latte', 
            price: 4.75, 
            description: 'Latte with vanilla syrup', 
            imageUrl: 'https://images.unsplash.com/photo-1537725525291-634ef8e08313?ixlib=rb-1.2.1&auto=format&fit=crop&w=500&q=60', 
            categoryId: 1, 
            status: 1, 
            createdAt: '2023-01-01T00:00:00Z', 
            updatedAt: '2023-01-01T00:00:00Z' 
          },
          { 
            id: 7, 
            productNo: 'P007', 
            name: 'Caramel Macchiato', 
            price: 4.95, 
            description: 'Espresso with steamed milk, caramel syrup, and foam', 
            imageUrl: 'https://images.unsplash.com/photo-1579145893815-1a104d82c26d?ixlib=rb-1.2.1&auto=format&fit=crop&w=500&q=60', 
            categoryId: 1, 
            status: 1, 
            createdAt: '2023-01-01T00:00:00Z', 
            updatedAt: '2023-01-01T00:00:00Z' 
          },
          { 
            id: 8, 
            productNo: 'P008', 
            name: 'Chocolate Croissant', 
            price: 3.00, 
            description: 'Croissant filled with chocolate', 
            imageUrl: 'https://images.unsplash.com/photo-1586495777744-4413f21062fa?ixlib=rb-1.2.1&auto=format&fit=crop&w=500&q=60', 
            categoryId: 2, 
            status: 1, 
            createdAt: '2023-01-01T00:00:00Z', 
            updatedAt: '2023-01-01T00:00:00Z' 
          }
        ]
        
        const mockProduct = mockProducts.find(p => p.id === id) || mockProducts[0]
        this.currentProduct = mockProduct
        this.error = null
        return mockProduct
      } finally {
        this.loading = false
      }
    },

    async fetchCategories() {
      this.loading = true
      this.error = null
      try {
        const response = await axios.get('/api/v1/categories')
        this.categories = response.data
        return response.data
      } catch (error: any) {
        // If API call fails, use mock data
        console.log('API call failed, using mock categories')
        const mockCategories: Category[] = [
          { id: 1, name: 'Coffee', description: 'Hot and cold coffee drinks' },
          { id: 2, name: 'Pastries', description: 'Fresh baked goods' },
          { id: 3, name: 'Tea', description: 'Various types of tea' },
          { id: 4, name: 'Smoothies', description: 'Fruit and vegetable smoothies' }
        ]
        this.categories = mockCategories
        this.error = null
        return mockCategories
      } finally {
        this.loading = false
      }
    },

    async fetchProductsByCategory(categoryId: number) {
      this.loading = true
      this.error = null
      try {
        const response = await axios.get(`/api/v1/products/category/${categoryId}`)
        this.products = response.data
        return response.data
      } catch (error: any) {
        // If API call fails, use mock data filtered by category
        console.log('API call failed, using mock products filtered by category', categoryId)
        const mockProducts: Product[] = [
          { 
            id: 1, 
            productNo: 'P001', 
            name: 'Americano', 
            price: 3.50, 
            description: 'Classic black coffee made with espresso and hot water', 
            imageUrl: 'https://images.unsplash.com/photo-1509042239860-f550ce710b93?ixlib=rb-1.2.1&auto=format&fit=crop&w=500&q=60', 
            categoryId: 1, 
            status: 1, 
            createdAt: '2023-01-01T00:00:00Z', 
            updatedAt: '2023-01-01T00:00:00Z' 
          },
          { 
            id: 2, 
            productNo: 'P002', 
            name: 'Latte', 
            price: 4.75, 
            description: 'Espresso with steamed milk and a small layer of foam', 
            imageUrl: 'https://images.unsplash.com/photo-1541167760496-1628856ab772?ixlib=rb-1.2.1&auto=format&fit=crop&w=500&q=60', 
            categoryId: 1, 
            status: 1, 
            createdAt: '2023-01-01T00:00:00Z', 
            updatedAt: '2023-01-01T00:00:00Z' 
          },
          { 
            id: 3, 
            productNo: 'P003', 
            name: 'Cappuccino', 
            price: 4.50, 
            description: 'Equal parts espresso, steamed milk, and foam', 
            imageUrl: 'https://images.unsplash.com/photo-1559056199-5f2371125432?ixlib=rb-1.2.1&auto=format&fit=crop&w=500&q=60', 
            categoryId: 1, 
            status: 1, 
            createdAt: '2023-01-01T00:00:00Z', 
            updatedAt: '2023-01-01T00:00:00Z' 
          },
          { 
            id: 4, 
            productNo: 'P004', 
            name: 'Croissant', 
            price: 2.50, 
            description: 'Buttery, flaky pastry', 
            imageUrl: 'https://images.unsplash.com/photo-1555244162-803834f7003b?ixlib=rb-1.2.1&auto=format&fit=crop&w=500&q=60', 
            categoryId: 2, 
            status: 1, 
            createdAt: '2023-01-01T00:00:00Z', 
            updatedAt: '2023-01-01T00:00:00Z' 
          },
          { 
            id: 5, 
            productNo: 'P005', 
            name: 'Blueberry Muffin', 
            price: 3.25, 
            description: 'Fresh blueberry muffin with a crumb topping', 
            imageUrl: 'https://images.unsplash.com/photo-1590845908939-57f40a875779?ixlib=rb-1.2.1&auto=format&fit=crop&w=500&q=60', 
            categoryId: 2, 
            status: 1, 
            createdAt: '2023-01-01T00:00:00Z', 
            updatedAt: '2023-01-01T00:00:00Z' 
          },
          { 
            id: 6, 
            productNo: 'P006', 
            name: 'Vanilla Latte', 
            price: 4.75, 
            description: 'Latte with vanilla syrup', 
            imageUrl: 'https://images.unsplash.com/photo-1537725525291-634ef8e08313?ixlib=rb-1.2.1&auto=format&fit=crop&w=500&q=60', 
            categoryId: 1, 
            status: 1, 
            createdAt: '2023-01-01T00:00:00Z', 
            updatedAt: '2023-01-01T00:00:00Z' 
          },
          { 
            id: 7, 
            productNo: 'P007', 
            name: 'Caramel Macchiato', 
            price: 4.95, 
            description: 'Espresso with steamed milk, caramel syrup, and foam', 
            imageUrl: 'https://images.unsplash.com/photo-1579145893815-1a104d82c26d?ixlib=rb-1.2.1&auto=format&fit=crop&w=500&q=60', 
            categoryId: 1, 
            status: 1, 
            createdAt: '2023-01-01T00:00:00Z', 
            updatedAt: '2023-01-01T00:00:00Z' 
          },
          { 
            id: 8, 
            productNo: 'P008', 
            name: 'Chocolate Croissant', 
            price: 3.00, 
            description: 'Croissant filled with chocolate', 
            imageUrl: 'https://images.unsplash.com/photo-1586495777744-4413f21062fa?ixlib=rb-1.2.1&auto=format&fit=crop&w=500&q=60', 
            categoryId: 2, 
            status: 1, 
            createdAt: '2023-01-01T00:00:00Z', 
            updatedAt: '2023-01-01T00:00:00Z' 
          }
        ]
        
        const filteredProducts = mockProducts.filter(p => p.categoryId === categoryId)
        this.products = filteredProducts
        this.error = null
        return filteredProducts
      } finally {
        this.loading = false
      }
    }
  }
})