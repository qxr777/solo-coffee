
<template>
  <div class="space-y-6">
    <!-- 页面标题和操作 -->
    <div class="flex flex-col md:flex-row md:items-center md:justify-between gap-4">
      <div>
        <h1 class="text-2xl font-bold text-gray-900">订单详情</h1>
        <p class="text-gray-600 mt-1">查看和管理订单的详细信息</p>
      </div>
      
      <div class="flex space-x-2">
        <button class="btn-secondary whitespace-nowrap">
          <svg class="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 10v6m0 0l-3-3m3 3l3-3m2 8H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"></path>
          </svg>
          导出订单
        </button>
        
        <button class="btn-primary whitespace-nowrap">
          <svg class="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7H5a2 2 0 00-2 2v9a2 2 0 002 2h14a2 2 0 002-2V9a2 2 0 00-2-2h-3m-1 4l-3 3m0 0l-3-3m3 3V4"></path>
          </svg>
          打印订单
        </button>
      </div>
    </div>

    <!-- 订单状态和基本信息 -->
    <div v-if="loading" class="flex justify-center items-center py-20">
      <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
    </div>
    
    <div v-else-if="order" class="grid grid-cols-1 lg:grid-cols-3 gap-6 animate-fade-in">
      <!-- 订单基本信息 -->
      <div class="card lg:col-span-2">
        <div class="flex flex-col md:flex-row md:items-start md:justify-between gap-4 mb-6">
          <div>
            <h2 class="text-xl font-bold text-gray-900">订单 #{{ order.orderNo }}</h2>
            <p class="text-sm text-gray-500 mt-1 flex items-center">
              <svg class="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"></path>
              </svg>
              创建于: {{ formatDate(order.createdAt) }}
            </p>
          </div>
          
          <div class="flex flex-col items-end">
            <span :class="getStatusClass(order.orderStatus)" class="mb-3 px-3 py-1 text-sm font-semibold rounded-full">
              {{ getStatusText(order.orderStatus) }}
            </span>
            <div class="flex space-x-2">
              <button v-if="order.orderStatus === 1" @click="updateStatus(2)" class="text-sm bg-green-50 text-green-700 hover:bg-green-100 px-3 py-1.5 rounded-lg font-medium transition-colors">
                确认支付
              </button>
              <button v-if="order.orderStatus === 2" @click="updateStatus(3)" class="text-sm bg-blue-50 text-blue-700 hover:bg-blue-100 px-3 py-1.5 rounded-lg font-medium transition-colors">
                标记完成
              </button>
              <button v-if="order.orderStatus <= 2" @click="updateStatus(4)" class="text-sm bg-red-50 text-red-700 hover:bg-red-100 px-3 py-1.5 rounded-lg font-medium transition-colors">
                取消订单
              </button>
            </div>
          </div>
        </div>
        
        <!-- 订单商品 -->
        <div class="border-t border-gray-100 pt-6">
          <h3 class="text-lg font-bold text-gray-900 mb-4">订单商品</h3>
          <div class="space-y-4">
            <div v-for="(item, index) in order.orderItems" :key="index" class="flex items-center justify-between p-4 bg-gray-50/50 rounded-xl border border-gray-100 hover:border-blue-200 transition-all">
              <div class="flex items-center space-x-4">
                <div class="w-14 h-14 bg-white rounded-xl shadow-sm flex items-center justify-center border border-gray-100">
                  <svg class="w-8 h-8 text-blue-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M18.364 18.364A9 9 0 005.636 5.636m12.728 12.728A9 9 0 015.636 5.636m12.728 12.728L5.636 5.636"></path>
                  </svg>
                </div>
                <div>
                  <p class="font-bold text-gray-900">{{ item.productName }}</p>
                  <p class="text-xs text-gray-500 mt-1 bg-white px-2 py-0.5 rounded border border-gray-100 inline-block">
                    {{ item.customizations?.length ? item.customizations.map(c => c.value).join(', ') : '标准配置' }}
                  </p>
                </div>
              </div>
              <div class="text-right">
                <p class="font-bold text-gray-900">¥{{ item.unitPrice.toFixed(2) }} × {{ item.quantity }}</p>
                <p class="text-sm text-blue-600 font-medium">¥{{ (item.unitPrice * item.quantity).toFixed(2) }}</p>
              </div>
            </div>
          </div>
        </div>
        
        <!-- 订单金额 -->
        <div class="border-t border-gray-100 pt-6 mt-6">
          <div class="bg-gray-50 p-6 rounded-2xl">
            <div class="space-y-3">
              <div class="flex justify-between text-sm">
                <span class="text-gray-500">商品总额</span>
                <span class="text-gray-900 font-medium">¥{{ order.totalAmount?.toFixed(2) }}</span>
              </div>
              <div class="flex justify-between text-sm">
                <span class="text-gray-500">优惠折扣</span>
                <span class="text-red-500 font-medium">-¥{{ (order.discountAmount || 0).toFixed(2) }}</span>
              </div>
              <div class="flex justify-between text-sm">
                <span class="text-gray-500">配送费用</span>
                <span class="text-gray-900 font-medium">¥{{ (order.deliveryFee || 0).toFixed(2) }}</span>
              </div>
              <div class="flex justify-between text-lg font-bold border-t border-gray-200 pt-4 mt-4">
                <span class="text-gray-900">实付总计</span>
                <span class="text-blue-600">¥{{ (order.actualAmount || order.totalAmount).toFixed(2) }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 客户信息和支付信息 -->
      <div class="space-y-6">
        <!-- 客户信息 -->
        <div class="card bg-white border border-gray-100 shadow-sm rounded-2xl p-6">
          <div class="flex items-center mb-4 space-x-2">
            <div class="p-1.5 bg-blue-50 rounded-lg text-blue-600">
              <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"></path>
              </svg>
            </div>
            <h3 class="text-lg font-bold text-gray-900">客户信息</h3>
          </div>
          <div class="space-y-4">
            <div class="flex justify-between items-center bg-gray-50 p-3 rounded-xl">
              <span class="text-sm text-gray-500">客户姓名</span>
              <span class="font-bold text-gray-900">{{ order.customerName || '匿名游客' }}</span>
            </div>
            <div class="flex justify-between items-center bg-gray-50 p-3 rounded-xl">
              <span class="text-sm text-gray-500">联系电话</span>
              <span class="font-bold text-gray-900">{{ order.customerPhone || '未绑定' }}</span>
            </div>
            <div class="flex justify-between items-center bg-gray-50 p-3 rounded-xl">
              <span class="text-sm text-gray-500">会员等级</span>
              <span class="px-2 py-0.5 bg-blue-100 text-blue-700 text-xs font-bold rounded-lg uppercase">{{ order.memberLevel || 'V0' }}</span>
            </div>
          </div>
        </div>
        
        <!-- 支付信息 -->
        <div class="card bg-white border border-gray-100 shadow-sm rounded-2xl p-6">
          <div class="flex items-center mb-4 space-x-2">
            <div class="p-1.5 bg-green-50 rounded-lg text-green-600">
              <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 10h18M7 15h1m4 0h1m-7 4h12a3 3 0 003-3V8a3 3 0 00-3-3H6a3 3 0 00-3 3v8a3 3 0 003 3z"></path>
              </svg>
            </div>
            <h3 class="text-lg font-bold text-gray-900">支付信息</h3>
          </div>
          <div class="space-y-4">
            <div class="flex justify-between items-center bg-gray-50 p-3 rounded-xl">
              <span class="text-sm text-gray-500">支付工具</span>
              <span class="font-bold text-gray-900">{{ getPaymentMethodText(order.paymentMethod) }}</span>
            </div>
            <div class="flex justify-between items-center bg-gray-50 p-3 rounded-xl">
              <span class="text-sm text-gray-500">支付状态</span>
              <span :class="order.paymentStatus === 1 ? 'text-green-600' : 'text-red-500'" class="font-bold">
                {{ order.paymentStatus === 1 ? '支付成功' : '待支付' }}
              </span>
            </div>
            <div v-if="order.paymentTime" class="flex justify-between items-center bg-gray-50 p-3 rounded-xl">
              <span class="text-sm text-gray-500">支付时间</span>
              <span class="text-xs font-bold text-gray-900">{{ formatDate(order.paymentTime) }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div v-else class="text-center py-20 bg-white rounded-2xl border border-dashed border-gray-300">
      <p class="text-gray-500 text-lg">无法找到该订单的信息</p>
      <router-link to="/orders" class="mt-4 inline-block text-blue-600 hover:underline">返回订单列表</router-link>
    </div>

    <!-- 订单日志 -->
    <div class="card">
      <h3 class="text-md font-medium text-gray-900 mb-4">订单日志</h3>
      <div class="space-y-4">
        <div class="flex">
          <div class="flex-shrink-0 mr-4">
            <div class="w-8 h-8 bg-green-100 rounded-full flex items-center justify-center">
              <svg class="w-4 h-4 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"></path>
              </svg>
            </div>
            <div class="h-full w-0.5 bg-gray-200 ml-4 mt-1"></div>
          </div>
          <div>
            <p class="font-medium text-gray-900">订单创建</p>
            <p class="text-sm text-gray-600 mt-1">订单 #{{ order.orderNo }} 已创建</p>
            <p class="text-xs text-gray-500 mt-2">{{ order.createdAt }}</p>
          </div>
        </div>
        
        <div class="flex">
          <div class="flex-shrink-0 mr-4">
            <div class="w-8 h-8 bg-green-100 rounded-full flex items-center justify-center">
              <svg class="w-4 h-4 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"></path>
              </svg>
            </div>
            <div class="h-full w-0.5 bg-gray-200 ml-4 mt-1"></div>
          </div>
          <div>
            <p class="font-medium text-gray-900">支付成功</p>
            <p class="text-sm text-gray-600 mt-1">订单支付成功，支付方式：{{ getPaymentMethodText(order.paymentMethod) }}</p>
            <p class="text-xs text-gray-500 mt-2">{{ order.paymentTime }}</p>
          </div>
        </div>
        
        <div class="flex">
          <div class="flex-shrink-0 mr-4">
            <div class="w-8 h-8 bg-green-100 rounded-full flex items-center justify-center">
              <svg class="w-4 h-4 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"></path>
              </svg>
            </div>
            <div class="h-full w-0.5 bg-gray-200 ml-4 mt-1"></div>
          </div>
          <div>
            <p class="font-medium text-gray-900">开始制作</p>
            <p class="text-sm text-gray-600 mt-1">订单开始制作</p>
            <p class="text-xs text-gray-500 mt-2">{{ order.processingTime }}</p>
          </div>
        </div>
        
        <div v-if="order.orderStatus >= 3" class="flex">
          <div class="flex-shrink-0 mr-4">
            <div class="w-8 h-8 bg-green-100 rounded-full flex items-center justify-center">
              <svg class="w-4 h-4 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"></path>
              </svg>
            </div>
          </div>
          <div>
            <p class="font-medium text-gray-900">订单完成</p>
            <p class="text-sm text-gray-600 mt-1">订单制作完成，已通知客户取餐</p>
            <p class="text-xs text-gray-500 mt-2">{{ order.completedTime }}</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { orderService } from '../../services/api'
import dayjs from 'dayjs'

const route = useRoute()
const orderId = computed(() => route.params.id as string)
const loading = ref(true)

// 订单数据
const order = ref<any>(null)

const fetchOrderDetail = async () => {
  loading.value = true
  try {
    const response = await orderService.getOrderById(parseInt(orderId.value))
    order.value = response.data
  } catch (error) {
    console.error('获取订单详情失败:', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchOrderDetail()
})

const updateStatus = async (status: number) => {
  try {
    await orderService.updateOrderStatus(order.value.id, status)
    fetchOrderDetail()
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
    1: 'inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-blue-100 text-blue-800',
    2: 'inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-yellow-100 text-yellow-800',
    3: 'inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-green-100 text-green-800',
    4: 'inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-red-100 text-red-800',
    5: 'inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-gray-100 text-gray-800',
    6: 'inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-purple-100 text-purple-800'
  }
  return classMap[status] || 'inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-gray-100 text-gray-800'
}

// 获取支付方式文本
const getPaymentMethodText = (method: number): string => {
  const methodMap: Record<number, string> = {
    1: '微信支付',
    2: '支付宝',
    3: '现金支付',
    4: '银行卡支付'
  }
  return methodMap[method] || '其他支付方式'
}

const formatDate = (date: string) => {
  if (!date) return '-'
  return dayjs(date).format('YYYY-MM-DD HH:mm:ss')
}
</script>

<style scoped>
/* 订单详情页面样式 */
</style>
