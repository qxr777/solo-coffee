
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
          <label class="text-sm font-medium text-gray-700">订单状态</label>
          <select class="input-field">
            <option value="">全部状态</option>
            <option value="1">待支付</option>
            <option value="2">制作中</option>
            <option value="3">已完成</option>
            <option value="4">已取消</option>
            <option value="5">退款中</option>
            <option value="6">已退款</option>
          </select>
        </div>
        
        <div class="flex items-center space-x-2">
          <label class="text-sm font-medium text-gray-700">搜索</label>
          <input type="text" class="input-field" placeholder="订单号/客户姓名/手机号">
        </div>
        
        <button class="btn-primary whitespace-nowrap">
          搜索
        </button>
      </div>
    </div>

    <!-- 订单统计卡片 -->
    <div class="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-6 gap-4">
      <div class="p-3 bg-gray-50 rounded-lg">
        <p class="text-sm text-gray-600">全部订单</p>
        <p class="text-xl font-bold text-gray-900 mt-1">3,245</p>
      </div>
      
      <div class="p-3 bg-blue-50 rounded-lg border border-blue-100">
        <p class="text-sm text-blue-700">待支付</p>
        <p class="text-xl font-bold text-gray-900 mt-1">128</p>
      </div>
      
      <div class="p-3 bg-yellow-50 rounded-lg border border-yellow-100">
        <p class="text-sm text-yellow-700">制作中</p>
        <p class="text-xl font-bold text-gray-900 mt-1">85</p>
      </div>
      
      <div class="p-3 bg-green-50 rounded-lg border border-green-100">
        <p class="text-sm text-green-700">已完成</p>
        <p class="text-xl font-bold text-gray-900 mt-1">2,890</p>
      </div>
      
      <div class="p-3 bg-red-50 rounded-lg border border-red-100">
        <p class="text-sm text-red-700">已取消</p>
        <p class="text-xl font-bold text-gray-900 mt-1">125</p>
      </div>
      
      <div class="p-3 bg-purple-50 rounded-lg border border-purple-100">
        <p class="text-sm text-purple-700">退款中</p>
        <p class="text-xl font-bold text-gray-900 mt-1">17</p>
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
            <tr v-for="order in orders" :key="order.id" class="hover:bg-gray-50 transition-colors">
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="font-medium text-gray-900">{{ order.orderNo }}</div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="font-medium text-gray-900">{{ order.customerName }}</div>
                <div class="text-sm text-gray-500">{{ order.customerPhone }}</div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="font-medium text-gray-900">¥{{ order.totalAmount.toFixed(2) }}</div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <span :class="getStatusClass(order.orderStatus)">
                  {{ getStatusText(order.orderStatus) }}
                </span>
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                {{ order.createdAt }}
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">
                <div class="flex space-x-2">
                  <router-link :to="`/orders/${order.id}`" class="text-blue-600 hover:text-blue-500">
                    查看
                  </router-link>
                  <button v-if="order.orderStatus === 1" class="text-green-600 hover:text-green-500">
                    确认
                  </button>
                  <button v-if="order.orderStatus === 2" class="text-blue-600 hover:text-blue-500">
                    完成
                  </button>
                  <button v-if="order.orderStatus === 3" class="text-yellow-600 hover:text-yellow-500">
                    退款
                  </button>
                  <button v-if="order.orderStatus === 1 || order.orderStatus === 2" class="text-red-600 hover:text-red-500">
                    取消
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
import { ref, computed } from 'vue'

// 模拟订单数据
const orders = ref([
  {
    id: 1,
    orderNo: 'SO202601140001',
    customerName: '张三',
    customerPhone: '13800138000',
    totalAmount: 64.00,
    orderStatus: 3,
    createdAt: '2026-01-14 10:30:00'
  },
  {
    id: 2,
    orderNo: 'SO202601140002',
    customerName: '李四',
    customerPhone: '13900139000',
    totalAmount: 48.00,
    orderStatus: 2,
    createdAt: '2026-01-14 10:15:00'
  },
  {
    id: 3,
    orderNo: 'SO202601140003',
    customerName: '王五',
    customerPhone: '13700137000',
    totalAmount: 36.00,
    orderStatus: 1,
    createdAt: '2026-01-14 10:00:00'
  },
  {
    id: 4,
    orderNo: 'SO202601140004',
    customerName: '赵六',
    customerPhone: '13600136000',
    totalAmount: 88.00,
    orderStatus: 4,
    createdAt: '2026-01-14 09:45:00'
  },
  {
    id: 5,
    orderNo: 'SO202601140005',
    customerName: '孙七',
    customerPhone: '13500135000',
    totalAmount: 72.00,
    orderStatus: 5,
    createdAt: '2026-01-14 09:30:00'
  }
])

// 获取订单状态文本
const getStatusText = (status: number): string => {
  const statusMap: Record<number, string> = {
    1: '待支付',
    2: '制作中',
    3: '已完成',
    4: '已取消',
    5: '退款中',
    6: '已退款'
  }
  return statusMap[status] || '未知状态'
}

// 获取订单状态样式
const getStatusClass = (status: number): string => {
  const classMap: Record<number, string> = {
    1: 'inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-blue-100 text-blue-800',
    2: 'inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-yellow-100 text-yellow-800',
    3: 'inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-green-100 text-green-800',
    4: 'inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-red-100 text-red-800',
    5: 'inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-purple-100 text-purple-800',
    6: 'inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-gray-100 text-gray-800'
  }
  return classMap[status] || 'inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-gray-100 text-gray-800'
}
</script>

<style scoped>
/* 订单列表页面样式 */
</style>
