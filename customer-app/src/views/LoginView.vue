<template>
  <div class="min-h-screen bg-gray-50 dark:bg-gray-900 flex items-center justify-center">
    <div class="bg-white dark:bg-gray-800 rounded-xl p-8 shadow-lg w-full max-w-md">
      <!-- Logo and Title -->
      <div class="text-center mb-8">
        <div class="w-16 h-16 bg-primary rounded-full flex items-center justify-center mx-auto mb-4">
          <span class="text-white text-2xl font-bold">S</span>
        </div>
        <h1 class="text-2xl font-bold text-gray-900 dark:text-white">欢迎回来</h1>
        <p class="text-gray-600 dark:text-gray-400">登录您的 Solo Coffee 账户</p>
      </div>

      <!-- Login Tabs -->
      <div class="flex mb-6 border-b border-gray-200 dark:border-gray-700">
        <button 
          @click="activeTab = 'phone'"
          :class="['flex-1 py-2 text-center font-medium transition', activeTab === 'phone' ? 'border-b-2 border-primary text-primary' : 'text-gray-500 hover:text-gray-700 dark:text-gray-400 dark:hover:text-gray-300']"
        >
          手机号登录
        </button>
        <button 
          @click="activeTab = 'sms'"
          :class="['flex-1 py-2 text-center font-medium transition', activeTab === 'sms' ? 'border-b-2 border-primary text-primary' : 'text-gray-500 hover:text-gray-700 dark:text-gray-400 dark:hover:text-gray-300']"
        >
          验证码登录
        </button>
      </div>

      <!-- Phone Login Form -->
      <form v-if="activeTab === 'phone'" @submit.prevent="handlePhoneLogin">
        <div class="space-y-4 mb-6">
          <div>
            <label for="phone" class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">手机号</label>
            <input 
              type="text" 
              id="phone"
              v-model="phone"
              placeholder="请输入手机号"
              class="w-full px-4 py-2 border border-gray-300 dark:border-gray-700 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary dark:bg-gray-700 dark:text-white"
              required
            >
          </div>
          
          <div>
            <label for="password" class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">密码</label>
            <input 
              type="password" 
              id="password"
              v-model="password"
              placeholder="请输入密码"
              class="w-full px-4 py-2 border border-gray-300 dark:border-gray-700 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary dark:bg-gray-700 dark:text-white"
              required
            >
          </div>
          
          <div class="flex justify-between items-center">
            <div class="flex items-center">
              <input 
                type="checkbox" 
                id="remember"
                v-model="rememberMe"
                class="w-4 h-4 text-primary border-gray-300 rounded focus:ring-primary dark:bg-gray-700 dark:border-gray-600 dark:focus:ring-primary dark:ring-offset-gray-800"
              >
              <label for="remember" class="ml-2 block text-sm text-gray-700 dark:text-gray-300">记住我</label>
            </div>
            <a @click="navigateToForgotPassword" class="text-sm text-primary hover:text-primary/80 cursor-pointer">忘记密码？</a>
          </div>
        </div>
        
        <button 
          type="submit"
          :disabled="authStore.loading"
          class="w-full bg-primary text-white px-4 py-3 rounded-lg font-semibold hover:bg-primary/90 disabled:bg-primary/60 disabled:cursor-not-allowed transition flex items-center justify-center"
        >
          <div v-if="authStore.loading" class="animate-spin rounded-full h-5 w-5 border-b-2 border-white mr-2"></div>
          登录
        </button>
      </form>

      <!-- SMS Login Form -->
      <form v-else @submit.prevent="handleSmsLogin">
        <div class="space-y-4 mb-6">
          <div>
            <label for="sms-phone" class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">手机号</label>
            <div class="flex space-x-2">
              <input 
                type="text" 
                id="sms-phone"
                v-model="smsPhone"
                placeholder="请输入手机号"
                class="flex-1 px-4 py-2 border border-gray-300 dark:border-gray-700 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary dark:bg-gray-700 dark:text-white"
                required
              >
              <button 
                type="button"
                @click="sendSmsCode"
                :disabled="isSendingSms"
                class="px-4 py-2 bg-secondary text-white rounded-lg font-medium hover:bg-secondary/90 disabled:bg-secondary/60 disabled:cursor-not-allowed whitespace-nowrap"
              >
                {{ isSendingSms ? `${countdown}s` : '获取验证码' }}
              </button>
            </div>
          </div>
          
          <div>
            <label for="verification-code" class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">验证码</label>
            <input 
              type="text" 
              id="verification-code"
              v-model="verificationCode"
              placeholder="请输入验证码"
              class="w-full px-4 py-2 border border-gray-300 dark:border-gray-700 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary dark:bg-gray-700 dark:text-white"
              required
            >
          </div>
        </div>
        
        <button 
          type="submit"
          :disabled="authStore.loading"
          class="w-full bg-primary text-white px-4 py-3 rounded-lg font-semibold hover:bg-primary/90 disabled:bg-primary/60 disabled:cursor-not-allowed transition flex items-center justify-center"
        >
          <div v-if="authStore.loading" class="animate-spin rounded-full h-5 w-5 border-b-2 border-white mr-2"></div>
          验证码登录
        </button>
      </form>

      <!-- Error Message -->
      <div v-if="authStore.error" class="mt-4 bg-red-50 dark:bg-red-900/20 border border-red-200 dark:border-red-800 rounded-lg p-3">
        <p class="text-red-600 dark:text-red-400 text-sm">{{ authStore.error }}</p>
      </div>

      <!-- Third Party Login -->
      <div class="mt-8">
        <div class="relative">
          <div class="absolute inset-0 flex items-center">
            <div class="w-full border-t border-gray-300 dark:border-gray-700"></div>
          </div>
          <div class="relative flex justify-center text-sm">
            <span class="px-2 bg-white dark:bg-gray-800 text-gray-500 dark:text-gray-400">其他登录方式</span>
          </div>
        </div>
        
        <div class="mt-4 flex justify-center space-x-6">
          <button @click="handleWechatLogin" class="flex items-center justify-center w-12 h-12 rounded-full border border-gray-300 dark:border-gray-700 bg-white dark:bg-gray-800 text-gray-600 dark:text-gray-400 hover:bg-gray-50 dark:hover:bg-gray-700 transition">
            <svg class="w-6 h-6" fill="currentColor" viewBox="0 0 24 24">
              <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm0 18c-4.41 0-8-3.59-8-8s3.59-8 8-8 8 3.59 8 8-3.59 8-8 8zm-1.5-8.5c0-.83-.67-1.5-1.5-1.5s-1.5.67-1.5 1.5.67 1.5 1.5 1.5 1.5-.67 1.5-1.5zm3 0c0-.83-.67-1.5-1.5-1.5s-1.5.67-1.5 1.5.67 1.5 1.5 1.5 1.5-.67 1.5-1.5z"/>
            </svg>
          </button>
          <button @click="handleAppleLogin" class="flex items-center justify-center w-12 h-12 rounded-full border border-gray-300 dark:border-gray-700 bg-white dark:bg-gray-800 text-gray-600 dark:text-gray-400 hover:bg-gray-50 dark:hover:bg-gray-700 transition">
            <svg class="w-6 h-6" fill="currentColor" viewBox="0 0 24 24">
              <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm0 18c-4.41 0-8-3.59-8-8s3.59-8 8-8 8 3.59 8 8-3.59 8-8 8zm-1.5-8.5c0-.83-.67-1.5-1.5-1.5s-1.5.67-1.5 1.5.67 1.5 1.5 1.5 1.5-.67 1.5-1.5zm3 0c0-.83-.67-1.5-1.5-1.5s-1.5.67-1.5 1.5.67 1.5 1.5 1.5 1.5-.67 1.5-1.5z"/>
            </svg>
          </button>
        </div>
      </div>

      <!-- Register Link -->
      <div class="mt-6 text-center">
        <p class="text-gray-600 dark:text-gray-400">
          还没有账户？ 
          <a @click="navigateToRegister" class="text-primary hover:text-primary/80 font-medium cursor-pointer">立即注册</a>
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

// Tabs
const activeTab = ref('phone')

// Phone login
const phone = ref('')
const password = ref('')
const rememberMe = ref(false)

// SMS login
const smsPhone = ref('')
const verificationCode = ref('')
const isSendingSms = ref(false)
const countdown = ref(60)
let countdownTimer: number | null = null

// Methods
const handlePhoneLogin = async () => {
  try {
    await authStore.login(phone.value, password.value)
    router.push('/')
  } catch (err) {
    console.error('登录失败:', err)
  }
}

const handleSmsLogin = async () => {
  try {
    await authStore.smsLogin(smsPhone.value, verificationCode.value)
    router.push('/')
  } catch (err) {
    console.error('验证码登录失败:', err)
  }
}

const sendSmsCode = async () => {
  if (isSendingSms.value) return
  
  try {
    isSendingSms.value = true
    await authStore.sendSmsCode(smsPhone.value, 1) // 1: 登录验证码
    
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

const handleWechatLogin = () => {
  // 微信登录逻辑
  console.log('微信登录')
  // 这里需要集成微信登录SDK
}

const handleAppleLogin = () => {
  // Apple ID登录逻辑
  console.log('Apple ID登录')
  // 这里需要集成Apple登录SDK
}

const navigateToRegister = () => {
  router.push('/register')
}

const navigateToForgotPassword = () => {
  router.push('/forgot-password')
}

// Cleanup
onUnmounted(() => {
  if (countdownTimer) {
    clearInterval(countdownTimer)
  }
})
</script>