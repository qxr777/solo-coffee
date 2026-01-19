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
          <h1 class="text-xl font-bold text-gray-900 dark:text-white">Voice Order</h1>
        </div>
        <div class="flex items-center space-x-4">
          <button @click="navigateToCart" class="relative">
            <svg class="w-6 h-6 text-gray-600 dark:text-gray-300" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 11V7a4 4 0 11-8 0v4M5 9h14l1 12H4L5 9z"></path>
            </svg>
            <span v-if="cartCount > 0" class="absolute -top-1 -right-1 bg-red-500 text-white text-xs rounded-full w-4 h-4 flex items-center justify-center">
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
        <h3 class="text-xl font-semibold mb-2 text-gray-900 dark:text-white">Error loading voice data</h3>
        <p class="text-gray-600 dark:text-gray-400 mb-6">{{ error }}</p>
        <button @click="fetchVoiceData" class="bg-blue-600 text-white px-6 py-2 rounded-full font-medium hover:bg-blue-700 transition">
          Try Again
        </button>
      </div>

      <!-- Voice Input Section -->
      <div v-else class="space-y-6">
        <!-- Voice Input -->
        <section class="bg-white dark:bg-gray-800 rounded-xl p-6 shadow-sm">
          <h2 class="text-lg font-semibold mb-4 text-gray-900 dark:text-white">Voice Input</h2>
          <div class="space-y-4">
            <!-- Recording Button -->
            <div class="flex items-center justify-center space-x-4">
              <button 
                @click="startRecording"
                :disabled="isRecording"
                :class="[
                  'w-16 h-16 rounded-full flex items-center justify-center transition',
                  isRecording 
                    ? 'bg-red-600 text-white animate-pulse' 
                    : 'bg-blue-600 text-white hover:bg-blue-700'
                ]"
              >
                <svg v-if="!isRecording" xmlns="http://www.w3.org/2000/svg" class="h-8 w-8" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 1a3 3 0 11-6 0 3 3 0 018 0zM19 10a14 9 0 00-6 3 3 0 006zm0 8a3 3 0 00-6 3 3 0 006zm0 8a3 3 0 00-6 3 3 0 006zm0 8a3 3 0 00-6 3 3 0 006z"></path>
                </svg>
                <svg v-else xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6v12a2 2 0 012-2v-4a2 2 0 00-4-4a2 2 0 00-4-4a2 2 0 006zm0 8a3 3 0 00-6 3 3 0 006zm0 8a3 3 0 00-6 3 3 0 006z"></path>
                </svg>
              </button>
              <div v-if="isRecording" class="text-center">
                <div class="text-sm text-gray-600 dark:text-gray-400 mb-1">Recording...</div>
                <div class="text-2xl font-bold text-red-600">{{ recordingTime }}s</div>
              </div>
            </div>

            <!-- Voice Commands -->
            <div class="flex-1">
              <h3 class="text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">Quick Commands</h3>
              <div class="grid grid-cols-2 gap-2">
                <button 
                  v-for="command in quickCommands" 
                  :key="command"
                  @click="executeCommand(command)"
                  class="p-3 border border-gray-200 dark:border-gray-700 rounded-lg text-sm hover:bg-gray-50 dark:hover:bg-gray-700 transition"
                >
                  {{ command }}
                </button>
              </div>
            </div>
          </div>

          <!-- Voice Recognition Result -->
          <div v-if="voiceResult" class="mt-4 p-4 bg-blue-50 dark:bg-blue-900/20 rounded-lg">
            <div class="flex items-center space-x-2 mb-2">
              <div class="w-8 h-8 bg-blue-600 rounded-full flex items-center justify-center">
                <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7l-4-4H5"></path>
                </svg>
              </div>
              <div>
                <h3 class="font-semibold text-gray-900 dark:text-white">Voice Recognized</h3>
                <p class="text-sm text-gray-600 dark:text-gray-400">{{ voiceResult }}</p>
              </div>
            </div>
          </div>
        </section>

        <!-- Order Preview -->
        <section v-if="orderPreview.length > 0" class="bg-white dark:bg-gray-800 rounded-xl p-6 shadow-sm">
          <h2 class="text-lg font-semibold mb-4 text-gray-900 dark:text-white">Order Preview</h2>
          <div class="space-y-3">
            <div v-for="(item, index) in orderPreview" :key="index" class="flex items-center justify-between p-4 border border-gray-200 dark:border-gray-700 rounded-lg">
              <div class="flex items-center space-x-4">
                <div class="w-12 h-12 bg-gray-100 dark:bg-gray-700 rounded-lg flex items-center justify-center">
                  <span class="text-2xl">{{ item.icon }}</span>
                </div>
                <div class="flex-1">
                  <h3 class="font-semibold text-gray-900 dark:text-white mb-1">{{ item.name }}</h3>
                  <p class="text-sm text-gray-600 dark:text-gray-400 mb-2">{{ item.description }}</p>
                  <div class="flex items-center space-x-2">
                    <span class="text-xs text-gray-500 dark:text-gray-400">Quantity: {{ item.quantity }}</span>
                    <span class="text-lg font-bold text-gray-900 dark:text-white">${{ (item.price * item.quantity).toFixed(2) }}</span>
                  </div>
                </div>
                <button @click="removePreviewItem(index)" class="text-red-600 dark:text-red-400 hover:text-red-700 transition">
                  <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 00-2.827 0l-4.243-4.243a8 8 0 1111.314 0z"></path>
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
        <section v-if="orderPreview.length > 0" class="bg-white dark:bg-gray-800 rounded-xl p-6 shadow-sm">
          <div class="space-y-4">
            <div class="flex justify-between items-center">
              <span class="text-gray-600 dark:text-gray-400">Total Items: {{ orderPreview.length }}</span>
              <span class="text-2xl font-bold text-gray-900 dark:text-white">${{ totalAmount.toFixed(2) }}</span>
            </div>
            <button 
              @click="submitVoiceOrder"
              :disabled="submitting"
              class="w-full bg-blue-600 text-white py-4 rounded-xl font-semibold hover:bg-blue-700 transition disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center"
            >
              <svg v-if="submitting" class="animate-spin rounded-full h-5 w-5 border-b-2 border-white mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 6h6" />
              </svg>
              {{ submitting ? 'Submitting...' : 'Submit Order' }}
            </button>
          </div>
        </section>
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../store/authStore'
import { useCartStore } from '../store/cartStore'
import { useProductStore } from '../store/productStore'

const router = useRouter()
const authStore = useAuthStore()
const cartStore = useCartStore()
const productStore = useProductStore()

// State
const loading = ref(false)
const error = ref<string | null>(null)
const isRecording = ref(false)
const recordingTime = ref(0)
const voiceResult = ref('')
const orderPreview = ref<any[]>([])
const selectedPickupTime = ref('ASAP')
const customPickupTime = ref('')
const orderNotes = ref('')
const submitting = ref(false)

// Quick Commands
const quickCommands = ref([
  'One Americano',
  'Two Lattes',
  'Three Cappuccinos',
  'One Croissant',
  'One Muffin',
  'Extra hot',
  'Iced please',
  'Large size',
  'Medium size'
])

let mediaRecorder: MediaRecorder | null = null
let recognitionInterval: number | null = null

const cartCount = computed(() => cartStore.cartCount)

const totalAmount = computed(() => {
  return orderPreview.value.reduce((total, item) => total + (item.price * item.quantity), 0)
})

// Methods
const startRecording = async () => {
  try {
    const stream = await navigator.mediaDevices.getUserMedia({ audio: true })
    mediaRecorder = new MediaRecorder(stream)
    const chunks: BlobPart[] = []
    
    mediaRecorder.ondataavailable = (event) => {
      chunks.push(event.data)
    }
    
    mediaRecorder.onstop = () => {
      // In a real app, send audio to voice recognition API
      
      const mockResults = [
        'One Americano please',
        'Two Lattes with oat milk',
        'Three Cappuccinos with extra foam',
        'One chocolate croissant',
        'One blueberry muffin',
        'Add sugar to my order',
        'Make it extra hot please',
        'Iced please',
        'Large size please',
        'Medium size please'
      ]
      const randomResult = mockResults[Math.floor(Math.random() * mockResults.length)]
      voiceResult.value = randomResult
      
      // Parse voice result and add to order
      parseVoiceResult(randomResult)
    }
    
    mediaRecorder.start()
    isRecording.value = true
    recordingTime.value = 0
    
    recognitionInterval = window.setInterval(() => {
      recordingTime.value++
    }, 1000)
    
    // Stop recording after 5 seconds
    setTimeout(() => {
      stopRecording()
    }, 5000)
  } catch (err) {
    console.error('Error accessing microphone:', err)
    alert('Unable to access microphone. Please check your permissions.')
  }
}

const stopRecording = () => {
  if (mediaRecorder && mediaRecorder.state !== 'inactive') {
    mediaRecorder.stop()
    isRecording.value = false
    
    if (recognitionInterval) {
      clearInterval(recognitionInterval)
      recognitionInterval = null
    }
  }
}

const addProductToOrder = (product: any, quantity: number) => {
  const existingItem = orderPreview.value.find(item => item.id === product.id)
  if (existingItem) {
    existingItem.quantity += quantity
  } else {
    orderPreview.value.push({
      id: product.id,
      name: product.name,
      productNo: product.productNo,
      description: product.description,
      price: product.price,
      quantity: quantity,
      icon: '☕',
      imageUrl: product.imageUrl
    })
  }
}

const parseVoiceResult = (result: string) => {
  const lowerResult = result.toLowerCase()
  
  // Try to find a product that matches the voice result
  const products = productStore.allProducts
  const matchedProduct = products.find(p => lowerResult.includes(p.name.toLowerCase()))
  
  if (matchedProduct) {
    // Basic quantity parsing
    let quantity = 1
    if (lowerResult.includes('two')) quantity = 2
    else if (lowerResult.includes('three')) quantity = 3
    else {
      const numMatch = lowerResult.match(/\d+/)
      if (numMatch) quantity = parseInt(numMatch[0])
    }
    
    addProductToOrder(matchedProduct, quantity)
  }
  
  // Clear voice result after processing
  setTimeout(() => {
    voiceResult.value = ''
  }, 3000)
}

const executeCommand = (command: string) => {
  voiceResult.value = command
  parseVoiceResult(command)
  
  // Clear voice result after processing
  setTimeout(() => {
    voiceResult.value = ''
  }, 2000)
}

const removePreviewItem = (index: number) => {
  orderPreview.value.splice(index, 1)
}

const submitVoiceOrder = async () => {
  submitting.value = true
  try {
    // Add items to cart
    orderPreview.value.forEach(item => {
      cartStore.addToCart({
        productId: item.id,
        productNo: item.productNo,
        name: item.name,
        price: item.price,
        quantity: item.quantity,
        imageUrl: item.imageUrl || ''
      })
    })
    
    alert('Voice order items added to cart!')
    router.push('/cart')
  } catch (err: any) {
    error.value = err.message || 'Failed to submit voice order'
  } finally {
    submitting.value = false
  }
}

const fetchVoiceData = async () => {
  loading.value = true
  error.value = null
  try {
    await productStore.fetchProducts()
    loading.value = false
  } catch (err: any) {
    error.value = err.message || 'Failed to load products'
    loading.value = false
  }
}

const navigateBack = () => {
  router.back()
}

const navigateToCart = () => {
  router.push('/cart')
}

onMounted(async () => {
  if (!authStore.isAuthenticated) {
    router.push('/login')
    return
  }
  await fetchVoiceData()
})

onUnmounted(() => {
  if (mediaRecorder && mediaRecorder.state !== 'inactive') {
    mediaRecorder.stop()
  }
  
  if (recognitionInterval) {
    clearInterval(recognitionInterval)
  }
})

onUnmounted(() => {
  if (mediaRecorder && mediaRecorder.state !== 'inactive') {
    mediaRecorder.stop()
  }
  
  if (recognitionInterval) {
    clearInterval(recognitionInterval)
  }
})
</script>