
<template>
  <div class="space-y-6">
    <!-- 页面标题和操作 -->
    <div class="flex flex-col md:flex-row md:items-center md:justify-between gap-4">
      <div>
        <h1 class="text-2xl font-bold text-gray-900">商品管理</h1>
        <p class="text-gray-600 mt-1">管理所有商品，包括商品信息、分类和库存</p>
      </div>
      
      <div class="flex space-x-2">
        <button class="btn-primary whitespace-nowrap">
          <svg class="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6"></path>
          </svg>
          添加商品
        </button>
        
        <button class="btn-secondary whitespace-nowrap">
          <svg class="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 11H5m14 0a2 2 0 012 2v6a2 2 0 01-2 2H5a2 2 0 01-2-2v-6a2 2 0 012-2m14 0V9a2 2 0 00-2-2M5 11V9a2 2 0 012-2m0 0V5a2 2 0 012-2h6a2 2 0 012 2v2M7 7h10"></path>
          </svg>
          批量操作
        </button>
      </div>
    </div>

    <!-- 商品筛选 -->
    <div class="card">
      <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">
            商品分类
          </label>
          <select class="input-field">
            <option value="">全部分类</option>
            <option value="1">咖啡</option>
            <option value="2">茶饮</option>
            <option value="3">甜点</option>
            <option value="4">轻食</option>
          </select>
        </div>
        
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">
            商品状态
          </label>
          <select class="input-field">
            <option value="">全部状态</option>
            <option value="1">上架</option>
            <option value="0">下架</option>
          </select>
        </div>
        
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">
            搜索
          </label>
          <div class="flex space-x-2">
            <input type="text" class="input-field flex-1" placeholder="商品名称/编号">
            <button class="btn-primary whitespace-nowrap">
              搜索
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 商品统计卡片 -->
    <div class="grid grid-cols-2 md:grid-cols-4 gap-4">
      <div class="p-3 bg-gray-50 rounded-lg">
        <p class="text-sm text-gray-600">商品总数</p>
        <p class="text-xl font-bold text-gray-900 mt-1">128</p>
      </div>
      
      <div class="p-3 bg-green-50 rounded-lg border border-green-100">
        <p class="text-sm text-green-700">上架商品</p>
        <p class="text-xl font-bold text-gray-900 mt-1">112</p>
      </div>
      
      <div class="p-3 bg-red-50 rounded-lg border border-red-100">
        <p class="text-sm text-red-700">下架商品</p>
        <p class="text-xl font-bold text-gray-900 mt-1">16</p>
      </div>
      
      <div class="p-3 bg-yellow-50 rounded-lg border border-yellow-100">
        <p class="text-sm text-yellow-700">库存预警</p>
        <p class="text-xl font-bold text-gray-900 mt-1">8</p>
      </div>
    </div>

    <!-- 商品列表 -->
    <div class="card">
      <div class="overflow-x-auto">
        <table class="min-w-full divide-y divide-gray-200">
          <thead class="bg-gray-50">
            <tr>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                <input type="checkbox" class="h-4 w-4 text-blue-600 focus:ring-blue-500 border-gray-300 rounded">
              </th>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                商品图片
              </th>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                商品信息
              </th>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                价格
              </th>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                库存
              </th>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                状态
              </th>
              <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                操作
              </th>
            </tr>
          </thead>
          <tbody class="bg-white divide-y divide-gray-200">
            <tr v-for="product in products" :key="product.id" class="hover:bg-gray-50 transition-colors">
              <td class="px-6 py-4 whitespace-nowrap">
                <input type="checkbox" class="h-4 w-4 text-blue-600 focus:ring-blue-500 border-gray-300 rounded">
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="w-12 h-12 bg-gray-200 rounded-lg flex items-center justify-center">
                  <svg class="w-6 h-6 text-gray-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M18,8A2,2 0 0,1 20,10V20A2,2 0 0,1 18,22H6A2,2 0 0,1 4,20V10C4,8.89 4.9,8 6,8H18M15,3V5H6C4.89,5 4,5.9 4,7V19A1,1 0 0,0 5,20H19A1,1 0 0,0 20,19V7C20,5.89 19.1,5 18,5H13V3M15,15H9V17H15V15Z"></path>
                  </svg>
                </div>
              </td>
              <td class="px-6 py-4">
                <div class="font-medium text-gray-900">{{ product.name }}</div>
                <div class="text-sm text-gray-500 mt-1">{{ product.productNo }}</div>
                <div class="text-sm text-gray-600 mt-1">{{ product.categoryName }}</div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="font-medium text-gray-900">¥{{ product.price.toFixed(2) }}</div>
                <div class="text-sm text-gray-500 mt-1">成本: ¥{{ product.costPrice.toFixed(2) }}</div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <div :class="getStockClass(product.stock)">
                  {{ product.stock }} {{ product.unit }}
                </div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <span :class="getStatusClass(product.status)">
                  {{ product.status === 1 ? '上架' : '下架' }}
                </span>
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">
                <div class="flex space-x-2">
                  <router-link :to="`/products/${product.id}`" class="text-blue-600 hover:text-blue-500">
                    编辑
                  </router-link>
                  <button v-if="product.status === 1" class="text-red-600 hover:text-red-500">
                    下架
                  </button>
                  <button v-else class="text-green-600 hover:text-green-500">
                    上架
                  </button>
                  <button class="text-gray-600 hover:text-gray-500">
                    库存
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
      
      <!-- 分页 -->
      <div class="flex items-center justify-between px-6 py-4 border-t border-gray-200 sm:px-6">
        <div class="flex-1 flex justify-between sm:hidden">
          <a href="#" class="relative inline-flex items-center px-4 py-2 border border-gray-300 text-sm font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50">
            上一页
          </a>
          <a href="#" class="ml-3 relative inline-flex items-center px-4 py-2 border border-gray-300 text-sm font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50">
            下一页
          </a>
        </div>
        <div class="hidden sm:flex-1 sm:flex sm:items-center sm:justify-between">
          <div>
            <p class="text-sm text-gray-700">
              显示第 <span class="font-medium">1</span> 到 <span class="font-medium">10</span> 条，共 <span class="font-medium">128</span> 条记录
            </p>
          </div>
          <div>
            <nav class="relative z-0 inline-flex rounded-md shadow-sm -space-x-px" aria-label="Pagination">
              <a href="#" class="relative inline-flex items-center px-2 py-2 rounded-l-md border border-gray-300 bg-white text-sm font-medium text-gray-500 hover:bg-gray-50">
                <span class="sr-only">上一页</span>
                <svg class="h-5 w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7"></path>
                </svg>
              </a>
              <a href="#" class="relative inline-flex items-center px-4 py-2 border border-gray-300 bg-blue-50 text-sm font-medium text-blue-600">
                1
              </a>
              <a href="#" class="relative inline-flex items-center px-4 py-2 border border-gray-300 bg-white text-sm font-medium text-gray-700 hover:bg-gray-50">
                2
              </a>
              <a href="#" class="relative inline-flex items-center px-4 py-2 border border-gray-300 bg-white text-sm font-medium text-gray-700 hover:bg-gray-50">
                3
              </a>
              <span class="relative inline-flex items-center px-4 py-2 border border-gray-300 bg-white text-sm font-medium text-gray-700">
                ...
              </span>
              <a href="#" class="relative inline-flex items-center px-4 py-2 border border-gray-300 bg-white text-sm font-medium text-gray-700 hover:bg-gray-50">
                13
              </a>
              <a href="#" class="relative inline-flex items-center px-2 py-2 rounded-r-md border border-gray-300 bg-white text-sm font-medium text-gray-500 hover:bg-gray-50">
                <span class="sr-only">下一页</span>
                <svg class="h-5 w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7"></path>
                </svg>
              </a>
            </nav>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'

// 模拟商品数据
const products = ref([
  {
    id: 1,
    productNo: 'PD202601010001',
    name: '美式咖啡',
    categoryName: '咖啡',
    price: 32.00,
    costPrice: 12.00,
    stock: 128,
    unit: '杯',
    status: 1
  },
  {
    id: 2,
    productNo: 'PD202601010002',
    name: '拿铁咖啡',
    categoryName: '咖啡',
    price: 36.00,
    costPrice: 15.00,
    stock: 96,
    unit: '杯',
    status: 1
  },
  {
    id: 3,
    productNo: 'PD202601010003',
    name: '卡布奇诺',
    categoryName: '咖啡',
    price: 36.00,
    costPrice: 15.00,
    stock: 72,
    unit: '杯',
    status: 1
  },
  {
    id: 4,
    productNo: 'PD202601010004',
    name: '摩卡咖啡',
    categoryName: '咖啡',
    price: 40.00,
    costPrice: 18.00,
    stock: 45,
    unit: '杯',
    status: 1
  },
  {
    id: 5,
    productNo: 'PD202601010005',
    name: '焦糖玛奇朵',
    categoryName: '咖啡',
    price: 42.00,
    costPrice: 20.00,
    stock: 15,
    unit: '杯',
    status: 1
  },
  {
    id: 6,
    productNo: 'PD202601010006',
    name: '抹茶拿铁',
    categoryName: '茶饮',
    price: 38.00,
    costPrice: 16.00,
    stock: 8,
    unit: '杯',
    status: 1
  },
  {
    id: 7,
    productNo: 'PD202601010007',
    name: '牛角包',
    categoryName: '甜点',
    price: 12.00,
    costPrice: 5.00,
    stock: 3,
    unit: '个',
    status: 1
  },
  {
    id: 8,
    productNo: 'PD202601010008',
    name: '芝士蛋糕',
    categoryName: '甜点',
    price: 28.00,
    costPrice: 12.00,
    stock: 0,
    unit: '块',
    status: 0
  }
])

// 获取库存样式
const getStockClass = (stock: number): string => {
  if (stock === 0) {
    return 'text-red-600 font-medium'
  } else if (stock < 10) {
    return 'text-yellow-600 font-medium'
  } else {
    return 'text-gray-900'
  }
}

// 获取状态样式
const getStatusClass = (status: number): string => {
  return status === 1 
    ? 'inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-green-100 text-green-800'
    : 'inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-gray-100 text-gray-800'
}
</script>

<style scoped>
/* 商品列表页面样式 */
</style>
