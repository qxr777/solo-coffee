
<template>
  <div class="min-h-screen flex items-center justify-center bg-gray-100">
    <div class="w-full max-w-md bg-white rounded-lg shadow-lg p-8">
      <div class="text-center mb-8">
        <h1 class="text-3xl font-bold text-gray-900">Solo Coffee</h1>
        <p class="text-gray-600 mt-2">管理后台登录</p>
      </div>

      <form @submit.prevent="handleLogin" class="space-y-6">
        <div>
          <label for="username" class="block text-sm font-medium text-gray-700 mb-1">
            用户名/手机号
          </label>
          <input
            type="text"
            id="username"
            v-model="form.username"
            class="input-field"
            placeholder="请输入用户名或手机号"
            required
          />
        </div>

        <div>
          <label for="password" class="block text-sm font-medium text-gray-700 mb-1">
            密码
          </label>
          <input
            type="password"
            id="password"
            v-model="form.password"
            class="input-field"
            placeholder="请输入密码"
            required
          />
        </div>

        <div class="flex items-center justify-between">
          <div class="flex items-center">
            <input
              id="remember-me"
              type="checkbox"
              v-model="form.rememberMe"
              class="h-4 w-4 text-blue-600 focus:ring-blue-500 border-gray-300 rounded"
            />
            <label for="remember-me" class="ml-2 block text-sm text-gray-700">
              记住我
            </label>
          </div>

          <a href="#" class="text-sm text-blue-600 hover:text-blue-500">
            忘记密码?
          </a>
        </div>

        <div>
          <button
            type="submit"
            class="w-full btn-primary flex justify-center items-center"
            :disabled="userStore.getLoading"
          >
            <span v-if="userStore.getLoading">登录中...</span>
            <span v-else>登录</span>
          </button>
        </div>
      </form>

      <div v-if="userStore.getError" class="mt-4 p-3 bg-red-100 text-red-700 rounded-lg text-sm">
        {{ userStore.getError }}
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../../stores/userStore'

const router = useRouter()
const userStore = useUserStore()

const form = ref({
  username: '',
  password: '',
  rememberMe: false
})

const handleLogin = async () => {
  try {
    await userStore.login(form.value.username, form.value.password)
    router.push('/')
  } catch (error) {
    // 错误已在store中处理
  }
}
</script>

<style scoped>
/* 登录页面样式 */
</style>
