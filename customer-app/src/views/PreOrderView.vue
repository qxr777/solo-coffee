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
          <h1 class="text-xl font-bold text-gray-900 dark:text-white">Pre-Order</h1>
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
        <h3 class="text-xl font-semibold mb-2 text-gray-900 dark:text-white">Error loading predictions</h3>
        <p class="text-gray-600 dark:text-gray-400 mb-6">{{ error }}</p>
        <button @click="fetchPredictions" class="bg-blue-600 text-white px-6 py-2 rounded-full font-medium hover:bg-blue-700 transition">
          Try Again
        </button>
      </div>

      <!-- Pre-Order Content -->
      <div v-else class="space-y-6">
        <!-- AI Prediction Section -->
        <section class="bg-white dark:bg-gray-800 rounded-xl p-6 shadow-sm">
          <h2 class="text-lg font-semibold mb-4 text-gray-900 dark:text-white">AI-Powered Recommendations</h2>
          <p class="text-sm text-gray-600 dark:text-gray-400 mb-4">Based on your order history and preferences, we recommend:</p>
          
          <div v-if="predictions.length > 0" class="space-y-4">
            <div v-for="prediction in predictions" :key="prediction.id" class="border border-gray-200 dark:border-gray-700 rounded-lg p-4 hover:shadow-md transition cursor-pointer" @click="selectPrediction(prediction)">
              <div class="flex items-start space-x-4">
                <div class="flex-1">
                  <div class="flex items-center space-x-2 mb-2">
                    <span :class="[
                      'px-3 py-1 rounded-full text-xs font-bold',
                      prediction.type === 'frequent' ? 'bg-blue-600 text-white' : 'bg-green-600 text-white'
                    ]">
                      {{ prediction.type === 'frequent' ? '🔥' : '⭐' }}
                    </span>
                    <h3 class="font-semibold text-gray-900 dark:text-white">{{ prediction.title }}</h3>
                  </div>
                  <div class="text-sm text-gray-600 dark:text-gray-400">
                    {{ prediction.description }}
                  </div>
                </div>
                <div class="text-right">
                  <div class="flex items-center space-x-1">
                    <span class="text-xs text-gray-500 dark:text-gray-400">{{ prediction.confidence }}% match</span>
                    <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 015-2 7-7-7 0l-7 7 7 015-2 7-7 7z" />
                    </svg>
                  </div>
                </div>
              </div>
              
              <div v-if="prediction.items && prediction.items.length > 0" class="mt-3 pt-3 border-t border-gray-200 dark:border-gray-700">
                <h4 class="text-sm font-semibold text-gray-900 dark:text-white mb-2">Suggested Items:</h4>
                <div class="grid grid-cols-2 gap-3">
                  <div v-for="item in prediction.items" :key="item.id" class="border border-gray-200 dark:border-gray-700 rounded-lg p-3">
                    <div class="w-full h-20 bg-gray-100 dark:bg-gray-700 rounded-lg flex items-center justify-center mb-2">
                      <span class="text-2xl">{{ item.icon }}</span>
                    </div>
                    <h3 class="font-semibold text-gray-900 dark:text-white mb-1">{{ item.name }}</h3>
                    <p class="text-sm text-gray-600 dark:text-gray-400 mb-2">{{ item.description }}</p>
                    <div class="flex justify-between items-center">
                      <span class="font-bold text-gray-900 dark:text-white">${{ item.price.toFixed(2) }}</span>
                      <button @click.stop="addItemToOrder(item)" class="bg-blue-600 text-white px-3 py-1 rounded-full text-sm font-medium hover:bg-blue-700 transition">
                        Add
                      </button>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </section>

        <!-- Selected Order -->
        <section v-if="selectedItems.length > 0" class="bg-white dark:bg-gray-800 rounded-xl p-6 shadow-sm">
          <div class="flex justify-between items-center mb-4">
            <h2 class="text-lg font-semibold text-gray-900 dark:text-white">Your Pre-Order</h2>
            <button @click="clearOrder" class="text-red-600 dark:text-red-400 text-sm font-medium hover:underline">
              Clear All
            </button>
          </div>
          
          <div class="space-y-3">
            <div v-for="(item, index) in selectedItems" :key="index" class="flex items-center justify-between p-4 border border-gray-200 dark:border-gray-700 rounded-lg">
              <div class="flex items-center space-x-4">
                <div class="w-12 h-12 bg-gray-100 dark:bg-gray-700 rounded-lg flex items-center justify-center">
                  <span class="text-xl">{{ item.icon }}</span>
                </div>
                <div class="flex-1">
                  <h3 class="font-semibold text-gray-900 dark:text-white mb-1">{{ item.name }}</h3>
                  <p class="text-sm text-gray-600 dark:text-gray-400">{{ item.description }}</p>
                  <div class="flex items-center space-x-2">
                    <button @click="decreaseQuantity(index)" class="bg-gray-200 dark:bg-gray-700 text-gray-700 dark:text-gray-300 w-8 h-8 rounded-full flex items-center justify-center hover:bg-gray-300 dark:hover:bg-gray-600 transition">
                      <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M18 12H6" />
                      </svg>
                    </button>
                    <span class="text-lg font-medium text-gray-900 dark:text-white">{{ item.quantity }}</span>
                    <button @click="increaseQuantity(index)" class="bg-gray-200 dark:bg-gray-700 text-gray-700 dark:text-gray-300 w-8 h-8 rounded-full flex items-center justify-center hover:bg-gray-300 dark:hover:bg-gray-600 transition">
                      <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 6h6" />
                      </svg>
                    </button>
                  </div>
                  <span class="font-bold text-gray-900 dark:text-white">${{ (item.price * item.quantity).toFixed(2) }}</span>
                </div>
                <button @click="removeItem(index)" class="text-red-600 dark:text-red-400 hover:text-red-700 transition">
                  <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 00-2.827 0l-4.243-4.243a8 8 0 1111.314 0z" />
                  </svg>
                </button>
              </div>
            </div>
          </div>
        </section>

        <!-- Pickup Time -->
        <section class="bg-white dark:bg-gray-800 rounded-xl p-6 shadow-sm">
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

        <!-- Submit Button -->
        <section v-if="selectedItems.length > 0" class="bg-white dark:bg-gray-800 rounded-xl p-6 shadow-sm">
          <div class="space-y-4">
            <div class="flex justify-between items-center">
              <span class="text-gray-600 dark:text-gray-400">Total Items: {{ selectedItems.length }}</span>
              <span class="text-2xl font-bold text-gray-900 dark:text-white">${{ totalAmount.toFixed(2) }}</span>
            </div>
            <button @click="submitPreOrder" :disabled="submitting" class="w-full bg-blue-600 text-white py-4 rounded-xl font-semibold hover:bg-blue-700 transition disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center">
              <svg v-if="submitting" class="animate-spin rounded-full h-5 w-5 border-b-2 border-white mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 6h6" />
              </svg>
              {{ submitting ? 'Submitting...' : 'Submit Pre-Order' }}
            </button>
          </div>
        </section>
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../store/authStore'

const router = useRouter()
const authStore = useAuthStore()

// State
const loading = ref(false)
const error = ref<string | null>(null)
const submitting = ref(false)

interface PredictionItem {
  id: number
  name: string
  description: string
  price: number
  icon: string
  quantity: number
}

interface Prediction {
  id: number
  type: string
  title: string
  description: string
  confidence: number
  items: PredictionItem[]
}

const predictions = ref<Prediction[]>([])
const selectedItems = ref<PredictionItem[]>([])
const selectedPickupTime = ref('ASAP')
const customPickupTime = ref('')
const orderNotes = ref('')

// Mock predictions
const mockPredictions = [
  {
    id: 1,
    type: 'frequent',
    title: 'Your Usual',
    description: 'Based on your recent orders, we recommend your favorite combination',
    confidence: 95,
    items: [
      {
        id: 1,
        name: 'Americano',
        description: 'Classic black coffee',
        price: 3.50,
        icon: '☕',
        quantity: 1
      },
      {
        id: 2,
        name: 'Croissant',
        description: 'Butter croissant',
        price: 2.50,
        icon: '🥐',
        quantity: 1
      }
    ]
  },
  {
    id: 2,
    type: 'popular',
    title: 'Popular Today',
    description: 'Most ordered items today',
    confidence: 88,
    items: [
      {
        id: 3,
        name: 'Latte',
        description: 'Espresso with steamed milk',
        price: 4.75,
        icon: '☕',
        quantity: 1
      },
      {
        id: 4,
        name: 'Blueberry Muffin',
        description: 'Fresh blueberry muffin',
        price: 3.25,
        icon: '🧁',
        quantity: 1
      }
    ]
  },
  {
    id: 3,
    type: 'suggestion',
    title: 'Try Something New',
    description: 'Based on your preferences, you might like this',
    confidence: 72,
    items: [
      {
        id: 5,
        name: 'Caramel Macchiato',
        description: 'Caramel flavored macchiato',
        price: 4.25,
        icon: '☕',
        quantity: 1
      },
      {
        id: 6,
        name: 'Chocolate Cake',
        description: 'Rich chocolate cake',
        price: 4.50,
        icon: '🎂',
        quantity: 1
      }
    ]
  }
]

const totalAmount = computed(() => {
  return selectedItems.value.reduce((total, item) => total + (item.price * item.quantity), 0)
})

// Methods
const selectPrediction = (prediction: Prediction) => {
  if (prediction.items && prediction.items.length > 0) {
    prediction.items.forEach(item => {
      const existingItem = selectedItems.value.find(selectedItem => selectedItem.id === item.id)
      if (existingItem) {
        existingItem.quantity += 1
      } else {
        selectedItems.value.push({
          ...item,
          quantity: 1
        })
      }
    })
  }
}

const addItemToOrder = (item: PredictionItem) => {
  const existingItem = selectedItems.value.find(selectedItem => selectedItem.id === item.id)
  if (existingItem) {
    existingItem.quantity += 1
  } else {
    selectedItems.value.push({
      ...item,
      quantity: 1
    })
  }
}

const decreaseQuantity = (index: number) => {
  if (selectedItems.value[index].quantity > 1) {
    selectedItems.value[index].quantity -= 1
  }
}

const increaseQuantity = (index: number) => {
  selectedItems.value[index].quantity += 1
}

const removeItem = (index: number) => {
  selectedItems.value.splice(index, 1)
}

const clearOrder = () => {
  selectedItems.value = []
}

const fetchPredictions = async () => {
  loading.value = true
  error.value = null
  try {
    // In a real app, fetch from API
    // const response = await predictionAPI.getPredictions(authStore.currentUser?.id)
    // predictions.value = response
    
    // Use mock data for now
    predictions.value = mockPredictions
  } catch (err: any) {
    error.value = err.message || 'Failed to load predictions'
  } finally {
    loading.value = false
  }
}

const submitPreOrder = async () => {
  submitting.value = true
  try {
    // In a real app, submit to API
    // await orderAPI.createOrder({
    //   items: selectedItems.value.map(item => ({
    //     productId: item.id,
    //     quantity: item.quantity
    //   })),
    //   pickupTime: selectedPickupTime.value === 'ASAP' ? new Date().toISOString() : customPickupTime.value,
    //   remarks: orderNotes.value
    // })
    
    alert('Pre-order submitted successfully!')
    router.push('/orders')
  } catch (err: any) {
    error.value = err.message || 'Failed to submit pre-order'
  } finally {
    submitting.value = false
  }
}

const navigateBack = () => {
  router.back()
}

onMounted(() => {
  if (!authStore.isAuthenticated) {
    router.push('/login')
    return
  }
  
  fetchPredictions()
})
</script>