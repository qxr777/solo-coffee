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
          <h1 class="text-xl font-bold text-gray-900 dark:text-white">Settings</h1>
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
        <h3 class="text-xl font-semibold mb-2 text-gray-900 dark:text-white">Error loading settings</h3>
        <p class="text-gray-600 dark:text-gray-400 mb-6">{{ error }}</p>
        <button @click="fetchSettingsData" class="bg-blue-600 text-white px-6 py-2 rounded-full font-medium hover:bg-blue-700 transition">
          Try Again
        </button>
      </div>

      <!-- Settings Content -->
      <div v-else class="space-y-6">
        <!-- Personal Information -->
        <section class="bg-white dark:bg-gray-800 rounded-xl p-6 shadow-sm">
          <h2 class="text-lg font-semibold mb-4 text-gray-900 dark:text-white">Personal Information</h2>
          <form @submit.prevent="updatePersonalInfo" class="space-y-4">
            <div>
              <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">Full Name</label>
              <input 
                v-model="personalInfo.name"
                type="text"
                class="w-full border border-gray-300 dark:border-gray-700 rounded-lg px-4 py-3 focus:outline-none focus:ring-2 focus:ring-blue-500 dark:bg-gray-700 dark:text-white"
              >
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">Email</label>
              <input 
                v-model="personalInfo.email"
                type="email"
                class="w-full border border-gray-300 dark:border-gray-700 rounded-lg px-4 py-3 focus:outline-none focus:ring-2 focus:ring-blue-500 dark:bg-gray-700 dark:text-white"
              >
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">Phone</label>
              <input 
                v-model="personalInfo.phone"
                type="tel"
                class="w-full border border-gray-300 dark:border-gray-700 rounded-lg px-4 py-3 focus:outline-none focus:ring-2 focus:ring-blue-500 dark:bg-gray-700 dark:text-white"
              >
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">Date of Birth</label>
              <input 
                v-model="personalInfo.dateOfBirth"
                type="date"
                class="w-full border border-gray-300 dark:border-gray-700 rounded-lg px-4 py-3 focus:outline-none focus:ring-2 focus:ring-blue-500 dark:bg-gray-700 dark:text-white"
              >
            </div>
            <button 
              type="submit"
              :disabled="updating"
              class="w-full bg-blue-600 text-white py-3 rounded-lg font-semibold hover:bg-blue-700 transition disabled:opacity-50 disabled:cursor-not-allowed"
            >
              {{ updating ? 'Updating...' : 'Update Information' }}
            </button>
          </form>
        </section>

        <!-- Address Management -->
        <section class="bg-white dark:bg-gray-800 rounded-xl p-6 shadow-sm">
          <div class="flex justify-between items-center mb-4">
            <h2 class="text-lg font-semibold text-gray-900 dark:text-white">Address Management</h2>
            <button @click="showAddAddressModal = true" class="bg-blue-600 text-white px-4 py-2 rounded-full text-sm font-medium hover:bg-blue-700 transition">
              + Add Address
            </button>
          </div>
          <div class="space-y-3">
            <div v-for="address in addresses" :key="address.id" class="flex items-start justify-between p-4 border border-gray-200 dark:border-gray-700 rounded-lg">
              <div class="flex-1">
                <div class="flex items-center mb-2">
                  <input 
                    type="checkbox" 
                    :checked="address.isDefault"
                    @change="setDefaultAddress(address.id)"
                    class="w-4 h-4 text-blue-600 rounded focus:ring-blue-500"
                  >
                  <span class="ml-2 text-sm text-gray-600 dark:text-gray-400">
                    {{ address.isDefault ? 'Default' : 'Set as Default' }}
                  </span>
                </div>
                <div>
                  <h3 class="font-semibold text-gray-900 dark:text-white mb-1">{{ address.name }}</h3>
                  <p class="text-sm text-gray-600 dark:text-gray-400 mb-1">{{ address.phone }}</p>
                  <p class="text-sm text-gray-600 dark:text-gray-400">{{ address.fullAddress }}</p>
                  <p v-if="address.isDefault" class="text-xs text-blue-600 dark:text-blue-400 font-medium">Default Address</p>
                </div>
              </div>
              <div class="flex space-x-2">
                <button @click="editAddress(address)" class="text-blue-600 dark:text-blue-400 text-sm font-medium hover:underline">
                  Edit
                </button>
                <button @click="deleteAddress(address.id)" class="text-red-600 dark:text-red-400 text-sm font-medium hover:underline">
                  Delete
                </button>
              </div>
            </div>
          </div>
        </section>

        <!-- Payment Methods -->
        <section class="bg-white dark:bg-gray-800 rounded-xl p-6 shadow-sm">
          <div class="flex justify-between items-center mb-4">
            <h2 class="text-lg font-semibold text-gray-900 dark:text-white">Payment Methods</h2>
            <button @click="showAddPaymentModal = true" class="bg-blue-600 text-white px-4 py-2 rounded-full text-sm font-medium hover:bg-blue-700 transition">
              + Add Payment Method
            </button>
          </div>
          <div class="space-y-3">
            <div v-for="payment in paymentMethods" :key="payment.id" class="flex items-center justify-between p-4 border border-gray-200 dark:border-gray-700 rounded-lg">
              <div class="flex items-center">
                <div class="w-12 h-8 bg-gray-100 dark:bg-gray-700 rounded-lg flex items-center justify-center mr-4">
                  <span class="text-xl">{{ getPaymentIcon(payment.type) }}</span>
                </div>
                <div class="flex-1">
                  <h3 class="font-semibold text-gray-900 dark:text-white mb-1">{{ payment.name }}</h3>
                  <p class="text-sm text-gray-600 dark:text-gray-400 mb-1">{{ payment.description }}</p>
                  <div class="flex items-center">
                    <span class="text-xs text-gray-500 dark:text-gray-400 mr-2">**** {{ payment.lastFour }}</span>
                    <span v-if="payment.isDefault" class="text-xs text-blue-600 dark:text-blue-400 font-medium">Default</span>
                  </div>
                </div>
              </div>
              <div class="flex space-x-2">
                <button @click="setDefaultPayment(payment.id)" class="text-blue-600 dark:text-blue-400 text-sm font-medium hover:underline">
                  Set Default
                </button>
                <button @click="deletePayment(payment.id)" class="text-red-600 dark:text-red-400 text-sm font-medium hover:underline">
                  Delete
                </button>
              </div>
            </div>
          </div>
        </section>

        <!-- Notification Settings -->
        <section class="bg-white dark:bg-gray-800 rounded-xl p-6 shadow-sm">
          <h2 class="text-lg font-semibold mb-4 text-gray-900 dark:text-white">Notification Preferences</h2>
          <div class="space-y-4">
            <div class="flex items-center justify-between">
              <div>
                <h3 class="font-medium text-gray-900 dark:text-white">Order Updates</h3>
                <p class="text-sm text-gray-600 dark:text-gray-400">Receive notifications about your orders</p>
              </div>
              <label class="relative inline-flex items-center cursor-pointer">
                <input 
                  type="checkbox" 
                  v-model="notificationSettings.orderUpdates"
                  class="sr-only peer"
                >
                <div class="w-11 h-6 bg-gray-200 peer-focus:outline-none peer-focus:ring-2 peer-focus:ring-blue-500 rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-gray-300 peer-checked:bg-blue-600 peer-checked:border-transparent after:transition-all after:duration-200 dark:peer-focus:ring-blue-500 dark:peer-checked:bg-blue-600 dark:peer-checked:border-transparent"></div>
              </label>
            </div>
            <div class="flex items-center justify-between">
              <div>
                <h3 class="font-medium text-gray-900 dark:text-white">Promotions</h3>
                <p class="text-sm text-gray-600 dark:text-gray-400">Receive special offers and discounts</p>
              </div>
              <label class="relative inline-flex items-center cursor-pointer">
                <input 
                  type="checkbox" 
                  v-model="notificationSettings.promotions"
                  class="sr-only peer"
                >
                <div class="w-11 h-6 bg-gray-200 peer-focus:outline-none peer-focus:ring-2 peer-focus:ring-blue-500 rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-gray-300 peer-checked:bg-blue-600 peer-checked:border-transparent after:transition-all after:duration-200 dark:peer-focus:ring-blue-500 dark:peer-checked:bg-blue-600 dark:peer-checked:border-transparent"></div>
              </label>
            </div>
            <div class="flex items-center justify-between">
              <div>
                <h3 class="font-medium text-gray-900 dark:text-white">New Products</h3>
                <p class="text-sm text-gray-600 dark:text-gray-400">Get notified about new menu items</p>
              </div>
              <label class="relative inline-flex items-center cursor-pointer">
                <input 
                  type="checkbox" 
                  v-model="notificationSettings.newProducts"
                  class="sr-only peer"
                >
                <div class="w-11 h-6 bg-gray-200 peer-focus:outline-none peer-focus:ring-2 peer-focus:ring-blue-500 rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-gray-300 peer-checked:bg-blue-600 peer-checked:border-transparent after:transition-all after:duration-200 dark:peer-focus:ring-blue-500 dark:peer-checked:bg-blue-600 dark:peer-checked:border-transparent"></div>
              </label>
            </div>
          </div>
        </section>

        <!-- Privacy Settings -->
        <section class="bg-white dark:bg-gray-800 rounded-xl p-6 shadow-sm">
          <h2 class="text-lg font-semibold mb-4 text-gray-900 dark:text-white">Privacy Settings</h2>
          <div class="space-y-4">
            <div class="flex items-center justify-between">
              <div>
                <h3 class="font-medium text-gray-900 dark:text-white">Profile Visibility</h3>
                <p class="text-sm text-gray-600 dark:text-gray-400">Control who can see your profile</p>
              </div>
              <select 
                v-model="privacySettings.profileVisibility"
                class="border border-gray-300 dark:border-gray-700 rounded-lg px-4 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500 dark:bg-gray-700 dark:text-white"
              >
                <option value="public">Public</option>
                <option value="members">Members Only</option>
                <option value="private">Private</option>
              </select>
            </div>
            <div class="flex items-center justify-between">
              <div>
                <h3 class="font-medium text-gray-900 dark:text-white">Order History</h3>
                <p class="text-sm text-gray-600 dark:text-gray-400">Control who can see your order history</p>
              </div>
              <select 
                v-model="privacySettings.orderHistoryVisibility"
                class="border border-gray-300 dark:border-gray-700 rounded-lg px-4 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500 dark:bg-gray-700 dark:text-white"
              >
                <option value="public">Public</option>
                <option value="members">Members Only</option>
                <option value="private">Private</option>
              </select>
            </div>
          </div>
        </section>
      </div>
    </main>

    <!-- Add Address Modal -->
    <div v-if="showAddAddressModal" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div class="bg-white dark:bg-gray-800 rounded-xl p-6 w-full max-w-md mx-4">
        <h3 class="text-lg font-semibold mb-4 text-gray-900 dark:text-white">Add New Address</h3>
        <form @submit.prevent="addAddress" class="space-y-4">
          <div>
            <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">Name</label>
            <input 
              v-model="newAddress.name"
              type="text"
              required
              class="w-full border border-gray-300 dark:border-gray-700 rounded-lg px-4 py-3 focus:outline-none focus:ring-2 focus:ring-blue-500 dark:bg-gray-700 dark:text-white"
            >
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">Phone</label>
            <input 
              v-model="newAddress.phone"
              type="tel"
              required
              class="w-full border border-gray-300 dark:border-gray-700 rounded-lg px-4 py-3 focus:outline-none focus:ring-2 focus:ring-blue-500 dark:bg-gray-700 dark:text-white"
            >
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">Address</label>
            <textarea 
              v-model="newAddress.fullAddress"
              required
              rows="3"
              class="w-full border border-gray-300 dark:border-gray-700 rounded-lg px-4 py-3 focus:outline-none focus:ring-2 focus:ring-blue-500 dark:bg-gray-700 dark:text-white"
            ></textarea>
          </div>
          <div class="flex items-center">
            <input 
              type="checkbox" 
              v-model="newAddress.isDefault"
              id="defaultAddress"
              class="w-4 h-4 text-blue-600 rounded focus:ring-blue-500"
            >
            <label for="defaultAddress" class="ml-2 text-sm text-gray-700 dark:text-gray-300">Set as default address</label>
          </div>
          <div class="flex space-x-3">
            <button 
              type="button"
              @click="showAddAddressModal = false"
              class="flex-1 border border-gray-300 dark:border-gray-700 text-gray-900 dark:text-white py-3 rounded-lg font-medium hover:bg-gray-50 dark:hover:bg-gray-700 transition"
            >
              Cancel
            </button>
            <button 
              type="submit"
              class="flex-1 bg-blue-600 text-white py-3 rounded-lg font-semibold hover:bg-blue-700 transition"
            >
              Add Address
            </button>
          </div>
        </form>
      </div>
    </div>

    <!-- Add Payment Method Modal -->
    <div v-if="showAddPaymentModal" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div class="bg-white dark:bg-gray-800 rounded-xl p-6 w-full max-w-md mx-4">
        <h3 class="text-lg font-semibold mb-4 text-gray-900 dark:text-white">Add Payment Method</h3>
        <form @submit.prevent="addPaymentMethod" class="space-y-4">
          <div>
            <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">Card Name</label>
            <input 
              v-model="newPayment.name"
              type="text"
              required
              class="w-full border border-gray-300 dark:border-gray-700 rounded-lg px-4 py-3 focus:outline-none focus:ring-2 focus:ring-blue-500 dark:bg-gray-700 dark:text-white"
            >
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">Card Number</label>
            <input 
              v-model="newPayment.cardNumber"
              type="text"
              required
              maxlength="16"
              placeholder="**** **** **** **** ****"
              class="w-full border border-gray-300 dark:border-gray-700 rounded-lg px-4 py-3 focus:outline-none focus:ring-2 focus:ring-blue-500 dark:bg-gray-700 dark:text-white"
            >
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">Expiry Date</label>
            <input 
              v-model="newPayment.expiryDate"
              type="text"
              required
              placeholder="MM/YY"
              maxlength="5"
              class="w-full border border-gray-300 dark:border-gray-700 rounded-lg px-4 py-3 focus:outline-none focus:ring-2 focus:ring-blue-500 dark:bg-gray-700 dark:text-white"
            >
          </div>
          <div class="flex items-center">
            <input 
              type="checkbox" 
              v-model="newPayment.isDefault"
              id="defaultPayment"
              class="w-4 h-4 text-blue-600 rounded focus:ring-blue-500"
            >
            <label for="defaultPayment" class="ml-2 text-sm text-gray-700 dark:text-gray-300">Set as default payment method</label>
          </div>
          <div class="flex space-x-3">
            <button 
              type="button"
              @click="showAddPaymentModal = false"
              class="flex-1 border border-gray-300 dark:border-gray-700 text-gray-900 dark:text-white py-3 rounded-lg font-medium hover:bg-gray-50 dark:hover:bg-gray-700 transition"
            >
              Cancel
            </button>
            <button 
              type="submit"
              class="flex-1 bg-blue-600 text-white py-3 rounded-lg font-semibold hover:bg-blue-700 transition"
            >
              Add Payment Method
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../store/authStore'

const router = useRouter()
const authStore = useAuthStore()

// State
const loading = ref(false)
const error = ref<string | null>(null)
const updating = ref(false)

// Personal Information
const personalInfo = ref({
  name: '',
  email: '',
  phone: '',
  dateOfBirth: ''
})

// Addresses
const addresses = ref([
  { id: 1, name: 'Home', phone: '123-456-7890', fullAddress: '123 Main Street, Apt 4B, New York, NY 10001', isDefault: true },
  { id: 2, name: 'Office', phone: '987-654-3210', fullAddress: '456 Business Ave, Suite 100, New York, NY 10002', isDefault: false },
  { id: 3, name: 'Other', phone: '555-123-4567', fullAddress: '789 Oak Street, New York, NY 10003', isDefault: false }
])

const showAddAddressModal = ref(false)
const newAddress = ref({
  name: '',
  phone: '',
  fullAddress: '',
  isDefault: false
})

// Payment Methods
interface PaymentMethod {
  id: number
  name: string
  type: string
  description: string
  lastFour: string
  isDefault: boolean
}

const paymentMethods = ref<PaymentMethod[]>([
  { id: 1, name: 'Visa ending in 4242', type: 'visa', description: 'Expires 12/25', lastFour: '4242', isDefault: true },
  { id: 2, name: 'Mastercard ending in 8765', type: 'mastercard', description: 'Expires 08/26', lastFour: '8765', isDefault: false },
  { id: 3, name: 'Apple Pay', type: 'applepay', description: 'Digital wallet', lastFour: '', isDefault: false }
])

const showAddPaymentModal = ref(false)
const newPayment = ref({
  name: '',
  cardNumber: '',
  expiryDate: '',
  isDefault: false
})

// Notification Settings
const notificationSettings = ref({
  orderUpdates: true,
  promotions: true,
  newProducts: false
})

// Privacy Settings
const privacySettings = ref({
  profileVisibility: 'public',
  orderHistoryVisibility: 'members'
})

// Methods
const getPaymentIcon = (type: string) => {
  switch (type) {
    case 'visa': return '💳'
    case 'mastercard': return '💳'
    case 'applepay': return '🍎'
    default: return '💳'
  }
}

const updatePersonalInfo = async () => {
  updating.value = true
  try {
    // In a real app, update via API
    // await authStore.updateProfile(personalInfo.value)
    alert('Personal information updated successfully')
  } catch (err: any) {
    error.value = err.message || 'Failed to update personal information'
  } finally {
    updating.value = false
  }
}

const addAddress = () => {
  addresses.value.push({
    id: Date.now(),
    ...newAddress.value
  })
  showAddAddressModal.value = false
  newAddress.value = { name: '', phone: '', fullAddress: '', isDefault: false }
  alert('Address added successfully')
}

const editAddress = (address: any) => {
  alert(`Edit address: ${address.name}`)
}

const deleteAddress = (addressId: number) => {
  addresses.value = addresses.value.filter(addr => addr.id !== addressId)
  alert('Address deleted successfully')
}

const setDefaultAddress = (addressId: number) => {
  addresses.value = addresses.value.map(addr => ({
    ...addr,
    isDefault: addr.id === addressId
  }))
  alert('Default address updated')
}

const addPaymentMethod = () => {
  paymentMethods.value.push({
    id: Date.now(),
    name: newPayment.value.name,
    type: 'visa',
    description: 'Custom card',
    lastFour: newPayment.value.cardNumber.slice(-4),
    isDefault: newPayment.value.isDefault
  })
  showAddPaymentModal.value = false
  newPayment.value = { name: '', cardNumber: '', expiryDate: '', isDefault: false }
  alert('Payment method added successfully')
}

const setDefaultPayment = (paymentId: number) => {
  paymentMethods.value = paymentMethods.value.map(payment => ({
    ...payment,
    isDefault: payment.id === paymentId
  }))
  alert('Default payment method updated')
}

const deletePayment = (paymentId: number) => {
  paymentMethods.value = paymentMethods.value.filter(payment => payment.id !== paymentId)
  alert('Payment method deleted successfully')
}

const fetchSettingsData = async () => {
  loading.value = true
  error.value = null
  try {
    // In a real app, fetch from API
    // await authStore.fetchUserProfile()
    // personalInfo.value = authStore.currentUser
  } catch (err: any) {
    error.value = err.message || 'Failed to load settings'
  } finally {
    loading.value = false
  }
}

const navigateBack = () => {
  router.back()
}

onMounted(() => {
  // Initialize with current user data
  if (authStore.currentUser) {
    personalInfo.value = {
      name: authStore.currentUser.name || '',
      email: authStore.currentUser.email || '',
      phone: authStore.currentUser.phone || '',
      dateOfBirth: ''
    }
  }
})
</script>