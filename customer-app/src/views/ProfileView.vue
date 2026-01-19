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
          <h1 class="text-xl font-bold text-gray-900 dark:text-white">Your Profile</h1>
        </div>
      </div>
    </header>

    <main class="container mx-auto px-4 py-8">
      <!-- User Information -->
      <section class="bg-white dark:bg-gray-800 rounded-xl p-6 shadow-sm mb-6">
        <div class="flex items-center mb-6">
          <div class="w-20 h-20 bg-blue-100 dark:bg-blue-900 rounded-full flex items-center justify-center mr-4">
            <span class="text-3xl">👤</span>
          </div>
          <div>
            <h2 class="text-xl font-bold text-gray-900 dark:text-white">{{ user?.name || 'Guest User' }}</h2>
            <p class="text-gray-600 dark:text-gray-400">{{ user?.email || 'Not logged in' }}</p>
          </div>
        </div>
        
        <div v-if="!isAuthenticated" class="flex space-x-4">
          <button @click="navigateToLogin" class="flex-1 bg-blue-600 text-white px-4 py-2 rounded-lg font-medium hover:bg-blue-700 transition">
            Login
          </button>
          <button @click="navigateToRegister" class="flex-1 border border-gray-300 dark:border-gray-700 text-gray-900 dark:text-white px-4 py-2 rounded-lg font-medium hover:bg-gray-50 dark:hover:bg-gray-700 transition">
            Register
          </button>
        </div>
      </section>

      <!-- Member Information -->
      <section class="bg-white dark:bg-gray-800 rounded-xl p-6 shadow-sm mb-6">
        <h2 class="text-lg font-semibold mb-4 text-gray-900 dark:text-white">Membership</h2>
        <div class="space-y-3">
          <div class="flex justify-between items-center">
            <span class="text-gray-600 dark:text-gray-400">Membership Level</span>
            <span class="font-semibold text-gray-900 dark:text-white">Level {{ user?.memberLevelId || 0 }}</span>
          </div>
          <div class="flex justify-between items-center">
            <span class="text-gray-600 dark:text-gray-400">Current Points</span>
            <span class="font-semibold text-gray-900 dark:text-white">{{ user?.points || 0 }} points</span>
          </div>
          <div class="flex justify-between items-center">
            <span class="text-gray-600 dark:text-gray-400">Next Level At</span>
            <span class="text-gray-900 dark:text-white">1000 points</span>
          </div>
        </div>
      </section>

      <!-- Order History -->
      <section class="bg-white dark:bg-gray-800 rounded-xl p-6 shadow-sm mb-6">
        <h2 class="text-lg font-semibold mb-4 text-gray-900 dark:text-white">Order History</h2>
        <div class="space-y-3">
          <div class="flex justify-between items-center">
            <span class="text-gray-600 dark:text-gray-400">Total Orders</span>
            <span class="font-semibold text-gray-900 dark:text-white">{{ orderCount }} orders</span>
          </div>
          <div class="flex justify-between items-center">
            <span class="text-gray-600 dark:text-gray-400">Total Spent</span>
            <span class="font-semibold text-gray-900 dark:text-white">${{ totalSpent.toFixed(2) }}</span>
          </div>
          <button @click="navigateToOrders" class="w-full bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300 py-2 rounded-lg font-medium hover:bg-gray-200 dark:hover:bg-gray-600 transition">
            View All Orders
          </button>
        </div>
      </section>

      <!-- Settings -->
      <section class="bg-white dark:bg-gray-800 rounded-xl p-6 shadow-sm">
        <h2 class="text-lg font-semibold mb-4 text-gray-900 dark:text-white">Settings</h2>
        <div class="space-y-3">
          <button @click="navigateToEditProfile" class="w-full flex justify-between items-center p-3 hover:bg-gray-50 dark:hover:bg-gray-700 rounded-lg transition">
            <span class="text-gray-900 dark:text-white">Edit Profile</span>
            <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7" />
            </svg>
          </button>
          <button @click="navigateToChangePassword" class="w-full flex justify-between items-center p-3 hover:bg-gray-50 dark:hover:bg-gray-700 rounded-lg transition">
            <span class="text-gray-900 dark:text-white">Change Password</span>
            <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7" />
            </svg>
          </button>
          <button @click="navigateToNotificationSettings" class="w-full flex justify-between items-center p-3 hover:bg-gray-50 dark:hover:bg-gray-700 rounded-lg transition">
            <span class="text-gray-900 dark:text-white">Notification Settings</span>
            <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7" />
            </svg>
          </button>
          <button v-if="isAuthenticated" @click="logout" class="w-full flex justify-between items-center p-3 text-red-600 dark:text-red-400 hover:bg-red-50 dark:hover:bg-red-900/20 rounded-lg transition">
            <span>Logout</span>
            <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1" />
            </svg>
          </button>
        </div>
      </section>
    </main>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../store/authStore'

const router = useRouter()
const authStore = useAuthStore()

const user = ref({
  id: 1,
  name: 'John Doe',
  email: 'john@example.com',
  phone: '123-456-7890',
  memberLevelId: 2,
  points: 1250
})

const isAuthenticated = computed(() => authStore.isAuthenticated)
const orderCount = ref(10)
const totalSpent = ref(250.75)

const navigateToLogin = () => {
  router.push('/login')
}

const navigateToRegister = () => {
  router.push('/register')
}

const navigateToOrders = () => {
  router.push('/orders')
}

const navigateToEditProfile = () => {
  router.push('/settings')
}

const navigateToChangePassword = () => {
  router.push('/change-password')
}

const navigateToNotificationSettings = () => {
  router.push('/settings')
}

const logout = () => {
  authStore.logout()
  router.push('/')
}

const navigateBack = () => {
  router.back()
}
</script>