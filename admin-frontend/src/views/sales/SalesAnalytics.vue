
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
          <input type="date" class="input-field" value="2026-01-01">
        </div>
        
        <div class="flex items-center space-x-2">
          <label class="text-sm font-medium text-gray-700">结束日期</label>
          <input type="date" class="input-field" value="2026-01-14">
        </div>
        
        <button class="btn-primary whitespace-nowrap">
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
            <h3 class="text-2xl font-bold text-gray-900 mt-1">¥156,845</h3>
            <p class="text-sm text-green-600 mt-2 flex items-center">
              <svg class="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 15l7-7 7 7"></path>
              </svg>
              8.2% 较上月
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
            <h3 class="text-2xl font-bold text-gray-900 mt-1">3,245</h3>
            <p class="text-sm text-green-600 mt-2 flex items-center">
              <svg class="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 15l7-7 7 7"></path>
              </svg>
              5.3% 较上月
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
            <h3 class="text-2xl font-bold text-gray-900 mt-1">¥48.33</h3>
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
            <p class="text-sm font-medium text-gray-600">转化率</p>
            <h3 class="text-2xl font-bold text-gray-900 mt-1">18.5%</h3>
            <p class="text-sm text-red-600 mt-2 flex items-center">
              <svg class="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7"></path>
              </svg>
              0.5% 较上月
            </p>
          </div>
          <div class="p-3 bg-yellow-100 rounded-lg">
            <svg class="w-6 h-6 text-yellow-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 7h8m0 0v8m0-8l-8 8-4-4-6 6"></path>
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
          <button class="px-3 py-1 text-sm bg-blue-100 text-blue-700 rounded-lg">日</button>
          <button class="px-3 py-1 text-sm text-gray-700 hover:bg-gray-100 rounded-lg">周</button>
          <button class="px-3 py-1 text-sm text-gray-700 hover:bg-gray-100 rounded-lg">月</button>
          <button class="px-3 py-1 text-sm text-gray-700 hover:bg-gray-100 rounded-lg">年</button>
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
import { ref, onMounted } from 'vue'
import * as echarts from 'echarts'

const trendChartRef = ref<HTMLElement | null>(null)
const productChartRef = ref<HTMLElement | null>(null)
const storeChartRef = ref<HTMLElement | null>(null)
const customerChartRef = ref<HTMLElement | null>(null)
const forecastChartRef = ref<HTMLElement | null>(null)

onMounted(() => {
  // 销售趋势图表
  if (trendChartRef.value) {
    const chart = echarts.init(trendChartRef.value)
    chart.setOption({
      tooltip: {
        trigger: 'axis'
      },
      legend: {
        data: ['销售额', '订单数']
      },
      grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true
      },
      xAxis: {
        type: 'category',
        boundaryGap: false,
        data: ['1月1日', '1月2日', '1月3日', '1月4日', '1月5日', '1月6日', '1月7日', '1月8日', '1月9日', '1月10日', '1月11日', '1月12日', '1月13日', '1月14日']
      },
      yAxis: [
        {
          type: 'value',
          name: '销售额',
          position: 'left'
        },
        {
          type: 'value',
          name: '订单数',
          position: 'right'
        }
      ],
      series: [
        {
          name: '销售额',
          type: 'line',
          smooth: true,
          data: [12000, 19000, 15000, 18000, 20000, 17000, 16000, 18200, 19320, 19010, 19340, 12900, 13300, 12845],
          lineStyle: {
            color: '#3b82f6'
          },
          areaStyle: {
            color: {
              type: 'linear',
              x: 0,
              y: 0,
              x2: 0,
              y2: 1,
              colorStops: [{
                offset: 0, color: 'rgba(59, 130, 246, 0.3)'
              }, {
                offset: 1, color: 'rgba(59, 130, 246, 0.05)'
              }]
            }
          }
        },
        {
          name: '订单数',
          type: 'line',
          yAxisIndex: 1,
          smooth: true,
          data: [320, 480, 380, 430, 480, 420, 400, 450, 480, 470, 480, 320, 330, 310],
          lineStyle: {
            color: '#10b981'
          }
        }
      ]
    })
    window.addEventListener('resize', () => chart.resize())
  }

  // 产品表现图表
  if (productChartRef.value) {
    const chart = echarts.init(productChartRef.value)
    chart.setOption({
      tooltip: {
        trigger: 'axis',
        axisPointer: {
          type: 'shadow'
        }
      },
      grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true
      },
      xAxis: {
        type: 'category',
        data: ['美式咖啡', '拿铁咖啡', '卡布奇诺', '摩卡咖啡', '焦糖玛奇朵', '抹茶拿铁']
      },
      yAxis: {
        type: 'value'
      },
      series: [
        {
          name: '销售额',
          type: 'bar',
          data: [32000, 28000, 22000, 18000, 15000, 12000],
          itemStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: '#3b82f6' },
              { offset: 1, color: '#93c5fd' }
            ])
          }
        }
      ]
    })
    window.addEventListener('resize', () => chart.resize())
  }

  // 门店表现图表
  if (storeChartRef.value) {
    const chart = echarts.init(storeChartRef.value)
    chart.setOption({
      tooltip: {
        trigger: 'axis',
        axisPointer: {
          type: 'shadow'
        }
      },
      legend: {
        data: ['销售额', '订单数']
      },
      grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true
      },
      xAxis: {
        type: 'value'
      },
      yAxis: {
        type: 'category',
        data: ['旗舰店', '中心店', '东区店', '西区店', '南区店', '北区店']
      },
      series: [
        {
          name: '销售额',
          type: 'bar',
          data: [45000, 38000, 32000, 28000, 25000, 22000],
          itemStyle: {
            color: '#3b82f6'
          }
        },
        {
          name: '订单数',
          type: 'bar',
          data: [1200, 1000, 850, 750, 680, 600],
          itemStyle: {
            color: '#10b981'
          }
        }
      ]
    })
    window.addEventListener('resize', () => chart.resize())
  }

  // 客户行为图表
  if (customerChartRef.value) {
    const chart = echarts.init(customerChartRef.value)
    chart.setOption({
      tooltip: {
        trigger: 'item'
      },
      legend: {
        orient: 'vertical',
        left: 'left'
      },
      series: [
        {
          name: '购买时段',
          type: 'pie',
          radius: '60%',
          data: [
            { value: 35, name: '9:00-11:00' },
            { value: 25, name: '11:00-13:00' },
            { value: 15, name: '13:00-15:00' },
            { value: 15, name: '15:00-17:00' },
            { value: 10, name: '17:00-19:00' }
          ],
          emphasis: {
            itemStyle: {
              shadowBlur: 10,
              shadowOffsetX: 0,
              shadowColor: 'rgba(0, 0, 0, 0.5)'
            }
          },
          itemStyle: {
            color: function(params) {
              const colors = ['#3b82f6', '#10b981', '#f59e0b', '#ef4444', '#8b5cf6']
              return colors[params.dataIndex]
            }
          }
        }
      ]
    })
    window.addEventListener('resize', () => chart.resize())
  }

  // AI销售预测图表
  if (forecastChartRef.value) {
    const chart = echarts.init(forecastChartRef.value)
    chart.setOption({
      tooltip: {
        trigger: 'axis'
      },
      legend: {
        data: ['历史销售额', '预测销售额']
      },
      grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true
      },
      xAxis: {
        type: 'category',
        data: ['1月15日', '1月16日', '1月17日', '1月18日', '1月19日', '1月20日', '1月21日']
      },
      yAxis: {
        type: 'value'
      },
      series: [
        {
          name: '历史销售额',
          type: 'line',
          data: [null, null, null, null, null, null, null],
          lineStyle: {
            color: '#3b82f6'
          }
        },
        {
          name: '预测销售额',
          type: 'line',
          data: [14300, 15200, 16500, 14800, 15800, 17200, 16800],
          lineStyle: {
            color: '#f59e0b',
            type: 'dashed'
          },
          areaStyle: {
            color: {
              type: 'linear',
              x: 0,
              y: 0,
              x2: 0,
              y2: 1,
              colorStops: [{
                offset: 0, color: 'rgba(245, 158, 11, 0.3)'
              }, {
                offset: 1, color: 'rgba(245, 158, 11, 0.05)'
              }]
            }
          }
        }
      ]
    })
    window.addEventListener('resize', () => chart.resize())
  }
})
</script>

<style scoped>
/* 销售分析页面样式 */
</style>
