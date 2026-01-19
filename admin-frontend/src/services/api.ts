
import axios, { AxiosInstance, AxiosRequestConfig, AxiosResponse } from 'axios'
import { useUserStore } from '../stores/userStore'

// API响应类型
interface ApiResponse<T = any> {
  code: number
  message: string
  data: T
  timestamp: number
  requestId: string
}

// 创建Axios实例
const api: AxiosInstance = axios.create({
  baseURL: '/api/v1',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
api.interceptors.request.use(
  (config: AxiosRequestConfig) => {
    const userStore = useUserStore()
    const token = userStore.getToken

    if (token) {
      config.headers = config.headers || {}
      config.headers.Authorization = `Bearer ${token}`
    }

    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 响应拦截器
api.interceptors.response.use(
  (response: AxiosResponse<ApiResponse>) => {
    const { data } = response

    // 统一处理业务错误
    if (data.code !== 200) {
      return Promise.reject(new Error(data.message || '请求失败'))
    }

    return data
  },
  (error) => {
    const userStore = useUserStore()

    if (error.response) {
      const { status } = error.response

      switch (status) {
        case 401:
          // 未授权，跳转到登录页面
          userStore.logout()
          window.location.href = '/login'
          break
        case 403:
          // 禁止访问
          console.error('没有权限访问该资源')
          break
        case 404:
          // 资源不存在
          console.error('请求的资源不存在')
          break
        case 500:
          // 服务器内部错误
          console.error('服务器内部错误，请稍后重试')
          break
        default:
          console.error('请求失败')
      }
    } else if (error.request) {
      // 请求已发送但没有收到响应
      console.error('网络错误，请检查网络连接')
    } else {
      // 请求配置错误
      console.error('请求配置错误')
    }

    return Promise.reject(error)
  }
)

export default api

// API服务
export const authService = {
  login: (username: string, password: string) => {
    return api.post<ApiResponse<{ token: string; user: any }>>('/auth/login', {
      username,
      password
    })
  },

  logout: () => {
    return api.post<ApiResponse>('/auth/logout')
  },

  refreshToken: (refreshToken: string) => {
    return api.post<ApiResponse<{ token: string; refreshToken: string }>>('/auth/refresh', {
      refreshToken
    })
  }
}

export const orderService = {
  getOrders: (params?: any) => {
    return api.get<ApiResponse<{ total: number; records: any[] }>>('/orders', { params })
  },

  getOrderById: (id: number) => {
    return api.get<ApiResponse<any>>(`/orders/${id}`)
  },

  createOrder: (orderData: any) => {
    return api.post<ApiResponse<any>>('/orders', orderData)
  },

  updateOrderStatus: (id: number, status: number) => {
    return api.put<ApiResponse<any>>(`/orders/${id}/status`, { status })
  }
}

export const productService = {
  getProducts: (params?: any) => {
    return api.get<ApiResponse<{ total: number; records: any[] }>>('/products', { params })
  },

  getProductById: (id: number) => {
    return api.get<ApiResponse<any>>(`/products/${id}`)
  },

  createProduct: (productData: any) => {
    return api.post<ApiResponse<any>>('/products', productData)
  },

  updateProduct: (id: number, productData: any) => {
    return api.put<ApiResponse<any>>(`/products/${id}`, productData)
  }
}

export const inventoryService = {
  getInventory: (params?: any) => {
    return api.get<ApiResponse<{ total: number; records: any[] }>>('/inventory', { params })
  },

  getInventoryById: (id: number) => {
    return api.get<ApiResponse<any>>(`/inventory/${id}`)
  },

  getInventoryByProductId: (productId: number) => {
    return api.get<ApiResponse<any>>(`/inventory/product/${productId}`)
  },

  createInventory: (inventoryData: any) => {
    return api.post<ApiResponse<any>>('/inventory', inventoryData)
  },

  updateInventory: (id: number, inventoryData: any) => {
    return api.put<ApiResponse<any>>(`/inventory/${id}`, inventoryData)
  },

  reduceInventory: (productId: number, quantity: number) => {
    return api.post<ApiResponse<any>>('/inventory/reduce', { productId, quantity })
  },

  triggerAutoReorder: () => {
    return api.post<ApiResponse<any>>('/inventory/auto-reorder')
  },

  getLowStockItems: () => {
    return api.get<ApiResponse<any[]>>('/inventory/warning')
  }
}

export const salesService = {
  getSalesData: (params?: any) => {
    return api.get<ApiResponse<any>>('/analytics/sales', { params })
  }
}

export const customerService = {
  getCustomers: (params?: any) => {
    return api.get<ApiResponse<{ total: number; records: any[] }>>('/customers', { params })
  },

  getCustomerById: (id: number) => {
    return api.get<ApiResponse<any>>(`/customers/${id}`)
  },

  createCustomer: (customerData: any) => {
    return api.post<ApiResponse<any>>('/customers', customerData)
  },

  updateCustomer: (id: number, customerData: any) => {
    return api.put<ApiResponse<any>>(`/customers/${id}`, customerData)
  },

  addPoints: (customerId: number, pointsData: any) => {
    return api.post<ApiResponse<any>>(`/customers/${customerId}/points`, pointsData)
  },

  redeemPoints: (customerId: number, redeemData: any) => {
    return api.post<ApiResponse<any>>(`/customers/${customerId}/redeem`, redeemData)
  },

  getCustomerPoints: (customerId: number) => {
    return api.get<ApiResponse<number>>(`/customers/${customerId}/points`)
  },

  getCustomerLevel: (customerId: number) => {
    return api.get<ApiResponse<any>>(`/customers/${customerId}/level`)
  }
}
