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
          <h1 class="text-xl font-bold text-gray-900 dark:text-white">Product Details</h1>
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
      <!-- Loading State -->
      <div v-if="loading" class="flex items-center justify-center py-20">
        <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
      </div>

      <!-- Error State -->
      <div v-else-if="error" class="text-center py-12">
        <div class="text-6xl mb-4">⚠️</div>
        <h3 class="text-xl font-semibold mb-2 text-gray-900 dark:text-white">Error loading product</h3>
        <p class="text-gray-600 dark:text-gray-400 mb-6">{{ error }}</p>
        <button 
          @click="fetchProduct"
          class="bg-blue-600 text-white px-6 py-2 rounded-full font-medium hover:bg-blue-700 transition"
        >
          Try Again
        </button>
      </div>

      <div v-else-if="product" class="bg-white dark:bg-gray-800 rounded-xl overflow-hidden shadow-sm">
        <!-- Product Image -->
        <div class="h-64 bg-gray-200 dark:bg-gray-700 flex items-center justify-center">
          <img 
            v-if="product.imageUrl" 
            :src="product.imageUrl" 
            :alt="product.name" 
            class="w-full h-full object-cover"
          >
          <span v-else class="text-6xl">☕</span>
        </div>

        <!-- Product Info -->
        <div class="p-6">
          <h2 class="text-2xl font-bold mb-2 text-gray-900 dark:text-white">{{ product.name }}</h2>
          <p class="text-gray-600 dark:text-gray-400 mb-4">{{ product.description }}</p>
          
          <div class="mb-6">
            <span class="text-3xl font-bold text-gray-900 dark:text-white">${{ product.price.toFixed(2) }}</span>
          </div>

          <!-- Product Customization -->
          <div class="mb-8 space-y-6">
            <!-- Size Options -->
            <div>
              <h3 class="text-lg font-semibold mb-3 text-gray-900 dark:text-white">Size</h3>
              <div class="flex flex-wrap gap-3">
                <button 
                  v-for="size in sizes" 
                  :key="size.value"
                  @click="selectedSize = size.value"
                  :class="[
                    'px-4 py-2 rounded-lg border transition',
                    selectedSize === size.value 
                      ? 'border-blue-600 bg-blue-50 text-blue-600 dark:border-blue-500 dark:bg-blue-900/20 dark:text-blue-400' 
                      : 'border-gray-300 text-gray-700 dark:border-gray-700 dark:text-gray-300 hover:border-gray-400'
                  ]"
                >
                  {{ size.label }}
                </button>
              </div>
            </div>

            <!-- Temperature Options -->
            <div>
              <h3 class="text-lg font-semibold mb-3 text-gray-900 dark:text-white">Temperature</h3>
              <div class="flex gap-3">
                <button 
                  @click="selectedTemperature = 'hot'"
                  :class="[
                    'flex-1 py-2 rounded-lg border transition',
                    selectedTemperature === 'hot' 
                      ? 'border-blue-600 bg-blue-50 text-blue-600 dark:border-blue-500 dark:bg-blue-900/20 dark:text-blue-400' 
                      : 'border-gray-300 text-gray-700 dark:border-gray-700 dark:text-gray-300 hover:border-gray-400'
                  ]"
                >
                  Hot
                </button>
                <button 
                  @click="selectedTemperature = 'iced'"
                  :class="[
                    'flex-1 py-2 rounded-lg border transition',
                    selectedTemperature === 'iced' 
                      ? 'border-blue-600 bg-blue-50 text-blue-600 dark:border-blue-500 dark:bg-blue-900/20 dark:text-blue-400' 
                      : 'border-gray-300 text-gray-700 dark:border-gray-700 dark:text-gray-300 hover:border-gray-400'
                  ]"
                >
                  Iced
                </button>
              </div>
            </div>

            <!-- Add-ons -->
            <div>
              <h3 class="text-lg font-semibold mb-3 text-gray-900 dark:text-white">Add-ons</h3>
              <div class="space-y-2">
                <label 
                  v-for="addon in addons" 
                  :key="addon.id"
                  class="flex items-center p-3 border rounded-lg cursor-pointer transition hover:bg-gray-50 dark:hover:bg-gray-700/50"
                >
                  <input 
                    type="checkbox" 
                    :value="addon.id"
                    v-model="selectedAddons"
                    class="w-4 h-4 text-blue-600 border-gray-300 rounded focus:ring-blue-500"
                  >
                  <span class="ml-3 text-gray-700 dark:text-gray-300">{{ addon.name }}</span>
                  <span class="ml-auto text-gray-500 dark:text-gray-400 text-sm">+${{ addon.price.toFixed(2) }}</span>
                </label>
              </div>
            </div>
          </div>

          <!-- Quantity Selector -->
          <div class="flex items-center mb-8">
            <button @click="decreaseQuantity" class="bg-gray-200 dark:bg-gray-700 text-gray-700 dark:text-gray-300 w-10 h-10 rounded-full flex items-center justify-center hover:bg-gray-300 dark:hover:bg-gray-600 transition">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M18 12H6" />
              </svg>
            </button>
            <span class="mx-4 text-lg font-medium text-gray-900 dark:text-white">{{ quantity }}</span>
            <button @click="increaseQuantity" class="bg-gray-200 dark:bg-gray-700 text-gray-700 dark:text-gray-300 w-10 h-10 rounded-full flex items-center justify-center hover:bg-gray-300 dark:hover:bg-gray-600 transition">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6" />
              </svg>
            </button>
          </div>

          <!-- Action Buttons -->
          <div class="space-y-4">
            <button 
              @click="addToCart"
              class="w-full bg-blue-600 text-white py-4 rounded-xl font-semibold hover:bg-blue-700 transition flex items-center justify-center"
            >
              <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 11V7a4 4 0 00-8 0v4M5 9h14l1 12H4L5 9z" />
              </svg>
              Add to Cart
            </button>
            <button 
              @click="buyNow"
              class="w-full bg-green-600 text-white py-4 rounded-xl font-semibold hover:bg-green-700 transition flex items-center justify-center"
            >
              <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 9V7a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2m2 4h10a2 2 0 002-2v-6a2 2 0 00-2-2H9a2 2 0 00-2 2v6a2 2 0 002 2zm7-5a2 2 0 11-4 0 2 2 0 014 0z" />
              </svg>
              Buy Now
            </button>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useProductStore } from '../store/productStore'
import { useCartStore } from '../store/cartStore'

const router = useRouter()
const route = useRoute()
const productStore = useProductStore()
const cartStore = useCartStore()

const productId = Number(route.params.id)
const product = ref<any>(null)
const quantity = ref(1)

// Product customization options
const selectedSize = ref('medium')
const selectedTemperature = ref('hot')
const selectedAddons = ref<number[]>([])

// Size options
const sizes = [
  { label: 'Small', value: 'small' },
  { label: 'Medium', value: 'medium' },
  { label: 'Large', value: 'large' }
]

// Add-on options (mock data)
const addons = [
  { id: 1, name: 'Extra Shot', price: 0.75 },
  { id: 2, name: 'Soy Milk', price: 0.50 },
  { id: 3, name: 'Almond Milk', price: 0.75 },
  { id: 4, name: 'Caramel Syrup', price: 0.50 },
  { id: 5, name: 'Vanilla Syrup', price: 0.50 }
]

const loading = computed(() => productStore.loading)
const error = computed(() => productStore.error)
const cartCount = computed(() => cartStore.cartCount)

const decreaseQuantity = () => {
  if (quantity.value > 1) {
    quantity.value--
  }
}

const increaseQuantity = () => {
  quantity.value++
}

const addToCart = () => {
  if (product.value) {
    // Calculate total price with add-ons
    const addonPrice = selectedAddons.value.reduce((total, addonId) => {
      const addon = addons.find(a => a.id === addonId)
      return total + (addon?.price || 0)
    }, 0)
    
    const totalPrice = product.value.price + addonPrice
    
    // Build product name with customization
    let customizedName = `${product.value.name}`
    if (selectedSize.value !== 'medium') {
      customizedName += ` (${selectedSize.value})`
    }
    customizedName += ` - ${selectedTemperature.value}`
    
    cartStore.addToCart({
      productId: product.value.id,
      productNo: product.value.productNo,
      name: customizedName,
      price: totalPrice,
      quantity: quantity.value,
      imageUrl: product.value.imageUrl,
      size: selectedSize.value,
      temperature: selectedTemperature.value,
      addons: selectedAddons.value
    })
  }
}

const buyNow = () => {
  addToCart()
  router.push('/checkout')
}

const navigateBack = () => {
  router.back()
}

const navigateToCart = () => {
  router.push('/cart')
}

const fetchProduct = async () => {
  try {
    await productStore.fetchProductById(productId)
    product.value = productStore.currentProductDetails
  } catch (error) {
    console.error('Failed to fetch product:', error)
  }
}

onMounted(async () => {
  await fetchProduct()
})
</script>