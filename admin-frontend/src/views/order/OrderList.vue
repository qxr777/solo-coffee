
<template>
  <div class="space-y-6">
    <!-- 页面标题和筛选器 -->
    <div class="flex flex-col md:flex-row md:items-center md:justify-between gap-4">
      <div>
        <h1 class="text-2xl font-bold text-gray-900">订单管理</h1>
        <p class="text-gray-600 mt-1">管理所有订单，包括订单状态更新和退款处理</p>
      </div>
      
      <div class="flex flex-col sm:flex-row gap-3">
        <div class="flex items-center space-x-2">
          <label class="text-sm font-medium text-gray-700">状态</label>
          <select v-model="filters.status" class="input-field min-w-[120px]">
            <option value="">全部</option>
            <option value="1">待支付</option>
            <option value="2">制作中</option>
            <option value="3">已完成</option>
            <option value="4">已取消</option>
            <option value="5">已退款</option>
          </select>
        </div>
        
        <div class="flex items-center space-x-2">
          <input 
            v-model="filters.search" 
            type="text" 
            class="input-field" 
            placeholder="订单号/手机号..."
            @keyup.enter="handleSearch"
          >
        </div>
        
        <button @click="handleSearch" class="btn-primary flex items-center">
          <svg class="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"></path>
          </svg>
          搜索
        </button>
      </div>
    </div>

    <!-- 订单统计卡片 -->
    <div class="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-6 gap-4">
      <div class="p-3 bg-gray-50 rounded-lg shadow-sm border border-gray-100">
        <p class="text-xs text-gray-500 font-medium uppercase tracking-wider">全部订单</p>
        <p class="text-xl font-bold text-gray-900 mt-1">{{ stats.total }}</p>
      </div>
      
      <div class="p-3 bg-blue-50/50 rounded-lg border border-blue-100">
        <p class="text-xs text-blue-600 font-medium uppercase tracking-wider">待支付</p>
        <p class="text-xl font-bold text-gray-900 mt-1">{{ stats.pending }}</p>
      </div>
      
      <div class="p-3 bg-yellow-50/50 rounded-lg border border-yellow-100">
        <p class="text-xs text-yellow-600 font-medium uppercase tracking-wider">制作中</p>
        <p class="text-xl font-bold text-gray-900 mt-1">{{ stats.processing }}</p>
      </div>
      
      <div class="p-3 bg-green-50/50 rounded-lg border border-green-100">
        <p class="text-xs text-green-600 font-medium uppercase tracking-wider">已完成</p>
        <p class="text-xl font-bold text-gray-900 mt-1">{{ stats.completed }}</p>
      </div>
      
      <div class="p-3 bg-red-50/50 rounded-lg border border-red-100">
        <p class="text-xs text-red-600 font-medium uppercase tracking-wider">已取消</p>
        <p class="text-xl font-bold text-gray-900 mt-1">{{ stats.cancelled }}</p>
      </div>
      
      <div class="p-3 bg-purple-50/50 rounded-lg border border-purple-100">
        <p class="text-xs text-purple-600 font-medium uppercase tracking-wider">已退款</p>
        <p class="text-xl font-bold text-gray-900 mt-1">{{ stats.refunded }}</p>
      </div>
    </div>

    <!-- 订单列表 -->
    <div class="card">
      <div class="overflow-x-auto">
        <table class="min-w-full divide-y divide-gray-200">
          <thead class="bg-gray-50">
            <tr>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                订单号
              </th>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                客户信息
              </th>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                订单金额
              </th>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                订单状态
              </th>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                创建时间
              </th>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                操作
              </th>
            </tr>
          </thead>
          <tbody class="bg-white divide-y divide-gray-200">
            <tr v-if="loading">
              <td colspan="6" class="px-6 py-10 text-center">
                <div class="flex justify-center items-center space-x-2 text-gray-500">
                  <svg class="animate-spin h-5 w-5" viewBox="0 0 24 24">
                    <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4" fill="none"></circle>
                    <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                  </svg>
                  <span>加载订单中...</span>
                </div>
              </td>
            </tr>
            <tr v-else-if="orders.length === 0">
              <td colspan="6" class="px-6 py-10 text-center text-gray-500">
                暂无订单数据
              </td>
            </tr>
            <tr v-for="order in orders" :key="order.id" v-else class="hover:bg-gray-50 transition-colors">
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="font-medium text-gray-900">#{{ order.orderNo }}</div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="flex items-center">
                  <div class="w-8 h-8 rounded-full bg-blue-100 flex items-center justify-center text-blue-600 font-bold text-xs mr-3">
                    {{ (order.customerName || 'U').charAt(0) }}
                  </div>
                  <div>
                    <div class="font-medium text-gray-900">{{ order.customerName || '游客' }}</div>
                    <div class="text-xs text-gray-500">{{ order.customerPhone || '无手机号' }}</div>
                  </div>
                </div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="font-bold text-gray-900">¥{{ order.actualAmount?.toFixed(2) || order.totalAmount?.toFixed(2) }}</div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <span :class="getStatusClass(order.orderStatus)">
                  {{ getStatusText(order.orderStatus) }}
                </span>
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                {{ formatDate(order.createdAt) }}
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">
                <div class="flex items-center space-x-3">
                  <router-link :to="`/orders/${order.id}`" class="text-blue-600 hover:text-blue-900 bg-blue-50 px-2 py-1 rounded">
                    详情
                  </router-link>
                  <button 
                    v-if="order.orderStatus === 1" 
                    @click="updateStatus(order.id, 2)"
                    class="text-green-600 hover:text-green-900 bg-green-50 px-2 py-1 rounded"
                  >
                    确认
                  </button>
                  <button 
                    v-if="order.orderStatus === 2" 
                    @click="updateStatus(order.id, 3)"
                    class="text-indigo-600 hover:text-indigo-900 bg-indigo-50 px-2 py-1 rounded"
                  >
                    完成
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
      
      <!-- 分页 -->
      <div class="flex items-center justify-between px-6 py-4 border-t border-gray-200 sm:px-6">
        <div class="flex-1 flex justify-between sm:hidden">
          <a href="#" class="relative inline-flex items-center px-4 py-2 border border-gray-300 text-sm font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50">
            上一页
          </a>
          <a href="#" class="ml-3 relative inline-flex items-center px-4 py-2 border border-gray-300 text-sm font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50">
            下一页
          </a>
        </div>
        <div class="hidden sm:flex-1 sm:flex sm:items-center sm:justify-between">
          <div>
            <p class="text-sm text-gray-700">
              显示第 <span class="font-medium">1</span> 到 <span class="font-medium">10</span> 条，共 <span class="font-medium">325</span> 条记录
            </p>
          </div>
          <div>
            <nav class="relative z-0 inline-flex rounded-md shadow-sm -space-x-px" aria-label="Pagination">
              <a href="#" class="relative inline-flex items-center px-2 py-2 rounded-l-md border border-gray-300 bg-white text-sm font-medium text-gray-500 hover:bg-gray-50">
                <span class="sr-only">上一页</span>
                <svg class="h-5 w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7"></path>
                </svg>
              </a>
              <a href="#" class="relative inline-flex items-center px-4 py-2 border border-gray-300 bg-blue-50 text-sm font-medium text-blue-600">
                1
              </a>
              <a href="#" class="relative inline-flex items-center px-4 py-2 border border-gray-300 bg-white text-sm font-medium text-gray-700 hover:bg-gray-50">
                2
              </a>
              <a href="#" class="relative inline-flex items-center px-4 py-2 border border-gray-300 bg-white text-sm font-medium text-gray-700 hover:bg-gray-50">
                3
              </a>
              <span class="relative inline-flex items-center px-4 py-2 border border-gray-300 bg-white text-sm font-medium text-gray-700">
                ...
              </span>
              <a href="#" class="relative inline-flex items-center px-4 py-2 border border-gray-300 bg-white text-sm font-medium text-gray-700 hover:bg-gray-50">
                33
              </a>
              <a href="#" class="relative inline-flex items-center px-2 py-2 rounded-r-md border border-gray-300 bg-white text-sm font-medium text-gray-500 hover:bg-gray-50">
                <span class="sr-only">下一页</span>
                <svg class="h-5 w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7"></path>
                </svg>
              </a>
            </nav>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { orderService } from '../../services/api'
import dayjs from 'dayjs'

const orders = ref<any[]>([])
const loading = ref(false)
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)

const filters = ref({
  status: '',
  search: ''
})

const stats = ref({
  total: 0,
  pending: 0,
  processing: 0,
  completed: 0,
  cancelled: 0,
  refunded: 0
})

const fetchOrders = async () => {
  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      size: pageSize.value,
      orderStatus: filters.value.status || undefined,
      keyword: filters.value.search || undefined
    }
    const response = await orderService.getOrders(params)
    // 兼容后端返回的 'orders' 或 'records'
    const data = response.data as any
    orders.value = data.orders || data.records || []
    total.value = data.total || 0
    
    // 生成一些模拟统计数据，实际生产中应从后端获取
    stats.value = {
      total: total.value,
      pending: Math.floor(total.value * 0.1),
      processing: Math.floor(total.value * 0.15),
      completed: Math.floor(total.value * 0.6),
      cancelled: Math.floor(total.value * 0.1),
      refunded: Math.floor(total.value * 0.05)
    }
  } catch (error) {
    console.error('获取订单列表失败:', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchOrders()
})

watch([currentPage, filters], () => {
  fetchOrders()
}, { deep: true })

const handleSearch = () => {
  currentPage.value = 1
  fetchOrders()
}

const updateStatus = async (id: number, status: number) => {
  try {
    await orderService.updateOrderStatus(id, status)
    fetchOrders()
  } catch (error) {
    alert('操作失败: ' + error)
  }
}

// 获取订单状态文本
const getStatusText = (status: number): string => {
  const statusMap: Record<number, string> = {
    1: '待支付',
    2: '制作中',
    3: '已完成',
    4: '已取消',
    5: '已退款',
    6: '退款中'
  }
  return statusMap[status] || '未知状态'
}

// 获取订单状态样式
const getStatusClass = (status: number): string => {
  const classMap: Record<number, string> = {
    1: 'status-tag bg-blue-100 text-blue-800',
    2: 'status-tag bg-yellow-100 text-yellow-800',
    3: 'status-tag bg-green-100 text-green-800',
    4: 'status-tag bg-red-100 text-red-800',
    5: 'status-tag bg-gray-100 text-gray-800',
    6: 'status-tag bg-purple-100 text-purple-800'
  }
  return classMap[status] || 'status-tag bg-gray-100 text-gray-800'
}

const formatDate = (date: string) => {
  return dayjs(date).format('YYYY-MM-DD HH:mm:ss')
}
</script>

<style scoped>
/* 订单列表页面样式 */
</style>
