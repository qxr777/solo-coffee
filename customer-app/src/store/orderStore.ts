import { defineStore } from 'pinia'
import { orderAPI } from '../services/api'

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
      this.loading = true
      this.error = null
      try {
        const response: any = await orderAPI.createOrder(orderData)
        this.currentOrder = response
        return response
      } catch (error: any) {
        this.error = error.response?.data?.message || '创建订单失败'
        throw error
      } finally {
        this.loading = false
      }
    },

    async fetchOrders() {
      this.loading = true
      this.error = null
      try {
        const response: any = await orderAPI.getOrders({ page: 1, size: 50 })
        this.orders = response
        return response
      } catch (error: any) {
        this.error = error.response?.data?.message || '获取订单列表失败'
        throw error
      } finally {
        this.loading = false
      }
    },

    async fetchOrderById(id: number) {
      this.loading = true
      this.error = null
      try {
        const response: any = await orderAPI.getOrderDetail(id)
        this.currentOrder = response
        return response
      } catch (error: any) {
        this.error = error.response?.data?.message || '获取订单详情失败'
        throw error
      } finally {
        this.loading = false
      }
    },

    async cancelOrder(orderId: number) {
      this.loading = true
      this.error = null
      try {
        const response: any = await orderAPI.updateOrderStatus(orderId, 4)
        const index = this.orders.findIndex(order => order.id === orderId)
        if (index !== -1) {
          this.orders[index] = response
        }
        return response
      } catch (error: any) {
        this.error = error.response?.data?.message || '取消订单失败'
        throw error
      } finally {
        this.loading = false
      }
    }
  }
})