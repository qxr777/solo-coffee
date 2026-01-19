<template>
  <div class="space-y-6">
    <!-- 页面标题和操作 -->
    <div class="flex flex-col md:flex-row md:items-center md:justify-between gap-4">
      <div>
        <h1 class="text-2xl font-bold text-gray-900">门店管理</h1>
        <p class="text-gray-600 mt-1">管理所有门店详情、营业状态和联系方式</p>
      </div>
      
      <div class="flex space-x-2">
        <button @click="handleAdd" class="btn-primary whitespace-nowrap">
          <svg class="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6"></path>
          </svg>
          添加门店
        </button>
      </div>
    </div>

    <!-- 门店列表 -->
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
      <div v-if="loading" class="col-span-full py-20 text-center text-gray-500 animate-pulse">
        正在获取门店数据...
      </div>
      <div v-else-if="stores.length === 0" class="col-span-full py-20 text-center text-gray-500 card">
        暂无门店数据
      </div>
      <div v-for="store in stores" :key="store.id" v-else class="card hover:shadow-md transition-shadow">
        <div class="flex justify-between items-start mb-4">
          <div class="flex items-center space-x-3">
            <div class="w-12 h-12 bg-blue-100 rounded-lg flex items-center justify-center text-blue-600">
              <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4"></path>
              </svg>
            </div>
            <div>
              <h3 class="text-lg font-bold text-gray-900">{{ store.name }}</h3>
              <p class="text-sm text-gray-500">ID: {{ store.id }}</p>
            </div>
          </div>
          <span :class="getStatusClass(store.status)" class="px-2.5 py-0.5 rounded-full text-xs font-medium">
            {{ getStatusName(store.status) }}
          </span>
        </div>
        
        <div class="space-y-3 border-t border-gray-100 pt-4">
          <div class="flex items-start text-sm">
            <svg class="w-4 h-4 text-gray-400 mt-0.5 mr-2 shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z"></path>
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 11a3 3 0 11-6 0 3 3 0 016 0z"></path>
            </svg>
            <span class="text-gray-600">{{ store.address }}</span>
          </div>
          
          <div class="flex items-center text-sm">
            <svg class="w-4 h-4 text-gray-400 mr-2 shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 5a2 2 0 012-2h3.28a1 1 0 01.948.684l1.498 4.493a1 1 0 01-.502 1.21l-2.257 1.13a11.042 11.042 0 005.516 5.516l1.13-2.257a1 1 0 011.21-.502l4.493 1.498a1 1 0 01.684.949V19a2 2 0 01-2 2h-1C9.716 21 3 14.284 3 6V5z"></path>
            </svg>
            <span class="text-gray-600">{{ store.phone }}</span>
          </div>
          
          <div class="flex items-center text-sm">
            <svg class="w-4 h-4 text-gray-400 mr-2 shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"></path>
            </svg>
            <span class="text-gray-600">{{ store.businessHours }}</span>
          </div>
        </div>

        <div class="mt-6 flex space-x-2">
          <button @click="handleEdit(store)" class="flex-1 btn-secondary text-sm py-1.5 ring-1 ring-gray-200">编辑门店</button>
          <button @click="handleSchedule(store.id)" class="flex-1 btn-secondary text-sm py-1.5 ring-1 ring-gray-200">排班管理</button>
          <button @click="confirmDelete(store)" class="px-3 btn-secondary text-sm py-1.5 ring-1 ring-gray-200 text-red-600 hover:bg-red-50">
            <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"></path>
            </svg>
          </button>
        </div>
      </div>
    </div>

    <!-- 门店编辑/添加模态框 -->
    <div v-if="showModal" class="fixed inset-0 z-50 overflow-y-auto">
      <div class="flex items-center justify-center min-h-screen px-4 pt-4 pb-20 text-center sm:block sm:p-0">
        <div class="fixed inset-0 transition-opacity" aria-hidden="true">
          <div class="absolute inset-0 bg-gray-500 opacity-75"></div>
        </div>
        <span class="hidden sm:inline-block sm:align-middle sm:h-screen" aria-hidden="true">&#8203;</span>
        <div class="inline-block align-bottom bg-white rounded-lg text-left overflow-hidden shadow-xl transform transition-all sm:my-8 sm:align-middle sm:max-w-lg sm:w-full">
          <div class="bg-white px-4 pt-5 pb-4 sm:p-6 sm:pb-4">
            <h3 class="text-lg font-medium leading-6 text-gray-900 mb-4">{{ isEdit ? '编辑门店' : '添加门店' }}</h3>
            <div class="space-y-4">
              <div>
                <label class="block text-sm font-medium text-gray-700">门店名称</label>
                <input v-model="storeForm.name" type="text" class="mt-1 block w-full border-gray-300 rounded-md shadow-sm focus:ring-blue-500 focus:border-blue-500 sm:text-sm p-2 bg-gray-50 border" />
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700">地址</label>
                <input v-model="storeForm.address" type="text" class="mt-1 block w-full border-gray-300 rounded-md shadow-sm focus:ring-blue-500 focus:border-blue-500 sm:text-sm p-2 bg-gray-50 border" />
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700">联系电话</label>
                <input v-model="storeForm.phone" type="text" class="mt-1 block w-full border-gray-300 rounded-md shadow-sm focus:ring-blue-500 focus:border-blue-500 sm:text-sm p-2 bg-gray-50 border" />
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700">营业时间</label>
                <input v-model="storeForm.businessHours" type="text" placeholder="如: 09:00 - 21:00" class="mt-1 block w-full border-gray-300 rounded-md shadow-sm focus:ring-blue-500 focus:border-blue-500 sm:text-sm p-2 bg-gray-50 border" />
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700">状态</label>
                <select v-model="storeForm.status" class="mt-1 block w-full border-gray-300 rounded-md shadow-sm focus:ring-blue-500 focus:border-blue-500 sm:text-sm p-2 bg-gray-50 border">
                  <option :value="1">营业中</option>
                  <option :value="2">暂停营业</option>
                  <option :value="3">已关闭</option>
                </select>
              </div>
            </div>
          </div>
          <div class="bg-gray-50 px-4 py-3 sm:px-6 sm:flex sm:flex-row-reverse">
            <button @click="handleSubmit" type="button" class="w-full inline-flex justify-center rounded-md border border-transparent shadow-sm px-4 py-2 bg-blue-600 text-base font-medium text-white hover:bg-blue-700 focus:outline-none sm:ml-3 sm:w-auto sm:text-sm" :disabled="submitting">
              {{ submitting ? '提交中...' : '提交' }}
            </button>
            <button @click="showModal = false" type="button" class="mt-3 w-full inline-flex justify-center rounded-md border border-gray-300 shadow-sm px-4 py-2 bg-white text-base font-medium text-gray-700 hover:bg-gray-50 focus:outline-none sm:mt-0 sm:ml-3 sm:w-auto sm:text-sm">
              取消
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { storeService } from '../../services/api'

const router = useRouter()
const stores = ref<any[]>([])
const loading = ref(false)
const submitting = ref(false)
const showModal = ref(false)
const isEdit = ref(false)
const currentStoreId = ref<number | null>(null)

const storeForm = reactive({
  name: '',
  address: '',
  phone: '',
  businessHours: '',
  status: 1
})

const fetchStores = async () => {
  loading.value = true
  try {
    const response = await storeService.getStores()
    stores.value = response.data || []
  } catch (error) {
    console.error('加载门店失败:', error)
  } finally {
    loading.value = false
  }
}

const handleAdd = () => {
  isEdit.value = false
  currentStoreId.value = null
  Object.assign(storeForm, {
    name: '',
    address: '',
    phone: '',
    businessHours: '',
    status: 1
  })
  showModal.value = true
}

const handleEdit = (store: any) => {
  isEdit.value = true
  currentStoreId.value = store.id
  Object.assign(storeForm, {
    name: store.name,
    address: store.address,
    phone: store.phone,
    businessHours: store.businessHours,
    status: store.status
  })
  showModal.value = true
}

const handleSubmit = async () => {
  if (!storeForm.name || !storeForm.address || !storeForm.phone) {
    alert('请填写必要信息')
    return
  }
  
  submitting.value = true
  try {
    if (isEdit.value && currentStoreId.value) {
      await storeService.updateStore(currentStoreId.value, storeForm)
    } else {
      await storeService.createStore(storeForm)
    }
    showModal.value = false
    fetchStores()
  } catch (error) {
    console.error('保存门店失败:', error)
    alert('保存失败，请重试')
  } finally {
    submitting.value = false
  }
}

const confirmDelete = async (store: any) => {
  if (confirm(`确定要删除门店 "${store.name}" 吗？此操作不可撤销。`)) {
    try {
      await storeService.deleteStore(store.id)
      fetchStores()
    } catch (error) {
      console.error('删除门店失败:', error)
      alert('删除失败，请重试')
    }
  }
}

const handleSchedule = (storeId: number) => {
  router.push({ name: 'EmployeeScheduling', query: { storeId } })
}

const getStatusClass = (status: number): string => {
  switch (status) {
    case 1: return 'bg-green-100 text-green-800'
    case 2: return 'bg-yellow-100 text-yellow-800'
    case 3: return 'bg-red-100 text-red-800'
    default: return 'bg-gray-100 text-gray-800'
  }
}

const getStatusName = (status: number): string => {
  switch (status) {
    case 1: return '营业中'
    case 2: return '暂停营业'
    case 3: return '已关闭'
    default: return '未知'
  }
}

onMounted(() => {
  fetchStores()
})
</script>

<style scoped>
.btn-primary {
  @apply px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 transition-colors flex items-center shadow-sm;
}

.btn-secondary {
  @apply px-4 py-2 bg-white text-gray-700 rounded-md hover:bg-gray-50 transition-colors flex items-center justify-center;
}

.card {
  @apply bg-white p-6 rounded-lg shadow-sm border border-gray-100;
}
</style>
