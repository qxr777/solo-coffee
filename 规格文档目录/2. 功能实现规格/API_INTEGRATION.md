# Solo Coffee API 联调文档 v1.0

本文档旨在为 **Solo Coffee** 项目提供前后端 API 联调说明。

## 1. 基础信息 (Base Information)

*   **API 根路径**: `http://localhost:8090/api/v1`
*   **数据格式**: 请求与响应均使用 `application/json`。

### 统一响应格式 (Uniform Response Format)

```json
{
  "code": 200,          // 状态码 (200: 成功, 400: 请求错误, 500: 系统错误)
  "message": "操作成功", // 提示信息
  "data": { ... }       // 业务数据
}
```

### 认证方式 (Authentication)

除登录/注册接口外，所有请求需在 Header 中携带 JWT Token:
*   **Header**: `Authorization: Bearer <Your_Token>`

---

## 2. 核心模块 (Core Modules)

### 2.1 认证模块 (Authentication)

#### 1) 账号密码登录
*   **URL**: `/auth/login`
*   **Method**: `POST`
*   **Body**:
    ```json
    {
      "phone": "13800138000",
      "password": "password123"
    }
    ```
*   **Response**: 返回 `accessToken`, `refreshToken` 及用户信息。

#### 2) 验证码登录 (短信)
*   **URL**: `/auth/sms-login`
*   **Method**: `POST`
*   **Body**:
    ```json
    {
      "phone": "13800138000",
      "code": "123456" // 测试环境通用验证码: 123456
    }
    ```

#### 3) 发送验证码
*   **URL**: `/auth/send-sms`
*   **Method**: `POST`
*   **Body**: `{"phone": "13800138000"}`

---

### 2.2 商品与分类 (Products & Categories)

#### 1) 获取分类列表
*   **URL**: `/categories`
*   **Method**: `GET`

#### 2) 获取商品列表 (带分页与过滤)
*   **URL**: `/products`
*   **Method**: `GET`
*   **Query Params**:
    *   `page`: 页码 (默认 1)
    *   `size`: 每页数量 (默认 10)
    *   `keyword`: 搜索关键词
    *   `categoryId`: 分类 ID
    *   `status`: 状态 (1: 上架, 0: 下架)

#### 3) 商品详情
*   **URL**: `/products/{id}`
*   **Method**: `GET`

---

### 2.3 门店管理 (Stores)

#### 1) 获取附近门店
*   **URL**: `/stores/nearby`
*   **Method**: `GET`
*   **Query Params**: `latitude`, `longitude`, `radius` (米)

#### 2) 搜索门店
*   **URL**: `/stores/search`
*   **Method**: `GET`
*   **Query Param**: `keyword`

---

### 2.4 订单流程 (Order Lifecycle)

#### 1) 下单 (创建订单)
*   **URL**: `/orders`
*   **Method**: `POST`
*   **Body**:
    ```json
    {
      "storeId": 1,
      "orderItems": [
        {
          "productId": 1,
          "productName": "美式咖啡",
          "quantity": 2,
          "price": 28.00
        }
      ],
      "paymentMethod": 1, 
      "remarks": "去冰"
    }
    ```

#### 2) 获取订单列表
*   **URL**: `/orders`
*   **Method**: `GET`
*   **Query Params**: `page`, `size`, `orderStatus` (1: 待支付, 2: 制作中, 3: 待取餐, 4: 已完成, 5: 已取消)

#### 3) 支付订单
*   **URL**: `/orders/{id}/pay`
*   **Method**: `POST`
*   **Body**: `{"paymentMethod": 1, "paymentChannel": "wechat"}`

#### 4) 更新订单状态 (管理端使用)
*   **URL**: `/orders/{id}/status`
*   **Method**: `PUT`
*   **Body**: `{"status": 2}` // 2: 制作中

---

### 2.5 会员与积分 (Members & Points)

#### 1) 获取个人资料
*   **URL**: `/members/me` (或 `/members/{id}`)
*   **Method**: `GET`

#### 2) 获取积分记录
*   **URL**: `/members/{id}/points`
*   **Method**: `GET`

---

## 3. 错误码说明 (Error Codes)

| 错误码 | 描述 | 建议处理 |
| :--- | :--- | :--- |
| 401 | 未认证/Token 过期 | 跳转至登录页 |
| 403 | 权限不足 | 提示联系管理员 |
| 404 | 资源未找到 | 检查 ID 是否正确 |
| 400 | 参数错误 | 校验输入格式 |
| 500 | 服务器宕机 | 稍后重试 |

---
**Documented by Antigravity AI Engine**
