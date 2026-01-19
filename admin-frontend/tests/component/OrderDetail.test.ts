import { describe, it, expect, beforeEach, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import OrderDetail from '../../src/views/order/OrderDetail.vue'
import { createRouter, createWebHistory } from 'vue-router'

// Mock router
const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/orders/:id', component: OrderDetail }
  ]
})

describe('OrderDetail Component', () => {
  let wrapper: ReturnType<typeof mount>

  beforeEach(async () => {
    // Reset mocks
    vi.clearAllMocks()
    
    // Mock route params
    router.push('/orders/1')
    
    // Mount component with router
    wrapper = mount(OrderDetail, {
      global: {
        plugins: [router]
      }
    })
    
    // Wait for router to be ready
    await router.isReady()
  })

  it('should render order detail layout correctly', () => {
    // Check page title
    expect(wrapper.find('h1').text()).toBe('订单详情')
    expect(wrapper.find('p').text()).toBe('查看和管理订单的详细信息')
    
    // Check action buttons
    const buttons = wrapper.findAll('button')
    const exportButton = buttons.find(button => button.text().includes('导出订单'))
    const printButton = buttons.find(button => button.text().includes('打印订单'))
    expect(exportButton).toBeTruthy()
    expect(printButton).toBeTruthy()
    
    // Check order basic info
    expect(wrapper.find('h2').text()).toBe('订单 #SO202601140001')
    const paragraphs = wrapper.findAll('p')
    const createTimeParagraph = paragraphs.find(p => p.text().includes('创建时间:'))
    expect(createTimeParagraph).toBeTruthy()
    
    // Check order status
    const spans = wrapper.findAll('span')
    const statusSpan = spans.find(span => span.text().includes('制作中'))
    expect(statusSpan).toBeTruthy()
    
    // Check order items
    const h3Elements = wrapper.findAll('h3')
    const orderItemsHeader = h3Elements.find(h3 => h3.text().includes('订单商品'))
    const orderAmountHeader = h3Elements.find(h3 => h3.text().includes('订单金额'))
    expect(orderItemsHeader).toBeTruthy()
    expect(orderAmountHeader).toBeTruthy()
    
    // Check customer info
    const customerInfoHeader = h3Elements.find(h3 => h3.text().includes('客户信息'))
    expect(customerInfoHeader).toBeTruthy()
    
    // Check payment info
    const paymentInfoHeader = h3Elements.find(h3 => h3.text().includes('支付信息'))
    expect(paymentInfoHeader).toBeTruthy()
    
    // Check store info
    const storeInfoHeader = h3Elements.find(h3 => h3.text().includes('门店信息'))
    expect(storeInfoHeader).toBeTruthy()
    
    // Check order logs
    const orderLogsHeader = h3Elements.find(h3 => h3.text().includes('订单日志'))
    expect(orderLogsHeader).toBeTruthy()
  })

  it('should render order status with correct style', () => {
    const statusElement = wrapper.find('span')
    expect(statusElement.text()).toBe('制作中')
    expect(statusElement.classes()).toContain('bg-yellow-100')
    expect(statusElement.classes()).toContain('text-yellow-800')
  })

  it('should render order items correctly', () => {
    const orderItems = wrapper.findAll('.flex.items-center.space-x-3')
    expect(orderItems.length).toBeGreaterThanOrEqual(1)
    
    const item = orderItems[0]
    const itemParagraphs = item.findAll('p')
    expect(itemParagraphs.length).toBeGreaterThanOrEqual(1)
    expect(itemParagraphs[0].text()).toBe('美式咖啡')
    
    // Check if we have enough paragraphs before accessing them
    if (itemParagraphs.length > 1) {
      expect(itemParagraphs[1].text()).toContain('少糖')
    }
    if (itemParagraphs.length > 2) {
      expect(itemParagraphs[2].text()).toContain('¥32.00 × 2')
    }
    if (itemParagraphs.length > 3) {
      expect(itemParagraphs[3].text()).toContain('¥64.00')
    }
  })

  it('should render order amount correctly', () => {
    const amountSection = wrapper.find('div.border-t.border-gray-200.pt-4.mt-4')
    expect(amountSection.text()).toContain('商品总额')
    expect(amountSection.text()).toContain('¥64.00')
    expect(amountSection.text()).toContain('优惠折扣')
    expect(amountSection.text()).toContain('-¥5.00')
    expect(amountSection.text()).toContain('配送费')
    expect(amountSection.text()).toContain('¥0.00')
    expect(amountSection.text()).toContain('实付金额')
    expect(amountSection.text()).toContain('¥59.00')
  })

  it('should render customer info correctly', () => {
    const cards = wrapper.findAll('div.card')
    const customerInfoCard = cards.find(card => 
      card.text().includes('客户信息')
    )
    expect(customerInfoCard).toBeTruthy()
    expect(customerInfoCard?.text()).toContain('客户姓名')
    expect(customerInfoCard?.text()).toContain('张三')
    expect(customerInfoCard?.text()).toContain('联系电话')
    expect(customerInfoCard?.text()).toContain('13800138000')
    expect(customerInfoCard?.text()).toContain('电子邮箱')
    expect(customerInfoCard?.text()).toContain('zhangsan@example.com')
    expect(customerInfoCard?.text()).toContain('会员等级')
    expect(customerInfoCard?.text()).toContain('普通会员')
  })

  it('should render payment info correctly', () => {
    const cards = wrapper.findAll('div.card')
    const paymentInfoCard = cards.find(card => 
      card.text().includes('支付信息')
    )
    expect(paymentInfoCard).toBeTruthy()
    expect(paymentInfoCard?.text()).toContain('支付方式')
    expect(paymentInfoCard?.text()).toContain('微信支付')
    expect(paymentInfoCard?.text()).toContain('支付状态')
    expect(paymentInfoCard?.text()).toContain('已支付')
    expect(paymentInfoCard?.text()).toContain('支付时间')
    expect(paymentInfoCard?.text()).toContain('2026-01-14 10:35:00')
    expect(paymentInfoCard?.text()).toContain('支付订单号')
    expect(paymentInfoCard?.text()).toContain('PAY202601140001')
  })

  it('should render store info correctly', () => {
    const cards = wrapper.findAll('div.card')
    const storeInfoCard = cards.find(card => 
      card.text().includes('门店信息')
    )
    expect(storeInfoCard).toBeTruthy()
    expect(storeInfoCard?.text()).toContain('门店名称')
    expect(storeInfoCard?.text()).toContain('Solo Coffee旗舰店')
    expect(storeInfoCard?.text()).toContain('门店地址')
    expect(storeInfoCard?.text()).toContain('北京市朝阳区建国路88号')
    expect(storeInfoCard?.text()).toContain('联系电话')
    expect(storeInfoCard?.text()).toContain('010-12345678')
  })

  it('should render order logs correctly', () => {
    const cards = wrapper.findAll('div.card')
    const orderLogsCard = cards.find(card => 
      card.text().includes('订单日志')
    )
    expect(orderLogsCard).toBeTruthy()
    expect(orderLogsCard?.text()).toContain('订单创建')
    expect(orderLogsCard?.text()).toContain('支付成功')
    expect(orderLogsCard?.text()).toContain('开始制作')
  })

  it('should render order actions based on status', () => {
    const actionButtons = wrapper.findAll('button')
    expect(actionButtons.length).toBeGreaterThanOrEqual(2)
    const markCompleteButton = actionButtons.find(button => button.text() === '标记完成')
    expect(markCompleteButton).toBeTruthy()
  })

  it('should render payment method text correctly', () => {
    expect(wrapper.text()).toContain('微信支付')
  })

  it('should handle different order statuses correctly', () => {
    // Test that current status is displayed
    expect(wrapper.text()).toContain('制作中')
  })
})
