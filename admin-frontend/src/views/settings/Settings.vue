<template>
  <div class="space-y-6">
    <!-- 页面标题 -->
    <div>
      <h1 class="text-2xl font-bold text-gray-900">系统设置</h1>
      <p class="text-gray-600 mt-1">配置系统基础参数、通知偏好和安全选项</p>
    </div>

    <!-- 设置分组 -->
    <div class="grid grid-cols-1 gap-6">
      <div class="card">
        <h3 class="text-lg font-medium text-gray-900 mb-4 border-b pb-2">基础设置</h3>
        <div class="space-y-4">
          <div class="flex items-center justify-between">
            <div>
              <p class="font-medium">系统名称</p>
              <p class="text-sm text-gray-500">显示在侧边栏和浏览器标题栏的名称</p>
            </div>
            <input type="text" class="input-field max-w-xs" value="Solo Coffee Admin">
          </div>
          <div class="flex items-center justify-between border-t pt-4">
            <div>
              <p class="font-medium">营业状态控制</p>
              <p class="text-sm text-gray-500">全局开启或关闭线上点餐功能</p>
            </div>
            <button class="relative inline-flex h-6 w-11 flex-shrink-0 cursor-pointer rounded-full border-2 border-transparent transition-colors duration-200 ease-in-out focus:outline-none focus:ring-2 focus:ring-blue-600 focus:ring-offset-2 bg-blue-600">
              <span class="translate-x-5 pointer-events-none inline-block h-5 w-5 transform rounded-full bg-white shadow ring-0 transition duration-200 ease-in-out"></span>
            </button>
          </div>
        </div>
      </div>

      <div class="card">
        <h3 class="text-lg font-medium text-gray-900 mb-4 border-b pb-2">通知设置</h3>
        <div class="space-y-4">
          <div class="flex items-center justify-between">
            <div>
              <p class="font-medium">库存预警通知</p>
              <p class="text-sm text-gray-500">当库存低于阈值时发送管理员通知</p>
            </div>
            <button class="relative inline-flex h-6 w-11 flex-shrink-0 cursor-pointer rounded-full border-2 border-transparent transition-colors duration-200 ease-in-out focus:outline-none focus:ring-2 focus:ring-blue-600 focus:ring-offset-2 bg-gray-200">
              <span class="translate-x-0 pointer-events-none inline-block h-5 w-5 transform rounded-full bg-white shadow ring-0 transition duration-200 ease-in-out"></span>
            </button>
          </div>
        </div>
      </div>
    </div>

    <div class="flex justify-end">
      <button class="btn-primary">保存所有更改</button>
    </div>
  </div>
</template>

<script setup lang="ts">
// 这里可以添加设置相关的逻辑
</script>

<style scoped>
.btn-primary {
  @apply px-6 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 transition-colors shadow-sm;
}

.input-field {
  @apply px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent;
}

.card {
  @apply bg-white p-6 rounded-lg shadow-sm border border-gray-100;
}
</style>
