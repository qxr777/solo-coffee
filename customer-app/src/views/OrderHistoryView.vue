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
          <h1 class="text-xl font-bold text-gray-900 dark:text-white">Order History</h1>
        </div>
      </div>
    </header>

    <main class="container mx-auto px-4 py-8">
      <!-- Order Filter -->
      <section class="mb-6">
        <div class="flex flex-wrap gap-2">
          <button 
            v-for="filter in orderFilters" 
            :key="filter.value"
            @click="selectedFilter = filter.value"
            :class="[
              'px-4 py-2 rounded-full text-sm font-medium transition',
              selectedFilter === filter.value 
                ? 'bg-blue-600 text-white' 
                : 'bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300 hover:bg-gray-200 dark:hover:bg-gray-600'
            ]"
          >
            {{ filter.label }}
          </button>
        </div>
      </section>

      <!-- Order List -->
      <section>
        <div v-for="order in filteredOrders" :key="order.id" @click="navigateToOrderDetail(order.id)" class="bg-white dark:bg-gray-800 rounded-xl p-6 shadow-sm mb-4 cursor-pointer hover:shadow-md transition">
          <div class="flex justify-between items-center mb-4">
            <div>
              <h2 class="font-semibold text-gray-900 dark:text-white">Order #{{ order.orderNo }}</h2>
              <p class="text-sm text-gray-600 dark:text-gray-400">{{ new Date(order.createdAt).toLocaleString() }}</p>
            </div>
            <div :class="[
              'px-3 py-1 rounded-full text-xs font-medium',
              order.orderStatus === 1 
                ? 'bg-yellow-100 text-yellow-800 dark:bg-yellow-900 dark:text-yellow-200' 
                : order.orderStatus === 2
                  ? 'bg-blue-100 text-blue-800 dark:bg-blue-900 dark:text-blue-200'
                  : order.orderStatus === 3
                    ? 'bg-green-100 text-green-800 dark:bg-green-900 dark:text-green-200'
                    : 'bg-red-100 text-red-800 dark:bg-red-900 dark:text-red-200'
            ]">
              {{ orderStatusText(order.orderStatus) }}
            </div>
          </div>
          
          <div class="border-t border-gray-200 dark:border-gray-700 pt-4 mb-4">
            <div v-for="(item, index) in order.items" :key="index" class="flex justify-between items-center mb-2">
              <span class="text-gray-600 dark:text-gray-400">{{ item.name }} × {{ item.quantity }}</span>
              <span class="text-gray-900 dark:text-white">${{ (item.price * item.quantity).toFixed(2) }}</span>
            </div>
          </div>
          
          <div class="flex justify-between items-center font-semibold text-gray-900 dark:text-white">
            <span>Total</span>
            <span>${{ order.totalAmount.toFixed(2) }}</span>
          </div>
        </div>
      </section>

      <!-- Empty State -->
      <section v-if="filteredOrders.length === 0" class="text-center py-16">
        <div class="text-6xl mb-4">📋</div>
        <h3 class="text-xl font-semibold mb-2 text-gray-900 dark:text-white">No orders found</h3>
        <p class="text-gray-600 dark:text-gray-400 mb-6">You haven't placed any orders yet</p>
        <button @click="navigateToProducts" class="bg-blue-600 text-white px-6 py-3 rounded-full font-semibold hover:bg-blue-700 transition">
          Place Your First Order
        </button>
      </section>
    </main>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useOrderStore } from '../store/orderStore'

const router = useRouter()
const orderStore = useOrderStore()

// Order filters
const orderFilters = ref([
  { label: 'All', value: 'all' },
  { label: 'Pending', value: 'pending' },
  { label: 'Processing', value: 'processing' },
  { label: 'Completed', value: 'completed' },
  { label: 'Cancelled', value: 'cancelled' }
])
const selectedFilter = ref('all')

onMounted(async () => {
  await orderStore.fetchOrders()
})

const orders = computed(() => orderStore.allOrders)

const filteredOrders = computed(() => {
  if (selectedFilter.value === 'all') {
    return orders.value
  }
  if (selectedFilter.value === 'pending') {
    return orders.value.filter(order => order.orderStatus === 1)
  }
  if (selectedFilter.value === 'processing') {
    return orders.value.filter(order => order.orderStatus === 2)
  }
  if (selectedFilter.value === 'completed') {
    return orders.value.filter(order => order.orderStatus === 3)
  }
  if (selectedFilter.value === 'cancelled') {
    return orders.value.filter(order => order.orderStatus === 4)
  }
  return orders.value
})

const orderStatusText = (status: number) => {
  switch (status) {
    case 1: return 'Pending'
    case 2: return 'Processing'
    case 3: return 'Completed'
    case 4: return 'Cancelled'
    default: return 'Unknown'
  }
}

const navigateToOrderDetail = (orderId: number) => {
  router.push(`/order/${orderId}`)
}

const navigateToProducts = () => {
  router.push('/products')
}

const navigateBack = () => {
  router.back()
}
</script>