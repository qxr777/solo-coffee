<template>
  <div class="space-y-6">
    <!-- 页面标题和操作 -->
    <div class="flex flex-col md:flex-row md:items-center md:justify-between gap-4">
      <div>
        <h1 class="text-2xl font-bold text-gray-900">库存管理</h1>
        <p class="text-gray-600 mt-1">管理所有商品的库存信息</p>
      </div>
      
      <div class="flex space-x-2">
        <button @click="triggerAutoReorder" class="btn-secondary whitespace-nowrap">
          <svg class="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15"></path>
          </svg>
          自动补货
        </button>
        
        <button class="btn-primary whitespace-nowrap">
          <svg class="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6"></path>
          </svg>
          添加库存
        </button>
      </div>
    </div>
    
    <!-- 库存筛选 -->
    <div class="card">
      <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">
            商品名称
          </label>
          <input type="text" class="input-field" placeholder="搜索商品名称">
        </div>
        
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">
            库存状态
          </label>
          <select class="input-field">
            <option value="">全部状态</option>
            <option value="normal">库存正常</option>
            <option value="warning">库存预警</option>
            <option value="empty">库存不足</option>
          </select>
        </div>
        
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">
            搜索
          </label>
          <div class="flex space-x-2">
            <input type="text" class="input-field flex-1" placeholder="搜索商品编号/名称">
            <button class="btn-primary whitespace-nowrap">
              搜索
            </button>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 库存统计卡片 -->
    <div class="grid grid-cols-2 md:grid-cols-4 gap-4">
      <div class="p-3 bg-gray-50 rounded-lg">
        <p class="text-sm text-gray-600">商品总数</p>
        <p class="text-xl font-bold text-gray-900 mt-1">{{ inventoryStats.total }}</p>
      </div>
      
      <div class="p-3 bg-green-50 rounded-lg border border-green-100">
        <p class="text-sm text-green-700">库存正常</p>
        <p class="text-xl font-bold text-gray-900 mt-1">{{ inventoryStats.normal }}</p>
      </div>
      
      <div class="p-3 bg-yellow-50 rounded-lg border border-yellow-100">
        <p class="text-sm text-yellow-700">库存预警</p>
        <p class="text-xl font-bold text-gray-900 mt-1">{{ inventoryStats.warning }}</p>
      </div>
      
      <div class="p-3 bg-red-50 rounded-lg border border-red-100">
        <p class="text-sm text-red-700">库存不足</p>
        <p class="text-xl font-bold text-gray-900 mt-1">{{ inventoryStats.empty }}</p>
      </div>
    </div>
    
    <!-- 库存列表 -->
    <div class="card">
      <div class="overflow-x-auto">
        <table class="min-w-full divide-y divide-gray-200">
          <thead class="bg-gray-50">
            <tr>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                <input type="checkbox" class="h-4 w-4 text-blue-600 focus:ring-blue-500 border-gray-300 rounded">
              </th>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                商品信息
              </th>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                当前库存
              </th>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                预警阈值
              </th>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                库存状态
              </th>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                操作
              </th>
            </tr>
          </thead>
          <tbody class="bg-white divide-y divide-gray-200">
            <tr v-for="item in inventoryList" :key="item.id" class="hover:bg-gray-50 transition-colors">
              <td class="px-6 py-4 whitespace-nowrap">
                <input type="checkbox" class="h-4 w-4 text-blue-600 focus:ring-blue-500 border-gray-300 rounded">
              </td>
              <td class="px-6 py-4">
                <div class="flex items-center space-x-3">
                  <div class="w-10 h-10 bg-gray-200 rounded-lg flex items-center justify-center">
                    <svg class="w-5 h-5 text-gray-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M18,8A2,2 0 0,1 20,10V20A2,2 0 0,1 18,22H6A2,2 0 0,1 4,20V10C4,8.89 4.9,8 6,8H18M15,3V5H6C4.89,5 4,5.9 4,7V19A1,1 0 0,0 5,20H19A1,1 0 0,0 20,19V7C20,5.89 19.1,5 18,5H13V3M15,15H9V17H15V15Z"></path>
                    </svg>
                  </div>
                  <div>
                    <div class="font-medium text-gray-900">{{ item.productName }}</div>
                    <div class="text-sm text-gray-500 mt-1">{{ item.productNo }}</div>
                  </div>
                </div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <div :class="getStockClass(item.quantity)">
                  {{ item.quantity }} {{ item.unit }}
                </div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="text-gray-900">
                  {{ item.warningThreshold }} {{ item.unit }}
                </div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <span :class="getStatusClass(item.quantity, item.warningThreshold)">
                  {{ getStatusText(item.quantity, item.warningThreshold) }}
                </span>
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">
                <div class="flex space-x-2">
                  <router-link :to="`/inventory/${item.id}`" class="text-blue-600 hover:text-blue-500">
                    编辑
                  </router-link>
                  <button @click="reduceInventory(item.id)" class="text-orange-600 hover:text-orange-500">
                    减少
                  </button>
                  <button @click="increaseInventory(item.id)" class="text-green-600 hover:text-green-500">
                    增加
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
              显示第 <span class="font-medium">1</span> 到 <span class="font-medium">10</span> 条，共 <span class="font-medium">58</span> 条记录
            </p>
          </div>
          <div>
            <nav class="relative z-0 inline-flex rounded-md shadow-sm -space-x-px" aria-label="Pagination">
              <a href="#" class="relative inline-flex items-center px-2 py-2 rounded-l-md border border-gray-300 bg-white text-sm font-medium text-gray-500 hover:bg-gray-50">
                <span class="sr-only">上一页</span>
                <svg class="h-5 w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
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
                6
              </a>
              <a href="#" class="relative inline-flex items-center px-2 py-2 rounded-r-md border border-gray-300 bg-white text-sm font-medium text-gray-500 hover:bg-gray-50">
                <span class="sr-only">下一页</span>
                <svg class="h-5 w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
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
import { ref, computed, onMounted } from 'vue'
import { inventoryService } from '../../services/api'

// 模拟库存数据
const inventoryList = ref([
  {
    id: 1,
    productId: 1,
    productName: '美式咖啡',
    productNo: 'PD202601010001',
    quantity: 128,
    unit: '杯',
    warningThreshold: 10
  },
  {
    id: 2,
    productId: 2,
    productName: '拿铁咖啡',
    productNo: 'PD202601010002',
    quantity: 96,
    unit: '杯',
    warningThreshold: 10
  },
  {
    id: 3,
    productId: 3,
    productName: '卡布奇诺',
    productNo: 'PD202601010003',
    quantity: 72,
    unit: '杯',
    warningThreshold: 10
  },
  {
    id: 4,
    productId: 4,
    productName: '摩卡咖啡',
    productNo: 'PD202601010004',
    quantity: 45,
    unit: '杯',
    warningThreshold: 10
  },
  {
    id: 5,
    productId: 5,
    productName: '焦糖玛奇朵',
    productNo: 'PD202601010005',
    quantity: 15,
    unit: '杯',
    warningThreshold: 10
  },
  {
    id: 6,
    productId: 6,
    productName: '抹茶拿铁',
    productNo: 'PD202601010006',
    quantity: 8,
    unit: '杯',
    warningThreshold: 10
  },
  {
    id: 7,
    productId: 7,
    productName: '牛角包',
    productNo: 'PD202601010007',
    quantity: 3,
    unit: '个',
    warningThreshold: 5
  },
  {
    id: 8,
    productId: 8,
    productName: '芝士蛋糕',
    productNo: 'PD202601010008',
    quantity: 0,
    unit: '块',
    warningThreshold: 5
  }
])

// 库存统计
const inventoryStats = computed(() => {
  const total = inventoryList.value.length
  const normal = inventoryList.value.filter(item => item.quantity >= item.warningThreshold).length
  const warning = inventoryList.value.filter(item => item.quantity > 0 && item.quantity < item.warningThreshold).length
  const empty = inventoryList.value.filter(item => item.quantity === 0).length
  
  return { total, normal, warning, empty }
})

// 获取库存样式
const getStockClass = (stock: number): string => {
  if (stock === 0) {
    return 'text-red-600 font-medium'
  } else if (stock < 10) {
    return 'text-yellow-600 font-medium'
  } else {
    return 'text-gray-900'
  }
}

// 获取状态样式
const getStatusClass = (quantity: number, threshold: number): string => {
  if (quantity === 0) {
    return 'inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-red-100 text-red-800'
  } else if (quantity < threshold) {
    return 'inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-yellow-100 text-yellow-800'
  } else {
    return 'inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-green-100 text-green-800'
  }
}

// 获取状态文本
const getStatusText = (quantity: number, threshold: number): string => {
  if (quantity === 0) {
    return '库存不足'
  } else if (quantity < threshold) {
    return '库存预警'
  } else {
    return '库存正常'
  }
}

// 减少库存
const reduceInventory = (id: number) => {
  // 实现减少库存的逻辑
  console.log('减少库存:', id)
}

// 增加库存
const increaseInventory = (id: number) => {
  // 实现增加库存的逻辑
  console.log('增加库存:', id)
}

// 触发自动补货
const triggerAutoReorder = async () => {
  try {
    // 实现自动补货的逻辑
    console.log('触发自动补货')
  } catch (error) {
    console.error('自动补货失败:', error)
  }
}

// 加载库存数据
const loadInventoryData = async () => {
  try {
    // 从API获取库存数据
    // const response = await inventoryService.getInventory()
    // inventoryList.value = response.data.records
  } catch (error) {
    console.error('加载库存数据失败:', error)
  }
}

// 初始化
onMounted(() => {
  loadInventoryData()
})
</script>

<style scoped>
.btn-primary {
  @apply px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 transition-colors;
}

.btn-secondary {
  @apply px-4 py-2 bg-gray-200 text-gray-800 rounded-md hover:bg-gray-300 transition-colors;
}

.input-field {
  @apply w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent;
}

.card {
  @apply bg-white p-6 rounded-lg shadow-sm border border-gray-100;
}
</style>