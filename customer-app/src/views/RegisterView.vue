<template>
  <div class="min-h-screen bg-gray-50 dark:bg-gray-900 flex items-center justify-center">
    <div class="bg-white dark:bg-gray-800 rounded-xl p-8 shadow-lg w-full max-w-md">
      <!-- Logo and Title -->
      <div class="text-center mb-8">
        <div class="w-16 h-16 bg-primary/10 rounded-full flex items-center justify-center mx-auto mb-4">
          <span class="text-3xl">👋</span>
        </div>
        <h1 class="text-2xl font-bold text-gray-900 dark:text-white">创建账户</h1>
        <p class="text-gray-600 dark:text-gray-400">加入 Solo Coffee，开始赚取积分奖励</p>
      </div>

      <!-- Registration Form -->
      <form @submit.prevent="handleRegister">
        <div class="space-y-4 mb-6">
          <div>
            <label for="name" class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">姓名</label>
            <input 
              type="text" 
              id="name"
              v-model="name"
              placeholder="请输入您的姓名"
              class="w-full px-4 py-2 border border-gray-300 dark:border-gray-700 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary dark:bg-gray-700 dark:text-white"
              required
            >
          </div>
          
          <div>
            <label for="phone" class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">手机号</label>
            <input 
              type="text" 
              id="phone"
              v-model="phone"
              placeholder="请输入您的手机号"
              class="w-full px-4 py-2 border border-gray-300 dark:border-gray-700 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary dark:bg-gray-700 dark:text-white"
              required
            >
          </div>
          
          <div>
            <label for="email" class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">邮箱地址</label>
            <input 
              type="email" 
              id="email"
              v-model="email"
              placeholder="请输入您的邮箱地址"
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
              placeholder="请设置密码"
              class="w-full px-4 py-2 border border-gray-300 dark:border-gray-700 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary dark:bg-gray-700 dark:text-white"
              required
              minlength="6"
            >
          </div>
          
          <div>
            <label for="confirmPassword" class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">确认密码</label>
            <input 
              type="password" 
              id="confirmPassword"
              v-model="confirmPassword"
              placeholder="请确认密码"
              class="w-full px-4 py-2 border border-gray-300 dark:border-gray-700 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary dark:bg-gray-700 dark:text-white"
              required
              minlength="6"
            >
          </div>
          
          <div class="flex items-center">
            <input 
              type="checkbox" 
              id="terms"
              v-model="agreeToTerms"
              class="w-4 h-4 text-primary border-gray-300 rounded focus:ring-primary dark:bg-gray-700 dark:border-gray-600 dark:focus:ring-primary dark:ring-offset-gray-800"
              required
            >
            <label for="terms" class="ml-2 block text-sm text-gray-700 dark:text-gray-300">
              我同意 <a href="#" class="text-primary hover:text-primary/80">服务条款</a> 和 <a href="#" class="text-primary hover:text-primary/80">隐私政策</a>
            </label>
          </div>
        </div>
        
        <button 
          type="submit"
          :disabled="authStore.loading || password !== confirmPassword"
          class="w-full bg-primary text-white px-4 py-3 rounded-lg font-semibold hover:bg-primary/90 disabled:bg-primary/60 disabled:cursor-not-allowed transition flex items-center justify-center"
        >
          <div v-if="authStore.loading" class="animate-spin rounded-full h-5 w-5 border-b-2 border-white mr-2"></div>
          创建账户
        </button>
      </form>

      <!-- Error Message -->
      <div v-if="authStore.error" class="mt-4 bg-red-50 dark:bg-red-900/20 border border-red-200 dark:border-red-800 rounded-lg p-3">
        <p class="text-red-600 dark:text-red-400 text-sm">{{ authStore.error }}</p>
      </div>

      <!-- Login Link -->
      <div class="mt-6 text-center">
        <p class="text-gray-600 dark:text-gray-400">
          已有账户？ 
          <a @click="navigateToLogin" class="text-primary hover:text-primary/80 font-medium cursor-pointer">立即登录</a>
        </p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../store/authStore'

const router = useRouter()
const authStore = useAuthStore()

// Form data
const name = ref('')
const phone = ref('')
const email = ref('')
const password = ref('')
const confirmPassword = ref('')
const agreeToTerms = ref(false)

// Methods
const handleRegister = async () => {
  if (password.value !== confirmPassword.value) {
    authStore.error = '两次输入的密码不一致'
    return
  }
  
  try {
    await authStore.register(name.value, phone.value, email.value, password.value)
    router.push('/')
  } catch (err) {
    console.error('注册失败:', err)
  }
}

const navigateToLogin = () => {
  router.push('/login')
}
</script>