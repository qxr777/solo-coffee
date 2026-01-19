import { describe, it, expect, beforeEach, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import ProductList from '../../src/views/product/ProductList.vue'
import { createRouter, createWebHistory } from 'vue-router'

// Mock router
const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/products', component: ProductList },
    { path: '/products/:id', component: ProductList }
  ]
})

describe('ProductList Component', () => {
  let wrapper: ReturnType<typeof mount>

  beforeEach(async () => {
    // Reset mocks
    vi.clearAllMocks()
    
    // Mount component with router
    wrapper = mount(ProductList, {
      global: {
        plugins: [router]
      }
    })
    
    // Wait for router to be ready
    await router.isReady()
  })

  it('should render product list layout correctly', () => {
    // Check page title
    expect(wrapper.find('h1').text()).toBe('商品管理')
    expect(wrapper.find('p').text()).toBe('管理所有商品，包括商品信息、分类和库存')
    
    // Check action buttons
    const buttons = wrapper.findAll('button')
    const addProductButton = buttons.find(button => button.text().includes('添加商品'))
    const batchActionButton = buttons.find(button => button.text().includes('批量操作'))
    const searchButton = buttons.find(button => button.text().includes('搜索'))
    expect(addProductButton).toBeTruthy()
    expect(batchActionButton).toBeTruthy()
    expect(searchButton).toBeTruthy()
    
    // Check filters
    expect(wrapper.find('select').exists()).toBe(true)
    expect(wrapper.find('input[type="text"]').exists()).toBe(true)
    
    // Check product statistics
    expect(wrapper.findAll('.grid.grid-cols-2').length).toBe(1)
    
    // Check product table
    expect(wrapper.find('table').exists()).toBe(true)
    expect(wrapper.find('thead').exists()).toBe(true)
    expect(wrapper.find('tbody').exists()).toBe(true)
    
    // Check pagination
    expect(wrapper.find('.flex.items-center.justify-between').exists()).toBe(true)
  })

  it('should render product statistics correctly', () => {
    const stats = wrapper.findAll('.p-3')
    
    expect(stats.length).toBe(4)
    expect(stats[0].text()).toContain('商品总数')
    expect(stats[0].text()).toContain('128')
    expect(stats[1].text()).toContain('上架商品')
    expect(stats[1].text()).toContain('112')
    expect(stats[2].text()).toContain('下架商品')
    expect(stats[2].text()).toContain('16')
    expect(stats[3].text()).toContain('库存预警')
    expect(stats[3].text()).toContain('8')
  })

  it('should render product table with correct data', () => {
    const rows = wrapper.findAll('tbody tr')
    expect(rows.length).toBe(8)
    
    // Check first product
    const firstRow = rows[0]
    expect(firstRow.text()).toContain('PD202601010001')
    expect(firstRow.text()).toContain('美式咖啡')
    expect(firstRow.text()).toContain('咖啡')
    expect(firstRow.text()).toContain('¥32.00')
    expect(firstRow.text()).toContain('¥12.00')
    expect(firstRow.text()).toContain('128 杯')
    expect(firstRow.text()).toContain('上架')
    
    // Check second product
    const secondRow = rows[1]
    expect(secondRow.text()).toContain('PD202601010002')
    expect(secondRow.text()).toContain('拿铁咖啡')
    expect(secondRow.text()).toContain('¥36.00')
    expect(secondRow.text()).toContain('¥15.00')
    expect(secondRow.text()).toContain('96 杯')
    expect(secondRow.text()).toContain('上架')
    
    // Check out of stock product
    const lastRow = rows[7]
    expect(lastRow.text()).toContain('PD202601010008')
    expect(lastRow.text()).toContain('芝士蛋糕')
    expect(lastRow.text()).toContain('0 块')
    expect(lastRow.text()).toContain('下架')
  })

  it('should render stock status with correct styles', () => {
    const stockElements = wrapper.findAll('td:nth-child(5)')
    
    // Check normal stock
    expect(stockElements[0].text()).toContain('128 杯')
    
    // Check low stock
    expect(stockElements[5].text()).toContain('8 杯')
    expect(stockElements[6].text()).toContain('3 个')
    
    // Check out of stock
    expect(stockElements[7].text()).toContain('0 块')
  })

  it('should render product status with correct styles', () => {
    const statusElements = wrapper.findAll('td:nth-child(6) span')
    
    statusElements.forEach((status, index) => {
      if (index < 7) {
        expect(status.text()).toBe('上架')
        expect(status.classes()).toContain('bg-green-100')
        expect(status.classes()).toContain('text-green-800')
      } else {
        expect(status.text()).toBe('下架')
        expect(status.classes()).toContain('bg-gray-100')
        expect(status.classes()).toContain('text-gray-800')
      }
    })
  })

  it('should render product actions based on status', () => {
    const actionCells = wrapper.findAll('td:nth-child(7)')
    
    // First 7 products (上架)
    for (let i = 0; i < 7; i++) {
      expect(actionCells[i].text()).toContain('编辑')
      expect(actionCells[i].text()).toContain('下架')
      expect(actionCells[i].text()).toContain('库存')
    }
    
    // Last product (下架)
    expect(actionCells[7].text()).toContain('编辑')
    expect(actionCells[7].text()).toContain('上架')
    expect(actionCells[7].text()).toContain('库存')
  })

  it('should have links to product detail pages', () => {
    const links = wrapper.findAll('a.text-blue-600')
    
    // Check if there are "编辑" links
    const editLinks = links.filter(link => link.text() === '编辑')
    expect(editLinks.length).toBeGreaterThanOrEqual(8)
    
    // Check link text
    editLinks.forEach(link => {
      expect(link.text()).toBe('编辑')
    })
  })

  it('should render pagination controls', () => {
    const pagination = wrapper.find('.flex.items-center.justify-between')
    expect(pagination.exists()).toBe(true)
    expect(pagination.text()).toContain('显示第 1 到 10 条，共 128 条记录')
    expect(pagination.findAll('a').length).toBeGreaterThanOrEqual(5)
  })

  it('should handle category filter correctly', async () => {
    const select = wrapper.findAll('select')[0]
    await select.setValue('1')
    expect(select.element.value).toBe('1')
    
    await select.setValue('')
    expect(select.element.value).toBe('')
  })

  it('should handle status filter correctly', async () => {
    const select = wrapper.findAll('select')[1]
    await select.setValue('1')
    expect(select.element.value).toBe('1')
    
    await select.setValue('0')
    expect(select.element.value).toBe('0')
    
    await select.setValue('')
    expect(select.element.value).toBe('')
  })

  it('should handle search input correctly', async () => {
    const input = wrapper.find('input[type="text"]')
    await input.setValue('美式咖啡')
    expect(input.element.value).toBe('美式咖啡')
  })
})
