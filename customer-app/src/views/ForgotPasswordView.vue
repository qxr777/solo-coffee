<template>
  <div class="min-h-screen bg-gray-50 dark:bg-gray-900 flex items-center justify-center">
    <div class="bg-white dark:bg-gray-800 rounded-xl p-8 shadow-lg w-full max-w-md">
      <!-- Logo and Title -->
      <div class="text-center mb-8">
        <div class="w-16 h-16 bg-primary/10 rounded-full flex items-center justify-center mx-auto mb-4">
          <span class="text-2xl font-bold text-primary">🔐</span>
        </div>
        <h1 class="text-2xl font-bold text-gray-900 dark:text-white">重置密码</h1>
        <p class="text-gray-600 dark:text-gray-400">通过手机号验证码重置您的密码</p>
      </div>

      <!-- Forgot Password Form -->
      <form @submit.prevent="handleForgotPassword">
        <div class="space-y-4 mb-6">
          <div>
            <label for="phone" class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">手机号</label>
            <div class="flex space-x-2">
              <input 
                type="text" 
                id="phone"
                v-model="phone"
                placeholder="请输入您的手机号"
                class="flex-1 px-4 py-2 border border-gray-300 dark:border-gray-700 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary dark:bg-gray-700 dark:text-white"
                required
              >
              <button 
                type="button"
                @click="sendVerificationCode"
                :disabled="isSendingSms"
                class="px-4 py-2 bg-secondary text-white rounded-lg font-medium hover:bg-secondary/90 disabled:bg-secondary/60 disabled:cursor-not-allowed whitespace-nowrap"
              >
                {{ isSendingSms ? `${countdown}s` : '获取验证码' }}
              </button>
            </div>
          </div>
          
          <div>
            <label for="verificationCode" class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">验证码</label>
            <input 
              type="text" 
              id="verificationCode"
              v-model="verificationCode"
              placeholder="请输入验证码"
              class="w-full px-4 py-2 border border-gray-300 dark:border-gray-700 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary dark:bg-gray-700 dark:text-white"
              required
            >
          </div>
          
          <div>
            <label for="newPassword" class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">新密码</label>
            <input 
              type="password" 
              id="newPassword"
              v-model="newPassword"
              placeholder="请设置新密码"
              class="w-full px-4 py-2 border border-gray-300 dark:border-gray-700 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary dark:bg-gray-700 dark:text-white"
              required
              minlength="6"
            >
          </div>
          
          <div>
            <label for="confirmPassword" class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">确认新密码</label>
            <input 
              type="password" 
              id="confirmPassword"
              v-model="confirmPassword"
              placeholder="请确认新密码"
              class="w-full px-4 py-2 border border-gray-300 dark:border-gray-700 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary dark:bg-gray-700 dark:text-white"
              required
              minlength="6"
            >
          </div>
        </div>
        
        <button 
          type="submit"
          :disabled="authStore.loading || newPassword !== confirmPassword"
          class="w-full bg-primary text-white px-4 py-3 rounded-lg font-semibold hover:bg-primary/90 disabled:bg-primary/60 disabled:cursor-not-allowed transition flex items-center justify-center"
        >
          <div v-if="authStore.loading" class="animate-spin rounded-full h-5 w-5 border-b-2 border-white mr-2"></div>
          重置密码
        </button>
      </form>

      <!-- Error Message -->
      <div v-if="authStore.error" class="mt-4 bg-red-50 dark:bg-red-900/20 border border-red-200 dark:border-red-800 rounded-lg p-3">
        <p class="text-red-600 dark:text-red-400 text-sm">{{ authStore.error }}</p>
      </div>

      <!-- Success Message -->
      <div v-if="success" class="mt-4 bg-green-50 dark:bg-green-900/20 border border-green-200 dark:border-green-800 rounded-lg p-3">
        <p class="text-green-600 dark:text-green-400 text-sm">密码重置成功！</p>
      </div>

      <!-- Login Link -->
      <div class="mt-6 text-center">
        <p class="text-gray-600 dark:text-gray-400">
          想起密码了？ 
          <a @click="navigateToLogin" class="text-primary hover:text-primary/80 font-medium cursor-pointer">立即登录</a>
        </p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../store/authStore'

const router = useRouter()
const authStore = useAuthStore()

// Form data
const phone = ref('')
const verificationCode = ref('')
const newPassword = ref('')
const confirmPassword = ref('')
const isSendingSms = ref(false)
const countdown = ref(60)
const success = ref(false)
let countdownTimer: number | null = null

// Methods
const sendVerificationCode = async () => {
  if (isSendingSms.value) return
  
  try {
    isSendingSms.value = true
    await authStore.sendSmsCode(phone.value, 2) // 2: 重置密码验证码
    
    // Start countdown
    countdown.value = 60
    countdownTimer = window.setInterval(() => {
      countdown.value--
      if (countdown.value <= 0) {
        isSendingSms.value = false
        if (countdownTimer) clearInterval(countdownTimer)
      }
    }, 1000)
  } catch (err) {
    isSendingSms.value = false
    console.error('发送验证码失败:', err)
  }
}

const handleForgotPassword = async () => {
  if (newPassword.value !== confirmPassword.value) {
    authStore.error = '两次输入的密码不一致'
    return
  }
  
  try {
    await authStore.forgotPassword(phone.value, verificationCode.value, newPassword.value)
    success.value = true
    authStore.error = null
    
    // Redirect to login after 2 seconds
    setTimeout(() => {
      router.push('/login')
    }, 2000)
  } catch (err) {
    console.error('密码重置失败:', err)
  }
}

const navigateToLogin = () => {
  router.push('/login')
}

// Cleanup
onUnmounted(() => {
  if (countdownTimer) {
    clearInterval(countdownTimer)
  }
})
</script>
