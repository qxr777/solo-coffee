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
          <h1 class="text-xl font-bold text-gray-900 dark:text-white">Order Detail</h1>
        </div>
      </div>
    </header>

    <main class="container mx-auto px-4 py-8">
      <div v-if="order" class="bg-white dark:bg-gray-800 rounded-xl overflow-hidden shadow-sm">
        <!-- Order Status -->
        <div :class="[
          'py-6 text-center',
          order.orderStatus === 1 
            ? 'bg-yellow-100 dark:bg-yellow-900/20' 
            : order.orderStatus === 2
              ? 'bg-blue-100 dark:bg-blue-900/20'
              : order.orderStatus === 3
                ? 'bg-green-100 dark:bg-green-900/20'
                : 'bg-red-100 dark:bg-red-900/20'
        ]">
          <h2 class="text-xl font-bold mb-2" :class="[
            order.orderStatus === 1 
              ? 'text-yellow-800 dark:text-yellow-200' 
              : order.orderStatus === 2
                ? 'text-blue-800 dark:text-blue-200'
                : order.orderStatus === 3
                  ? 'text-green-800 dark:text-green-200'
                  : 'text-red-800 dark:text-red-200'
          ]">
            {{ orderStatusText(order.orderStatus) }}
          </h2>
          <p class="text-sm" :class="[
            order.orderStatus === 1 
              ? 'text-yellow-700 dark:text-yellow-300' 
              : order.orderStatus === 2
                ? 'text-blue-700 dark:text-blue-300'
                : order.orderStatus === 3
                  ? 'text-green-700 dark:text-green-300'
                  : 'text-red-700 dark:text-red-300'
          ]">
            {{ orderStatusDescription(order.orderStatus) }}
          </p>
        </div>

        <!-- Order Information -->
        <div class="p-6">
          <!-- Order Header -->
          <div class="flex justify-between items-center mb-6">
            <div>
              <h2 class="text-lg font-semibold text-gray-900 dark:text-white">Order #{{ order.orderNo }}</h2>
              <p class="text-sm text-gray-600 dark:text-gray-400">{{ new Date(order.createdAt).toLocaleString() }}</p>
            </div>
            <button 
              v-if="order.orderStatus === 1"
              @click="cancelOrder"
              class="bg-red-600 text-white px-4 py-2 rounded-full text-sm font-medium hover:bg-red-700 transition"
            >
              Cancel Order
            </button>
          </div>

          <!-- Store Information -->
          <div class="mb-6">
            <h3 class="font-medium text-gray-900 dark:text-white mb-2">Store Information</h3>
            <p class="text-gray-600 dark:text-gray-400">Store {{ order.storeId }}</p>
            <p class="text-gray-600 dark:text-gray-400">123 Coffee Street, New York</p>
          </div>

          <!-- Pickup Information -->
          <div class="mb-6">
            <h3 class="font-medium text-gray-900 dark:text-white mb-2">Pickup Information</h3>
            <p class="text-gray-600 dark:text-gray-400">Pickup Time: {{ new Date(order.pickupTime).toLocaleString() }}</p>
          </div>

          <!-- Order Items -->
          <div class="mb-6">
            <h3 class="font-medium text-gray-900 dark:text-white mb-3">Order Items</h3>
            <div class="space-y-3">
              <div v-for="(item, index) in (order.orderItems || [])" :key="index" class="flex justify-between items-center pb-2 border-b border-gray-200 dark:border-gray-700">
                <div>
                  <p class="text-gray-900 dark:text-white">{{ item.productName || 'Unknown Product' }}</p>
                  <p class="text-sm text-gray-600 dark:text-gray-400">× {{ item.quantity || 0 }}</p>
                </div>
                <span class="font-medium text-gray-900 dark:text-white">${{ (item.subtotal || 0).toFixed(2) }}</span>
              </div>
            </div>
          </div>

          <!-- Payment Information -->
          <div class="mb-6">
            <h3 class="font-medium text-gray-900 dark:text-white mb-3">Payment Information</h3>
            <div class="space-y-2">
              <div class="flex justify-between items-center">
                <span class="text-gray-600 dark:text-gray-400">Payment Method</span>
                <span class="text-gray-900 dark:text-white">{{ paymentMethodText(order.paymentMethod) }}</span>
              </div>
              <div class="flex justify-between items-center">
                <span class="text-gray-600 dark:text-gray-400">Total Amount</span>
                <span class="text-gray-900 dark:text-white">${{ order.totalAmount.toFixed(2) }}</span>
              </div>
            </div>
          </div>

          <!-- Order Notes -->
          <div v-if="order.remarks" class="mb-6">
            <h3 class="font-medium text-gray-900 dark:text-white mb-2">Order Notes</h3>
            <p class="text-gray-600 dark:text-gray-400">{{ order.remarks }}</p>
          </div>

          <!-- Action Buttons -->
          <div class="flex space-x-4">
            <button @click="navigateBack" class="flex-1 border border-gray-300 dark:border-gray-700 text-gray-900 dark:text-white px-4 py-3 rounded-lg font-medium hover:bg-gray-50 dark:hover:bg-gray-700 transition">
              Back to Orders
            </button>
            <button @click="reorder" class="flex-1 bg-blue-600 text-white px-4 py-3 rounded-lg font-medium hover:bg-blue-700 transition">
              Reorder
            </button>
          </div>
        </div>
      </div>

      <!-- Loading State -->
      <div v-else class="flex items-center justify-center py-20">
        <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useOrderStore } from '../store/orderStore'

const router = useRouter()
const route = useRoute()
const orderStore = useOrderStore()

const orderId = Number(route.params.id)
const order = ref<any>(null)

// ... omitted methods ...

const orderStatusText = (status: number) => {
  switch (status) {
    case 1: return 'Pending'
    case 2: return 'Processing'
    case 3: return 'Completed'
    case 4: return 'Cancelled'
    default: return 'Unknown'
  }
}

const orderStatusDescription = (status: number) => {
  switch (status) {
    case 1: return 'Your order is waiting to be processed'
    case 2: return 'Your order is being prepared'
    case 3: return 'Your order is ready for pickup'
    case 4: return 'Your order has been cancelled'
    default: return 'Unknown order status'
  }
}

const paymentMethodText = (method: number) => {
  switch (method) {
    case 1: return 'Credit Card'
    case 2: return 'Cash'
    case 3: return 'Mobile Payment'
    default: return 'Unknown'
  }
}

const cancelOrder = async () => {
  try {
    await orderStore.cancelOrder(orderId)
    // Refresh order details after cancellation
    await orderStore.fetchOrderById(orderId)
    order.value = orderStore.currentOrderDetails
    alert('Order cancelled successfully')
  } catch (error) {
    console.error('Failed to cancel order:', error)
    alert('Failed to cancel order')
  }
}

const reorder = () => {
  // Navigate to products page to reorder
  router.push('/products')
}

const navigateBack = () => {
  router.push('/orders')
}

onMounted(async () => {
  try {
    await orderStore.fetchOrderById(orderId)
    order.value = orderStore.currentOrderDetails
  } catch (e) {
    console.error('Fetch order error', e)
    // Consider adding error handling/redirect
  }
})
</script>