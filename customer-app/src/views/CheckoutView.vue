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
          <h1 class="text-xl font-bold text-gray-900 dark:text-white">Checkout</h1>
        </div>
      </div>
    </header>

    <main class="container mx-auto px-4 py-8">
      <div class="grid grid-cols-1 md:grid-cols-3 gap-8">
        <!-- Order Details -->
        <div class="md:col-span-2">
          <!-- Store Selection -->
          <section class="bg-white dark:bg-gray-800 rounded-xl p-6 shadow-sm mb-6">
            <h2 class="text-lg font-semibold mb-4 text-gray-900 dark:text-white">Select Store</h2>
            <div class="space-y-3">
              <div 
                v-for="store in stores" 
                :key="store.id"
                @click="selectedStore = store.id"
                :class="[
                  'border rounded-lg p-4 cursor-pointer transition',
                  selectedStore === store.id 
                    ? 'border-blue-600 bg-blue-50 dark:bg-blue-900/20' 
                    : 'border-gray-300 dark:border-gray-700 hover:border-gray-400 dark:hover:border-gray-600'
                ]"
              >
                <div class="flex items-center">
                  <div class="w-6 h-6 rounded-full border-2 border-blue-600 flex items-center justify-center mr-3">
                    <div v-if="selectedStore === store.id" class="w-3 h-3 rounded-full bg-blue-600"></div>
                  </div>
                  <div>
                    <h3 class="font-medium text-gray-900 dark:text-white">{{ store.name }}</h3>
                    <p class="text-sm text-gray-600 dark:text-gray-400">{{ store.address }}</p>
                  </div>
                </div>
              </div>
            </div>
          </section>

          <!-- Pickup Time -->
          <section class="bg-white dark:bg-gray-800 rounded-xl p-6 shadow-sm mb-6">
            <h2 class="text-lg font-semibold mb-4 text-gray-900 dark:text-white">Pickup Time</h2>
            <div class="space-y-3">
              <div class="grid grid-cols-2 gap-3">
                <button 
                  @click="selectedPickupTime = 'ASAP'"
                  :class="[
                    'border rounded-lg p-3 text-center cursor-pointer transition',
                    selectedPickupTime === 'ASAP' 
                      ? 'border-blue-600 bg-blue-50 dark:bg-blue-900/20' 
                      : 'border-gray-300 dark:border-gray-700 hover:border-gray-400 dark:hover:border-gray-600'
                  ]"
                >
                  <span class="font-medium text-gray-900 dark:text-white">ASAP</span>
                  <p class="text-sm text-gray-600 dark:text-gray-400">Within 15 minutes</p>
                </button>
                <button 
                  @click="selectedPickupTime = 'LATER'"
                  :class="[
                    'border rounded-lg p-3 text-center cursor-pointer transition',
                    selectedPickupTime === 'LATER' 
                      ? 'border-blue-600 bg-blue-50 dark:bg-blue-900/20' 
                      : 'border-gray-300 dark:border-gray-700 hover:border-gray-400 dark:hover:border-gray-600'
                  ]"
                >
                  <span class="font-medium text-gray-900 dark:text-white">Later</span>
                  <p class="text-sm text-gray-600 dark:text-gray-400">Select time</p>
                </button>
              </div>
              <div v-if="selectedPickupTime === 'LATER'" class="mt-3">
                <input 
                  type="datetime-local" 
                  v-model="customPickupTime"
                  class="w-full border border-gray-300 dark:border-gray-700 rounded-lg p-3 focus:outline-none focus:ring-2 focus:ring-blue-500 dark:bg-gray-700 dark:text-white"
                >
              </div>
            </div>
          </section>

          <!-- Payment Method -->
          <section class="bg-white dark:bg-gray-800 rounded-xl p-6 shadow-sm mb-6">
            <h2 class="text-lg font-semibold mb-4 text-gray-900 dark:text-white">Payment Method</h2>
            <div class="space-y-3">
              <div 
                v-for="method in paymentMethods" 
                :key="method.id"
                @click="selectedPaymentMethod = method.id"
                :class="[
                  'border rounded-lg p-4 cursor-pointer transition',
                  selectedPaymentMethod === method.id 
                    ? 'border-blue-600 bg-blue-50 dark:bg-blue-900/20' 
                    : 'border-gray-300 dark:border-gray-700 hover:border-gray-400 dark:hover:border-gray-600'
                ]"
              >
                <div class="flex items-center">
                  <div class="w-6 h-6 rounded-full border-2 border-blue-600 flex items-center justify-center mr-3">
                    <div v-if="selectedPaymentMethod === method.id" class="w-3 h-3 rounded-full bg-blue-600"></div>
                  </div>
                  <div>
                    <h3 class="font-medium text-gray-900 dark:text-white">{{ method.name }}</h3>
                    <p class="text-sm text-gray-600 dark:text-gray-400">{{ method.description }}</p>
                  </div>
                </div>
              </div>
            </div>
          </section>

          <!-- Order Notes -->
          <section class="bg-white dark:bg-gray-800 rounded-xl p-6 shadow-sm">
            <h2 class="text-lg font-semibold mb-4 text-gray-900 dark:text-white">Order Notes</h2>
            <textarea 
              v-model="orderNotes"
              placeholder="Add any special instructions..."
              class="w-full border border-gray-300 dark:border-gray-700 rounded-lg p-3 focus:outline-none focus:ring-2 focus:ring-blue-500 dark:bg-gray-700 dark:text-white"
              rows="3"
            ></textarea>
          </section>
        </div>

        <!-- Payment Summary -->
        <div class="md:col-span-1">
          <section class="bg-white dark:bg-gray-800 rounded-xl p-6 shadow-sm sticky top-24">
            <h2 class="text-lg font-semibold mb-4 text-gray-900 dark:text-white">Payment Summary</h2>
            
            <div class="space-y-3 mb-6">
              <div v-for="item in cartItems" :key="item.productId" class="flex justify-between items-center">
                <span class="text-gray-600 dark:text-gray-400">{{ item.name }} × {{ item.quantity }}</span>
                <span class="text-gray-900 dark:text-white">${{ item.total.toFixed(2) }}</span>
              </div>
            </div>
            
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
                <span class="font-bold text-gray-900 dark:text-white">Total</span>
                <span class="font-bold text-gray-900 dark:text-white">${{ totalAmount.toFixed(2) }}</span>
              </div>
            </div>
            
            <button @click="placeOrder" class="w-full bg-blue-600 text-white py-3 rounded-xl font-semibold hover:bg-blue-700 transition flex items-center justify-center">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 9V7a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2m2 4h10a2 2 0 002-2v-6a2 2 0 00-2-2H9a2 2 0 00-2 2v6a2 2 0 002 2zm7-5a2 2 0 11-4 0 2 2 0 014 0z" />
              </svg>
              Place Order
            </button>
          </section>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useCartStore } from '../store/cartStore'
import { useStoreStore } from '../store/storeStore'
import { useOrderStore } from '../store/orderStore'

const router = useRouter()
const cartStore = useCartStore()
const storeStore = useStoreStore()
const orderStore = useOrderStore()

// Store selection
const stores = computed(() => storeStore.nearbyStores)
const selectedStore = ref<number | null>(null)

// Pickup time
const selectedPickupTime = ref('ASAP')
const customPickupTime = ref('')

// Payment methods
const paymentMethods = ref([
  { id: 1, name: 'Credit Card', description: 'Pay with your credit/debit card' },
  { id: 2, name: 'Cash', description: 'Pay with cash at pickup' },
  { id: 3, name: 'Mobile Payment', description: 'Pay with Apple Pay or Google Pay' }
])
const selectedPaymentMethod = ref(1)

// Order notes
const orderNotes = ref('')

// Cart data
const cartItems = computed(() => cartStore.cartItems)
const cartTotal = computed(() => cartStore.cartTotal)
const taxAmount = computed(() => cartTotal.value * 0.08) // 8% tax
const discountAmount = ref(0) // No discount for now
const totalAmount = computed(() => cartTotal.value + taxAmount.value - discountAmount.value)

const placeOrder = async () => {
  if (!selectedStore.value) {
    alert('Please select a store')
    return
  }

  try {
    const orderData = {
      storeId: selectedStore.value,
      items: cartItems.value.map(item => ({
        productId: item.productId,
        quantity: item.quantity
      })),
      paymentMethod: selectedPaymentMethod.value,
      pickupTime: selectedPickupTime.value === 'ASAP' ? new Date().toISOString() : customPickupTime.value,
      remarks: orderNotes.value
    }
    
    await orderStore.createOrder(orderData)
    
    // Clear cart
    cartStore.clearCart()
    
    // Navigate to order confirmation
    router.push('/order-history')
  } catch (error: any) {
    console.error('Failed to place order:', error)
    alert(error.response?.data?.message || 'Failed to place order')
  }
}

const navigateBack = () => {
  router.back()
}

onMounted(async () => {
  // Load stores from backend
  try {
    await storeStore.getNearbyStores()
    if (storeStore.nearbyStores.length > 0) {
      selectedStore.value = storeStore.nearbyStores[0].id
    }
  } catch (error) {
    console.error('Failed to fetch stores:', error)
  }
})
</script>