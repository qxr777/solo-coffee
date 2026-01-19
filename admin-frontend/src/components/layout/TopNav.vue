
<template>
  <div class="bg-white border-b border-gray-200 h-16 flex items-center justify-between px-6">
    <!-- 左侧：页面标题 -->
    <div class="flex items-center">
      <h2 class="text-lg font-medium text-gray-900">
        {{ route.meta.title || '管理后台' }}
      </h2>
    </div>

    <!-- 右侧：用户信息和操作 -->
    <div class="flex items-center space-x-4">
      <!-- 通知图标 -->
      <button class="relative p-2 text-gray-500 hover:text-gray-700 hover:bg-gray-100 rounded-full transition-colors">
        <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9"></path>
        </svg>
        <span class="absolute top-1 right-1 w-2 h-2 bg-red-500 rounded-full"></span>
      </button>

      <!-- 用户菜单 -->
      <div class="relative">
        <button 
          @click="userMenuOpen = !userMenuOpen"
          class="flex items-center space-x-2 p-1 text-gray-700 hover:bg-gray-100 rounded-lg transition-colors"
        >
          <div class="w-8 h-8 bg-blue-100 rounded-full flex items-center justify-center text-blue-600 font-medium">
            {{ userStore.getUser?.name?.charAt(0) || '管' }}
          </div>
          <span class="text-sm font-medium">{{ userStore.getUser?.name || '管理员' }}</span>
          <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7"></path>
          </svg>
        </button>

        <!-- 用户下拉菜单 -->
        <div 
          v-if="userMenuOpen"
          class="absolute right-0 mt-2 w-48 bg-white rounded-lg shadow-lg py-1 z-10"
        >
          <a 
            href="#" 
            class="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100"
          >
            个人资料
          </a>
          <a 
            href="#" 
            class="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100"
          >
            账户设置
          </a>
          <div class="border-t border-gray-100"></div>
          <button 
            @click="handleLogout"
            class="block w-full text-left px-4 py-2 text-sm text-red-700 hover:bg-gray-100"
          >
            退出登录
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '../../stores/userStore'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const userMenuOpen = ref(false)

const handleLogout = async () => {
  try {
    await userStore.logout()
    router.push('/login')
  } catch (error) {
    console.error('退出登录失败:', error)
  }
}
</script>

<style scoped>
/* 顶部导航样式 */
</style>
