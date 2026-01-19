
<template>
  <div class="space-y-6 animate-fade-in">
    <!-- 页面标题与操作栏 -->
    <div class="flex flex-col md:flex-row md:items-center justify-between gap-4 bg-white p-6 rounded-2xl shadow-sm border border-gray-100">
      <div>
        <h2 class="text-2xl font-bold text-gray-900">员工智能排班</h2>
        <p class="text-sm text-gray-500 mt-1 flex items-center">
          <span class="w-2 h-2 bg-green-500 rounded-full mr-2"></span>
          基于 AI 预测客流量生成的排班建议（已匹配今日预测高峰）
        </p>
      </div>
      <div class="flex items-center space-x-3">
        <button class="px-4 py-2 text-sm font-medium text-gray-700 bg-gray-50 hover:bg-gray-100 border border-gray-200 rounded-xl transition-all duration-200 flex items-center">
          <svg class="w-4 h-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 10v6m0 0l-3-3m3 3l3-3m2 8H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"></path>
          </svg>
          导出班表
        </button>
        <button @click="triggerSmartSchedule" :disabled="isScheduling" class="px-6 py-2 text-sm font-medium text-white bg-gradient-to-r from-blue-600 to-indigo-600 hover:from-blue-700 hover:to-indigo-700 rounded-xl shadow-lg shadow-blue-200 transition-all duration-300 transform hover:-translate-y-0.5 disabled:opacity-50 disabled:cursor-not-allowed flex items-center">
          <svg v-if="!isScheduling" class="w-4 h-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 10V3L4 14h7v7l9-11h-7z"></path>
          </svg>
          <svg v-else class="animate-spin -ml-1 mr-3 h-4 w-4 text-white" fill="none" viewBox="0 0 24 24">
            <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
            <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
          </svg>
          {{ isScheduling ? '正在计算最优解...' : '一键智能排班' }}
        </button>
      </div>
    </div>

    <div class="grid grid-cols-1 lg:grid-cols-4 gap-6">
      <!-- 员工列表 -->
      <div class="lg:col-span-1 space-y-4">
        <div class="bg-white p-5 rounded-2xl shadow-sm border border-gray-100 h-full">
          <div class="flex items-center justify-between mb-4">
            <h3 class="text-lg font-bold text-gray-900">可选员工</h3>
            <span class="px-2 py-1 text-xs bg-blue-50 text-blue-600 rounded-lg font-medium">8位在岗</span>
          </div>
          <div class="space-y-3 max-h-[600px] overflow-y-auto pr-2 custom-scrollbar">
            <div v-for="emp in employees" :key="emp.id" class="group flex items-center p-3 rounded-xl border border-dashed border-gray-200 hover:border-blue-400 hover:bg-blue-50 transition-all duration-200 cursor-move">
              <div class="w-10 h-10 rounded-full bg-gradient-to-br from-gray-100 to-gray-200 flex items-center justify-center text-gray-500 font-bold mr-3 border-2 border-white shadow-sm overflow-hidden">
                <img v-if="emp.avatar" :src="emp.avatar" :alt="emp.name" class="w-full h-full object-cover" />
                <span v-else>{{ emp.name.charAt(0) }}</span>
              </div>
              <div class="flex-1 min-w-0">
                <p class="text-sm font-semibold text-gray-900 truncate">{{ emp.name }}</p>
                <p class="text-xs text-gray-500">{{ emp.position }} · 已排 {{ emp.hours }}h</p>
              </div>
              <div class="hidden group-hover:block">
                <svg class="w-4 h-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 5v.01M12 12v.01M12 19v.01M12 6a1 1 0 110-2 1 1 0 010 2zm0 7a1 1 0 110-2 1 1 0 010 2zm0 7a1 1 0 110-2 1 1 0 010 2z"></path>
                </svg>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 日历/排班视口 -->
      <div class="lg:col-span-3">
        <div class="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden">
          <!-- 这里模拟一个精美的周排班视图 -->
          <div class="grid grid-cols-8 border-b border-gray-100 bg-gray-50/50">
            <div class="p-4 text-xs font-semibold text-gray-400 text-center border-r border-gray-100">时间</div>
            <div v-for="day in weekDays" :key="day" class="p-4 text-center border-r last:border-r-0 border-gray-100">
              <p class="text-xs font-medium text-gray-500 uppercase tracking-wider">{{ day.label }}</p>
              <p class="text-lg font-bold text-gray-900 mt-1">{{ day.date }}</p>
            </div>
          </div>

          <div class="relative max-h-[600px] overflow-y-auto custom-scrollbar">
            <!-- 模拟时间行 -->
            <div v-for="hour in 14" :key="hour" class="grid grid-cols-8 border-b border-gray-50 group min-h-[60px]">
              <div class="p-3 text-xs text-gray-400 text-center border-r border-gray-100 bg-gray-50/30 font-mono">
                {{ 8 + hour - 1 }}:00
              </div>
              <div v-for="d in 7" :key="d" class="p-2 border-r last:border-r-0 border-gray-50 relative group-hover:bg-gray-50/20 transition-colors">
                <!-- 随机渲染几个排班块 -->
                <div v-if="shouldShowBlock(hour, d)" 
                     class="group/item absolute inset-x-1 inset-y-1 rounded-lg p-2 shadow-sm border-l-4 transition-all duration-300 hover:shadow-md hover:scale-[1.02] cursor-pointer"
                     :class="getBlockClass(hour, d)">
                  <div class="flex justify-between items-start">
                    <p class="text-[10px] font-bold truncate">{{ getEmployeeName(hour, d) }}</p>
                    <span class="opacity-0 group-hover/item:opacity-100 transition-opacity">
                      <svg class="w-3 h-3" fill="currentColor" viewBox="0 0 20 20">
                        <path d="M13.586 3.586a2 2 0 112.828 2.828l-.707.707-2.828-2.828.707-.707zM11.364 5.828l-8 8V17h3.172l8-8-3.172-3.172z"></path>
                      </svg>
                    </span>
                  </div>
                </div>
              </div>
            </div>

            <!-- 客流预测虚线图模拟 Overlay -->
            <div class="absolute inset-0 pointer-events-none overflow-hidden opacity-10">
              <svg class="w-full h-full" viewBox="0 0 1000 600" preserveAspectRatio="none">
                <path d="M0,450 Q250,100 500,300 T1000,150" fill="none" stroke="#3b82f6" stroke-width="2" stroke-dasharray="10,5" />
              </svg>
            </div>
          </div>
        </div>

        <!-- 底部负载状态卡片 -->
        <div class="grid grid-cols-3 gap-4 mt-6">
          <div class="bg-blue-50/50 p-4 rounded-xl border border-blue-100 flex items-center">
            <div class="p-2 bg-blue-100 rounded-lg mr-3 text-blue-600">
              <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 7h8m0 0v8m0-8l-8 8-4-4-6 6"></path>
              </svg>
            </div>
            <div>
              <p class="text-xs text-gray-500 font-medium uppercase">平均人效</p>
              <p class="text-lg font-bold text-gray-900">¥450/h</p>
            </div>
          </div>
          <div class="bg-green-50/50 p-4 rounded-xl border border-green-100 flex items-center">
            <div class="p-2 bg-green-100 rounded-lg mr-3 text-green-600">
              <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"></path>
              </svg>
            </div>
            <div>
              <p class="text-xs text-gray-500 font-medium uppercase">人力饱和度</p>
              <p class="text-lg font-bold text-gray-900">92.4%</p>
            </div>
          </div>
          <div class="bg-yellow-50/50 p-4 rounded-xl border border-yellow-100 flex items-center">
            <div class="p-2 bg-yellow-100 rounded-lg mr-3 text-yellow-600">
              <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path>
              </svg>
            </div>
            <div>
              <p class="text-xs text-gray-500 font-medium uppercase">潜在过载风险</p>
              <p class="text-lg font-bold text-gray-900">周五 14:00</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { employeeService } from '../../services/api' 

const isScheduling = ref(false)
const employees = ref<any[]>([])

const fetchEmployees = async () => {
  try {
    const response = await employeeService.getEmployees({ page: 1, size: 100 })
    const data = response.data
    if (data.items) {
      // 映射后端数据到前端视图
      employees.value = data.items.map((emp: any) => ({
        id: emp.id,
        name: emp.name,
        position: emp.position || '咖啡师',
        hours: Math.floor(Math.random() * 40), // 模拟工时
        avatar: `https://i.pravatar.cc/150?u=${emp.id}`
      }))
    }
  } catch (error) {
    console.error('获取员工列表失败:', error)
    // Fallback data if API fails
    employees.value = [
      { id: 1, name: '陈晓文', position: '高级咖啡师', hours: 32, avatar: 'https://i.pravatar.cc/150?u=1' },
      { id: 2, name: '李明', position: '咖啡师长', hours: 40, avatar: 'https://i.pravatar.cc/150?u=2' },
      { id: 3, name: '张琦', position: '初级咖啡师', hours: 24, avatar: 'https://i.pravatar.cc/150?u=3' },
      { id: 4, name: '王思嘉', position: '前台收银', hours: 36, avatar: 'https://i.pravatar.cc/150?u=4' },
    ]
  }
}

onMounted(() => {
  fetchEmployees()
})

const triggerSmartSchedule = () => {
  isScheduling.value = true
  // 模拟 AI 计算过程
  setTimeout(() => {
    isScheduling.value = false
    alert('智能排班计算完成！已根据客流预测模型为您调整了 12 个班次冲突。')
  }, 2000)
}

const weekDays = [
  { label: '周一', date: '19' },
  { label: '周二', date: '20' },
  { label: '周三', date: '21' },
  { label: '周四', date: '22' },
  { label: '周五', date: '23' },
  { label: '周六', date: '24' },
  { label: '周日', date: '25' },
]

// 模拟排班渲染逻辑
const shouldShowBlock = (hour: number, day: number) => {
  return employees.value.length > 0 && ((hour + day) % 3 === 0 || (hour * day) % 5 === 2)
}

const getBlockClass = (hour: number, day: number) => {
  const themes = [
    'bg-blue-50 text-blue-700 border-blue-400',
    'bg-indigo-50 text-indigo-700 border-indigo-400',
    'bg-purple-50 text-purple-700 border-purple-400',
    'bg-teal-50 text-teal-700 border-teal-400'
  ]
  return themes[(hour + day) % themes.length]
}

const getEmployeeName = (hour: number, day: number) => {
  if (employees.value.length === 0) return ''
  const index = (hour + day) % employees.value.length
  return employees.value[index].name
}

</script>

<style scoped>
.animate-fade-in {
  animation: fadeIn 0.5s ease-out;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}

.custom-scrollbar::-webkit-scrollbar {
  width: 6px;
}
.custom-scrollbar::-webkit-scrollbar-track {
  background: transparent;
}
.custom-scrollbar::-webkit-scrollbar-thumb {
  background: #e2e8f0;
  border-radius: 10px;
}
.custom-scrollbar::-webkit-scrollbar-thumb:hover {
  background: #cbd5e1;
}

.card {
  @apply bg-white p-6 rounded-2xl shadow-sm border border-gray-100 transition-all duration-300 hover:shadow-md;
}
</style>
