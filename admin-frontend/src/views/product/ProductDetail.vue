<template>
  <div class="space-y-6">
    <!-- 页面标题和操作 -->
    <div class="flex flex-col md:flex-row md:items-center md:justify-between gap-4">
      <div>
        <h1 class="text-2xl font-bold text-gray-900">商品详情</h1>
        <p class="text-gray-600 mt-1">查看和编辑商品信息</p>
      </div>
      
      <div class="flex space-x-2">
        <button @click="goBack" class="btn-secondary whitespace-nowrap">
          <svg class="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7"></path>
          </svg>
          返回列表
        </button>
        
        <button v-if="product.status === 1" @click="updateProductStatus(0)" class="btn-danger whitespace-nowrap">
          <svg class="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"></path>
          </svg>
          下架
        </button>
        <button v-else @click="updateProductStatus(1)" class="btn-success whitespace-nowrap">
          <svg class="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"></path>
          </svg>
          上架
        </button>
      </div>
    </div>
    
    <!-- 商品信息卡片 -->
    <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
      <!-- 左侧：商品基本信息 -->
      <div class="lg:col-span-2 space-y-6">
        <!-- 基本信息卡片 -->
        <div class="card">
          <h2 class="text-lg font-semibold text-gray-900 mb-4">基本信息</h2>
          <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">
                商品编号
              </label>
              <input v-model="product.productNo" type="text" class="input-field" readonly>
            </div>
            
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">
                商品名称 <span class="text-red-600">*</span>
              </label>
              <input v-model="product.name" type="text" class="input-field">
            </div>
            
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">
                商品分类 <span class="text-red-600">*</span>
              </label>
              <select v-model="product.categoryId" class="input-field">
                <option value="">请选择分类</option>
                <option value="1">咖啡</option>
                <option value="2">茶饮</option>
                <option value="3">甜点</option>
                <option value="4">轻食</option>
              </select>
            </div>
            
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">
                商品价格 <span class="text-red-600">*</span>
              </label>
              <input v-model="product.price" type="number" step="0.01" min="0" class="input-field">
            </div>
            
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">
                成本价格
              </label>
              <input v-model="product.costPrice" type="number" step="0.01" min="0" class="input-field">
            </div>
            
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">
                商品状态
              </label>
              <select v-model="product.status" class="input-field">
                <option value="1">上架</option>
                <option value="0">下架</option>
              </select>
            </div>
          </div>
        </div>
        
        <!-- 商品描述卡片 -->
        <div class="card">
          <h2 class="text-lg font-semibold text-gray-900 mb-4">商品描述</h2>
          <textarea v-model="product.description" class="input-field" rows="4" placeholder="请输入商品描述"></textarea>
        </div>
      </div>
      
      <!-- 右侧：商品图片和库存信息 -->
      <div class="space-y-6">
        <!-- 商品图片卡片 -->
        <div class="card">
          <h2 class="text-lg font-semibold text-gray-900 mb-4">商品图片</h2>
          <div class="space-y-4">
            <div class="w-full aspect-square bg-gray-200 rounded-lg flex items-center justify-center relative">
              <img v-if="product.imageUrl" :src="product.imageUrl" alt="商品图片" class="w-full h-full object-cover rounded-lg">
              <div v-else class="text-center">
                <svg class="w-12 h-12 text-gray-500 mx-auto" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z"></path>
                </svg>
                <p class="text-sm text-gray-500 mt-2">暂无图片</p>
              </div>
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">
                图片URL
              </label>
              <input v-model="product.imageUrl" type="text" class="input-field" placeholder="请输入图片URL">
            </div>
          </div>
        </div>
        
        <!-- 库存信息卡片 -->
        <div class="card">
          <h2 class="text-lg font-semibold text-gray-900 mb-4">库存信息</h2>
          <div v-if="inventory" class="space-y-2">
            <div class="flex justify-between">
              <span class="text-gray-600">当前库存</span>
              <span class="font-medium" :class="getStockClass(inventory.quantity)">
                {{ inventory.quantity }} {{ inventory.unit }}
              </span>
            </div>
            <div class="flex justify-between">
              <span class="text-gray-600">预警阈值</span>
              <span class="font-medium">{{ inventory.warningThreshold }} {{ inventory.unit }}</span>
            </div>
            <div class="flex justify-between">
              <span class="text-gray-600">库存状态</span>
              <span :class="getStockStatusClass(inventory.quantity, inventory.warningThreshold)">
                {{ getStockStatus(inventory.quantity, inventory.warningThreshold) }}
              </span>
            </div>
          </div>
          <div v-else class="text-center py-4">
            <p class="text-gray-500">暂无库存信息</p>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 操作按钮 -->
    <div class="flex justify-end space-x-4 pt-4">
      <button @click="resetForm" class="btn-secondary">
        重置
      </button>
      <button @click="saveProduct" class="btn-primary">
        保存修改
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { productService, inventoryService } from '../../services/api'

const router = useRouter()
const route = useRoute()
const productId = Number(route.params.id)

// 商品数据
const product = ref({
  id: 0,
  productNo: '',
  name: '',
  categoryId: 0,
  price: 0,
  costPrice: 0,
  status: 1,
  description: '',
  imageUrl: ''
})

// 库存数据
const inventory = ref<any>(null)

// 加载商品详情
const loadProductDetail = async () => {
  try {
    if (productId) {
      const response = await productService.getProductById(productId)
      product.value = response.data
      
      // 加载库存信息
      await loadInventory()
    }
  } catch (error) {
    console.error('加载商品详情失败:', error)
  }
}

// 加载库存信息
const loadInventory = async () => {
  try {
    if (productId) {
      const response = await inventoryService.getInventoryById(productId)
      inventory.value = response.data
    }
  } catch (error) {
    console.error('加载库存信息失败:', error)
  }
}

// 保存商品信息
const saveProduct = async () => {
  try {
    if (productId) {
      await productService.updateProduct(productId, product.value)
    } else {
      await productService.createProduct(product.value)
    }
    router.push('/products')
  } catch (error) {
    console.error('保存商品信息失败:', error)
  }
}

// 更新商品状态
const updateProductStatus = async (status: number) => {
  try {
    product.value.status = status
    await productService.updateProduct(productId, product.value)
  } catch (error) {
    console.error('更新商品状态失败:', error)
  }
}

// 获取库存样式
const getStockClass = (stock: number): string => {
  if (stock === 0) {
    return 'text-red-600'
  } else if (stock < 10) {
    return 'text-yellow-600'
  } else {
    return 'text-gray-900'
  }
}

// 获取库存状态样式
const getStockStatusClass = (stock: number, threshold: number): string => {
  if (stock === 0) {
    return 'text-red-600 font-medium'
  } else if (stock < threshold) {
    return 'text-yellow-600 font-medium'
  } else {
    return 'text-green-600 font-medium'
  }
}

// 获取库存状态
const getStockStatus = (stock: number, threshold: number): string => {
  if (stock === 0) {
    return '缺货'
  } else if (stock < threshold) {
    return '库存不足'
  } else {
    return '库存充足'
  }
}

// 重置表单
const resetForm = () => {
  loadProductDetail()
}

// 返回列表
const goBack = () => {
  router.push('/products')
}

// 初始化
onMounted(() => {
  loadProductDetail()
})
</script>

<style scoped>
.btn-primary {
  @apply px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 transition-colors;
}

.btn-secondary {
  @apply px-4 py-2 bg-gray-200 text-gray-800 rounded-md hover:bg-gray-300 transition-colors;
}

.btn-danger {
  @apply px-4 py-2 bg-red-600 text-white rounded-md hover:bg-red-700 transition-colors;
}

.btn-success {
  @apply px-4 py-2 bg-green-600 text-white rounded-md hover:bg-green-700 transition-colors;
}

.input-field {
  @apply w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent;
}

.card {
  @apply bg-white p-6 rounded-lg shadow-sm border border-gray-100;
}
</style>