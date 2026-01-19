<template>
  <div class="min-h-screen bg-gray-50 dark:bg-gray-900">
    <!-- Header -->
    <header class="bg-white dark:bg-gray-800 shadow-sm sticky top-0 z-10">
      <div class="container mx-auto px-4 py-4 flex items-center justify-between">
        <button @click="goBack" class="p-2 rounded-full hover:bg-gray-100 dark:hover:bg-gray-700 transition-colors">
          <svg class="w-6 h-6 text-gray-600 dark:text-gray-300" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7"></path>
          </svg>
        </button>
        <h1 class="text-xl font-bold text-gray-900 dark:text-white">选择门店</h1>
        <div class="w-10"></div> <!-- Placeholder for balance -->
      </div>
      
      <!-- Search Bar -->
      <div class="container mx-auto px-4 pb-4">
        <div class="relative">
          <input 
            type="text" 
            v-model="searchKeyword"
            @input="handleSearch"
            placeholder="搜索门店名称或地址" 
            class="w-full bg-gray-100 dark:bg-gray-700 rounded-lg px-4 py-3 pl-12 focus:outline-none focus:ring-2 focus:ring-primary"
          >
          <svg class="w-5 h-5 text-gray-400 absolute left-4 top-1/2 transform -translate-y-1/2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"></path>
          </svg>
        </div>
      </div>
    </header>

    <main class="container mx-auto px-4 py-6">
      <!-- Location Permission -->
      <div v-if="!hasLocationPermission" class="mb-6 bg-blue-50 dark:bg-blue-900/20 border border-blue-200 dark:border-blue-800 rounded-lg p-4">
        <div class="flex items-start">
          <svg class="w-5 h-5 text-blue-500 mr-3 mt-0.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path>
          </svg>
          <div>
            <h3 class="font-medium text-blue-800 dark:text-blue-300">需要位置权限</h3>
            <p class="text-sm text-blue-600 dark:text-blue-400 mt-1">请允许访问您的位置，以便我们为您推荐附近的门店</p>
            <button @click="requestLocationPermission" class="mt-2 text-sm text-blue-700 dark:text-blue-300 font-medium hover:underline">
              允许位置访问
            </button>
          </div>
        </div>
      </div>

      <!-- Store Filters -->
      <div class="mb-6 flex items-center space-x-2 overflow-x-auto pb-2">
        <button 
          v-for="filter in filters" 
          :key="filter.id"
          @click="selectedFilter = filter.id"
          class="px-4 py-2 rounded-full text-sm font-medium whitespace-nowrap transition-all"
          :class="{ 
            'bg-primary text-white': selectedFilter === filter.id, 
            'bg-white dark:bg-gray-800 text-gray-600 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-700': selectedFilter !== filter.id 
          }"
        >
          {{ filter.name }}
        </button>
      </div>

      <!-- Store List -->
      <div class="space-y-4">
        <div 
          v-for="store in displayedStores" 
          :key="store.id"
          @click="navigateToStoreDetail(store.id)"
          class="bg-white dark:bg-gray-800 rounded-xl overflow-hidden shadow-sm hover:shadow-md transition-shadow cursor-pointer"
        >
          <div class="flex">
            <div class="w-1/4">
              <img :src="store.image" :alt="store.name" class="w-full h-full object-cover min-h-24">
            </div>
            <div class="w-3/4 p-4 flex flex-col justify-between">
              <div>
                <div class="flex items-start justify-between mb-1">
                  <h3 class="font-semibold text-gray-900 dark:text-white">{{ store.name }}</h3>
                  <button @click.stop="toggleFavorite(store)" class="p-1 rounded-full hover:bg-gray-100 dark:hover:bg-gray-700">
                    <svg class="w-5 h-5" fill="currentColor" viewBox="0 0 24 24">
                      <path v-if="store.isFavorite" d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z"></path>
                      <path v-else d="M16.5 3c-1.74 0-3.41.81-4.5 2.09C10.91 3.81 9.24 3 7.5 3 4.42 3 2 5.42 2 8.5c0 3.78 3.4 6.86 8.55 11.54L12 21.35l1.45-1.31C18.6 15.36 22 12.28 22 8.5 22 5.42 19.58 3 16.5 3zm-4.4 15.55l-.1.1-.1-.1C7.14 14.24 4 11.39 4 8.5 4 6.5 5.5 5 7.5 5c1.54 0 3.04.99 3.57 2.36h1.87C13.46 5.99 14.96 5 16.5 5c2 0 3.5 1.5 3.5 3.5 0 2.89-3.14 5.74-7.9 10.05z"></path>
                    </svg>
                  </button>
                </div>
                <div class="flex items-center text-sm text-gray-600 dark:text-gray-400 mb-1">
                  <svg class="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z"></path>
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 11a3 3 0 11-6 0 3 3 0 016 0z"></path>
                  </svg>
                  <span>{{ store.address }}</span>
                </div>
                <div class="flex items-center text-sm text-gray-600 dark:text-gray-400 mb-1">
                  <svg class="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 5a2 2 0 012-2h3.28a1 1 0 01.948.684l1.498 4.493a1 1 0 01-.502 1.21l-2.257 1.13a11.042 11.042 0 005.516 5.516l1.13-2.257a1 1 0 011.21-.502l4.493 1.498a1 1 0 01.684.949V19a2 2 0 01-2 2h-1C9.716 21 3 14.284 3 6V5z"></path>
                  </svg>
                  <span>{{ store.phone }}</span>
                </div>
              </div>
              <div class="flex items-center justify-between">
                <div class="flex items-center">
                  <span class="flex items-center text-sm font-medium text-primary mr-3">
                    <svg class="w-4 h-4 mr-1" fill="currentColor" viewBox="0 0 20 20">
                      <path d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z"></path>
                    </svg>
                    {{ store.rating }}
                  </span>
                  <span class="text-sm text-gray-500 dark:text-gray-400">{{ store.reviewCount }}条评价</span>
                </div>
                <div class="flex items-center">
                  <span v-if="store.isOpen" class="text-sm text-success mr-2">营业中</span>
                  <span v-else class="text-sm text-gray-500 dark:text-gray-400 mr-2">休息中</span>
                  <span class="text-sm text-gray-600 dark:text-gray-400">{{ store.distance ? `${store.distance}km` : '未知距离' }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
        
        <!-- Empty State -->
        <div v-if="displayedStores.length === 0" class="flex flex-col items-center justify-center p-8 bg-white dark:bg-gray-800 rounded-xl">
          <svg class="w-16 h-16 text-gray-300 dark:text-gray-600 mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z"></path>
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 11a3 3 0 11-6 0 3 3 0 016 0z"></path>
          </svg>
          <p class="text-gray-500 dark:text-gray-400">{{ searchKeyword ? '没有找到匹配的门店' : '附近暂无门店' }}</p>
        </div>
      </div>

      <!-- Loading -->
      <div v-if="storeStore.isLoading" class="fixed inset-0 bg-white/80 dark:bg-gray-900/80 flex items-center justify-center z-50">
        <div class="bg-white dark:bg-gray-800 rounded-xl p-6 shadow-lg flex flex-col items-center">
          <div class="w-10 h-10 border-4 border-primary border-t-transparent rounded-full animate-spin mb-4"></div>
          <p class="text-gray-600 dark:text-gray-300">加载中...</p>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useStoreStore } from '../store/storeStore'

const router = useRouter()
const storeStore = useStoreStore()

// State
const searchKeyword = ref('')
const selectedFilter = ref('nearby')
const hasLocationPermission = ref(true)
let searchTimeout: number | null = null

// Filters
const filters = [
  { id: 'nearby', name: '附近' },
  { id: 'favorites', name: '收藏' },
  { id: 'recent', name: '最近' },
  { id: 'all', name: '全部' }
]

// Computed
const displayedStores = computed(() => {
  switch (selectedFilter.value) {
    case 'nearby':
      return storeStore.getAllNearbyStores
    case 'favorites':
      return storeStore.getAllFavoriteStores
    case 'all':
      return storeStore.getStores.length > 0 ? storeStore.getStores : storeStore.getAllNearbyStores
    default:
      return storeStore.getAllNearbyStores
  }
})

// Methods
const goBack = () => {
  router.back()
}

const navigateToStoreDetail = (storeId: number) => {
  router.push(`/store/${storeId}`)
}

const toggleFavorite = async (store: any) => {
  try {
    await storeStore.toggleFavorite(store.id, !store.isFavorite)
  } catch (error) {
    console.error('切换收藏状态失败:', error)
  }
}

const handleSearch = () => {
  if (searchTimeout) {
    clearTimeout(searchTimeout)
  }
  
  searchTimeout = window.setTimeout(async () => {
    if (searchKeyword.value.trim()) {
      try {
        await storeStore.searchStores({
          keyword: searchKeyword.value.trim()
        })
        selectedFilter.value = 'all'
      } catch (error) {
        console.error('搜索失败:', error)
      }
    } else {
      // 清空搜索，返回附近门店
      try {
        await storeStore.getNearbyStores()
        selectedFilter.value = 'nearby'
      } catch (error) {
        console.error('获取附近门店失败:', error)
      }
    }
  }, 300)
}

const requestLocationPermission = async () => {
  try {
    const location = await storeStore.getCurrentPosition()
    hasLocationPermission.value = true
    // 重新获取附近门店
    await storeStore.getNearbyStores({
      latitude: location.latitude,
      longitude: location.longitude
    })
  } catch (error) {
    console.error('获取位置失败:', error)
    hasLocationPermission.value = false
  }
}

// Lifecycle
onMounted(async () => {
  try {
    // 加载附近门店
    await storeStore.getNearbyStores()
    // 加载收藏门店
    await storeStore.getFavoriteStores()
  } catch (error) {
    console.error('加载门店失败:', error)
  }
})
</script>
