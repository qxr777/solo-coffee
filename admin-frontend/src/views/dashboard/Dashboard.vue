
<template>
  <div class="space-y-6">
    <!-- 销售概览卡片 -->
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
      <div class="card">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-sm font-medium text-gray-600">今日销售额</p>
            <h3 class="text-2xl font-bold text-gray-900 mt-1">¥12,845</h3>
            <p class="text-sm text-green-600 mt-2 flex items-center">
              <svg class="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 15l7-7 7 7"></path>
              </svg>
              12.5% 较昨日
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
            <h3 class="text-2xl font-bold text-gray-900 mt-1">245</h3>
            <p class="text-sm text-green-600 mt-2 flex items-center">
              <svg class="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 15l7-7 7 7"></path>
              </svg>
              8.3% 较昨日
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
            <h3 class="text-2xl font-bold text-gray-900 mt-1">12</h3>
            <p class="text-sm text-red-600 mt-2 flex items-center">
              <svg class="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7"></path>
              </svg>
              3 较昨日
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
            <h3 class="text-2xl font-bold text-gray-900 mt-1">12,845</h3>
            <p class="text-sm text-green-600 mt-2 flex items-center">
              <svg class="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 15l7-7 7 7"></path>
              </svg>
              2.1% 较上月
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
          <div class="flex items-center justify-between p-3 hover:bg-gray-50 rounded-lg transition-colors">
            <div>
              <p class="font-medium text-gray-900">订单 #SO202601140001</p>
              <p class="text-sm text-gray-600 mt-1">张三 · 美式咖啡 × 2</p>
              <p class="text-xs text-gray-500 mt-1">2026-01-14 10:30</p>
            </div>
            <div class="text-right">
              <p class="font-medium text-gray-900">¥64.00</p>
              <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-green-100 text-green-800">
                已完成
              </span>
            </div>
          </div>

          <div class="flex items-center justify-between p-3 hover:bg-gray-50 rounded-lg transition-colors">
            <div>
              <p class="font-medium text-gray-900">订单 #SO202601140002</p>
              <p class="text-sm text-gray-600 mt-1">李四 · 拿铁 × 1, 牛角包 × 1</p>
              <p class="text-xs text-gray-500 mt-1">2026-01-14 10:15</p>
            </div>
            <div class="text-right">
              <p class="font-medium text-gray-900">¥48.00</p>
              <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-blue-100 text-blue-800">
                制作中
              </span>
            </div>
          </div>

          <div class="flex items-center justify-between p-3 hover:bg-gray-50 rounded-lg transition-colors">
            <div>
              <p class="font-medium text-gray-900">订单 #SO202601140003</p>
              <p class="text-sm text-gray-600 mt-1">王五 · 卡布奇诺 × 1</p>
              <p class="text-xs text-gray-500 mt-1">2026-01-14 10:00</p>
            </div>
            <div class="text-right">
              <p class="font-medium text-gray-900">¥36.00</p>
              <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-yellow-100 text-yellow-800">
                待支付
              </span>
            </div>
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
          <div class="flex items-center justify-between p-3 hover:bg-gray-50 rounded-lg transition-colors">
            <div class="flex items-center space-x-3">
              <div class="w-12 h-12 bg-gray-200 rounded-lg flex items-center justify-center">
                <svg class="w-6 h-6 text-gray-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M18,8A2,2 0 0,1 20,10V20A2,2 0 0,1 18,22H6A2,2 0 0,1 4,20V10C4,8.89 4.9,8 6,8H18M15,3V5H6C4.89,5 4,5.9 4,7V19A1,1 0 0,0 5,20H19A1,1 0 0,0 20,19V7C20,5.89 19.1,5 18,5H13V3M15,15H9V17H15V15Z"></path>
                </svg>
              </div>
              <div>
                <p class="font-medium text-gray-900">美式咖啡</p>
                <p class="text-sm text-gray-600 mt-1">¥32.00</p>
              </div>
            </div>
            <div class="text-right">
              <p class="font-medium text-gray-900">128 杯</p>
              <p class="text-xs text-green-600">+12.5%</p>
            </div>
          </div>

          <div class="flex items-center justify-between p-3 hover:bg-gray-50 rounded-lg transition-colors">
            <div class="flex items-center space-x-3">
              <div class="w-12 h-12 bg-gray-200 rounded-lg flex items-center justify-center">
                <svg class="w-6 h-6 text-gray-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M18,8A2,2 0 0,1 20,10V20A2,2 0 0,1 18,22H6A2,2 0 0,1 4,20V10C4,8.89 4.9,8 6,8H18M15,3V5H6C4.89,5 4,5.9 4,7V19A1,1 0 0,0 5,20H19A1,1 0 0,0 20,19V7C20,5.89 19.1,5 18,5H13V3M15,15H9V17H15V15Z"></path>
                </svg>
              </div>
              <div>
                <p class="font-medium text-gray-900">拿铁咖啡</p>
                <p class="text-sm text-gray-600 mt-1">¥36.00</p>
              </div>
            </div>
            <div class="text-right">
              <p class="font-medium text-gray-900">96 杯</p>
              <p class="text-xs text-green-600">+8.3%</p>
            </div>
          </div>

          <div class="flex items-center justify-between p-3 hover:bg-gray-50 rounded-lg transition-colors">
            <div class="flex items-center space-x-3">
              <div class="w-12 h-12 bg-gray-200 rounded-lg flex items-center justify-center">
                <svg class="w-6 h-6 text-gray-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M18,8A2,2 0 0,1 20,10V20A2,2 0 0,1 18,22H6A2,2 0 0,1 4,20V10C4,8.89 4.9,8 6,8H18M15,3V5H6C4.89,5 4,5.9 4,7V19A1,1 0 0,0 5,20H19A1,1 0 0,0 20,19V7C20,5.89 19.1,5 18,5H13V3M15,15H9V17H15V15Z"></path>
                </svg>
              </div>
              <div>
                <p class="font-medium text-gray-900">卡布奇诺</p>
                <p class="text-sm text-gray-600 mt-1">¥36.00</p>
              </div>
            </div>
            <div class="text-right">
              <p class="font-medium text-gray-900">72 杯</p>
              <p class="text-xs text-green-600">+5.2%</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import * as echarts from 'echarts'

const chartRef = ref<HTMLElement | null>(null)

onMounted(() => {
  if (chartRef.value) {
    const chart = echarts.init(chartRef.value)
    
    const option = {
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
        data: ['1月8日', '1月9日', '1月10日', '1月11日', '1月12日', '1月13日', '1月14日']
      },
      yAxis: {
        type: 'value'
      },
      series: [
        {
          name: '销售额',
          type: 'line',
          smooth: true,
          data: [8200, 9320, 9010, 9340, 12900, 13300, 12845],
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
        }
      ]
    }
    
    chart.setOption(option)
    
    window.addEventListener('resize', () => {
      chart.resize()
    })
  }
})
</script>

<style scoped>
/* 仪表盘样式 */
</style>
