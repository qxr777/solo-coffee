import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'
import { useAuthStore } from './store/authStore'

import './style.css'

const app = createApp(App)

app.use(createPinia())
app.use(router)

app.mount('#app')

// 处理OAuth回调
const handleOAuthCallback = async () => {
  const callbackData = localStorage.getItem('oauth_callback')
  if (callbackData) {
    try {
      const { provider, code } = JSON.parse(callbackData)
      localStorage.removeItem('oauth_callback')
      
      const authStore = useAuthStore()
      await authStore.oauthLogin(provider, code)
      
      // 登录成功后跳转到首页
      router.push('/')
    } catch (error) {
      console.error('OAuth登录失败:', error)
    }
  }
}

// 监听消息事件处理OAuth回调
window.addEventListener('message', (event) => {
  if (event.data && event.data.type === 'OAUTH_CODE') {
    const { provider, code } = event.data
    const authStore = useAuthStore()
    authStore.oauthLogin(provider, code)
      .then(() => {
        router.push('/')
      })
      .catch(error => {
        console.error('OAuth登录失败:', error)
      })
  }
})

// 初始化时处理可能的OAuth回调
handleOAuthCallback()