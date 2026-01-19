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
          <h1 class="text-xl font-bold text-gray-900 dark:text-white">Your Cart</h1>
        </div>
        <div class="flex items-center space-x-4">
          <span class="font-semibold text-gray-900 dark:text-white">{{ cartCount }} items</span>
        </div>
      </div>
    </header>

    <main class="container mx-auto px-4 py-8">
      <!-- Cart Items -->
      <section v-if="!cartIsEmpty" class="mb-8">
        <div v-for="(item, index) in cartItems" :key="index" class="bg-white dark:bg-gray-800 rounded-xl p-4 shadow-sm mb-4">
          <div class="flex items-center">
            <!-- Product Image -->
            <div class="w-16 h-16 bg-gray-200 dark:bg-gray-700 rounded-lg flex items-center justify-center mr-4">
              <img 
                v-if="item.imageUrl" 
                :src="item.imageUrl" 
                :alt="item.name" 
                class="w-full h-full object-cover rounded-lg"
              >
              <span v-else class="text-2xl">☕</span>
            </div>
            
            <!-- Product Info -->
            <div class="flex-1">
              <h3 class="font-semibold text-gray-900 dark:text-white mb-1">{{ item.name }}</h3>
              <div v-if="item.size || item.temperature" class="text-xs text-gray-500 dark:text-gray-400 mb-1">
                {{ item.size }} | {{ item.temperature }}
              </div>
              <p class="text-sm text-gray-600 dark:text-gray-400 mb-2">${{ item.price.toFixed(2) }}</p>
              <div class="flex items-center justify-between">
                <!-- Quantity Controls -->
                <div class="flex items-center">
                  <button @click="updateQuantity(index, item.quantity - 1)" class="bg-gray-200 dark:bg-gray-700 text-gray-700 dark:text-gray-300 w-8 h-8 rounded-full flex items-center justify-center hover:bg-gray-300 dark:hover:bg-gray-600 transition">
                    <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M18 12H6" />
                    </svg>
                  </button>
                  <span class="mx-3 text-sm font-medium text-gray-900 dark:text-white">{{ item.quantity }}</span>
                  <button @click="updateQuantity(index, item.quantity + 1)" class="bg-gray-200 dark:bg-gray-700 text-gray-700 dark:text-gray-300 w-8 h-8 rounded-full flex items-center justify-center hover:bg-gray-300 dark:hover:bg-gray-600 transition">
                    <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6" />
                    </svg>
                  </button>
                </div>
                
                <!-- Price and Delete -->
                <div class="flex items-center">
                  <span class="font-bold text-gray-900 dark:text-white mr-4">${{ item.total.toFixed(2) }}</span>
                  <button @click="removeFromCart(index)" class="text-red-500 hover:text-red-700 transition">
                    <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                    </svg>
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      <!-- Empty Cart -->
      <section v-else class="text-center py-16">
        <div class="text-6xl mb-4">🛒</div>
        <h3 class="text-xl font-semibold mb-2 text-gray-900 dark:text-white">Your cart is empty</h3>
        <p class="text-gray-600 dark:text-gray-400 mb-6">Start adding items to your cart</p>
        <button @click="navigateToProducts" class="bg-blue-600 text-white px-6 py-3 rounded-full font-semibold hover:bg-blue-700 transition">
          Browse Menu
        </button>
      </section>

      <!-- Checkout Summary -->
      <section v-if="!cartIsEmpty" class="bg-white dark:bg-gray-800 rounded-xl p-6 shadow-sm">
        <h2 class="text-xl font-bold mb-4 text-gray-900 dark:text-white">Order Summary</h2>
        
        <div class="space-y-3 mb-6">
          <div class="flex justify-between items-center">
            <span class="text-gray-600 dark:text-gray-400">Subtotal</span>
            <span class="text-gray-900 dark:text-white">${{ cartTotal.toFixed(2) }}</span>
          </div>
          <div class="flex justify-between items-center">
            <span class="text-gray-600 dark:text-gray-400">Tax</span>
            <span class="text-gray-900 dark:text-white">${{ taxAmount.toFixed(2) }}</span>
          </div>
          <div class="flex justify-between items-center">
            <span class="text-gray-600 dark:text-gray-400">Discount</span>
            <span class="text-green-600 dark:text-green-400">-${{ discountAmount.toFixed(2) }}</span>
          </div>
        </div>
        
        <div class="border-t border-gray-200 dark:border-gray-700 pt-4 mb-6">
          <div class="flex justify-between items-center">
            <span class="text-lg font-bold text-gray-900 dark:text-white">Total</span>
            <span class="text-lg font-bold text-gray-900 dark:text-white">${{ totalAmount.toFixed(2) }}</span>
          </div>
        </div>
        
        <button @click="navigateToCheckout" class="w-full bg-blue-600 text-white py-4 rounded-xl font-semibold hover:bg-blue-700 transition flex items-center justify-center">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 9V7a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2m2 4h10a2 2 0 002-2v-6a2 2 0 00-2-2H9a2 2 0 00-2 2v6a2 2 0 002 2zm7-5a2 2 0 11-4 0 2 2 0 014 0z" />
          </svg>
          Proceed to Checkout
        </button>
      </section>
    </main>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useCartStore } from '../store/cartStore'

const router = useRouter()
const cartStore = useCartStore()

const cartItems = computed(() => cartStore.cartItems)
const cartCount = computed(() => cartStore.cartCount)
const cartTotal = computed(() => cartStore.cartTotal)
const cartIsEmpty = computed(() => cartStore.isEmpty)

const taxAmount = computed(() => cartTotal.value * 0.08) // 8% tax
const discountAmount = computed(() => 0) // No discount for now
const totalAmount = computed(() => cartTotal.value + taxAmount.value - discountAmount.value)

const updateQuantity = (index: number, quantity: number) => {
  cartStore.updateQuantity(index, quantity)
}

const removeFromCart = (index: number) => {
  cartStore.removeFromCart(index)
}

const navigateToProducts = () => {
  router.push('/products')
}

const navigateToCheckout = () => {
  router.push('/checkout')
}

const navigateBack = () => {
  router.back()
}

onMounted(() => {
  // Load cart from localStorage
  cartStore.loadFromLocalStorage()
})
</script>