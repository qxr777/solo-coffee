import { describe, it, expect, beforeEach, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import Dashboard from '../../src/views/dashboard/Dashboard.vue'
import * as echarts from 'echarts'

// Mock echarts
vi.mock('echarts', () => ({
  init: vi.fn().mockReturnValue({
    setOption: vi.fn(),
    resize: vi.fn()
  })
}))

describe('Dashboard Component', () => {
  let wrapper: ReturnType<typeof mount>

  beforeEach(() => {
    // Reset mocks
    vi.clearAllMocks()
    
    // Mock window resize event
    window.addEventListener = vi.fn()
    
    // Mount component
    wrapper = mount(Dashboard)
  })

  it('should render dashboard layout correctly', () => {
    // Check sales overview cards
    expect(wrapper.findAll('.card').length).toBeGreaterThanOrEqual(4)
    
    // Check sales trend chart
    expect(wrapper.find('.h-80 div').exists()).toBe(true)
    
    // Check recent orders section
    const h3Elements = wrapper.findAll('h3')
    const recentOrdersHeader = h3Elements.find(h3 => h3.text() === '最近订单')
    expect(recentOrdersHeader).toBeTruthy()
    
    // Check hot products section
    const hotProductsHeader = h3Elements.find(h3 => h3.text() === '热销商品')
    expect(hotProductsHeader).toBeTruthy()
  })

  it('should render sales overview cards with correct data', () => {
    const cards = wrapper.findAll('.card').slice(0, 4)
    
    // Today's sales card
    expect(cards[0].text()).toContain('今日销售额')
    expect(cards[0].text()).toContain('¥12,845')
    expect(cards[0].text()).toContain('12.5% 较昨日')
    
    // Today's orders card
    expect(cards[1].text()).toContain('今日订单数')
    expect(cards[1].text()).toContain('245')
    expect(cards[1].text()).toContain('8.3% 较昨日')
    
    // Inventory warning card
    expect(cards[2].text()).toContain('库存预警')
    expect(cards[2].text()).toContain('12')
    expect(cards[2].text()).toContain('3 较昨日')
    
    // Total members card
    expect(cards[3].text()).toContain('会员总数')
    expect(cards[3].text()).toContain('12,845')
    expect(cards[3].text()).toContain('2.1% 较上月')
  })

  it('should render recent orders correctly', () => {
    const orderItems = wrapper.findAll('.flex.items-center.justify-between.p-3').slice(0, 3)
    
    expect(orderItems.length).toBe(3)
    
    // First order
    expect(orderItems[0].text()).toContain('订单 #SO202601140001')
    expect(orderItems[0].text()).toContain('张三 · 美式咖啡 × 2')
    expect(orderItems[0].text()).toContain('¥64.00')
    expect(orderItems[0].text()).toContain('已完成')
    
    // Second order
    expect(orderItems[1].text()).toContain('订单 #SO202601140002')
    expect(orderItems[1].text()).toContain('李四 · 拿铁 × 1, 牛角包 × 1')
    expect(orderItems[1].text()).toContain('¥48.00')
    expect(orderItems[1].text()).toContain('制作中')
    
    // Third order
    expect(orderItems[2].text()).toContain('订单 #SO202601140003')
    expect(orderItems[2].text()).toContain('王五 · 卡布奇诺 × 1')
    expect(orderItems[2].text()).toContain('¥36.00')
    expect(orderItems[2].text()).toContain('待支付')
  })

  it('should render hot products correctly', () => {
    const productItems = wrapper.findAll('.flex.items-center.justify-between.p-3').slice(3, 6)
    
    expect(productItems.length).toBe(3)
    
    // First product
    expect(productItems[0].text()).toContain('美式咖啡')
    expect(productItems[0].text()).toContain('¥32.00')
    expect(productItems[0].text()).toContain('128 杯')
    expect(productItems[0].text()).toContain('+12.5%')
    
    // Second product
    expect(productItems[1].text()).toContain('拿铁咖啡')
    expect(productItems[1].text()).toContain('¥36.00')
    expect(productItems[1].text()).toContain('96 杯')
    expect(productItems[1].text()).toContain('+8.3%')
    
    // Third product
    expect(productItems[2].text()).toContain('卡布奇诺')
    expect(productItems[2].text()).toContain('¥36.00')
    expect(productItems[2].text()).toContain('72 杯')
    expect(productItems[2].text()).toContain('+5.2%')
  })

  it('should initialize echarts on mount', () => {
    expect(echarts.init).toHaveBeenCalled()
    expect(window.addEventListener).toHaveBeenCalledWith('resize', expect.any(Function))
  })

  it('should render date range buttons for sales trend', () => {
    const buttons = wrapper.findAll('button.px-3.py-1.text-sm')
    expect(buttons.length).toBe(3)
    expect(buttons[0].text()).toBe('日')
    expect(buttons[1].text()).toBe('周')
    expect(buttons[2].text()).toBe('月')
    expect(buttons[0].classes()).toContain('bg-blue-100')
    expect(buttons[0].classes()).toContain('text-blue-700')
  })

  it('should have links to orders and products pages', () => {
    const links = wrapper.findAll('a.text-sm.text-blue-600')
    expect(links.length).toBe(2)
    expect(links[0].text()).toBe('查看全部')
    expect(links[1].text()).toBe('查看全部')
    expect(links[0].attributes('href')).toBe('/orders')
    expect(links[1].attributes('href')).toBe('/products')
  })
})
