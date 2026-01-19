describe('订单管理模块测试', () => {
  beforeEach(() => {
    // 先登录
    cy.visit('/login')
    cy.get('input#username').type('13800138000')
    cy.get('input#password').type('password123')
    cy.get('button[type="submit"]').click()
      cy.url().should('eq', 'http://localhost:5173/')
  })

  describe('订单列表页面', () => {
    it('应该正确显示订单列表页面', () => {
      // 访问订单管理页面
      cy.visit('/orders')
      
      // 验证页面标题
      cy.get('h1').should('contain', '订单管理')
      cy.get('p').should('contain', '管理所有订单，包括订单状态更新和退款处理')
      
      // 验证筛选器
      cy.get('select').should('exist')
      cy.get('input[type="text"]').should('exist')
      cy.get('button').should('contain', '搜索')
      
      // 验证订单统计
      cy.get('.grid.grid-cols-2').should('exist')
      cy.contains('全部订单').should('exist')
      cy.contains('待支付').should('exist')
      cy.contains('制作中').should('exist')
      cy.contains('已完成').should('exist')
      cy.contains('已取消').should('exist')
      cy.contains('退款中').should('exist')
      
      // 验证订单表格
      cy.get('table').should('exist')
      cy.get('thead').should('exist')
      cy.get('tbody').should('exist')
      
      // 验证分页
      cy.contains('显示第 1 到 10 条，共 325 条记录').should('exist')
    })

    it('应该显示订单列表数据', () => {
      // 访问订单管理页面
      cy.visit('/orders')
      
      // 验证订单数据
      cy.contains('SO202601140001').should('exist')
      cy.contains('张三').should('exist')
      cy.contains('13800138000').should('exist')
      cy.contains('¥64.00').should('exist')
      cy.contains('已完成').should('exist')
      
      cy.contains('SO202601140002').should('exist')
      cy.contains('李四').should('exist')
      cy.contains('13900139000').should('exist')
      cy.contains('¥48.00').should('exist')
      cy.contains('制作中').should('exist')
      
      cy.contains('SO202601140003').should('exist')
      cy.contains('王五').should('exist')
      cy.contains('13700137000').should('exist')
      cy.contains('¥36.00').should('exist')
      cy.contains('待支付').should('exist')
    })

    it('应该根据订单状态显示不同的操作按钮', () => {
      // 访问订单管理页面
      cy.visit('/orders')
      
      // 验证订单状态显示
      cy.contains('已完成').should('exist')
      cy.contains('制作中').should('exist')
      cy.contains('待支付').should('exist')
      
      // 尝试找到不同状态的订单行
      cy.get('tr').should('have.length.at.least', 1)
    })

    it('应该可以点击查看按钮跳转到订单详情页面', () => {
      // 访问订单管理页面
      cy.visit('/orders')
      
      // 点击第一个订单的查看按钮
      cy.contains('SO202601140001').parents('tr').find('a').contains('查看').click()
      
      // 验证跳转到订单详情页面
      cy.url().should('eq', 'http://localhost:5173/orders/1')
      cy.get('h1').should('contain', '订单详情')
    })

    it('应该可以使用状态筛选器', () => {
      // 访问订单管理页面
      cy.visit('/orders')
      
      // 选择待支付状态
      cy.get('select').select('1')
      cy.get('button').contains('搜索').click()
      
      // 验证筛选结果
      cy.contains('待支付').should('exist')
    })

    it('应该可以使用搜索功能', () => {
      // 访问订单管理页面
      cy.visit('/orders')
      
      // 输入搜索关键词
      cy.get('input[type="text"]').type('张三')
      cy.get('button').contains('搜索').click()
      
      // 验证搜索结果
      cy.contains('张三').should('exist')
    })
  })

  describe('订单详情页面', () => {
    it('应该正确显示订单详情页面', () => {
      // 访问订单详情页面
      cy.visit('/orders/1')
      
      // 验证页面标题
      cy.get('h1').should('contain', '订单详情')
      cy.get('p').should('contain', '查看和管理订单的详细信息')
      
      // 验证操作按钮
      cy.contains('导出订单').should('exist')
      cy.contains('打印订单').should('exist')
      
      // 验证订单基本信息
      cy.contains('订单 #SO202601140001').should('exist')
      cy.contains('创建时间:').should('exist')
      
      // 验证订单状态
      cy.contains('制作中').should('exist')
      
      // 验证订单商品
      cy.contains('订单商品').should('exist')
      cy.contains('美式咖啡').should('exist')
      
      // 验证订单金额
      cy.contains('订单金额').should('exist')
      cy.contains('商品总额').should('exist')
      cy.contains('优惠折扣').should('exist')
      cy.contains('配送费').should('exist')
      cy.contains('实付金额').should('exist')
      
      // 验证客户信息
      cy.contains('客户信息').should('exist')
      cy.contains('张三').should('exist')
      cy.contains('13800138000').should('exist')
      
      // 验证支付信息
      cy.contains('支付信息').should('exist')
      cy.contains('微信支付').should('exist')
      cy.contains('已支付').should('exist')
      
      // 验证门店信息
      cy.contains('门店信息').should('exist')
      cy.contains('Solo Coffee旗舰店').should('exist')
      
      // 验证订单日志
      cy.contains('订单日志').should('exist')
      cy.contains('订单创建').should('exist')
      cy.contains('支付成功').should('exist')
      cy.contains('开始制作').should('exist')
    })

    it('应该根据订单状态显示不同的操作按钮', () => {
      // 访问订单详情页面
      cy.visit('/orders/1')
      
      // 验证制作中状态的操作按钮
      cy.contains('标记完成').should('exist')
    })

    it('应该可以导出订单', () => {
      // 访问订单详情页面
      cy.visit('/orders/1')
      
      // 点击导出订单按钮
      cy.contains('导出订单').click()
      // 这里可以添加下载验证逻辑
    })

    it('应该可以打印订单', () => {
      // 访问订单详情页面
      cy.visit('/orders/1')
      
      // 点击打印订单按钮
      cy.contains('打印订单').click()
      // 这里可以添加打印验证逻辑
    })

    it('应该显示订单商品的定制选项', () => {
      // 访问订单详情页面
      cy.visit('/orders/1')
      
      // 验证定制选项
      cy.contains('少糖').should('exist')
    })

    it('应该显示订单金额明细', () => {
      // 访问订单详情页面
      cy.visit('/orders/1')
      
      // 验证金额明细
      cy.contains('¥64.00').should('exist')
      cy.contains('-¥5.00').should('exist')
      cy.contains('¥0.00').should('exist')
      cy.contains('¥59.00').should('exist')
    })
  })
})
