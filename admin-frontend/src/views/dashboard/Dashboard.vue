
<template>
  <div class="space-y-6">
    <!-- 销售概览卡片 -->
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
      <div class="card">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-sm font-medium text-gray-600">今日销售额</p>
            <h3 class="text-2xl font-bold text-gray-900 mt-1">¥{{ stats.todaySales.toLocaleString() }}</h3>
            <p class="text-sm text-green-600 mt-2 flex items-center">
              <svg class="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 15l7-7 7 7"></path>
              </svg>
              {{ stats.salesGrowth }}% 较昨日
            </p>
          </div>
          <div class="p-3 bg-blue-100 rounded-lg">
            <svg class="w-6 h-6 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path>
            </svg>
          </div>
        </div>
      </div>

      <div class="card">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-sm font-medium text-gray-600">今日订单数</p>
            <h3 class="text-2xl font-bold text-gray-900 mt-1">{{ stats.todayOrderCount }}</h3>
            <p class="text-sm text-green-600 mt-2 flex items-center">
              <svg class="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 15l7-7 7 7"></path>
              </svg>
              {{ stats.orderGrowth }}% 较昨日
            </p>
          </div>
          <div class="p-3 bg-green-100 rounded-lg">
            <svg class="w-6 h-6 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2"></path>
            </svg>
          </div>
        </div>
      </div>

      <div class="card">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-sm font-medium text-gray-600">库存预警</p>
            <h3 class="text-2xl font-bold text-gray-900 mt-1">{{ stats.lowStockCount }}</h3>
            <p :class="stats.inventoryGrowth > 0 ? 'text-red-600' : 'text-green-600'" class="text-sm mt-2 flex items-center">
              <svg class="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path v-if="stats.inventoryGrowth > 0" stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 15l7-7 7 7"></path>
                <path v-else stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7"></path>
              </svg>
              {{ Math.abs(stats.inventoryGrowth) }} 较昨日
            </p>
          </div>
          <div class="p-3 bg-yellow-100 rounded-lg">
            <svg class="w-6 h-6 text-yellow-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-2.5L13.732 4c-.77-.833-1.732-.833-3.464 0L3.34 16.5c-.77.833.192 2.5 1.732 2.5z"></path>
            </svg>
          </div>
        </div>
      </div>

      <div class="card">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-sm font-medium text-gray-600">会员总数</p>
            <h3 class="text-2xl font-bold text-gray-900 mt-1">{{ stats.totalCustomers.toLocaleString() }}</h3>
            <p class="text-sm text-green-600 mt-2 flex items-center">
              <svg class="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 15l7-7 7 7"></path>
              </svg>
              {{ stats.customerGrowth }}% 较上月
            </p>
          </div>
          <div class="p-3 bg-purple-100 rounded-lg">
            <svg class="w-6 h-6 text-purple-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"></path>
            </svg>
          </div>
        </div>
      </div>
    </div>

    <!-- 销售趋势图表 -->
    <div class="card">
      <div class="flex items-center justify-between mb-6">
        <h3 class="text-lg font-semibold text-gray-900">销售趋势</h3>
        <div class="flex space-x-2">
          <button class="px-3 py-1 text-sm bg-blue-100 text-blue-700 rounded-lg">日</button>
          <button class="px-3 py-1 text-sm text-gray-700 hover:bg-gray-100 rounded-lg">周</button>
          <button class="px-3 py-1 text-sm text-gray-700 hover:bg-gray-100 rounded-lg">月</button>
        </div>
      </div>
      <div class="h-80">
        <div ref="chartRef" class="w-full h-full"></div>
      </div>
    </div>

    <!-- 最近订单和热销商品 -->
    <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
      <!-- 最近订单 -->
      <div class="card">
        <div class="flex items-center justify-between mb-6">
          <h3 class="text-lg font-semibold text-gray-900">最近订单</h3>
          <a href="/orders" class="text-sm text-blue-600 hover:text-blue-500">查看全部</a>
        </div>
        <div class="space-y-4">
          <div v-for="order in recentOrders" :key="order.id" class="flex items-center justify-between p-3 hover:bg-gray-50 rounded-lg transition-colors">
            <div>
              <p class="font-medium text-gray-900">订单 #{{ order.orderNo }}</p>
              <p class="text-sm text-gray-600 mt-1">
                {{ order.customerName || '匿名客户' }} · 
                <span v-for="(item, index) in order.orderItems" :key="index">
                  {{ item.productName }} × {{ item.quantity }}{{ index < order.orderItems.length - 1 ? ', ' : '' }}
                </span>
              </p>
              <p class="text-xs text-gray-500 mt-1">{{ formatDate(order.createdAt) }}</p>
            </div>
            <div class="text-right">
              <p class="font-medium text-gray-900">¥{{ order.actualAmount?.toFixed(2) || '0.00' }}</p>
              <span :class="getStatusClass(order.orderStatus)" class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium">
                {{ getStatusName(order.orderStatus) }}
              </span>
            </div>
          </div>
          <div v-if="recentOrders.length === 0" class="text-center py-8 text-gray-500">
            暂无最近订单
          </div>
        </div>
      </div>

      <!-- 热销商品 -->
      <div class="card">
        <div class="flex items-center justify-between mb-6">
          <h3 class="text-lg font-semibold text-gray-900">热销商品</h3>
          <a href="/products" class="text-sm text-blue-600 hover:text-blue-500">查看全部</a>
        </div>
        <div class="space-y-4">
          <div v-for="item in popularProducts" :key="item.id" class="flex items-center justify-between p-3 hover:bg-gray-50 rounded-lg transition-colors">
            <div class="flex items-center space-x-3">
              <div class="w-12 h-12 bg-gray-200 rounded-lg flex items-center justify-center overflow-hidden">
                <img v-if="item.imageUrl" :src="item.imageUrl" class="w-full h-full object-cover">
                <svg v-else class="w-6 h-6 text-gray-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M18,8A2,2 0 0,1 20,10V20A2,2 0 0,1 18,22H6A2,2 0 0,1 4,20V10C4,8.89 4.9,8 6,8H18M15,3V5H6C4.89,5 4,5.9 4,7V19A1,1 0 0,0 5,20H19A1,1 0 0,0 20,19V7C20,5.89 19.1,5 18,5H13V3M15,15H9V17H15V15Z"></path>
                </svg>
              </div>
              <div>
                <p class="font-medium text-gray-900">{{ item.name }}</p>
                <p class="text-sm text-gray-600 mt-1">¥{{ item.price?.toFixed(2) || '0.00' }}</p>
              </div>
            </div>
            <div class="text-right">
              <p class="font-medium text-gray-900">{{ item.salesCount || 0 }} 杯</p>
              <p class="text-xs text-green-600">+{{ item.growth || 0 }}%</p>
            </div>
          </div>
          <div v-if="popularProducts.length === 0" class="text-center py-8 text-gray-500">
            暂无热销商品
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import * as echarts from 'echarts'
import { analyticsService, orderService, productService } from '../../services/api'
import dayjs from 'dayjs'

const chartRef = ref<HTMLElement | null>(null)
let chart: echarts.ECharts | null = null

const stats = ref({
  todaySales: 0,
  salesGrowth: 0,
  todayOrderCount: 0,
  orderGrowth: 0,
  lowStockCount: 0,
  inventoryGrowth: 0,
  totalCustomers: 0,
  customerGrowth: 0
})

const recentOrders = ref<any[]>([])
const popularProducts = ref<any[]>([])

const fetchDashboardData = async () => {
  try {
    // 1. 获取概览统计
    const statsRes = await analyticsService.getOverview()
    stats.value = statsRes.data

    // 2. 获取销售趋势 (默认日)
    const trendRes = await analyticsService.getSalesTrend({ interval: 'day' })
    updateChart(trendRes.data)

    // 3. 获取最近订单 (取前5条)
    const ordersRes = await orderService.getOrders({ size: 5 })
    recentOrders.value = ordersRes.data.orders || ordersRes.data.records || []

    // 4. 获取热销商品 (模拟获取前3条商品作为热销，真实应由专门API返回)
    const productsRes = await productService.getProducts({ size: 3 })
    popularProducts.value = productsRes.data.records || []
  } catch (error) {
    console.error('获取仪表盘数据失败:', error)
  }
}

const updateChart = (data: any) => {
  if (!chart && chartRef.value) {
    chart = echarts.init(chartRef.value)
  }
  
  if (!chart) return

  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' }
    },
    grid: {
      left: '3%', right: '4%', bottom: '3%', containLabel: true
    },
    xAxis: {
      type: 'category',
      data: data.labels || ['1月15日', '1月16日', '1月17日', '1月18日', '1月19日']
    },
    yAxis: { type: 'value' },
    series: [
      {
        name: '销售额',
        type: 'line',
        smooth: true,
        data: data.datasets?.[0]?.data || [0, 0, 0, 0, 0],
        lineStyle: { color: '#3b82f6' },
        areaStyle: {
          color: {
            type: 'linear', x: 0, y: 0, x2: 0, y2: 1,
            colorStops: [
              { offset: 0, color: 'rgba(59, 130, 246, 0.3)' },
              { offset: 1, color: 'rgba(59, 130, 246, 0.05)' }
            ]
          }
        }
      }
    ]
  }
  chart.setOption(option)
}

const formatDate = (date: string) => dayjs(date).format('YYYY-MM-DD HH:mm')

const getStatusName = (status: number) => {
  const statuses: any = { 0: '待支付', 1: '制作中', 2: '制作完成', 3: '已完成', 4: '已评价', 5: '已取消', 6: '退款中', 7: '已退款' }
  return statuses[status] || '未知'
}

const getStatusClass = (status: number) => {
  if (status === 3) return 'bg-green-100 text-green-800'
  if (status === 1 || status === 2) return 'bg-blue-100 text-blue-800'
  if (status === 0) return 'bg-yellow-100 text-yellow-800'
  return 'bg-gray-100 text-gray-800'
}

onMounted(() => {
  fetchDashboardData()
  window.addEventListener('resize', () => chart?.resize())
})
</script>

<style scoped>
/* 仪表盘样式 */
</style>
