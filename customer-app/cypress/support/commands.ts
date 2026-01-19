/// <reference types="cypress" />

/**
 * 自定义登录命令
 */
Cypress.Commands.add('login', (phone = '13800138000', password = 'password123') => {
  cy.visit('/login')
  cy.get('input[type="text"][placeholder="请输入手机号"]').type(phone)
  cy.get('input[type="password"][placeholder="请输入密码"]').type(password)
  cy.get('button[type="submit"]').click()
  // 验证登录成功，应该跳转到首页
  cy.url().should('include', '/')
})

/**
 * 自定义添加商品到购物车命令
 */
Cypress.Commands.add('addToCart', (productId: number = 1) => {
  // 先确保在商品详情页
  cy.visit(`/product/${productId}`)
  // 点击添加到购物车按钮
  cy.get('button').contains('Add to Cart').click()
  // 验证购物车数量增加
  cy.get('.cart-count').should('not.have.text', '0')
})

/**
 * 自定义导航到购物车命令
 */
Cypress.Commands.add('goToCart', () => {
  cy.get('.cart-icon').click()
  cy.url().should('include', '/cart')
})

/**
 * 自定义清除购物车命令
 */
Cypress.Commands.add('clearCart', () => {
  cy.goToCart()
  cy.get('.remove-item').each(($el) => {
    cy.wrap($el).click()
  })
  cy.get('.empty-cart').should('be.visible')
})

/**
 * 自定义添加地址命令
 */
Cypress.Commands.add('addAddress', () => {
  cy.visit('/profile/address')
  cy.get('button').contains('Add Address').click()
  // 填写地址表单
  cy.get('input[name="name"]').type('Test User')
  cy.get('input[name="phone"]').type('13800138000')
  cy.get('select[name="province"]').select('北京')
  cy.get('select[name="city"]').select('北京市')
  cy.get('select[name="district"]').select('朝阳区')
  cy.get('input[name="address"]').type('测试地址')
  cy.get('button[type="submit"]').click()
  // 验证地址添加成功
  cy.get('.address-list').should('contain', 'Test User')
})

declare global {
  namespace Cypress {
    interface Chainable {
      login(phone?: string, password?: string): Chainable<void>
      addToCart(productId?: number): Chainable<void>
      goToCart(): Chainable<void>
      clearCart(): Chainable<void>
      addAddress(): Chainable<void>
    }
  }
}

export {}
