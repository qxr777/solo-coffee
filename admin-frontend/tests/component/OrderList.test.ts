import { describe, it, expect, beforeEach, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import OrderList from '../../src/views/order/OrderList.vue'
import { createRouter, createWebHistory } from 'vue-router'

// Mock router
const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/orders', component: OrderList },
    { path: '/orders/:id', component: OrderList }
  ]
})

describe('OrderList Component', () => {
  let wrapper: ReturnType<typeof mount>

  beforeEach(async () => {
    // Reset mocks
    vi.clearAllMocks()
    
    // Mount component with router
    wrapper = mount(OrderList, {
      global: {
        plugins: [router]
      }
    })
    
    // Wait for router to be ready
    await router.isReady()
  })

  it('should render order list layout correctly', () => {
    // Check page title
    expect(wrapper.find('h1').text()).toBe('订单管理')
    expect(wrapper.find('p').text()).toBe('管理所有订单，包括订单状态更新和退款处理')
    
    // Check filter inputs
    expect(wrapper.find('select').exists()).toBe(true)
    expect(wrapper.find('input[type="text"]').exists()).toBe(true)
    const buttons = wrapper.findAll('button')
    const searchButton = buttons.find(button => button.text().includes('搜索'))
    expect(searchButton).toBeTruthy()
    
    // Check order statistics
    expect(wrapper.findAll('.grid.grid-cols-2').length).toBe(1)
    
    // Check order table
    expect(wrapper.find('table').exists()).toBe(true)
    expect(wrapper.find('thead').exists()).toBe(true)
    expect(wrapper.find('tbody').exists()).toBe(true)
    
    // Check pagination
    expect(wrapper.find('.flex.items-center.justify-between').exists()).toBe(true)
  })

  it('should render order statistics correctly', () => {
    const stats = wrapper.findAll('.p-3')
    
    expect(stats.length).toBe(6)
    expect(stats[0].text()).toContain('全部订单')
    expect(stats[0].text()).toContain('3,245')
    expect(stats[1].text()).toContain('待支付')
    expect(stats[1].text()).toContain('128')
    expect(stats[2].text()).toContain('制作中')
    expect(stats[2].text()).toContain('85')
    expect(stats[3].text()).toContain('已完成')
    expect(stats[3].text()).toContain('2,890')
    expect(stats[4].text()).toContain('已取消')
    expect(stats[4].text()).toContain('125')
    expect(stats[5].text()).toContain('退款中')
    expect(stats[5].text()).toContain('17')
  })

  it('should render order table with correct data', () => {
    const rows = wrapper.findAll('tbody tr')
    expect(rows.length).toBe(5)
    
    // Check first order
    const firstRow = rows[0]
    expect(firstRow.text()).toContain('SO202601140001')
    expect(firstRow.text()).toContain('张三')
    expect(firstRow.text()).toContain('13800138000')
    expect(firstRow.text()).toContain('¥64.00')
    expect(firstRow.text()).toContain('已完成')
    expect(firstRow.text()).toContain('2026-01-14 10:30:00')
    
    // Check second order
    const secondRow = rows[1]
    expect(secondRow.text()).toContain('SO202601140002')
    expect(secondRow.text()).toContain('李四')
    expect(secondRow.text()).toContain('13900139000')
    expect(secondRow.text()).toContain('¥48.00')
    expect(secondRow.text()).toContain('制作中')
    expect(secondRow.text()).toContain('2026-01-14 10:15:00')
    
    // Check third order
    const thirdRow = rows[2]
    expect(thirdRow.text()).toContain('SO202601140003')
    expect(thirdRow.text()).toContain('王五')
    expect(thirdRow.text()).toContain('13700137000')
    expect(thirdRow.text()).toContain('¥36.00')
    expect(thirdRow.text()).toContain('待支付')
    expect(thirdRow.text()).toContain('2026-01-14 10:00:00')
  })

  it('should render order status with correct styles', () => {
    const statusElements = wrapper.findAll('td:nth-child(4) span')
    
    expect(statusElements.length).toBe(5)
    expect(statusElements[0].text()).toBe('已完成')
    expect(statusElements[1].text()).toBe('制作中')
    expect(statusElements[2].text()).toBe('待支付')
    expect(statusElements[3].text()).toBe('已取消')
    expect(statusElements[4].text()).toBe('退款中')
    
    // Check status classes
    expect(statusElements[0].classes()).toContain('bg-green-100')
    expect(statusElements[0].classes()).toContain('text-green-800')
    expect(statusElements[1].classes()).toContain('bg-yellow-100')
    expect(statusElements[1].classes()).toContain('text-yellow-800')
    expect(statusElements[2].classes()).toContain('bg-blue-100')
    expect(statusElements[2].classes()).toContain('text-blue-800')
    expect(statusElements[3].classes()).toContain('bg-red-100')
    expect(statusElements[3].classes()).toContain('text-red-800')
    expect(statusElements[4].classes()).toContain('bg-purple-100')
    expect(statusElements[4].classes()).toContain('text-purple-800')
  })

  it('should render order actions based on status', () => {
    const actionCells = wrapper.findAll('td:nth-child(6)')
    
    // First order (已完成)
    expect(actionCells[0].text()).toContain('查看')
    expect(actionCells[0].text()).toContain('退款')
    
    // Second order (制作中)
    expect(actionCells[1].text()).toContain('查看')
    expect(actionCells[1].text()).toContain('完成')
    expect(actionCells[1].text()).toContain('取消')
    
    // Third order (待支付)
    expect(actionCells[2].text()).toContain('查看')
    expect(actionCells[2].text()).toContain('确认')
    expect(actionCells[2].text()).toContain('取消')
  })

  it('should have links to order detail pages', () => {
    const links = wrapper.findAll('a.text-blue-600')
    
    // 检查是否有"查看"链接
    const viewLinks = links.filter(link => link.text() === '查看')
    expect(viewLinks.length).toBeGreaterThanOrEqual(5)
    
    // 检查链接文本
    viewLinks.forEach(link => {
      expect(link.text()).toBe('查看')
    })
  })

  it('should render pagination controls', () => {
    const pagination = wrapper.find('.flex.items-center.justify-between')
    expect(pagination.exists()).toBe(true)
    expect(pagination.text()).toContain('显示第 1 到 10 条，共 325 条记录')
    expect(pagination.findAll('a').length).toBeGreaterThanOrEqual(5)
  })

  it('should handle status filter correctly', async () => {
    const select = wrapper.find('select')
    await select.setValue('1')
    expect(select.element.value).toBe('1')
    
    await select.setValue('2')
    expect(select.element.value).toBe('2')
    
    await select.setValue('')
    expect(select.element.value).toBe('')
  })

  it('should handle search input correctly', async () => {
    const input = wrapper.find('input[type="text"]')
    await input.setValue('SO202601140001')
    expect(input.element.value).toBe('SO202601140001')
  })
})
