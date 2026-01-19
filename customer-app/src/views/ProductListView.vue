<template>
  <div class="min-h-screen bg-gray-50 dark:bg-gray-900">
    <!-- Header -->
    <header class="bg-white dark:bg-gray-800 shadow-sm sticky top-0 z-50">
      <div class="container mx-auto px-4 py-4 flex justify-between items-center">
        <div class="flex items-center space-x-2">
          <button @click="navigateBack" class="text-gray-600 dark:text-gray-300 hover:text-gray-900 dark:hover:text-white">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7" />
            </svg>
          </button>
          <h1 class="text-xl font-bold text-gray-900 dark:text-white">Menu</h1>
        </div>
        <div class="flex items-center space-x-4">
          <button @click="navigateToCart" class="text-gray-600 dark:text-gray-300 hover:text-gray-900 dark:hover:text-white relative">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 11V7a4 4 0 00-8 0v4M5 9h14l1 12H4L5 9z" />
            </svg>
            <span v-if="cartCount > 0" class="absolute -top-2 -right-2 bg-red-500 text-white text-xs rounded-full w-5 h-5 flex items-center justify-center">
              {{ cartCount }}
            </span>
          </button>
        </div>
      </div>
    </header>

    <main class="container mx-auto px-4 py-8">
      <!-- Search and Filter -->
      <section class="mb-8">
        <div class="bg-white dark:bg-gray-800 rounded-xl p-4 shadow-sm">
          <div class="flex items-center mb-4">
            <div class="relative flex-1">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
              </svg>
              <input 
                type="text" 
                v-model="searchQuery"
                placeholder="Search products..." 
                class="w-full pl-10 pr-4 py-2 border border-gray-300 dark:border-gray-700 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 dark:bg-gray-700 dark:text-white"
              >
            </div>
          </div>
          <div class="flex flex-wrap gap-2">
            <button 
              v-for="category in categories" 
              :key="category.id"
              @click="filterByCategory(category.id)"
              :class="[
                'px-4 py-2 rounded-full text-sm font-medium transition',
                selectedCategory === category.id 
                  ? 'bg-blue-600 text-white' 
                  : 'bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300 hover:bg-gray-200 dark:hover:bg-gray-600'
              ]"
            >
              {{ category.name }}
            </button>
            <button 
              @click="clearFilters"
              class="px-4 py-2 rounded-full text-sm font-medium bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300 hover:bg-gray-200 dark:hover:bg-gray-600 transition"
            >
              All
            </button>
          </div>
        </div>
      </section>

      <!-- Loading State -->
      <div v-if="loading" class="flex items-center justify-center py-20">
        <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
      </div>

      <!-- Error State -->
      <div v-else-if="error" class="text-center py-12">
        <div class="text-6xl mb-4">⚠️</div>
        <h3 class="text-xl font-semibold mb-2 text-gray-900 dark:text-white">Error loading products</h3>
        <p class="text-gray-600 dark:text-gray-400 mb-6">{{ error }}</p>
        <button 
          @click="fetchData"
          class="bg-blue-600 text-white px-6 py-2 rounded-full font-medium hover:bg-blue-700 transition"
        >
          Try Again
        </button>
      </div>

      <!-- Products Grid -->
      <section v-else>
        <div class="grid grid-cols-2 gap-4">
          <div 
            v-for="product in filteredProducts" 
            :key="product.id" 
            @click="navigateToProduct(product.id)"
            class="bg-white dark:bg-gray-800 rounded-xl overflow-hidden shadow-sm hover:shadow-md transition cursor-pointer"
          >
            <div class="h-48 bg-gray-200 dark:bg-gray-700 flex items-center justify-center">
              <img 
                v-if="product.imageUrl" 
                :src="product.imageUrl" 
                :alt="product.name" 
                class="w-full h-full object-cover"
              >
              <span v-else class="text-4xl">☕</span>
            </div>
            <div class="p-4">
              <h3 class="font-semibold text-gray-900 dark:text-white mb-2">{{ product.name }}</h3>
              <p class="text-sm text-gray-600 dark:text-gray-400 mb-3 line-clamp-2">{{ product.description }}</p>
              <div class="flex justify-between items-center">
                <span class="font-bold text-gray-900 dark:text-white">${{ product.price.toFixed(2) }}</span>
                <button 
                  @click.stop="addToCart(product)"
                  class="bg-blue-600 text-white px-3 py-1 rounded-full text-sm hover:bg-blue-700 transition"
                >
                  Add
                </button>
              </div>
            </div>
          </div>
        </div>

        <!-- Empty State -->
        <div v-if="filteredProducts.length === 0" class="text-center py-12">
          <div class="text-6xl mb-4">🔍</div>
          <h3 class="text-xl font-semibold mb-2 text-gray-900 dark:text-white">No products found</h3>
          <p class="text-gray-600 dark:text-gray-400 mb-6">Try adjusting your search or filters</p>
          <button 
            @click="clearFilters"
            class="bg-blue-600 text-white px-6 py-2 rounded-full font-medium hover:bg-blue-700 transition"
          >
            Clear Filters
          </button>
        </div>
      </section>
    </main>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useProductStore } from '../store/productStore'
import { useCartStore } from '../store/cartStore'

const router = useRouter()
const route = useRoute() || { query: {} }
const productStore = useProductStore()
const cartStore = useCartStore()

const searchQuery = ref('')
const selectedCategory = ref(Number(route.query?.category) || 0)

const loading = computed(() => productStore.loading)
const error = computed(() => productStore.error)
const categories = computed(() => productStore.allCategories)
const products = computed(() => productStore.activeProducts)

const filteredProducts = computed(() => {
  return products.value.filter(product => {
    const matchesSearch = product.name.toLowerCase().includes(searchQuery.value.toLowerCase()) || 
                          product.description.toLowerCase().includes(searchQuery.value.toLowerCase())
    const matchesCategory = selectedCategory.value === 0 || product.categoryId === selectedCategory.value
    return matchesSearch && matchesCategory
  })
})

const cartCount = computed(() => cartStore.cartCount)

const navigateBack = () => {
  router.back()
}

const navigateToCart = () => {
  router.push('/cart')
}

const navigateToProduct = (productId: number) => {
  router.push(`/product/${productId}`)
}

const filterByCategory = (categoryId: number) => {
  selectedCategory.value = categoryId
}

const clearFilters = () => {
  selectedCategory.value = 0
  searchQuery.value = ''
}

const addToCart = (product: any) => {
  cartStore.addToCart({
    productId: product.id,
    productNo: product.productNo,
    name: product.name,
    price: product.price,
    quantity: 1,
    imageUrl: product.imageUrl
  })
}

const fetchData = async () => {
  try {
    await productStore.fetchProducts()
    await productStore.fetchCategories()
  } catch (error) {
    console.error('Failed to fetch data:', error)
  }
}

onMounted(async () => {
  await fetchData()
})
</script>