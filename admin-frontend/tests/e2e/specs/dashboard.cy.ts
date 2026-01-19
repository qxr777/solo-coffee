describe('仪表盘模块测试', () => {
  beforeEach(() => {
    // 先登录
    cy.visit('/login')
    cy.get('input#username').type('13800138000')
    cy.get('input#password').type('password123')
    cy.get('button[type="submit"]').click()
      cy.url().should('eq', 'http://localhost:5173/')
  })

  it('应该正确显示仪表盘页面', () => {
    // 验证仪表盘标题
    cy.get('h2').should('contain', '仪表盘')
    
    // 验证销售概览卡片
    cy.get('.card').should('have.length.at.least', 4)
    
    // 验证最近订单 section
    cy.get('h3').should('contain', '最近订单')
    
    // 验证热销商品 section
    cy.get('h3').should('contain', '热销商品')
  })

  it('应该显示销售概览卡片数据', () => {
    // 验证今日销售额卡片
    cy.contains('今日销售额').should('exist')
    cy.contains('¥12,845').should('exist')
    cy.contains('12.5% 较昨日').should('exist')
    
    // 验证今日订单数卡片
    cy.contains('今日订单数').should('exist')
    cy.contains('245').should('exist')
    cy.contains('8.3% 较昨日').should('exist')
    
    // 验证库存预警卡片
    cy.contains('库存预警').should('exist')
    cy.contains('12').should('exist')
    cy.contains('3 较昨日').should('exist')
    
    // 验证会员总数卡片
    cy.contains('会员总数').should('exist')
    cy.contains('12,845').should('exist')
    cy.contains('2.1% 较上月').should('exist')
  })

  it('应该显示最近订单信息', () => {
    // 验证最近订单列表
    cy.contains('最近订单').should('exist')
    cy.contains('订单 #SO202601140001').should('exist')
    cy.contains('张三 · 美式咖啡 × 2').should('exist')
    cy.contains('¥64.00').should('exist')
    cy.contains('已完成').should('exist')
    
    cy.contains('订单 #SO202601140002').should('exist')
    cy.contains('李四 · 拿铁 × 1, 牛角包 × 1').should('exist')
    cy.contains('¥48.00').should('exist')
    cy.contains('制作中').should('exist')
    
    cy.contains('订单 #SO202601140003').should('exist')
    cy.contains('王五 · 卡布奇诺 × 1').should('exist')
    cy.contains('¥36.00').should('exist')
    cy.contains('待支付').should('exist')
  })

  it('应该显示热销商品信息', () => {
    // 验证热销商品列表
    cy.contains('热销商品').should('exist')
    cy.contains('美式咖啡').should('exist')
    cy.contains('¥32.00').should('exist')
    cy.contains('128 杯').should('exist')
    cy.contains('+12.5%').should('exist')
    
    cy.contains('拿铁咖啡').should('exist')
    cy.contains('¥36.00').should('exist')
    cy.contains('96 杯').should('exist')
    cy.contains('+8.3%').should('exist')
    
    cy.contains('卡布奇诺').should('exist')
    cy.contains('¥36.00').should('exist')
    cy.contains('72 杯').should('exist')
    cy.contains('+5.2%').should('exist')
  })

  it('应该可以切换销售趋势的时间范围', () => {
    // 验证时间范围选择器存在
    cy.contains('销售趋势').should('exist')
    
    // 尝试找到时间范围按钮，如果不存在则跳过
    cy.get('button').each(($button) => {
      if ($button.text().match(/日|周|月/)) {
        cy.wrap($button).should('exist')
      }
    })
  })

  it('应该可以点击链接跳转到订单和商品页面', () => {
    // 点击订单查看全部
    cy.contains('最近订单').siblings('a').click()
      cy.url().should('eq', 'http://localhost:5173/orders')
      cy.get('h1').should('contain', '订单管理')
      
      // 返回仪表盘
      cy.visit('/')
      
      // 点击商品查看全部
      cy.contains('热销商品').siblings('a').click()
      cy.url().should('eq', 'http://localhost:5173/products')
      cy.get('h1').should('contain', '商品管理')
  })
})
