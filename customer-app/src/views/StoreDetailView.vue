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
        <h1 class="text-xl font-bold text-gray-900 dark:text-white">门店详情</h1>
        <button @click="toggleFavorite" class="p-2 rounded-full hover:bg-gray-100 dark:hover:bg-gray-700 transition-colors">
          <svg class="w-6 h-6" fill="currentColor" viewBox="0 0 24 24">
            <path v-if="store?.isFavorite" d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z" class="text-primary"></path>
            <path v-else d="M16.5 3c-1.74 0-3.41.81-4.5 2.09C10.91 3.81 9.24 3 7.5 3 4.42 3 2 5.42 2 8.5c0 3.78 3.4 6.86 8.55 11.54L12 21.35l1.45-1.31C18.6 15.36 22 12.28 22 8.5 22 5.42 19.58 3 16.5 3zm-4.4 15.55l-.1.1-.1-.1C7.14 14.24 4 11.39 4 8.5 4 6.5 5.5 5 7.5 5c1.54 0 3.04.99 3.57 2.36h1.87C13.46 5.99 14.96 5 16.5 5c2 0 3.5 1.5 3.5 3.5 0 2.89-3.14 5.74-7.9 10.05z" class="text-gray-400"></path>
          </svg>
        </button>
      </div>
    </header>

    <main class="container mx-auto px-4 py-6">
      <!-- Store Image -->
      <div class="mb-6 rounded-xl overflow-hidden h-64 bg-gray-200 dark:bg-gray-700">
        <img 
          :src="store?.image" 
          :alt="store?.name" 
          class="w-full h-full object-cover"
        >
      </div>

      <!-- Store Info -->
      <div class="bg-white dark:bg-gray-800 rounded-xl p-6 shadow-sm mb-6">
        <h2 class="text-xl font-bold text-gray-900 dark:text-white mb-2">{{ store?.name }}</h2>
        
        <div class="space-y-3 mb-4">
          <div class="flex items-start">
            <svg class="w-5 h-5 text-gray-400 mr-3 mt-0.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z"></path>
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 11a3 3 0 11-6 0 3 3 0 016 0z"></path>
            </svg>
            <div>
              <h3 class="font-medium text-gray-700 dark:text-gray-300">地址</h3>
              <p class="text-gray-600 dark:text-gray-400">{{ store?.address }}</p>
            </div>
          </div>
          
          <div class="flex items-start">
            <svg class="w-5 h-5 text-gray-400 mr-3 mt-0.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 5a2 2 0 012-2h3.28a1 1 0 01.948.684l1.498 4.493a1 1 0 01-.502 1.21l-2.257 1.13a11.042 11.042 0 005.516 5.516l1.13-2.257a1 1 0 011.21-.502l4.493 1.498a1 1 0 01.684.949V19a2 2 0 01-2 2h-1C9.716 21 3 14.284 3 6V5z"></path>
            </svg>
            <div>
              <h3 class="font-medium text-gray-700 dark:text-gray-300">电话</h3>
              <p class="text-gray-600 dark:text-gray-400">{{ store?.phone }}</p>
            </div>
          </div>
          
          <div class="flex items-start">
            <svg class="w-5 h-5 text-gray-400 mr-3 mt-0.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"></path>
            </svg>
            <div>
              <h3 class="font-medium text-gray-700 dark:text-gray-300">营业时间</h3>
              <p class="text-gray-600 dark:text-gray-400">{{ store?.openingHours }}</p>
              <p v-if="store?.isOpen" class="text-sm text-success mt-1">当前状态：营业中</p>
              <p v-else class="text-sm text-gray-500 dark:text-gray-400 mt-1">当前状态：休息中</p>
            </div>
          </div>
          
          <div class="flex items-start">
            <svg class="w-5 h-5 text-gray-400 mr-3 mt-0.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"></path>
            </svg>
            <div>
              <h3 class="font-medium text-gray-700 dark:text-gray-300">门店特色</h3>
              <div class="flex flex-wrap gap-2 mt-1">
                <span 
                  v-for="(feature, index) in store?.features" 
                  :key="index"
                  class="px-3 py-1 bg-gray-100 dark:bg-gray-700 rounded-full text-sm text-gray-600 dark:text-gray-400"
                >
                  {{ feature }}
                </span>
              </div>
            </div>
          </div>
          
          <div class="flex items-start">
            <svg class="w-5 h-5 text-gray-400 mr-3 mt-0.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path>
            </svg>
            <div>
              <h3 class="font-medium text-gray-700 dark:text-gray-300">位置</h3>
              <p class="text-gray-600 dark:text-gray-400">{{ store?.distance ? `距离您约 ${store.distance}km` : '未知距离' }}</p>
            </div>
          </div>
        </div>
        
        <!-- Store Rating -->
        <div class="flex items-center justify-between pt-4 border-t border-gray-100 dark:border-gray-700">
          <div class="flex items-center">
            <span class="flex items-center text-lg font-bold text-primary">
              <svg class="w-5 h-5 mr-1" fill="currentColor" viewBox="0 0 20 20">
                <path d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z"></path>
              </svg>
              {{ store?.rating }}
            </span>
            <span class="text-gray-500 dark:text-gray-400 ml-2">{{ store?.reviewCount }}条评价</span>
          </div>
          <button @click="callStore" class="flex items-center text-primary hover:text-primary/80">
            <svg class="w-5 h-5 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 5a2 2 0 012-2h3.28a1 1 0 01.948.684l1.498 4.493a1 1 0 01-.502 1.21l-2.257 1.13a11.042 11.042 0 005.516 5.516l1.13-2.257a1 1 0 011.21-.502l4.493 1.498a1 1 0 01.684.949V19a2 2 0 01-2 2h-1C9.716 21 3 14.284 3 6V5z"></path>
            </svg>
            打电话
          </button>
        </div>
      </div>

      <!-- Action Buttons -->
      <div class="fixed bottom-0 left-0 right-0 bg-white dark:bg-gray-800 shadow-lg p-4">
        <div class="container mx-auto flex flex-col space-y-3">
          <button 
            @click="selectStore"
            class="w-full bg-primary text-white py-3 rounded-lg font-semibold hover:bg-primary/90 transition-colors flex items-center justify-center"
          >
            <svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"></path>
            </svg>
            选择此门店
          </button>
          <div class="flex space-x-3">
            <button @click="navigateToMenu" class="flex-1 bg-white dark:bg-gray-700 text-primary border border-primary py-2 rounded-lg font-medium hover:bg-primary/5 transition-colors flex items-center justify-center">
              <svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253"></path>
              </svg>
              查看菜单
            </button>
            <button @click="navigateToMap" class="flex-1 bg-white dark:bg-gray-700 text-gray-600 dark:text-gray-300 border border-gray-300 dark:border-gray-600 py-2 rounded-lg font-medium hover:bg-gray-50 dark:hover:bg-gray-600 transition-colors flex items-center justify-center">
              <svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z"></path>
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 11a3 3 0 11-6 0 3 3 0 016 0z"></path>
              </svg>
              查看地图
            </button>
          </div>
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
import { computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useStoreStore } from '../store/storeStore'

const router = useRouter()
const route = useRoute()
const storeStore = useStoreStore()

// Computed
const store = computed(() => storeStore.getSelectedStore)

// Methods
const goBack = () => {
  router.back()
}

const toggleFavorite = async () => {
  if (store.value) {
    try {
      await storeStore.toggleFavorite(store.value.id, !store.value.isFavorite)
    } catch (error) {
      console.error('切换收藏状态失败:', error)
    }
  }
}

const selectStore = () => {
  if (store.value) {
    storeStore.selectStore(store.value)
    router.push('/')
  }
}

const navigateToMenu = () => {
  if (store.value) {
    storeStore.selectStore(store.value)
    router.push('/products')
  }
}

const navigateToMap = () => {
  if (store.value) {
    // 这里可以集成地图API，或者打开外部地图应用
    const url = `https://maps.google.com/?q=${store.value.latitude},${store.value.longitude}`
    window.open(url, '_blank')
  }
}

const callStore = () => {
  if (store.value) {
    window.location.href = `tel:${store.value.phone}`
  }
}

// Lifecycle
onMounted(async () => {
  const storeId = Number(route.params.id)
  if (storeId) {
    try {
      await storeStore.getStoreDetail(storeId)
    } catch (error) {
      console.error('获取门店详情失败:', error)
    }
  }
})
</script>
