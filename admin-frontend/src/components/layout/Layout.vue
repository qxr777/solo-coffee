
<template>
  <div class="flex h-screen bg-gray-50 overflow-hidden">
    <!-- 左侧菜单 -->
    <Sidebar />
    
    <!-- 主内容区域 -->
    <div class="flex-1 flex flex-col overflow-hidden">
      <!-- 顶部导航 -->
      <TopNav />
      
      <!-- 内容区域 -->
      <main class="flex-1 overflow-y-auto p-6 bg-gray-50">
        <div class="max-w-7xl mx-auto">
          <!-- 页面标题 -->
          <div class="mb-6">
            <h1 class="text-2xl font-semibold text-gray-900">
              {{ route.meta.title || '管理后台' }}
            </h1>
          </div>
          
          <!-- 路由视图 -->
          <RouterView />
        </div>
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import Sidebar from './Sidebar.vue'
import TopNav from './TopNav.vue'

const route = useRoute()
</script>

<style scoped>
/* 布局样式 */
</style>
