import { defineStore } from 'pinia'
import axios from 'axios'
import { useAuthStore } from './authStore'

interface Order {
  id: number
  orderNo: string
  customerId: number
  storeId: number
  totalAmount: number
  actualAmount: number
  paymentMethod: number
  orderStatus: number
  pickupTime: string
  createdAt: string
  updatedAt: string
  remarks: string
  items?: Array<{
    productId: number
    quantity: number
    price: number
    total: number
  }>
}

interface OrderState {
  orders: Order[]
  currentOrder: Order | null
  loading: boolean
  error: string | null
}

export const useOrderStore = defineStore('order', {
  state: (): OrderState => ({
    orders: [],
    currentOrder: null,
    loading: false,
    error: null
  }),

  getters: {
    allOrders: (state) => state.orders,
    pendingOrders: (state) => state.orders.filter(order => order.orderStatus === 1),
    processingOrders: (state) => state.orders.filter(order => order.orderStatus === 2),
    completedOrders: (state) => state.orders.filter(order => order.orderStatus === 3),
    cancelledOrders: (state) => state.orders.filter(order => order.orderStatus === 4),
    currentOrderDetails: (state) => state.currentOrder
  },

  actions: {
    async createOrder(orderData: {
      storeId: number
      items: Array<{ productId: number; quantity: number }>
      paymentMethod: number
      pickupTime: string
      remarks?: string
    }) {
      const authStore = useAuthStore()
      this.loading = true
      this.error = null
      try {
        const response = await axios.post('/api/v1/orders', orderData, {
          headers: {
            Authorization: `Bearer ${authStore.token}`
          }
        })
        this.currentOrder = response.data
        return response.data
      } catch (error: any) {
        this.error = error.response?.data?.message || 'Failed to create order'
        throw error
      } finally {
        this.loading = false
      }
    },

    async fetchOrders() {
      const authStore = useAuthStore()
      this.loading = true
      this.error = null
      try {
        const response = await axios.get('/api/v1/orders', {
          headers: {
            Authorization: `Bearer ${authStore.token}`
          }
        })
        this.orders = response.data
        return response.data
      } catch (error: any) {
        this.error = error.response?.data?.message || 'Failed to fetch orders'
        throw error
      } finally {
        this.loading = false
      }
    },

    async fetchOrderById(id: number) {
      const authStore = useAuthStore()
      this.loading = true
      this.error = null
      try {
        const response = await axios.get(`/api/v1/orders/${id}`, {
          headers: {
            Authorization: `Bearer ${authStore.token}`
          }
        })
        this.currentOrder = response.data
        return response.data
      } catch (error: any) {
        this.error = error.response?.data?.message || 'Failed to fetch order details'
        throw error
      } finally {
        this.loading = false
      }
    },

    async cancelOrder(orderId: number) {
      const authStore = useAuthStore()
      this.loading = true
      this.error = null
      try {
        const response = await axios.put(`/api/v1/orders/${orderId}/status`, { status: 4 }, {
          headers: {
            Authorization: `Bearer ${authStore.token}`
          }
        })
        const index = this.orders.findIndex(order => order.id === orderId)
        if (index !== -1) {
          this.orders[index] = response.data
        }
        return response.data
      } catch (error: any) {
        this.error = error.response?.data?.message || 'Failed to cancel order'
        throw error
      } finally {
        this.loading = false
      }
    }
  }
})