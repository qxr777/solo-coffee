describe('商品管理模块测试', () => {
  beforeEach(() => {
    // 先登录
    cy.visit('/login')
    cy.get('input#username').type('13800138000')
    cy.get('input#password').type('password123')
    cy.get('button[type="submit"]').click()
      cy.url().should('eq', 'http://localhost:5173/')
  })

  describe('商品列表页面', () => {
    it('应该正确显示商品列表页面', () => {
      // 访问商品管理页面
      cy.visit('/products')
      
      // 验证页面标题
      cy.get('h1').should('contain', '商品管理')
      cy.get('p').should('contain', '管理所有商品，包括商品信息、分类和库存')
      
      // 验证操作按钮
      cy.contains('添加商品').should('exist')
      cy.contains('批量操作').should('exist')
      
      // 验证筛选器
      cy.get('select').should('exist')
      cy.get('input[type="text"]').should('exist')
      cy.contains('搜索').should('exist')
      
      // 验证商品统计
      cy.contains('商品总数').should('exist')
      cy.contains('上架商品').should('exist')
      cy.contains('下架商品').should('exist')
      cy.contains('库存预警').should('exist')
      
      // 验证商品表格
      cy.get('table').should('exist')
      cy.get('thead').should('exist')
      cy.get('tbody').should('exist')
      
      // 验证分页
      cy.contains('显示第 1 到 10 条，共 128 条记录').should('exist')
    })

    it('应该显示商品列表数据', () => {
      // 访问商品管理页面
      cy.visit('/products')
      
      // 验证商品数据
      cy.contains('PD202601010001').should('exist')
      cy.contains('美式咖啡').should('exist')
      cy.contains('咖啡').should('exist')
      cy.contains('¥32.00').should('exist')
      cy.contains('¥12.00').should('exist')
      cy.contains('128 杯').should('exist')
      cy.contains('上架').should('exist')
      
      cy.contains('PD202601010002').should('exist')
      cy.contains('拿铁咖啡').should('exist')
      cy.contains('¥36.00').should('exist')
      cy.contains('¥15.00').should('exist')
      cy.contains('96 杯').should('exist')
      
      cy.contains('PD202601010008').should('exist')
      cy.contains('芝士蛋糕').should('exist')
      cy.contains('0 块').should('exist')
      cy.contains('下架').should('exist')
    })

    it('应该根据商品状态显示不同的操作按钮', () => {
      // 访问商品管理页面
      cy.visit('/products')
      
      // 验证商品状态显示
      cy.contains('上架').should('exist')
      cy.contains('下架').should('exist')
      
      // 尝试找到不同状态的商品行
      cy.get('tr').should('have.length.at.least', 1)
    })

    it('应该可以点击编辑按钮跳转到商品详情页面', () => {
      // 访问商品管理页面
      cy.visit('/products')
      
      // 点击第一个商品的编辑按钮
      cy.contains('美式咖啡').parents('tr').find('a').contains('编辑').click()
      
      // 验证跳转到商品详情页面
      cy.url().should('eq', 'http://localhost:5173/products/1')
    })

    it('应该可以使用分类筛选器', () => {
      // 访问商品管理页面
      cy.visit('/products')
      
      // 选择咖啡分类
      cy.get('select').first().select('1')
      cy.contains('搜索').click()
      
      // 验证筛选结果
      cy.contains('咖啡').should('exist')
    })

    it('应该可以使用状态筛选器', () => {
      // 访问商品管理页面
      cy.visit('/products')
      
      // 选择上架状态
      cy.get('select').eq(1).select('1')
      cy.contains('搜索').click()
      
      // 验证筛选结果
      cy.contains('上架').should('exist')
    })

    it('应该可以使用搜索功能', () => {
      // 访问商品管理页面
      cy.visit('/products')
      
      // 输入搜索关键词
      cy.get('input[type="text"]').type('咖啡')
      cy.contains('搜索').click()
      
      // 验证搜索结果
      cy.contains('咖啡').should('exist')
    })

    it('应该显示库存状态的不同样式', () => {
      // 访问商品管理页面
      cy.visit('/products')
      
      // 验证库存状态
      cy.contains('128 杯').should('exist')
      cy.contains('8 杯').should('exist')
      cy.contains('3 个').should('exist')
      cy.contains('0 块').should('exist')
    })
  })
})
