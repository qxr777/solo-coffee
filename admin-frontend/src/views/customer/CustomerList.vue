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
      <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">
            客户姓名
          </label>
          <input type="text" class="input-field" placeholder="搜索客户姓名">
        </div>
        
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">
            手机号码
          </label>
          <input type="text" class="input-field" placeholder="搜索手机号码">
        </div>
        
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">
            会员等级
          </label>
          <select class="input-field">
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
            <input type="text" class="input-field flex-1" placeholder="搜索客户ID/邮箱">
            <button class="btn-primary whitespace-nowrap">
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
        <p class="text-xl font-bold text-gray-900 mt-1">{{ customerStats.total }}</p>
      </div>
      
      <div class="p-3 bg-blue-50 rounded-lg border border-blue-100">
        <p class="text-sm text-blue-700">会员总数</p>
        <p class="text-xl font-bold text-gray-900 mt-1">{{ customerStats.members }}</p>
      </div>
      
      <div class="p-3 bg-green-50 rounded-lg border border-green-100">
        <p class="text-sm text-green-700">活跃客户</p>
        <p class="text-xl font-bold text-gray-900 mt-1">{{ customerStats.active }}</p>
      </div>
      
      <div class="p-3 bg-purple-50 rounded-lg border border-purple-100">
        <p class="text-sm text-purple-700">平均积分</p>
        <p class="text-xl font-bold text-gray-900 mt-1">{{ customerStats.avgPoints }}</p>
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
            <tr v-for="customer in customers" :key="customer.id" class="hover:bg-gray-50 transition-colors">
              <td class="px-6 py-4 whitespace-nowrap">
                <input type="checkbox" class="h-4 w-4 text-blue-600 focus:ring-blue-500 border-gray-300 rounded">
              </td>
              <td class="px-6 py-4">
                <div class="flex items-center space-x-3">
                  <div class="w-10 h-10 bg-gray-200 rounded-full flex items-center justify-center">
                    <svg class="w-5 h-5 text-gray-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"></path>
                    </svg>
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
                    积分: <span class="font-medium">{{ customer.points }}</span>
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
                  <button @click="addPoints(customer.id)" class="text-green-600 hover:text-green-600">
                    加积分
                  </button>
                  <button @click="editCustomer(customer.id)" class="text-gray-600 hover:text-gray-500">
                    编辑
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
              显示第 <span class="font-medium">1</span> 到 <span class="font-medium">10</span> 条，共 <span class="font-medium">156</span> 条记录
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
                16
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
import { customerService } from '../../services/api'

// 模拟客户数据
const customers = ref([
  {
    id: 1,
    name: '张三',
    phone: '13800138000',
    email: 'zhangsan@example.com',
    points: 1500,
    memberLevelId: 2,
    createdAt: '2026-01-01T10:00:00'
  },
  {
    id: 2,
    name: '李四',
    phone: '13900139000',
    email: 'lisi@example.com',
    points: 850,
    memberLevelId: 1,
    createdAt: '2026-01-02T11:30:00'
  },
  {
    id: 3,
    name: '王五',
    phone: '13700137000',
    email: 'wangwu@example.com',
    points: 3200,
    memberLevelId: 3,
    createdAt: '2026-01-03T14:20:00'
  },
  {
    id: 4,
    name: '赵六',
    phone: '13600136000',
    email: 'zhaoliu@example.com',
    points: 5000,
    memberLevelId: 4,
    createdAt: '2026-01-04T09:15:00'
  },
  {
    id: 5,
    name: '钱七',
    phone: '13500135000',
    email: 'qianqi@example.com',
    points: 320,
    memberLevelId: 1,
    createdAt: '2026-01-05T16:45:00'
  },
  {
    id: 6,
    name: '孙八',
    phone: '13400134000',
    email: 'sunba@example.com',
    points: 1800,
    memberLevelId: 2,
    createdAt: '2026-01-06T13:30:00'
  },
  {
    id: 7,
    name: '周九',
    phone: '13300133000',
    email: 'zhoujiu@example.com',
    points: 2500,
    memberLevelId: 2,
    createdAt: '2026-01-07T10:45:00'
  },
  {
    id: 8,
    name: '吴十',
    phone: '13200132000',
    email: 'wushi@example.com',
    points: 4200,
    memberLevelId: 3,
    createdAt: '2026-01-08T15:20:00'
  }
])

// 客户统计
const customerStats = computed(() => {
  const total = customers.value.length
  const members = customers.value.filter(customer => customer.memberLevelId > 0).length
  const active = customers.value.filter(customer => customer.points > 0).length
  const avgPoints = Math.round(customers.value.reduce((sum, customer) => sum + customer.points, 0) / total)
  
  return { total, members, active, avgPoints }
})

// 获取会员等级样式
const getLevelClass = (levelId: number): string => {
  const classes = {
    1: 'bg-gray-100 text-gray-800',
    2: 'bg-blue-100 text-blue-800',
    3: 'bg-purple-100 text-purple-800',
    4: 'bg-yellow-100 text-yellow-800'
  }
  return classes[levelId] || 'bg-gray-100 text-gray-800'
}

// 获取会员等级名称
const getLevelName = (levelId: number): string => {
  const levelNames = {
    1: '普通会员',
    2: '银卡会员',
    3: '金卡会员',
    4: '钻石会员'
  }
  return levelNames[levelId] || '普通会员'
}

// 格式化日期
const formatDate = (dateString: string): string => {
  const date = new Date(dateString)
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit'
  })
}

// 添加积分
const addPoints = (customerId: number) => {
  // 实现添加积分的逻辑
  console.log('添加积分:', customerId)
}

// 编辑客户
const editCustomer = (customerId: number) => {
  // 实现编辑客户的逻辑
  console.log('编辑客户:', customerId)
}

// 加载客户数据
const loadCustomers = async () => {
  try {
    // 从API获取客户数据
    // const response = await customerService.getCustomers()
    // customers.value = response.data.records
  } catch (error) {
    console.error('加载客户数据失败:', error)
  }
}

// 初始化
onMounted(() => {
  loadCustomers()
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