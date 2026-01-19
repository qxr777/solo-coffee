
<template>
  <div class="space-y-6">
    <!-- 页面标题和筛选器 -->
    <div class="flex flex-col md:flex-row md:items-center md:justify-between gap-4">
      <div>
        <h1 class="text-2xl font-bold text-gray-900">销售分析</h1>
        <p class="text-gray-600 mt-1">深入分析销售数据，发现业务趋势和机会</p>
      </div>
      
      <div class="flex flex-col sm:flex-row gap-3">
        <div class="flex items-center space-x-2">
          <label class="text-sm font-medium text-gray-700">开始日期</label>
          <input v-model="filters.startDate" type="date" class="input-field">
        </div>
        
        <div class="flex items-center space-x-2">
          <label class="text-sm font-medium text-gray-700">结束日期</label>
          <input v-model="filters.endDate" type="date" class="input-field">
        </div>
        
        <button class="btn-primary whitespace-nowrap" @click="fetchData">
          应用筛选
        </button>
      </div>
    </div>

    <!-- 销售概览卡片 -->
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
      <div class="card">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-sm font-medium text-gray-600">总销售额</p>
            <h3 class="text-2xl font-bold text-gray-900 mt-1">¥{{ overviewStats.totalSales.toLocaleString() }}</h3>
            <p class="text-sm text-green-600 mt-2 flex items-center">
              <svg class="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 15l7-7 7 7"></path>
              </svg>
              {{ overviewStats.salesGrowth }}% 较上月
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
            <p class="text-sm font-medium text-gray-600">总订单数</p>
            <h3 class="text-2xl font-bold text-gray-900 mt-1">{{ overviewStats.orderCount.toLocaleString() }}</h3>
            <p class="text-sm text-green-600 mt-2 flex items-center">
              <svg class="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 15l7-7 7 7"></path>
              </svg>
              {{ overviewStats.orderGrowth }}% 较上月
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
            <p class="text-sm font-medium text-gray-600">平均客单价</p>
            <h3 class="text-2xl font-bold text-gray-900 mt-1">¥{{ overviewStats.averageOrderValue.toFixed(2) }}</h3>
            <p class="text-sm text-green-600 mt-2 flex items-center">
              <svg class="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 15l7-7 7 7"></path>
              </svg>
              2.8% 较上月
            </p>
          </div>
          <div class="p-3 bg-purple-100 rounded-lg">
            <svg class="w-6 h-6 text-purple-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"></path>
            </svg>
          </div>
        </div>
      </div>

      <div class="card">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-sm font-medium text-gray-600">客户总数</p>
            <h3 class="text-2xl font-bold text-gray-900 mt-1">{{ overviewStats.totalCustomers.toLocaleString() }}</h3>
            <p class="text-sm text-green-600 mt-2 flex items-center">
              <svg class="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 15l7-7 7 7"></path>
              </svg>
              2.1% 较上月
            </p>
          </div>
          <div class="p-3 bg-yellow-100 rounded-lg">
            <svg class="w-6 h-6 text-yellow-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z"></path>
            </svg>
          </div>
        </div>
      </div>
    </div>

    <!-- 销售趋势图表 -->
    <div class="card">
      <div class="flex items-center justify-between mb-6">
        <h3 class="text-lg font-semibold text-gray-900">销售趋势分析</h3>
        <div class="flex space-x-2">
          <button v-for="i in ['day', 'week', 'month']" :key="i" 
                  @click="filters.interval = i; fetchData()"
                  :class="filters.interval === i ? 'bg-blue-100 text-blue-700' : 'text-gray-700 hover:bg-gray-100'"
                  class="px-3 py-1 text-sm rounded-lg capitalize">{{ i }}</button>
        </div>
      </div>
      <div class="h-80">
        <div ref="trendChartRef" class="w-full h-full"></div>
      </div>
    </div>

    <!-- 产品表现和门店表现 -->
    <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
      <!-- 产品表现分析 -->
      <div class="card">
        <div class="flex items-center justify-between mb-6">
          <h3 class="text-lg font-semibold text-gray-900">产品表现分析</h3>
          <a href="/products" class="text-sm text-blue-600 hover:text-blue-500">查看全部产品</a>
        </div>
        <div class="h-80">
          <div ref="productChartRef" class="w-full h-full"></div>
        </div>
      </div>

      <!-- 门店表现对比 -->
      <div class="card">
        <div class="flex items-center justify-between mb-6">
          <h3 class="text-lg font-semibold text-gray-900">门店表现对比</h3>
          <a href="/stores" class="text-sm text-blue-600 hover:text-blue-500">查看全部门店</a>
        </div>
        <div class="h-80">
          <div ref="storeChartRef" class="w-full h-full"></div>
        </div>
      </div>
    </div>

    <!-- 客户行为分析和AI销售预测 -->
    <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
      <!-- 客户行为分析 -->
      <div class="card">
        <div class="flex items-center justify-between mb-6">
          <h3 class="text-lg font-semibold text-gray-900">客户行为分析</h3>
          <button class="text-sm text-blue-600 hover:text-blue-500">导出报告</button>
        </div>
        <div class="space-y-4">
          <div>
            <p class="text-sm font-medium text-gray-600 mb-2">客户购买时段分布</p>
            <div ref="customerChartRef" class="h-60"></div>
          </div>
          
          <div class="grid grid-cols-2 gap-4 mt-4">
            <div class="p-3 bg-gray-50 rounded-lg">
              <p class="text-sm text-gray-600">新客户占比</p>
              <p class="text-2xl font-bold text-gray-900 mt-1">35%</p>
              <p class="text-xs text-green-600 mt-1">+5% 较上月</p>
            </div>
            
            <div class="p-3 bg-gray-50 rounded-lg">
              <p class="text-sm text-gray-600">复购率</p>
              <p class="text-2xl font-bold text-gray-900 mt-1">42%</p>
              <p class="text-xs text-green-600 mt-1">+3% 较上月</p>
            </div>
          </div>
        </div>
      </div>

      <!-- AI销售预测 -->
      <div class="card">
        <div class="flex items-center justify-between mb-6">
          <h3 class="text-lg font-semibold text-gray-900">AI销售预测</h3>
          <div class="flex items-center space-x-2">
            <span class="text-xs bg-blue-100 text-blue-800 px-2 py-1 rounded-full">AI驱动</span>
          </div>
        </div>
        <div class="h-80">
          <div ref="forecastChartRef" class="w-full h-full"></div>
        </div>
        <div class="mt-4 p-3 bg-blue-50 rounded-lg border border-blue-100">
          <div class="flex items-start space-x-3">
            <div class="p-2 bg-blue-100 rounded-full">
              <svg class="w-4 h-4 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9.663 17h4.673M12 3v1m6.364 1.636l-.707.707M21 12h-1M4 12H3m3.343-5.657l-.707-.707m2.828 9.9a5 5 0 117.072 0l-.548.547A3.374 3.374 0 0014 18.469V19a2 2 0 11-4 0v-.531c0-.895-.356-1.754-.988-2.386l-.548-.547z"></path>
              </svg>
            </div>
            <div>
              <h4 class="font-medium text-gray-900">AI洞察</h4>
              <p class="text-sm text-gray-600 mt-1">预计未来7天销售额将增长12%，建议增加美式咖啡和拿铁的库存，特别是在上午9-11点时段。</p>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 异常检测与预警 -->
    <div class="card">
      <div class="flex items-center justify-between mb-6">
        <h3 class="text-lg font-semibold text-gray-900">异常检测与预警</h3>
        <button class="text-sm text-blue-600 hover:text-blue-500">查看全部预警</button>
      </div>
      <div class="space-y-3">
        <div class="flex items-center justify-between p-3 bg-yellow-50 border border-yellow-100 rounded-lg">
          <div class="flex items-start space-x-3">
            <div class="p-2 bg-yellow-100 rounded-full">
              <svg class="w-4 h-4 text-yellow-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-2.5L13.732 4c-.77-.833-1.732-.833-3.464 0L3.34 16.5c-.77.833.192 2.5 1.732 2.5z"></path>
              </svg>
            </div>
            <div>
              <h4 class="font-medium text-gray-900">销售额异常波动</h4>
              <p class="text-sm text-gray-600 mt-1">1月12日销售额较前一日下降25%，可能与天气变化有关</p>
            </div>
          </div>
          <span class="text-xs bg-yellow-100 text-yellow-800 px-2 py-1 rounded-full">警告</span>
        </div>

        <div class="flex items-center justify-between p-3 bg-red-50 border border-red-100 rounded-lg">
          <div class="flex items-start space-x-3">
            <div class="p-2 bg-red-100 rounded-full">
              <svg class="w-4 h-4 text-red-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path>
              </svg>
            </div>
            <div>
              <h4 class="font-medium text-gray-900">库存不足预警</h4>
              <p class="text-sm text-gray-600 mt-1">美式咖啡原料库存不足，预计2天后耗尽</p>
            </div>
          </div>
          <span class="text-xs bg-red-100 text-red-800 px-2 py-1 rounded-full">紧急</span>
        </div>

        <div class="flex items-center justify-between p-3 bg-blue-50 border border-blue-100 rounded-lg">
          <div class="flex items-start space-x-3">
            <div class="p-2 bg-blue-100 rounded-full">
              <svg class="w-4 h-4 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path>
              </svg>
            </div>
            <div>
              <h4 class="font-medium text-gray-900">销售机会</h4>
              <p class="text-sm text-gray-600 mt-1">周末促销活动期间，拿铁咖啡销量预计增长30%</p>
            </div>
          </div>
          <span class="text-xs bg-blue-100 text-blue-800 px-2 py-1 rounded-full">机会</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, reactive } from 'vue'
import * as echarts from 'echarts'
import { analyticsService } from '../../services/api'
import dayjs from 'dayjs'

const trendChartRef = ref<HTMLElement | null>(null)
const productChartRef = ref<HTMLElement | null>(null)
const storeChartRef = ref<HTMLElement | null>(null)
const customerChartRef = ref<HTMLElement | null>(null)
const forecastChartRef = ref<HTMLElement | null>(null)

let trendChart: echarts.ECharts | null = null
let productChart: echarts.ECharts | null = null
let storeChart: echarts.ECharts | null = null
let customerChart: echarts.ECharts | null = null
let forecastChart: echarts.ECharts | null = null

const filters = reactive({
  startDate: dayjs().subtract(30, 'day').format('YYYY-MM-DD'),
  endDate: dayjs().format('YYYY-MM-DD'),
  interval: 'day'
})

const overviewStats = reactive({
  totalSales: 0,
  salesGrowth: 0,
  orderCount: 0,
  orderGrowth: 0,
  averageOrderValue: 0,
  totalCustomers: 0
})

const fetchData = async () => {
  try {
    const [salesRes, trendRes, productRes, customerRes] = await Promise.all([
      analyticsService.getSalesData(filters),
      analyticsService.getSalesTrend(filters),
      analyticsService.getPopularProducts(filters),
      analyticsService.getCustomerAnalytics(filters)
    ])

    // 更新概览
    const salesData = salesRes.data
    overviewStats.totalSales = salesData.totalSales || 0
    overviewStats.orderCount = salesData.orderCount || 0
    overviewStats.averageOrderValue = salesData.averageOrderValue || 0
    
    // 从概览接口获取客户数等
    const overviewRes = await analyticsService.getOverview()
    overviewStats.totalCustomers = overviewRes.data.totalCustomers || 0
    overviewStats.salesGrowth = overviewRes.data.salesGrowth || 0
    overviewStats.orderGrowth = overviewRes.data.orderGrowth || 0

    // 更新图表
    updateTrendChart(trendRes.data)
    updateProductChart(productRes.data)
    updateCustomerChart(customerRes.data)
    updateStoreChart() // 暂无门店接口，保持 mock
    updateForecastChart() // 保持 mock
  } catch (error) {
    console.error('Fetch analytics failed:', error)
  }
}

const updateTrendChart = (data: any) => {
  if (!trendChart) return
  const labels = data.labels || []
  const sales = data.trendData?.map((d: any) => d.sales) || []
  const orders = data.trendData?.map((d: any) => d.orders) || []
  
  trendChart.setOption({
    xAxis: { data: labels },
    series: [
      { name: '销售额', data: sales },
      { name: '订单数', data: orders }
    ]
  })
}

const updateProductChart = (data: any) => {
  if (!productChart) return
  const products = data.popularProducts || []
  productChart.setOption({
    xAxis: { data: products.map((p: any) => p.product_name) },
    series: [{ data: products.map((p: any) => p.sales) }]
  })
}

const updateCustomerChart = (data: any) => {
  if (!customerChart) return
  // 简化的客户端数据解析，实际应匹配后端
  customerChart.setOption({
    series: [{
      data: [
        { value: 35, name: '9:00-11:00' },
        { value: 25, name: '11:00-13:00' },
        { value: 40, name: '其他时段' }
      ]
    }]
  })
}

const updateStoreChart = () => {
  if (!storeChart) return
  storeChart.setOption({
    series: [
      { name: '销售额', data: [45000, 38000, 32000, 28000, 25000, 22000] },
      { name: '订单数', data: [1200, 1000, 850, 750, 680, 600] }
    ]
  })
}

const updateForecastChart = () => {
  if (!forecastChart) return
  forecastChart.setOption({
    series: [
      { name: '预测销售额', data: [14300, 15200, 16500, 14800, 15800, 17200, 16800] }
    ]
  })
}

onMounted(() => {
  initCharts()
  fetchData()
})

const initCharts = () => {
  if (trendChartRef.value) {
    trendChart = echarts.init(trendChartRef.value)
    trendChart.setOption({
      tooltip: { trigger: 'axis' },
      legend: { data: ['销售额', '订单数'] },
      grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
      xAxis: { type: 'category', boundaryGap: false, data: [] },
      yAxis: [{ type: 'value', name: '销售额' }, { type: 'value', name: '订单数', position: 'right' }],
      series: [
        { name: '销售额', type: 'line', smooth: true, data: [], lineStyle: { color: '#3b82f6' }, areaStyle: { color: 'rgba(59, 130, 246, 0.1)' } },
        { name: '订单数', type: 'line', yAxisIndex: 1, smooth: true, data: [], lineStyle: { color: '#10b981' } }
      ]
    })
  }

  if (productChartRef.value) {
    productChart = echarts.init(productChartRef.value)
    productChart.setOption({
      tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
      xAxis: { type: 'category', data: [] },
      yAxis: { type: 'value' },
      series: [{ name: '销售额', type: 'bar', data: [], itemStyle: { color: '#3b82f6' } }]
    })
  }

  if (storeChartRef.value) {
    storeChart = echarts.init(storeChartRef.value)
    storeChart.setOption({
      tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
      legend: { data: ['销售额', '订单数'] },
      xAxis: { type: 'value' },
      yAxis: { type: 'category', data: ['旗舰店', '中心店', '东区店', '西区店', '南区店', '北区店'].reverse() },
      series: [
        { name: '销售额', type: 'bar', data: [], itemStyle: { color: '#3b82f6' } },
        { name: '订单数', type: 'bar', data: [], itemStyle: { color: '#10b981' } }
      ]
    })
  }

  if (customerChartRef.value) {
    customerChart = echarts.init(customerChartRef.value)
    customerChart.setOption({
      tooltip: { trigger: 'item' },
      legend: { orient: 'vertical', left: 'left' },
      series: [{ name: '购买时段', type: 'pie', radius: '60%', data: [] }]
    })
  }

  if (forecastChartRef.value) {
    forecastChart = echarts.init(forecastChartRef.value)
    forecastChart.setOption({
      tooltip: { trigger: 'axis' },
      legend: { data: ['预测销售额'] },
      xAxis: { type: 'category', data: ['T+1', 'T+2', 'T+3', 'T+4', 'T+5', 'T+6', 'T+7'] },
      yAxis: { type: 'value' },
      series: [{ name: '预测销售额', type: 'line', data: [], lineStyle: { type: 'dashed' }, areaStyle: { color: 'rgba(245, 158, 11, 0.1)' } }]
    })
  }

  window.addEventListener('resize', () => {
    trendChart?.resize()
    productChart?.resize()
    storeChart?.resize()
    customerChart?.resize()
    forecastChart?.resize()
  })
}
</script>

<style scoped>
/* 销售分析页面样式 */
</style>
