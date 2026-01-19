
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
    <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
      <!-- 订单基本信息 -->
      <div class="card lg:col-span-2">
        <div class="flex flex-col md:flex-row md:items-start md:justify-between gap-4 mb-6">
          <div>
            <h2 class="text-lg font-semibold text-gray-900">订单 #{{ order.orderNo }}</h2>
            <p class="text-sm text-gray-600 mt-1">创建时间: {{ order.createdAt }}</p>
          </div>
          
          <div>
            <span :class="getStatusClass(order.orderStatus)" class="mb-2 inline-block">
              {{ getStatusText(order.orderStatus) }}
            </span>
            <div class="mt-2">
              <button v-if="order.orderStatus === 1" class="text-sm text-green-600 hover:text-green-500 mr-3">
                确认支付
              </button>
              <button v-if="order.orderStatus === 2" class="text-sm text-blue-600 hover:text-blue-500 mr-3">
                标记完成
              </button>
              <button v-if="order.orderStatus === 3" class="text-sm text-yellow-600 hover:text-yellow-500 mr-3">
                申请退款
              </button>
              <button v-if="order.orderStatus === 1 || order.orderStatus === 2" class="text-sm text-red-600 hover:text-red-500">
                取消订单
              </button>
            </div>
          </div>
        </div>
        
        <!-- 订单商品 -->
        <div class="border-t border-gray-200 pt-4">
          <h3 class="text-md font-medium text-gray-900 mb-4">订单商品</h3>
          <div class="space-y-4">
            <div v-for="(item, index) in order.orderItems" :key="index" class="flex items-center justify-between p-3 hover:bg-gray-50 rounded-lg transition-colors">
              <div class="flex items-center space-x-3">
                <div class="w-12 h-12 bg-gray-200 rounded-lg flex items-center justify-center">
                  <svg class="w-6 h-6 text-gray-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M18,8A2,2 0 0,1 20,10V20A2,2 0 0,1 18,22H6A2,2 0 0,1 4,20V10C4,8.89 4.9,8 6,8H18M15,3V5H6C4.89,5 4,5.9 4,7V19A1,1 0 0,0 5,20H19A1,1 0 0,0 20,19V7C20,5.89 19.1,5 18,5H13V3M15,15H9V17H15V15Z"></path>
                  </svg>
                </div>
                <div>
                  <p class="font-medium text-gray-900">{{ item.productName }}</p>
                  <p class="text-sm text-gray-600 mt-1">{{ item.customizations?.length ? item.customizations.map(c => c.value).join(', ') : '标准' }}</p>
                </div>
              </div>
              <div class="text-right">
                <p class="font-medium text-gray-900">¥{{ item.unitPrice.toFixed(2) }} × {{ item.quantity }}</p>
                <p class="text-sm text-gray-600 mt-1">¥{{ (item.unitPrice * item.quantity).toFixed(2) }}</p>
              </div>
            </div>
          </div>
        </div>
        
        <!-- 订单金额 -->
        <div class="border-t border-gray-200 pt-4 mt-4">
          <h3 class="text-md font-medium text-gray-900 mb-4">订单金额</h3>
          <div class="space-y-2">
            <div class="flex justify-between text-sm">
              <span class="text-gray-600">商品总额</span>
              <span class="text-gray-900">¥{{ order.totalAmount.toFixed(2) }}</span>
            </div>
            <div class="flex justify-between text-sm">
              <span class="text-gray-600">优惠折扣</span>
              <span class="text-green-600">-¥{{ order.discountAmount.toFixed(2) }}</span>
            </div>
            <div class="flex justify-between text-sm">
              <span class="text-gray-600">配送费</span>
              <span class="text-gray-900">¥{{ order.deliveryFee.toFixed(2) }}</span>
            </div>
            <div class="flex justify-between font-medium border-t border-gray-200 pt-2 mt-2">
              <span class="text-gray-900">实付金额</span>
              <span class="text-gray-900">¥{{ order.actualAmount.toFixed(2) }}</span>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 客户信息和支付信息 -->
      <div class="space-y-6">
        <!-- 客户信息 -->
        <div class="card">
          <h3 class="text-md font-medium text-gray-900 mb-4">客户信息</h3>
          <div class="space-y-3">
            <div>
              <p class="text-sm text-gray-600">客户姓名</p>
              <p class="font-medium text-gray-900">{{ order.customerName }}</p>
            </div>
            <div>
              <p class="text-sm text-gray-600">联系电话</p>
              <p class="font-medium text-gray-900">{{ order.customerPhone }}</p>
            </div>
            <div>
              <p class="text-sm text-gray-600">电子邮箱</p>
              <p class="font-medium text-gray-900">{{ order.customerEmail || '未提供' }}</p>
            </div>
            <div>
              <p class="text-sm text-gray-600">会员等级</p>
              <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-blue-100 text-blue-800">
                {{ order.memberLevel }}
              </span>
            </div>
          </div>
        </div>
        
        <!-- 支付信息 -->
        <div class="card">
          <h3 class="text-md font-medium text-gray-900 mb-4">支付信息</h3>
          <div class="space-y-3">
            <div>
              <p class="text-sm text-gray-600">支付方式</p>
              <p class="font-medium text-gray-900">{{ getPaymentMethodText(order.paymentMethod) }}</p>
            </div>
            <div>
              <p class="text-sm text-gray-600">支付状态</p>
              <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-green-100 text-green-800">
                {{ order.paymentStatus === 1 ? '已支付' : '未支付' }}
              </span>
            </div>
            <div v-if="order.paymentStatus === 1">
              <p class="text-sm text-gray-600">支付时间</p>
              <p class="font-medium text-gray-900">{{ order.paymentTime }}</p>
            </div>
            <div v-if="order.paymentStatus === 1">
              <p class="text-sm text-gray-600">支付订单号</p>
              <p class="font-medium text-gray-900 text-sm">{{ order.paymentOrderNo }}</p>
            </div>
          </div>
        </div>
        
        <!-- 门店信息 -->
        <div class="card">
          <h3 class="text-md font-medium text-gray-900 mb-4">门店信息</h3>
          <div class="space-y-3">
            <div>
              <p class="text-sm text-gray-600">门店名称</p>
              <p class="font-medium text-gray-900">{{ order.storeName }}</p>
            </div>
            <div>
              <p class="text-sm text-gray-600">门店地址</p>
              <p class="font-medium text-gray-900 text-sm">{{ order.storeAddress }}</p>
            </div>
            <div>
              <p class="text-sm text-gray-600">联系电话</p>
              <p class="font-medium text-gray-900">{{ order.storePhone }}</p>
            </div>
          </div>
        </div>
      </div>
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
import { ref, computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const orderId = computed(() => route.params.id as string)

// 模拟订单数据
const order = ref({
  id: parseInt(orderId.value) || 1,
  orderNo: 'SO202601140001',
  customerName: '张三',
  customerPhone: '13800138000',
  customerEmail: 'zhangsan@example.com',
  memberLevel: '普通会员',
  storeName: 'Solo Coffee旗舰店',
  storeAddress: '北京市朝阳区建国路88号',
  storePhone: '010-12345678',
  totalAmount: 64.00,
  discountAmount: 5.00,
  deliveryFee: 0.00,
  actualAmount: 59.00,
  paymentMethod: 1,
  paymentStatus: 1,
  paymentTime: '2026-01-14 10:35:00',
  paymentOrderNo: 'PAY202601140001',
  orderStatus: 2,
  createdAt: '2026-01-14 10:30:00',
  processingTime: '2026-01-14 10:36:00',
  completedTime: null,
  orderItems: [
    {
      productId: 1,
      productName: '美式咖啡',
      quantity: 2,
      unitPrice: 32.00,
      customizations: [
        {
          optionName: '糖度',
          value: '少糖'
        }
      ]
    }
  ]
})

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
</script>

<style scoped>
/* 订单详情页面样式 */
</style>
