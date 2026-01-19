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
            库存状态
          </label>
          <select v-model="filters.status" class="input-field" @change="handleSearch">
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
            <input v-model="filters.keyword" type="text" class="input-field flex-1" placeholder="搜索商品编号/名称" @keyup.enter="handleSearch">
            <button class="btn-primary whitespace-nowrap" @click="handleSearch">
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
        <p class="text-xl font-bold text-gray-900 mt-1">{{ stats.total }}</p>
      </div>
      
      <div class="p-3 bg-green-50 rounded-lg border border-green-100">
        <p class="text-sm text-green-700">库存正常</p>
        <p class="text-xl font-bold text-gray-900 mt-1">{{ stats.normal }}</p>
      </div>
      
      <div class="p-3 bg-yellow-50 rounded-lg border border-yellow-100">
        <p class="text-sm text-yellow-700">库存预警</p>
        <p class="text-xl font-bold text-gray-900 mt-1">{{ stats.warning }}</p>
      </div>
      
      <div class="p-3 bg-red-50 rounded-lg border border-red-100">
        <p class="text-sm text-red-700">库存不足</p>
        <p class="text-xl font-bold text-gray-900 mt-1">{{ stats.empty }}</p>
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
            <tr v-if="loading" class="animate-pulse">
              <td colspan="6" class="px-6 py-10 text-center text-gray-500">正在加载库存数据...</td>
            </tr>
            <tr v-else-if="filteredInventory.length === 0">
              <td colspan="6" class="px-6 py-10 text-center text-gray-500">未找到相关库存</td>
            </tr>
            <tr v-for="item in filteredInventory" :key="item.id" v-else class="hover:bg-gray-50 transition-colors">
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
                    <div class="font-medium text-gray-900">{{ item.productName || '未知商品' }}</div>
                    <div class="text-sm text-gray-500 mt-1">{{ item.productNo || '-' }}</div>
                  </div>
                </div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <div :class="getStockClass(item.quantity)">
                  {{ (item.quantity || 0).toFixed(0) }} {{ item.unit || '件' }}
                </div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="text-gray-900">
                  {{ (item.warningThreshold || 0).toFixed(0) }} {{ item.unit || '件' }}
                </div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <span :class="getStatusClass(item.quantity, item.warningThreshold)">
                  {{ getStatusText(item.quantity, item.warningThreshold) }}
                </span>
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">
                <div class="flex space-x-2">
                  <button @click="handleEdit(item)" class="text-blue-600 hover:text-blue-500">
                    编辑
                  </button>
                  <button @click="updateStock(item, -10)" class="text-orange-600 hover:text-orange-500">
                    减少
                  </button>
                  <button @click="updateStock(item, 10)" class="text-green-600 hover:text-green-500">
                    增加
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
      
      <div class="flex items-center justify-between px-6 py-4 border-t border-gray-200 sm:px-6">
        <div class="hidden sm:flex-1 sm:flex sm:items-center sm:justify-between">
          <div>
            <p class="text-sm text-gray-700">
              共 <span class="font-medium">{{ filteredInventory.length }}</span> 条记录
            </p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { inventoryService } from '../../services/api'

const inventoryList = ref<any[]>([])
const loading = ref(false)

const filters = ref({
  status: '',
  keyword: ''
})

const stats = ref({
  total: 0,
  normal: 0,
  warning: 0,
  empty: 0
})

const fetchInventory = async () => {
  loading.value = true
  try {
    const response = await inventoryService.getInventory()
    inventoryList.value = response.data || []
    calculateStats()
  } catch (error) {
    console.error('加载库存失败:', error)
  } finally {
    loading.value = false
  }
}

const calculateStats = () => {
  stats.value.total = inventoryList.value.length
  stats.value.normal = inventoryList.value.filter(i => i.quantity >= i.warningThreshold).length
  stats.value.warning = inventoryList.value.filter(i => i.quantity > 0 && i.quantity < i.warningThreshold).length
  stats.value.empty = inventoryList.value.filter(i => (i.quantity || 0) <= 0).length
}

const filteredInventory = computed(() => {
  return inventoryList.value.filter(item => {
    const matchKeyword = !filters.value.keyword || 
      (item.productName && item.productName.includes(filters.value.keyword)) ||
      (item.productNo && item.productNo.includes(filters.value.keyword))
    
    let matchStatus = true
    if (filters.value.status === 'normal') matchStatus = item.quantity >= item.warningThreshold
    if (filters.value.status === 'warning') matchStatus = item.quantity > 0 && item.quantity < item.warningThreshold
    if (filters.value.status === 'empty') matchStatus = (item.quantity || 0) <= 0
    
    return matchKeyword && matchStatus
  })
})

const handleSearch = () => {
  // 纯前端过滤不需要重新请求，由于后端暂不支持过滤参数
}

const updateStock = async (item: any, delta: number) => {
  try {
    const newQuantity = Math.max(0, (item.quantity || 0) + delta)
    await inventoryService.updateInventoryQuantity(item.id, newQuantity)
    item.quantity = newQuantity
    calculateStats()
  } catch (error) {
    alert('操作失败: ' + error)
  }
}

const triggerAutoReorder = async () => {
  try {
    await inventoryService.triggerAutoReorder()
    await fetchInventory()
    alert('自动补货任务已触发')
  } catch (error) {
    alert('补货失败: ' + error)
  }
}

const getStockClass = (stock: number): string => {
  if (stock <= 0) return 'text-red-600 font-medium'
  if (stock < 10) return 'text-yellow-600 font-medium'
  return 'text-gray-900'
}

const getStatusClass = (quantity: number, threshold: number): string => {
  if (quantity <= 0) return 'inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-red-100 text-red-800'
  if (quantity < threshold) return 'inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-yellow-100 text-yellow-800'
  return 'inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-green-100 text-green-800'
}

const getStatusText = (quantity: number, threshold: number): string => {
  if (quantity <= 0) return '库存不足'
  if (quantity < threshold) return '库存预警'
  return '库存正常'
}

const handleEdit = (item: any) => {
  console.log('Edit item:', item)
}

onMounted(() => {
  fetchInventory()
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