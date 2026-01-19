import { defineConfig } from 'vitest/config'
import vue from '@vitejs/plugin-vue'
import { fileURLToPath } from 'url'

// https://vitest.dev/config/
export default defineConfig({
  plugins: [vue()],
  test: {
    // 测试环境配置
    environment: 'jsdom',
    // 测试文件匹配规则
    include: ['src/__tests__/**/*.test.ts', 'src/**/*.test.ts'],
    // 排除文件
    exclude: ['node_modules/**', 'dist/**'],
    // 测试覆盖率配置
    coverage: {
      provider: 'v8',
      reporter: ['text', 'json', 'html'],
      include: ['src/**/*.ts', 'src/**/*.vue'],
      exclude: ['node_modules/**', 'src/main.ts', 'src/router/**', 'src/**/*.d.ts'],
      all: true,
      thresholds: {
        lines: 80,
        functions: 80,
        branches: 80,
        statements: 80
      }
    },
    // 测试全局配置
    globals: true,
    // 模拟定时器
    mockReset: true,
    // 测试钩子
    setupFiles: ['src/__tests__/setup.ts'],
    // 测试运行超时时间
    timeout: 5000
  },
  // 路径别名配置
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  }
})