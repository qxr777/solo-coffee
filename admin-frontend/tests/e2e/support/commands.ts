// 自定义Cypress命令
Cypress.Commands.add('login', (username: string, password: string) => {
  cy.visit('/login')
  cy.get('input#username').type(username)
  cy.get('input#password').type(password)
  cy.get('button[type="submit"]').click()
})

Cypress.Commands.add('logout', () => {
  cy.get('button').contains('管理员').click()
  cy.get('button').contains('退出登录').click()
})

// 导出命令类型以获得TypeScript支持
export {}

declare global {
  namespace Cypress {
    interface Chainable {
      login(username: string, password: string): Chainable<void>
      logout(): Chainable<void>
    }
  }
}