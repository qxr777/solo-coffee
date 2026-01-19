// 全局测试设置文件
import { vi } from 'vitest'

// Mock localStorage for all tests
const localStorageMock = (() => {
  let store: Record<string, string> = {}
  return {
    getItem: (key: string) => store[key] || null,
    setItem: (key: string, value: string) => {
      store[key] = value.toString()
    },
    clear: () => {
      store = {}
    },
    removeItem: (key: string) => {
      delete store[key]
    }
  }
})()

global.localStorage = localStorageMock as any

// Mock console.error to avoid cluttering test output
vi.spyOn(console, 'error').mockImplementation(() => {})

// Mock console.warn to avoid cluttering test output
vi.spyOn(console, 'warn').mockImplementation(() => {})

// Setup Vue Test Utils if needed
// import { config } from '@vue/test-utils'
// config.global.mocks = {
//   $t: (msg: string) => msg
// }