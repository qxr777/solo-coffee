<template>
  <div class="min-h-screen bg-gray-50 dark:bg-gray-900">
    <!-- Header -->
    <header class="bg-white dark:bg-gray-800 shadow-sm sticky top-0 z-10">
      <div class="container mx-auto px-4 py-4 flex items-center justify-between">
        <!-- Logo -->
        <div class="flex items-center">
          <div class="w-10 h-10 bg-primary rounded-full flex items-center justify-center mr-2">
            <span class="text-white text-xl font-bold">S</span>
          </div>
          <h1 class="text-xl font-bold text-gray-900 dark:text-white">Solo Coffee</h1>
        </div>
        
        <!-- Location and Search -->
        <div class="flex items-center space-x-4 flex-1 max-w-md mx-4">
          <div class="flex items-center bg-gray-100 dark:bg-gray-700 rounded-lg px-3 py-2 flex-1">
            <svg class="w-5 h-5 text-gray-400 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z"></path>
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 11a3 3 0 11-6 0 3 3 0 016 0z"></path>
            </svg>
            <span class="text-sm text-gray-600 dark:text-gray-300">
              {{ selectedStore ? selectedStore.name : '选择门店' }}
            </span>
          </div>
          <div class="relative">
            <input 
              type="text" 
              placeholder="搜索饮品、食品..." 
              class="bg-gray-100 dark:bg-gray-700 rounded-lg px-4 py-2 pl-10 text-sm w-64 focus:outline-none focus:ring-2 focus:ring-primary"
            >
            <svg class="w-5 h-5 text-gray-400 absolute left-3 top-1/2 transform -translate-y-1/2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"></path>
            </svg>
          </div>
        </div>
        
        <!-- User Menu -->
        <div class="flex items-center space-x-4">
          <button class="relative">
            <svg class="w-6 h-6 text-gray-600 dark:text-gray-300" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 11V7a4 4 0 00-8 0v4M5 9h14l1 12H4L5 9z"></path>
            </svg>
            <span class="absolute -top-1 -right-1 bg-red-500 text-white text-xs rounded-full w-4 h-4 flex items-center justify-center">3</span>
          </button>
          <button @click="navigateToCart" class="relative">
            <svg class="w-6 h-6 text-gray-600 dark:text-gray-300" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 11V7a4 4 0 00-8 0v4M5 9h14l1 12H4L5 9z"></path>
            </svg>
            <span class="absolute -top-1 -right-1 bg-primary text-white text-xs rounded-full w-4 h-4 flex items-center justify-center">2</span>
          </button>
          <button @click="navigateToProfile" class="w-10 h-10 rounded-full bg-gray-200 dark:bg-gray-700 flex items-center justify-center">
            <svg class="w-5 h-5 text-gray-600 dark:text-gray-300" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"></path>
            </svg>
          </button>
        </div>
      </div>
    </header>

    <main class="container mx-auto px-4 py-6">
      <!-- Carousel -->
      <section class="mb-8">
        <div class="relative rounded-2xl overflow-hidden h-64 bg-gray-200 dark:bg-gray-700">
          <div class="absolute inset-0 flex items-center justify-center">
            <img 
              v-for="(promotion, index) in promotionRecommendations" 
              :key="promotion.id"
              :src="promotion.image" 
              :alt="promotion.title"
              class="w-full h-full object-cover transition-opacity duration-500"
              :class="{ 'opacity-100': currentSlide === index, 'opacity-0': currentSlide !== index }"
            >
            <!-- Fallback image if no promotions -->
            <img 
              v-if="promotionRecommendations.length === 0"
              src="https://neeko-copilot.bytedance.net/api/text2image?prompt=coffee%20shop%20promotion%20banner%20with%20latte%20and%20pastries&size=1024x512"
              alt="Solo Coffee Promotion"
              class="w-full h-full object-cover"
            >
          </div>
          
          <!-- Carousel Controls -->
          <div class="absolute bottom-4 left-0 right-0 flex justify-center space-x-2">
            <button 
              v-for="(_, index) in promotionRecommendations" 
              :key="index"
              @click="currentSlide = index"
              class="w-2 h-2 rounded-full transition-all duration-300"
              :class="{ 'bg-white w-6': currentSlide === index, 'bg-white/50': currentSlide !== index }"
            ></button>
          </div>
        </div>
      </section>

      <!-- Quick Access -->
      <section class="mb-8">
        <div class="grid grid-cols-4 gap-4">
          <button @click="navigateToMenu" class="flex flex-col items-center p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm hover:shadow-md transition-shadow">
            <div class="w-12 h-12 bg-primary/10 rounded-full flex items-center justify-center mb-2">
              <svg class="w-6 h-6 text-primary" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253"></path>
              </svg>
            </div>
            <span class="text-sm font-medium text-gray-900 dark:text-white">菜单</span>
          </button>
          
          <button @click="navigateToStores" class="flex flex-col items-center p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm hover:shadow-md transition-shadow">
            <div class="w-12 h-12 bg-secondary/10 rounded-full flex items-center justify-center mb-2">
              <svg class="w-6 h-6 text-secondary" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z"></path>
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 11a3 3 0 11-6 0 3 3 0 016 0z"></path>
              </svg>
            </div>
            <span class="text-sm font-medium text-gray-900 dark:text-white">门店</span>
          </button>
          
          <button @click="navigateToMember" class="flex flex-col items-center p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm hover:shadow-md transition-shadow">
            <div class="w-12 h-12 bg-coffee/10 rounded-full flex items-center justify-center mb-2">
              <svg class="w-6 h-6 text-coffee" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"></path>
              </svg>
            </div>
            <span class="text-sm font-medium text-gray-900 dark:text-white">会员</span>
          </button>
          
          <button @click="navigateToPreOrder" class="flex flex-col items-center p-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm hover:shadow-md transition-shadow">
            <div class="w-12 h-12 bg-success/10 rounded-full flex items-center justify-center mb-2">
              <svg class="w-6 h-6 text-success" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"></path>
              </svg>
            </div>
            <span class="text-sm font-medium text-gray-900 dark:text-white">预点单</span>
          </button>
        </div>
      </section>

      <!-- Personalized Recommendations -->
      <section class="mb-8">
        <div class="flex items-center justify-between mb-4">
          <h2 class="text-xl font-bold text-gray-900 dark:text-white">为你推荐</h2>
          <button @click="refreshRecommendations" class="flex items-center text-sm text-primary hover:text-primary/80">
            换一批
            <svg class="w-4 h-4 ml-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15"></path>
            </svg>
          </button>
        </div>
        
        <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
          <div 
            v-for="recommendation in recommendations" 
            :key="recommendation.product.id"
            @click="navigateToProduct(recommendation.product.id)"
            class="bg-white dark:bg-gray-800 rounded-xl overflow-hidden shadow-sm hover:shadow-md transition-all duration-300 hover:-translate-y-1 cursor-pointer"
          >
            <div class="relative h-48 overflow-hidden">
              <img :src="recommendation.product.image" :alt="recommendation.product.name" class="w-full h-full object-cover">
              <div v-if="recommendation.product.isNew" class="absolute top-2 left-2 bg-primary text-white text-xs px-2 py-1 rounded-full">新品</div>
              <div v-if="recommendation.product.isHot" class="absolute top-2 right-2 bg-secondary text-white text-xs px-2 py-1 rounded-full">热门</div>
            </div>
            <div class="p-4">
              <h3 class="font-semibold text-gray-900 dark:text-white mb-1">{{ recommendation.product.name }}</h3>
              <p class="text-sm text-gray-600 dark:text-gray-400 mb-2 line-clamp-2">{{ recommendation.product.description }}</p>
              <div class="flex items-center justify-between">
                <span class="font-bold text-primary">¥{{ recommendation.product.price }}</span>
                <button @click.stop="addToCart(recommendation.product)" class="p-2 bg-primary/10 text-primary rounded-full hover:bg-primary/20 transition-colors">
                  <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 11V7a4 4 0 00-8 0v4M5 9h14l1 12H4L5 9z"></path>
                  </svg>
                </button>
              </div>
            </div>
          </div>
          
          <!-- Placeholder if no recommendations -->
          <div v-if="recommendations.length === 0" class="col-span-full flex flex-col items-center justify-center p-8 bg-white dark:bg-gray-800 rounded-xl">
            <svg class="w-16 h-16 text-gray-300 dark:text-gray-600 mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19.428 15.428a2 2 0 00-1.022-.547l-2.387-.477a6 6 0 00-3.86.517l-.318.158a6 6 0 01-3.86.517L6.05 15.21a2 2 0 00-1.806.547M8 4h8l-1 1v5.172a2 2 0 00.586 1.414l5 5c1.26 1.26.367 3.414-1.415 3.414H4.828c-1.782 0-2.674-2.154-1.414-3.414l5-5A2 2 0 009 10.172V5L8 4z"></path>
            </svg>
            <p class="text-gray-500 dark:text-gray-400">暂无推荐内容</p>
          </div>
        </div>
      </section>

      <!-- Product Combinations -->
      <section class="mb-8">
        <h2 class="text-xl font-bold text-gray-900 dark:text-white mb-4">超值组合</h2>
        
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div 
            v-for="combination in productCombinations" 
            :key="combination.id"
            class="bg-white dark:bg-gray-800 rounded-xl overflow-hidden shadow-sm hover:shadow-md transition-shadow flex cursor-pointer"
          >
            <div class="flex-1">
              <div class="relative h-48 overflow-hidden">
                <img :src="combination.products[0].image" :alt="combination.products[0].name" class="w-full h-full object-cover">
              </div>
              <div class="flex-1 p-4">
                <h3 class="font-semibold text-gray-900 dark:text-white mb-1">{{ combination.products[0].name }}</h3>
                <p class="text-sm text-gray-600 dark:text-gray-400 mb-2">{{ combination.products[0].description }}</p>
                <div class="flex items-center justify-between">
                  <span class="font-bold text-primary">¥{{ combination.products[0].price }}</span>
                  <button class="px-3 py-1 bg-primary text-white text-sm rounded-full hover:bg-primary/90 transition-colors">
                    查看详情
                  </button>
                </div>
              </div>
            </div>
          </div>
          
          <!-- Placeholder if no combinations -->
          <div v-if="productCombinations.length === 0" class="col-span-full flex flex-col items-center justify-center p-8 bg-white dark:bg-gray-800 rounded-xl">
            <svg class="w-16 h-16 text-gray-300 dark:text-gray-600 mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 11V7a4 4 0 00-8 0v4M5 9h14l1 12H4L5 9z"></path>
            </svg>
            <p class="text-gray-500 dark:text-gray-400">暂无组合优惠</p>
          </div>
        </div>
      </section>
    </main>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useRecommendationStore } from '../store/recommendationStore'
import { useAuthStore } from '../store/authStore'
import { useCartStore } from '../store/cartStore'

const router = useRouter()
const recommendationStore = useRecommendationStore()
const authStore = useAuthStore()
const cartStore = useCartStore()

// State
const currentSlide = ref(0)
const selectedStore = ref<any>(null)
let slideInterval: number | null = null

// Computed
const recommendations = computed(() => recommendationStore.personalizedRecommendations || [])
const promotionRecommendations = computed(() => recommendationStore.promotionRecommendations || [])
const productCombinations = computed(() => recommendationStore.productCombinations || [])

// Methods
const navigateToCart = () => {
  router.push('/cart')
}

const navigateToProfile = () => {
  if (authStore.isAuthenticated) {
    router.push('/profile')
  } else {
    router.push('/login')
  }
}

const navigateToMenu = () => {
  router.push('/products')
}

const navigateToStores = () => {
  router.push('/stores')
}

const navigateToMember = () => {
  if (authStore.isAuthenticated) {
    router.push('/member')
  } else {
    router.push('/login')
  }
}

const navigateToPreOrder = () => {
  if (authStore.isAuthenticated) {
    router.push('/pre-order')
  } else {
    router.push('/login')
  }
}

const navigateToProduct = (productId: number) => {
  router.push(`/product/${productId}`)
}

const addToCart = (product: any) => {
  cartStore.addToCart({
    productId: product.id,
    productNo: product.productNo,
    name: product.name,
    price: product.price,
    quantity: 1,
    imageUrl: product.imageUrl
  }) // 显示添加成功提示
  alert('已添加到购物车')
}

const refreshRecommendations = () => {
  recommendationStore.getPersonalizedRecommendations()
}

// Lifecycle
onMounted(() => {
  // Load recommendations
  recommendationStore.getPersonalizedRecommendations()
  recommendationStore.getPromotionRecommendations()
  recommendationStore.getProductCombinations()
  
  // Start carousel
  slideInterval = window.setInterval(() => {
    const promotions = promotionRecommendations.value || []
    currentSlide.value = (currentSlide.value + 1) % (promotions.length || 1)
  }, 5000)
})

onUnmounted(() => {
  if (slideInterval) {
    clearInterval(slideInterval)
  }
})
</script>
