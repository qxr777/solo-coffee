<template>
  <div class="space-y-6">
    <!-- 页面标题和操作 -->
    <div class="flex flex-col md:flex-row md:items-center md:justify-between gap-4">
      <div>
        <h1 class="text-2xl font-bold text-gray-900">客户管理</h1>
        <p class="text-gray-600 mt-1">管理所有客户信息和会员数据</p>
      </div>
      
      <div class="flex space-x-2">
        <button class="btn-primary whitespace-nowrap">
          <svg class="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6"></path>
          </svg>
          添加客户
        </button>
      </div>
    </div>
    
    <!-- 客户筛选 -->
    <div class="card">
      <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">
            会员等级
          </label>
          <select v-model="filters.levelId" class="input-field" @change="handleSearch">
            <option value="">全部等级</option>
            <option value="1">普通会员</option>
            <option value="2">银卡会员</option>
            <option value="3">金卡会员</option>
            <option value="4">钻石会员</option>
          </select>
        </div>
        
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">
            搜索
          </label>
          <div class="flex space-x-2">
            <input v-model="filters.keyword" type="text" class="input-field flex-1" placeholder="客户姓名/手机号" @keyup.enter="handleSearch">
            <button class="btn-primary whitespace-nowrap" @click="handleSearch">
              搜索
            </button>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 客户统计卡片 -->
    <div class="grid grid-cols-2 md:grid-cols-4 gap-4">
      <div class="p-3 bg-gray-50 rounded-lg">
        <p class="text-sm text-gray-600">客户总数</p>
        <p class="text-xl font-bold text-gray-900 mt-1">{{ total }}</p>
      </div>
      
      <div class="p-3 bg-blue-50 rounded-lg border border-blue-100">
        <p class="text-sm text-blue-700">会员总数</p>
        <p class="text-xl font-bold text-gray-900 mt-1">{{ stats.members }}</p>
      </div>
      
      <div class="p-3 bg-green-50 rounded-lg border border-green-100">
        <p class="text-sm text-green-700">活跃客户</p>
        <p class="text-xl font-bold text-gray-900 mt-1">{{ stats.active }}</p>
      </div>
      
      <div class="p-3 bg-purple-50 rounded-lg border border-purple-100">
        <p class="text-sm text-purple-700">平均积分</p>
        <p class="text-xl font-bold text-gray-900 mt-1">{{ stats.avgPoints }}</p>
      </div>
    </div>
    
    <!-- 客户列表 -->
    <div class="card">
      <div class="overflow-x-auto">
        <table class="min-w-full divide-y divide-gray-200">
          <thead class="bg-gray-50">
            <tr>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                <input type="checkbox" class="h-4 w-4 text-blue-600 focus:ring-blue-500 border-gray-300 rounded">
              </th>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                客户信息
              </th>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                会员信息
              </th>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                联系方式
              </th>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                注册时间
              </th>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                操作
              </th>
            </tr>
          </thead>
          <tbody class="bg-white divide-y divide-gray-200">
            <tr v-if="loading" class="animate-pulse">
              <td colspan="6" class="px-6 py-10 text-center text-gray-500">正在获取客户数据...</td>
            </tr>
            <tr v-else-if="customers.length === 0">
              <td colspan="6" class="px-6 py-10 text-center text-gray-500">暂无客户数据</td>
            </tr>
            <tr v-for="customer in customers" :key="customer.id" v-else class="hover:bg-gray-50 transition-colors">
              <td class="px-6 py-4 whitespace-nowrap">
                <input type="checkbox" class="h-4 w-4 text-blue-600 focus:ring-blue-500 border-gray-300 rounded">
              </td>
              <td class="px-6 py-4">
                <div class="flex items-center space-x-3">
                  <div class="w-10 h-10 bg-blue-100 rounded-full flex items-center justify-center text-blue-600 font-bold">
                    {{ (customer.name || '?').charAt(0) }}
                  </div>
                  <div>
                    <div class="font-medium text-gray-900">{{ customer.name }}</div>
                    <div class="text-sm text-gray-500 mt-1">ID: {{ customer.id }}</div>
                  </div>
                </div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="space-y-1">
                  <div class="flex items-center">
                    <span :class="getLevelClass(customer.memberLevelId)" class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium">
                      {{ getLevelName(customer.memberLevelId) }}
                    </span>
                  </div>
                  <div class="text-sm text-gray-600">
                    积分: <span class="font-medium">{{ customer.points || 0 }}</span>
                  </div>
                </div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="space-y-1">
                  <div class="text-sm text-gray-600">
                    {{ customer.phone }}
                  </div>
                  <div class="text-sm text-gray-500">
                    {{ customer.email }}
                  </div>
                </div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                {{ formatDate(customer.createdAt) }}
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">
                <div class="flex space-x-2">
                  <router-link :to="`/customers/${customer.id}`" class="text-blue-600 hover:text-blue-500">
                    详情
                  </router-link>
                  <button class="text-green-600 hover:text-green-700">
                    加积分
                  </button>
                  <button class="text-gray-600 hover:text-gray-500">
                    编辑
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
              显示第 <span class="font-medium">{{ (currentPage - 1) * pageSize + 1 }}</span> 到 <span class="font-medium">{{ Math.min(currentPage * pageSize, total) }}</span> 条，共 <span class="font-medium">{{ total }}</span> 条记录
            </p>
          </div>
          <div v-if="total > pageSize">
            <nav class="relative z-0 inline-flex rounded-md shadow-sm -space-x-px">
              <button @click="currentPage--" :disabled="currentPage === 1" class="relative inline-flex items-center px-2 py-2 rounded-l-md border border-gray-300 bg-white text-sm font-medium text-gray-500 hover:bg-gray-50 disabled:opacity-50">
                <svg class="h-5 w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7"></path>
                </svg>
              </button>
              <span class="relative inline-flex items-center px-4 py-2 border border-gray-300 bg-blue-50 text-sm font-medium text-blue-600">
                {{ currentPage }}
              </span>
              <button @click="currentPage++" :disabled="currentPage * pageSize >= total" class="relative inline-flex items-center px-2 py-2 rounded-r-md border border-gray-300 bg-white text-sm font-medium text-gray-500 hover:bg-gray-50 disabled:opacity-50">
                <svg class="h-5 w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7"></path>
                </svg>
              </button>
            </nav>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { customerService } from '../../services/api'
import dayjs from 'dayjs'

const customers = ref<any[]>([])
const loading = ref(false)
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)

const filters = ref({
  levelId: '',
  keyword: ''
})

const stats = ref({
  total: 0,
  members: 0,
  active: 0,
  avgPoints: 0
})

const fetchCustomers = async () => {
  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      size: pageSize.value,
      levelId: filters.value.levelId || undefined,
      keyword: filters.value.keyword || undefined
    }
    const response = await customerService.getCustomers(params)
    customers.value = response.data.records || []
    total.value = response.data.total || 0
    
    // 加载一些统计数据（实际应由后端接口提供，此处根据当前列表大致估算）
    stats.value.total = total.value
    stats.value.members = customers.value.filter(c => c.memberLevelId >= 1).length
    stats.value.active = customers.value.filter(c => c.points > 0).length
    const sumPoints = customers.value.reduce((sum, c) => sum + (c.points || 0), 0)
    stats.value.avgPoints = customers.value.length > 0 ? Math.round(sumPoints / customers.value.length) : 0
  } catch (error) {
    console.error('加载客户数据失败:', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  currentPage.value = 1
  fetchCustomers()
}

// 获取会员等级样式
const getLevelClass = (levelId: number): string => {
  const classes: any = {
    1: 'bg-gray-100 text-gray-800',
    2: 'bg-blue-100 text-blue-800',
    3: 'bg-purple-100 text-purple-800',
    4: 'bg-yellow-100 text-yellow-800'
  }
  return classes[levelId] || 'bg-gray-100 text-gray-800'
}

// 获取会员等级名称
const getLevelName = (levelId: number): string => {
  const levelNames: any = {
    1: '普通会员',
    2: '银卡会员',
    3: '金卡会员',
    4: '钻石会员'
  }
  return levelNames[levelId] || '普通会员'
}

// 格式化日期
const formatDate = (dateString?: string): string => {
  if (!dateString) return '从未记录'
  return dayjs(dateString).format('YYYY-MM-DD')
}

watch(currentPage, fetchCustomers)

// 初始化
onMounted(() => {
  fetchCustomers()
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