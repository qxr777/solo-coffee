import axios from 'axios'

const api = axios.create({
  baseURL: (import.meta as any).env?.VITE_API_BASE_URL || 'http://localhost:8080/api/v1',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// Only add interceptors if we're not in test environment
if ((import.meta as any).env?.MODE !== 'test') {
  // Add crypto polyfill for environments that don't support it
  if (typeof crypto === 'undefined') {
    (globalThis as any).crypto = {
      randomUUID: () => Math.random().toString(36).substring(2, 15) + Math.random().toString(36).substring(2, 15)
    } as any
  }
  
  // Import authStore dynamically to avoid circular dependency
  import('../store/authStore').then(({ useAuthStore }) => {
    // 请求拦截器
    api.interceptors.request.use(
      (config) => {
        const authStore = useAuthStore()
        const token = authStore.token
        
        if (token) {
          config.headers.Authorization = `Bearer ${token}`
        }
        
        config.headers['X-Request-Id'] = crypto.randomUUID()
        config.headers['X-Timestamp'] = Date.now().toString()
        
        return config
      },
      (error) => {
        return Promise.reject(error)
      }
    )

    // 响应拦截器
    api.interceptors.response.use(
      (response) => {
        return response.data
      },
      (error) => {
        const authStore = useAuthStore()
        
        if (error.response) {
          const { status, data } = error.response
          
          switch (status) {
            case 401:
              // Token过期或无效
              authStore.logout()
              // 跳转到登录页面
              if (typeof window !== 'undefined' && window.location.pathname !== '/login') {
                window.location.href = '/login'
              }
              break
            case 403:
              console.error('权限不足:', data.message || '您没有权限访问此资源')
              break
            case 404:
              console.error('资源不存在:', data.message || '请求的资源不存在')
              break
            case 500:
              console.error('服务器错误:', data.message || '服务器内部错误，请稍后重试')
              break
            default:
              console.error('请求失败:', data.message || '网络请求失败')
          }
        } else if (error.request) {
          console.error('网络错误:', '无法连接到服务器，请检查网络连接')
        } else {
          console.error('请求错误:', error.message || '请求配置错误')
        }
        
        return Promise.reject(error)
      }
    )
  })
}

export default api

// API服务封装
export const authAPI = {
  login: (credentials: { phone: string; password: string }) => 
    api.post('/auth/login', credentials),
  
  register: (userData: { name: string; phone: string; email: string; password: string }) => 
    api.post('/auth/register', userData),
  
  logout: () => 
    api.post('/auth/logout'),
  
  sendSms: (data: { phone: string; type: number }) => 
    api.post('/auth/send-sms', data),
  
  smsLogin: (data: { phone: string; verificationCode: string }) => 
    api.post('/auth/sms-login', data),
  
  oauthLogin: (provider: string, code: string) => 
    api.post(`/auth/oauth/${provider}`, { code }),
  
  getProfile: () => 
    api.get('/auth/profile'),
  
  forgotPassword: (data: { phone: string; verificationCode: string; newPassword: string }) => 
    api.post('/auth/forgot-password', data),
  
  resetPassword: (data: { oldPassword: string; newPassword: string }) => 
    api.post('/auth/reset-password', data)
}

export const storeAPI = {
  getNearbyStores: (params: { latitude?: number; longitude?: number; radius?: number; page?: number; size?: number }) => 
    api.get('/stores/nearby', { params }),
  
  searchStores: (params: { keyword: string; latitude?: number; longitude?: number; page?: number; size?: number }) => 
    api.get('/stores/search', { params }),
  
  getStoreDetail: (storeId: number) => 
    api.get(`/stores/${storeId}`),
  
  toggleFavorite: (storeId: number, isFavorite: boolean) => 
    api.post(`/stores/${storeId}/favorite`, { isFavorite }),
  
  getFavoriteStores: (params: { page?: number; size?: number }) => 
    api.get('/stores/favorites', { params })
}

export const productAPI = {
  getProducts: (params: { page?: number; size?: number; categoryId?: number; status?: number; keyword?: string }) => 
    api.get('/products', { params }),
  
  getProductDetail: (productId: number) => 
    api.get(`/products/${productId}`),
  
  getCategories: () => 
    api.get('/categories')
}

export const orderAPI = {
  createOrder: (orderData: any) => 
    api.post('/orders', orderData),
  
  getOrders: (params: { page?: number; size?: number; startTime?: string; endTime?: string; orderStatus?: number; storeId?: number }) => 
    api.get('/orders', { params }),
  
  getOrderDetail: (orderId: number) => 
    api.get(`/orders/${orderId}`),
  
  updateOrderStatus: (orderId: number, status: number) => 
    api.put(`/orders/${orderId}/status`, { status }),
  
  payOrder: (orderId: number, paymentMethod: number, paymentChannel: string) => 
    api.post(`/orders/${orderId}/pay`, { paymentMethod, paymentChannel }),
  
  cancelOrder: (orderId: number, reason?: string) => 
    api.post(`/orders/${orderId}/cancel`, { reason })
}

export const memberAPI = {
  getMemberInfo: (memberId?: number) => 
    api.get(`/members/${memberId || 'me'}`),
  
  getPoints: (memberId?: number) => 
    api.get(`/members/${memberId || 'me'}/points`),
  
  exchangePoints: (memberId: number, points: number, productId: number) => 
    api.post(`/members/${memberId || 'me'}/points/exchange`, { points, productId }),
  
  getCoupons: (params: { storeId?: number }) => 
    api.get('/coupons/available', { params })
}

export const recommendAPI = {
  getRecommendations: (params: { customerId?: number; limit?: number; storeId?: number }) => 
    api.post('/recommend/products', params),
  
  getPromotionRecommendations: (params: { customerId?: number; limit?: number; storeId?: number }) => 
    api.post('/recommend/promotions', params),
  
  getProductCombinations: (params: { customerId?: number; limit?: number; storeId?: number }) => 
    api.post('/recommend/combinations', params),
  
  submitFeedback: (feedbackData: { userId: number; productId: number; feedbackType: string; score?: number; comment?: string }) => 
    api.post('/recommend/feedback', feedbackData)
}

export const predictionAPI = {
  getOrderPrediction: (customerId: number) => 
    api.post('/prediction/predict', { customerId }),
  
  confirmPrediction: (predictionId: number) => 
    api.post(`/prediction/${predictionId}/confirm`),
  
  getPredictions: (params: { customerId?: number; page?: number; size?: number }) => 
    api.get('/prediction/orders', { params }),
  
  cancelPrediction: (predictionId: number) => 
    api.post(`/prediction/${predictionId}/cancel`)
}

export const voiceAPI = {
  createVoiceOrder: (data: { voiceInput: string; customerId?: number }) => 
    api.post('/voice/order', data),
  
  getVoiceCommands: () => 
    api.get('/voice/commands')
}

export const reviewAPI = {
  createReview: (reviewData: { orderId: number; rating: number; comment: string; customerId: number; productReviews?: Array<{ productId: number; rating: number; comment: string }> }) => 
    api.post('/reviews', reviewData),
  
  getOrderReviews: (orderId: number) => 
    api.get(`/reviews/order/${orderId}`),
  
  getCustomerReviews: (customerId: number, params: { page?: number; size?: number }) => 
    api.get(`/reviews/customer/${customerId}`, { params }),
  
  deleteReview: (reviewId: number, customerId?: number) => 
    api.delete(`/reviews/${reviewId}`, { params: { customerId } })
}

export const addressAPI = {
  getAddresses: () => 
    api.get('/addresses'),
  
  createAddress: (addressData: any) => 
    api.post('/addresses', addressData),
  
  updateAddress: (addressId: number, addressData: any) => 
    api.put(`/addresses/${addressId}`, addressData),
  
  deleteAddress: (addressId: number) => 
    api.delete(`/addresses/${addressId}`)
}

export const notificationAPI = {
  getNotifications: (params: { page?: number; size?: number; status?: number; type?: number }) => 
    api.get('/notifications', { params }),
  
  markAsRead: (notificationId: number) => 
    api.put(`/notifications/${notificationId}/read`),
  
  markAllAsRead: () => 
    api.put('/notifications/read-all')
}
