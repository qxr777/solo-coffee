describe('Home Page', () => {
  beforeEach(() => {
    // 访问首页
    cy.visit('/')
  })

  it('should display the home page correctly', () => {
    // 验证页面标题
    cy.title().should('include', 'Solo Coffee')
    
    // 验证导航栏存在
    cy.get('.navbar').should('be.visible')
    
    // 验证logo存在
    cy.get('.logo').should('be.visible')
    
    // 验证导航链接存在
    cy.get('.nav-links').should('be.visible')
    cy.get('.nav-links').contains('Home')
    cy.get('.nav-links').contains('Products')
    cy.get('.nav-links').contains('Stores')
    cy.get('.nav-links').contains('Profile')
    
    // 验证购物车图标存在
    cy.get('.cart-icon').should('be.visible')
    
    // 验证页面内容存在
    cy.get('.hero').should('be.visible')
    cy.get('.featured-products').should('be.visible')
    cy.get('.nearby-stores').should('be.visible')
    
    // 验证页脚存在
    cy.get('.footer').should('be.visible')
  })

  it('should navigate to products page', () => {
    // 点击Products链接
    cy.get('.nav-links').contains('Products').click()
    
    // 验证导航到products页面
    cy.url().should('include', '/products')
    cy.get('.products-page').should('be.visible')
  })

  it('should navigate to stores page', () => {
    // 点击Stores链接
    cy.get('.nav-links').contains('Stores').click()
    
    // 验证导航到stores页面
    cy.url().should('include', '/stores')
    cy.get('.stores-page').should('be.visible')
  })

  it('should navigate to profile page', () => {
    // 点击Profile链接
    cy.get('.nav-links').contains('Profile').click()
    
    // 验证导航到profile页面或登录页面
    cy.url().should('satisfy', (url) => {
      return url.includes('/profile') || url.includes('/login')
    })
  })

  it('should display cart when clicking cart icon', () => {
    // 点击购物车图标
    cy.get('.cart-icon').click()
    
    // 验证购物车面板显示
    cy.get('.cart-panel').should('be.visible')
  })
})
