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
          <h1 class="text-xl font-bold text-gray-900 dark:text-white">Member Center</h1>
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
        <h3 class="text-xl font-semibold mb-2 text-gray-900 dark:text-white">Error loading member data</h3>
        <p class="text-gray-600 dark:text-gray-400 mb-6">{{ error }}</p>
        <button @click="fetchMemberData" class="bg-blue-600 text-white px-6 py-2 rounded-full font-medium hover:bg-blue-700 transition">
          Try Again
        </button>
      </div>

      <!-- Member Content -->
      <div v-else class="space-y-6">
        <!-- Member Info Card -->
        <section class="bg-white dark:bg-gray-800 rounded-xl p-6 shadow-sm">
          <h2 class="text-lg font-semibold mb-4 text-gray-900 dark:text-white">Member Information</h2>
          <div class="flex items-center space-x-4">
            <div class="w-16 h-16 bg-primary/10 rounded-full flex items-center justify-center">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-8 w-8 text-primary" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"></path>
              </svg>
            </div>
            <div class="flex-1">
              <h3 class="text-xl font-bold text-gray-900 dark:text-white">{{ memberInfo?.name || 'Guest' }}</h3>
              <div class="flex items-center space-x-2 mt-1">
                <span :class="[
                  'px-3 py-1 rounded-full text-sm font-medium',
                  getMemberLevelColor(memberInfo?.memberLevelId || 1)
                ]">
                  {{ getMemberLevelText(memberInfo?.memberLevelId || 1) }}
                </span>
                <span class="text-gray-500 dark:text-gray-400 text-sm">
                  Level {{ memberInfo?.memberLevelId || 1 }}
                </span>
              </div>
              <div class="mt-3 space-y-2">
                <div class="flex items-center text-gray-600 dark:text-gray-400">
                  <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 5a2 2 0 012-2 7a2 2 0 012 2zm0 8a3 3 0 00-6 3 3 0 006zm0 8a3 3 0 00-6 3 3 0 006zm18 10a3 3 0 00-6 3 3 0 006z"></path>
                  </svg>
                  <span>Points: {{ memberInfo?.points || 0 }}</span>
                </div>
                <div class="flex items-center text-gray-600 dark:text-gray-400">
                  <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 11V7a4 4 0 00-8 0v4M5 9h14l1 12H4L5 9z"></path>
                  </svg>
                  <span>Orders: {{ orderCount }}</span>
                </div>
              </div>
            </div>
          </div>
        </section>

        <!-- Points History -->
        <section class="bg-white dark:bg-gray-800 rounded-xl p-6 shadow-sm">
          <div class="flex justify-between items-center mb-4">
            <h2 class="text-lg font-semibold text-gray-900 dark:text-white">Points History</h2>
            <button @click="navigateToPoints" class="text-blue-600 dark:text-blue-400 text-sm font-medium hover:underline">
              View All
            </button>
          </div>
          <div class="space-y-3">
            <div v-for="(record, index) in pointsHistory.slice(0, 5)" :key="index" class="flex justify-between items-center pb-3 border-b border-gray-200 dark:border-gray-700">
              <div>
                <p class="text-gray-900 dark:text-white font-medium">{{ record.description }}</p>
                <p class="text-sm text-gray-600 dark:text-gray-400">{{ new Date(record.date).toLocaleDateString() }}</p>
              </div>
              <span :class="[
                'text-sm font-medium',
                record.type === 'earn' ? 'text-green-600 dark:text-green-400' : 'text-red-600 dark:text-red-400'
              ]">
                {{ record.type === 'earn' ? '+' : '-' }}{{ record.points }}
              </span>
            </div>
          </div>
        </section>

        <!-- Points Redemption -->
        <section class="bg-white dark:bg-gray-800 rounded-xl p-6 shadow-sm">
          <h2 class="text-lg font-semibold mb-4 text-gray-900 dark:text-white">Points Redemption</h2>
          <div class="grid grid-cols-2 gap-4">
            <div v-for="item in redemptionItems" :key="item.id" class="border border-gray-200 dark:border-gray-700 rounded-lg p-4 hover:shadow-md transition cursor-pointer" @click="redeemItem(item)">
              <div class="w-full h-32 bg-gray-100 dark:bg-gray-700 rounded-lg flex items-center justify-center mb-3">
                <span class="text-4xl">{{ item.icon }}</span>
              </div>
              <h3 class="font-semibold text-gray-900 dark:text-white mb-1">{{ item.name }}</h3>
              <p class="text-sm text-gray-600 dark:text-gray-400 mb-2">{{ item.description }}</p>
              <div class="flex justify-between items-center">
                <span class="text-blue-600 dark:text-blue-400 font-bold">{{ item.points }} points</span>
                <button class="bg-blue-600 text-white px-3 py-1 rounded-full text-sm font-medium hover:bg-blue-700 transition">
                  Redeem
                </button>
              </div>
            </div>
          </div>
        </section>

        <!-- Coupons -->
        <section class="bg-white dark:bg-gray-800 rounded-xl p-6 shadow-sm">
          <div class="flex justify-between items-center mb-4">
            <h2 class="text-lg font-semibold text-gray-900 dark:text-white">Available Coupons</h2>
            <button @click="navigateToCoupons" class="text-blue-600 dark:text-blue-400 text-sm font-medium hover:underline">
              View All
            </button>
          </div>
          <div class="space-y-3">
            <div v-for="coupon in availableCoupons.slice(0, 3)" :key="coupon.id" class="flex items-center justify-between p-4 border border-gray-200 dark:border-gray-700 rounded-lg">
              <div class="flex-1">
                <div class="flex items-center mb-2">
                  <div :class="[
                    'w-12 h-12 rounded-lg flex items-center justify-center text-white font-bold',
                    coupon.type === 'discount' ? 'bg-green-600' : 'bg-blue-600'
                  ]">
                    {{ coupon.type === 'discount' ? coupon.discount + '%' : '$' + coupon.discount }}
                  </div>
                  <div>
                    <h3 class="font-semibold text-gray-900 dark:text-white">{{ coupon.title }}</h3>
                    <p class="text-sm text-gray-600 dark:text-gray-400">{{ coupon.description }}</p>
                  </div>
                </div>
                <div class="text-sm text-gray-500 dark:text-gray-400">
                  <p>Valid until: {{ new Date(coupon.expiryDate).toLocaleDateString() }}</p>
                  <p>Min. purchase: ${{ coupon.minPurchase }}</p>
                </div>
              </div>
              <button @click="useCoupon(coupon)" class="bg-blue-600 text-white px-4 py-2 rounded-lg text-sm font-medium hover:bg-blue-700 transition">
                Use
              </button>
            </div>
          </div>
        </section>

        <!-- Member Level Benefits -->
        <section class="bg-white dark:bg-gray-800 rounded-xl p-6 shadow-sm">
          <h2 class="text-lg font-semibold mb-4 text-gray-900 dark:text-white">Member Benefits</h2>
          <div class="space-y-3">
            <div v-for="benefit in memberBenefits" :key="benefit.level" class="flex items-start space-x-3 p-4 border border-gray-200 dark:border-gray-700 rounded-lg">
              <div :class="[
                'w-10 h-10 rounded-full flex items-center justify-center text-white font-bold',
                getMemberLevelColor(benefit.level)
              ]">
                {{ benefit.level }}
              </div>
              <div class="flex-1">
                <h3 class="font-semibold text-gray-900 dark:text-white mb-1">{{ benefit.name }}</h3>
                <p class="text-sm text-gray-600 dark:text-gray-400">{{ benefit.description }}</p>
                <ul class="mt-2 space-y-1">
                  <li v-for="perk in benefit.perks" :key="perk" class="flex items-center text-sm text-gray-600 dark:text-gray-400">
                    <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4 mr-2 text-green-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7l-4-4H5"></path>
                    </svg>
                    <span>{{ perk }}</span>
                  </li>
                </ul>
              </div>
            </div>
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
const memberInfo = computed(() => authStore.currentUser)

// Mock data
const orderCount = ref(12)

const pointsHistory = ref([
  { id: 1, description: 'Purchase reward', date: new Date(Date.now() - 86400000).toISOString(), points: 50, type: 'earn' },
  { id: 2, description: 'Points redemption', date: new Date(Date.now() - 172800000).toISOString(), points: -25, type: 'redeem' },
  { id: 3, description: 'Daily check-in bonus', date: new Date(Date.now() - 259200000).toISOString(), points: 10, type: 'earn' },
  { id: 4, description: 'Referral bonus', date: new Date(Date.now() - 345600000).toISOString(), points: 100, type: 'earn' },
  { id: 5, description: 'Birthday bonus', date: new Date(Date.now() - 432000000).toISOString(), points: 20, type: 'earn' }
])

const redemptionItems = ref([
  { id: 1, name: 'Free Coffee', description: 'Get a free regular coffee', points: 100, icon: '☕' },
  { id: 2, name: 'Pastry Discount', description: '50% off any pastry', points: 50, icon: '🥐' },
  { id: 3, name: 'Free Upgrade', description: 'Get a size upgrade for free', points: 75, icon: '⬆️' },
  { id: 4, name: 'Merchandise', description: 'Exclusive Solo Coffee merchandise', points: 200, icon: '🎁' }
])

const availableCoupons = ref([
  { id: 1, title: 'Welcome Bonus', description: '20% off your first order', discount: 20, type: 'discount', expiryDate: new Date(Date.now() + 7 * 24 * 60 * 60 * 1000).toISOString(), minPurchase: 5 },
  { id: 2, title: 'Weekday Special', description: '15% off on weekdays', discount: 15, type: 'discount', expiryDate: new Date(Date.now() + 30 * 24 * 60 * 60 * 1000).toISOString(), minPurchase: 10 },
  { id: 3, title: 'Member Exclusive', description: '$5 off orders over $20', discount: 5, type: 'amount', expiryDate: new Date(Date.now() + 60 * 24 * 60 * 60 * 1000).toISOString(), minPurchase: 20 }
])

const memberBenefits = ref([
  {
    level: 1,
    name: 'Bronze Member',
    description: 'Start your journey with basic benefits',
    perks: ['5% off all drinks', 'Birthday treat', 'Priority seating']
  },
  {
    level: 2,
    name: 'Silver Member',
    description: 'Enjoy enhanced member privileges',
    perks: ['10% off all drinks', 'Free pastry with coffee', 'Early access to new items']
  },
  {
    level: 3,
    name: 'Gold Member',
    description: 'Premium experience with exclusive benefits',
    perks: ['15% off all drinks', 'Free size upgrade', 'Exclusive events access']
  }
])

// Methods
const getMemberLevelColor = (level: number) => {
  switch (level) {
    case 1: return 'bg-yellow-600'
    case 2: return 'bg-gray-400'
    case 3: return 'bg-yellow-500'
    default: return 'bg-gray-500'
  }
}

const getMemberLevelText = (level: number) => {
  switch (level) {
    case 1: return 'Bronze'
    case 2: return 'Silver'
    case 3: return 'Gold'
    default: return 'Member'
  }
}

const redeemItem = (item: any) => {
  const points = memberInfo.value?.points || 0
  if (points >= item.points) {
    alert(`Successfully redeemed: ${item.name}`)
  } else {
    alert(`Not enough points. You need ${item.points - points} more points.`)
  }
}

const useCoupon = (coupon: any) => {
  alert(`Coupon applied: ${coupon.title}`)
}

const fetchMemberData = async () => {
  loading.value = true
  error.value = null
  try {
    await authStore.fetchUserProfile()
  } catch (err: any) {
    error.value = err.message || 'Failed to load member data'
  } finally {
    loading.value = false
  }
}

const navigateBack = () => {
  router.back()
}

const navigateToPoints = () => {
  alert('Navigate to points history page')
}

const navigateToCoupons = () => {
  alert('Navigate to coupons page')
}

onMounted(() => {
  fetchMemberData()
})
</script>