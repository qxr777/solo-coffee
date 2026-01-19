# Solo Coffee 企业级应用系统 - 后端API规格说明书

## 1. 文档概述

### 1.1 文档目的
本文档旨在详细描述Solo Coffee企业级应用系统的API接口设计规格，包括API设计原则、接口定义、请求/响应格式、错误处理、认证授权等，为前后端开发团队提供清晰的API通信指导。

### 1.2 文档范围
本文档涵盖Solo Coffee系统的所有API接口设计，包括认证API、订单API、会员API、商品API、库存API、员工API、数据分析API、个性化推荐API等，确保前后端之间的高效、安全通信。

## 2. API通信架构

### 2.1 通信协议
- **协议选择**：HTTPS协议
- **端口**：443
- **加密方式**：TLS 1.3

### 2.2 API设计规范
- **RESTful风格**：使用HTTP动词表示资源操作
- **统一路径前缀**：所有API接口使用`/api/v1`路径前缀
- **版本控制**：通过URL路径进行API版本控制
- **统一响应格式**：所有API返回统一的响应格式
- **错误处理**：使用统一的错误码和错误消息

### 2.3 响应格式
所有API响应使用统一的JSON格式，包含以下字段：

```json
{
  "code": 200,
  "message": "请求成功",
  "data": {
    // 响应数据
  },
  "timestamp": 1678901234567,
  "requestId": "uuid-123456789"
}
```

| 字段名 | 类型 | 描述 |
|--------|------|------|
| code | Integer | 业务状态码，200表示成功，其他表示失败 |
| message | String | 响应消息，描述请求结果 |
| data | Object | 响应数据，具体内容根据API接口而定 |
| timestamp | Long | 响应时间戳，单位为毫秒 |
| requestId | String | 请求ID，用于追踪请求 |

### 2.4 错误处理

#### 2.4.1 HTTP状态码
| 状态码 | 描述 |
|--------|------|
| 200 | 请求成功 |
| 400 | 请求参数错误 |
| 401 | 未授权，需要登录 |
| 403 | 禁止访问，没有权限 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |
| 501 | 服务未实现 |
| 502 | 网关错误 |
| 503 | 服务不可用 |
| 504 | 网关超时 |

#### 2.4.2 业务错误码
| 错误码 | 描述 | HTTP状态码 |
|--------|------|------------|
| 200 | 请求成功 | 200 |
| 40001 | 请求参数错误 | 400 |
| 40002 | 验证失败 | 400 |
| 40101 | 未授权 | 401 |
| 40102 | 令牌过期 | 401 |
| 40103 | 令牌无效 | 401 |
| 40301 | 禁止访问 | 403 |
| 40401 | 资源不存在 | 404 |
| 50001 | 服务器内部错误 | 500 |
| 50002 | 数据库错误 | 500 |
| 50003 | 第三方服务错误 | 500 |

## 3. API请求配置

### 3.1 请求拦截器
请求拦截器用于在发送API请求前进行处理，包括添加认证信息、设置请求格式、添加请求标识等。

```javascript
// 请求拦截器配置
axios.interceptors.request.use(
  config => {
    // 添加认证信息
    const token = localStorage.getItem('token');
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`;
    }
    
    // 设置请求格式
    config.headers['Content-Type'] = 'application/json';
    
    // 添加请求标识
    config.headers['X-Request-Id'] = uuidv4();
    
    // 添加时间戳
    config.headers['X-Timestamp'] = Date.now();
    
    return config;
  },
  error => {
    return Promise.reject(error);
  }
);
```

### 3.2 响应拦截器
响应拦截器用于在接收API响应后进行处理，包括统一响应格式、统一错误处理、刷新令牌等。

```javascript
// 响应拦截器配置
axios.interceptors.response.use(
  response => {
    const { data } = response;
    
    // 统一响应格式处理
    if (data.code !== 200) {
      // 处理业务错误
      return Promise.reject(new Error(data.message || '请求失败'));
    }
    
    return data;
  },
  error => {
    // 统一错误处理
    if (error.response) {
      const { status, data } = error.response;
      
      switch (status) {
        case 401:
          // 处理未授权错误
          if (data.code === 40102) {
            // 令牌过期，刷新令牌
            return refreshToken(error);
          } else {
            // 其他未授权错误，跳转到登录页面
            router.push('/auth/login');
          }
          break;
        case 403:
          // 处理禁止访问错误
          message.error('没有权限访问该资源');
          break;
        case 404:
          // 处理资源不存在错误
          message.error('请求的资源不存在');
          break;
        case 500:
          // 处理服务器内部错误
          message.error('服务器内部错误，请稍后重试');
          break;
        default:
          message.error(data.message || '请求失败');
      }
    } else if (error.request) {
      // 请求已发送但没有收到响应
      message.error('网络错误，请检查网络连接');
    } else {
      // 请求配置错误
      message.error('请求配置错误');
    }
    
    return Promise.reject(error);
  }
);
```

## 4. 核心API接口

### 4.1 认证API

#### 4.1.1 登录
- **接口路径**：`/api/v1/auth/login`
- **方法**：`POST`
- **描述**：用户登录，获取认证令牌
- **请求参数**：
  | 参数名 | 类型 | 必需 | 描述 |
  |--------|------|------|------|
  | username | String | 是 | 用户名或手机号 |
  | password | String | 是 | 密码 |
  | rememberMe | Boolean | 否 | 是否记住登录状态 |

- **响应数据**：
  ```json
  {
    "token": "jwt-token",
    "refreshToken": "refresh-token",
    "expiresIn": 3600,
    "user": {
      "id": 1,
      "name": "张三",
      "phone": "13800138000",
      "role": "admin"
    }
  }
  ```

#### 4.1.2 刷新令牌
- **接口路径**：`/api/v1/auth/refresh`
- **方法**：`POST`
- **描述**：刷新认证令牌
- **请求参数**：
  | 参数名 | 类型 | 必需 | 描述 |
  |--------|------|------|------|
  | refreshToken | String | 是 | 刷新令牌 |

- **响应数据**：
  ```json
  {
    "token": "new-jwt-token",
    "refreshToken": "new-refresh-token",
    "expiresIn": 3600
  }
  ```

#### 4.1.3 登出
- **接口路径**：`/api/v1/auth/logout`
- **方法**：`POST`
- **描述**：用户登出，清除认证信息
- **请求参数**：无
- **响应数据**：`null`

#### 4.1.4 手机验证码登录
- **接口路径**：`/api/v1/auth/sms-login`
- **方法**：`POST`
- **描述**：使用手机验证码登录
- **请求参数**：
  | 参数名 | 类型 | 必需 | 描述 |
  |--------|------|------|------|
  | phone | String | 是 | 手机号 |
  | verificationCode | String | 是 | 验证码 |

- **响应数据**：
  ```json
  {
    "token": "jwt-token",
    "refreshToken": "refresh-token",
    "expiresIn": 3600,
    "user": {
      "id": 1,
      "name": "张三",
      "phone": "13800138000",
      "role": "customer"
    }
  }
  ```

#### 4.1.5 发送手机验证码
- **接口路径**：`/api/v1/auth/send-sms`
- **方法**：`POST`
- **描述**：发送手机验证码
- **请求参数**：
  | 参数名 | 类型 | 必需 | 描述 |
  |--------|------|------|------|
  | phone | String | 是 | 手机号 |
  | type | Integer | 是 | 验证码类型（1：登录，2：注册，3：密码重置） |

- **响应数据**：`null`

#### 4.1.6 第三方登录
- **接口路径**：`/api/v1/auth/oauth/{provider}`
- **方法**：`POST`
- **描述**：第三方登录（微信、Apple ID等）
- **请求参数**：
  | 参数名 | 类型 | 必需 | 描述 |
  |--------|------|------|------|
  | provider | String | 是 | 第三方登录提供商（wechat, apple等） |
  | code | String | 是 | 第三方登录授权码 |

- **响应数据**：
  ```json
  {
    "token": "jwt-token",
    "refreshToken": "refresh-token",
    "expiresIn": 3600,
    "user": {
      "id": 1,
      "name": "张三",
      "phone": "13800138000",
      "role": "customer"
    }
  }
  ```

### 4.2 订单API

#### 4.2.1 创建订单
- **接口路径**：`/api/v1/orders`
- **方法**：`POST`
- **描述**：创建新订单
- **请求参数**：
  ```json
  {
    "storeId": 1,
    "items": [
      {
        "productId": 1,
        "quantity": 2,
        "unitPrice": 32.00,
        "customizations": [
          {
            "optionId": 1,
            "value": "少糖"
          }
        ]
      },
      {
        "productId": 2,
        "quantity": 1,
        "unitPrice": 36.00
      }
    ],
    "totalAmount": 100.00,
    "actualAmount": 95.00,
    "paymentMethod": 1,
    "pickupTime": "2023-01-01T10:00:00Z",
    "addressId": 1,
    "isTakeout": true
  }
  ```

- **响应数据**：
  ```json
  {
    "id": 1,
    "orderNo": "SO202301010001",
    "orderStatus": 1,
    "qrCode": "https://example.com/qrcode/123"
  }
  ```

#### 4.2.2 查询订单详情
- **接口路径**：`/api/v1/orders/{orderId}`
- **方法**：`GET`
- **描述**：查询订单详情
- **请求参数**：
  | 参数名 | 类型 | 必需 | 描述 |
  |--------|------|------|------|
  | orderId | Long | 是 | 订单ID |

- **响应数据**：
  ```json
  {
    "id": 1,
    "orderNo": "SO202301010001",
    "customerId": 1,
    "storeId": 1,
    "totalAmount": 100.00,
    "actualAmount": 95.00,
    "paymentMethod": 1,
    "orderStatus": 3,
    "pickupTime": "2023-01-01T10:00:00Z",
    "createdAt": "2023-01-01T09:30:00Z",
    "updatedAt": "2023-01-01T10:15:00Z",
    "items": [
      {
        "id": 1,
        "orderId": 1,
        "productId": 1,
        "productName": "美式咖啡",
        "quantity": 2,
        "unitPrice": 32.00,
        "totalPrice": 64.00,
        "customizations": [
          {
            "optionName": "糖度",
            "value": "少糖"
          }
        ]
      },
      {
        "id": 2,
        "orderId": 1,
        "productId": 2,
        "productName": "拿铁咖啡",
        "quantity": 1,
        "unitPrice": 36.00,
        "totalPrice": 36.00
      }
    ],
    "store": {
      "id": 1,
      "name": "Solo Coffee旗舰店",
      "address": "北京市朝阳区建国路88号"
    },
    "qrCode": "https://example.com/qrcode/123"
  }
  ```

#### 4.2.3 查询订单列表
- **接口路径**：`/api/v1/orders`
- **方法**：`GET`
- **描述**：查询订单列表，支持分页和筛选
- **请求参数**：
  | 参数名 | 类型 | 必需 | 描述 |
  |--------|------|------|------|
  | page | Integer | 否 | 页码，默认1 |
  | size | Integer | 否 | 每页大小，默认10 |
  | startTime | String | 否 | 开始时间，格式：YYYY-MM-DD HH:mm:ss |
  | endTime | String | 否 | 结束时间，格式：YYYY-MM-DD HH:mm:ss |
  | orderStatus | Integer | 否 | 订单状态 |
  | storeId | Long | 否 | 门店ID |
  | customerId | Long | 否 | 顾客ID |

- **响应数据**：
  ```json
  {
    "total": 100,
    "pages": 10,
    "current": 1,
    "size": 10,
    "records": [
      {
        "id": 1,
        "orderNo": "SO202301010001",
        "customerId": 1,
        "customerName": "张三",
        "customerPhone": "13800138000",
        "storeId": 1,
        "storeName": "Solo Coffee旗舰店",
        "totalAmount": 100.00,
        "actualAmount": 95.00,
        "paymentMethod": 1,
        "orderStatus": 3,
        "pickupTime": "2023-01-01T10:00:00Z",
        "createdAt": "2023-01-01T09:30:00Z"
      }
      // 更多订单记录
    ]
  }
  ```

#### 4.2.4 更新订单状态
- **接口路径**：`/api/v1/orders/{orderId}/status`
- **方法**：`PUT`
- **描述**：更新订单状态
- **请求参数**：
  | 参数名 | 类型 | 必需 | 描述 |
  |--------|------|------|------|
  | orderId | Long | 是 | 订单ID |
  | status | Integer | 是 | 新订单状态 |

- **响应数据**：
  ```json
  {
    "id": 1,
    "orderNo": "SO202301010001",
    "orderStatus": 2,
    "updatedAt": "2023-01-01T09:45:00Z"
  }
  ```

#### 4.2.5 订单支付
- **接口路径**：`/api/v1/orders/{orderId}/pay`
- **方法**：`POST`
- **描述**：订单支付
- **请求参数**：
  | 参数名 | 类型 | 必需 | 描述 |
  |--------|------|------|------|
  | orderId | Long | 是 | 订单ID |
  | paymentMethod | Integer | 是 | 支付方式（1：微信支付，2：支付宝，3：Apple Pay） |
  | paymentChannel | String | 是 | 支付渠道（h5, app, pc等） |

- **响应数据**：
  ```json
  {
    "orderId": 1,
    "paymentUrl": "https://pay.example.com/123",
    "paymentParams": {
      "appId": "wx123456789",
      "timeStamp": "1234567890",
      "nonceStr": "abc123",
      "package": "prepay_id=123456789",
      "signType": "MD5",
      "paySign": "abcdef123456"
    },
    "status": "PAYING"
  }
  ```

#### 4.2.6 取消订单
- **接口路径**：`/api/v1/orders/{orderId}/cancel`
- **方法**：`POST`
- **描述**：取消订单
- **请求参数**：
  | 参数名 | 类型 | 必需 | 描述 |
  |--------|------|------|------|
  | orderId | Long | 是 | 订单ID |
  | reason | String | 否 | 取消原因 |

- **响应数据**：
  ```json
  {
    "id": 1,
    "orderNo": "SO202301010001",
    "orderStatus": 4,
    "updatedAt": "2023-01-01T09:45:00Z"
  }
  ```

### 4.3 门店API

#### 4.3.1 获取附近门店
- **接口路径**：`/api/v1/stores/nearby`
- **方法**：`GET`
- **描述**：获取附近门店
- **请求参数**：
  | 参数名 | 类型 | 必需 | 描述 |
  |--------|------|------|------|
  | latitude | Double | 是 | 纬度 |
  | longitude | Double | 是 | 经度 |
  | radius | Integer | 否 | 搜索半径（米），默认5000 |
  | page | Integer | 否 | 页码，默认1 |
  | size | Integer | 否 | 每页大小，默认10 |

- **响应数据**：
  ```json
  {
    "total": 10,
    "pages": 1,
    "current": 1,
    "size": 10,
    "records": [
      {
        "id": 1,
        "name": "Solo Coffee旗舰店",
        "address": "北京市朝阳区建国路88号",
        "phone": "010-12345678",
        "latitude": 39.9087,
        "longitude": 116.3975,
        "distance": 1200,
        "businessHours": "08:00-22:00",
        "status": 1,
        "isFavorite": true,
        "imageUrl": "https://example.com/images/store1.jpg"
      }
      // 更多门店记录
    ]
  }
  ```

#### 4.3.2 搜索门店
- **接口路径**：`/api/v1/stores/search`
- **方法**：`GET`
- **描述**：搜索门店
- **请求参数**：
  | 参数名 | 类型 | 必需 | 描述 |
  |--------|------|------|------|
  | keyword | String | 是 | 搜索关键词 |
  | latitude | Double | 否 | 纬度 |
  | longitude | Double | 否 | 经度 |
  | page | Integer | 否 | 页码，默认1 |
  | size | Integer | 否 | 每页大小，默认10 |

- **响应数据**：
  ```json
  {
    "total": 5,
    "pages": 1,
    "current": 1,
    "size": 10,
    "records": [
      {
        "id": 1,
        "name": "Solo Coffee旗舰店",
        "address": "北京市朝阳区建国路88号",
        "phone": "010-12345678",
        "latitude": 39.9087,
        "longitude": 116.3975,
        "distance": 1200,
        "businessHours": "08:00-22:00",
        "status": 1,
        "isFavorite": true,
        "imageUrl": "https://example.com/images/store1.jpg"
      }
      // 更多门店记录
    ]
  }
  ```

#### 4.3.3 获取门店详情
- **接口路径**：`/api/v1/stores/{storeId}`
- **方法**：`GET`
- **描述**：获取门店详情
- **请求参数**：
  | 参数名 | 类型 | 必需 | 描述 |
  |--------|------|------|------|
  | storeId | Long | 是 | 门店ID |

- **响应数据**：
  ```json
  {
    "id": 1,
    "name": "Solo Coffee旗舰店",
    "address": "北京市朝阳区建国路88号",
    "phone": "010-12345678",
    "latitude": 39.9087,
    "longitude": 116.3975,
    "businessHours": "08:00-22:00",
    "status": 1,
    "isFavorite": true,
    "imageUrl": "https://example.com/images/store1.jpg",
    "description": "Solo Coffee旗舰店提供高品质的咖啡和舒适的环境",
    "facilities": ["免费WiFi", "座位", "停车位"],
    "menuCategories": [
      {
        "id": 1,
        "name": "咖啡",
        "productCount": 10
      },
      {
        "id": 2,
        "name": "甜点",
        "productCount": 5
      }
    ]
  }
  ```

#### 4.3.4 收藏/取消收藏门店
- **接口路径**：`/api/v1/stores/{storeId}/favorite`
- **方法**：`POST`
- **描述**：收藏/取消收藏门店
- **请求参数**：
  | 参数名 | 类型 | 必需 | 描述 |
  |--------|------|------|------|
  | storeId | Long | 是 | 门店ID |
  | isFavorite | Boolean | 是 | 是否收藏（true：收藏，false：取消收藏） |

- **响应数据**：
  ```json
  {
    "storeId": 1,
    "isFavorite": true
  }
  ```

#### 4.3.5 获取收藏门店列表
- **接口路径**：`/api/v1/stores/favorites`
- **方法**：`GET`
- **描述**：获取收藏门店列表
- **请求参数**：
  | 参数名 | 类型 | 必需 | 描述 |
  |--------|------|------|------|
  | page | Integer | 否 | 页码，默认1 |
  | size | Integer | 否 | 每页大小，默认10 |

- **响应数据**：
  ```json
  {
    "total": 3,
    "pages": 1,
    "current": 1,
    "size": 10,
    "records": [
      {
        "id": 1,
        "name": "Solo Coffee旗舰店",
        "address": "北京市朝阳区建国路88号",
        "phone": "010-12345678",
        "latitude": 39.9087,
        "longitude": 116.3975,
        "distance": 1200,
        "businessHours": "08:00-22:00",
        "status": 1,
        "isFavorite": true,
        "imageUrl": "https://example.com/images/store1.jpg"
      }
      // 更多收藏门店记录
    ]
  }
  ```

### 4.4 会员API

#### 4.4.1 注册会员
- **接口路径**：`/api/v1/members`
- **方法**：`POST`
- **描述**：注册新会员
- **请求参数**：
  ```json
  {
    "name": "张三",
    "phone": "13800138000",
    "email": "zhangsan@example.com",
    "password": "password123"
  }
  ```

- **响应数据**：
  ```json
  {
    "id": 1,
    "memberNo": "MB202301010001",
    "name": "张三",
    "phone": "13800138000",
    "email": "zhangsan@example.com",
    "points": 0,
    "level": {
      "id": 1,
      "name": "普通会员",
      "minPoints": 0,
      "discountRate": 1.0
    },
    "createdAt": "2023-01-01T09:00:00Z"
  }
  ```

#### 4.4.2 查询会员详情
- **接口路径**：`/api/v1/members/{memberId}`
- **方法**：`GET`
- **描述**：查询会员详情
- **请求参数**：
  | 参数名 | 类型 | 必需 | 描述 |
  |--------|------|------|------|
  | memberId | Long | 是 | 会员ID |

- **响应数据**：
  ```json
  {
    "id": 1,
    "memberNo": "MB202301010001",
    "name": "张三",
    "phone": "13800138000",
    "email": "zhangsan@example.com",
    "points": 1000,
    "level": {
      "id": 2,
      "name": "黄金会员",
      "minPoints": 500,
      "discountRate": 0.9
    },
    "createdAt": "2023-01-01T09:00:00Z",
    "updatedAt": "2023-01-15T14:30:00Z",
    "lastVisitAt": "2023-01-15T14:30:00Z"
  }
  ```

#### 4.4.3 查询会员积分
- **接口路径**：`/api/v1/members/{memberId}/points`
- **方法**：`GET`
- **描述**：查询会员积分余额和积分记录
- **请求参数**：
  | 参数名 | 类型 | 必需 | 描述 |
  |--------|------|------|------|
  | memberId | Long | 是 | 会员ID |
  | page | Integer | 否 | 页码，默认1 |
  | size | Integer | 否 | 每页大小，默认10 |

- **响应数据**：
  ```json
  {
    "totalPoints": 1000,
    "records": [
      {
        "id": 1,
        "memberId": 1,
        "points": 100,
        "type": 1,
        "relatedId": 1,
        "description": "消费获得积分",
        "createdAt": "2023-01-15T14:30:00Z"
      }
      // 更多积分记录
    ]
  }
  ```

### 4.5 商品API

#### 4.5.1 创建商品
- **接口路径**：`/api/v1/products`
- **方法**：`POST`
- **描述**：创建新商品
- **请求参数**：
  ```json
  {
    "name": "美式咖啡",
    "description": "精选阿拉比卡咖啡豆，现磨现煮，口感醇厚",
    "price": 32.00,
    "imageUrl": "/images/coffee.jpg",
    "categoryId": 1,
    "status": 1
  }
  ```

- **响应数据**：
  ```json
  {
    "id": 1,
    "productNo": "PD202301010001",
    "name": "美式咖啡",
    "description": "精选阿拉比卡咖啡豆，现磨现煮，口感醇厚",
    "price": 32.00,
    "imageUrl": "/images/coffee.jpg",
    "categoryId": 1,
    "categoryName": "咖啡",
    "status": 1,
    "createdAt": "2023-01-01T09:00:00Z"
  }
  ```

#### 4.5.2 查询商品详情
- **接口路径**：`/api/v1/products/{productId}`
- **方法**：`GET`
- **描述**：查询商品详情
- **请求参数**：
  | 参数名 | 类型 | 必需 | 描述 |
  |--------|------|------|------|
  | productId | Long | 是 | 商品ID |

- **响应数据**：
  ```json
  {
    "id": 1,
    "productNo": "PD202301010001",
    "name": "美式咖啡",
    "description": "精选阿拉比卡咖啡豆，现磨现煮，口感醇厚",
    "price": 32.00,
    "imageUrl": "/images/coffee.jpg",
    "categoryId": 1,
    "categoryName": "咖啡",
    "status": 1,
    "createdAt": "2023-01-01T09:00:00Z",
    "updatedAt": "2023-01-10T15:30:00Z"
  }
  ```

#### 4.5.3 查询商品列表
- **接口路径**：`/api/v1/products`
- **方法**：`GET`
- **描述**：查询商品列表，支持分页和筛选
- **请求参数**：
  | 参数名 | 类型 | 必需 | 描述 |
  |--------|------|------|------|
  | page | Integer | 否 | 页码，默认1 |
  | size | Integer | 否 | 每页大小，默认10 |
  | categoryId | Long | 否 | 商品分类ID |
  | status | Integer | 否 | 商品状态 |
  | keyword | String | 否 | 搜索关键词 |

- **响应数据**：
  ```json
  {
    "total": 50,
    "pages": 5,
    "current": 1,
    "size": 10,
    "records": [
      {
        "id": 1,
        "productNo": "PD202301010001",
        "name": "美式咖啡",
        "price": 32.00,
        "imageUrl": "/images/coffee.jpg",
        "categoryId": 1,
        "categoryName": "咖啡",
        "status": 1
      }
      // 更多商品记录
    ]
  }
  ```

### 4.6 库存API

#### 4.6.1 查询库存详情
- **接口路径**：`/api/v1/inventory/{inventoryId}`
- **方法**：`GET`
- **描述**：查询库存详情
- **请求参数**：
  | 参数名 | 类型 | 必需 | 描述 |
  |--------|------|------|------|
  | inventoryId | Long | 是 | 库存ID |

- **响应数据**：
  ```json
  {
    "id": 1,
    "storeId": 1,
    "storeName": "Solo Coffee旗舰店",
    "rawMaterialId": 1,
    "rawMaterialName": "阿拉比卡咖啡豆",
    "quantity": 100,
    "warningThreshold": 20,
    "unit": "kg",
    "createdAt": "2023-01-01T09:00:00Z",
    "updatedAt": "2023-01-10T15:30:00Z",
    "lastPurchaseAt": "2023-01-05T10:00:00Z"
  }
  ```

#### 4.6.2 查询库存列表
- **接口路径**：`/api/v1/inventory`
- **方法**：`GET`
- **描述**：查询库存列表，支持分页和筛选
- **请求参数**：
  | 参数名 | 类型 | 必需 | 描述 |
  |--------|------|------|------|
  | page | Integer | 否 | 页码，默认1 |
  | size | Integer | 否 | 每页大小，默认10 |
  | storeId | Long | 否 | 门店ID |
  | rawMaterialId | Long | 否 | 原料ID |
  | warning | Boolean | 否 | 是否只查询预警库存 |

- **响应数据**：
  ```json
  {
    "total": 30,
    "pages": 3,
    "current": 1,
    "size": 10,
    "records": [
      {
        "id": 1,
        "storeId": 1,
        "storeName": "Solo Coffee旗舰店",
        "rawMaterialId": 1,
        "rawMaterialName": "阿拉比卡咖啡豆",
        "quantity": 100,
        "warningThreshold": 20,
        "unit": "kg",
        "isWarning": false
      }
      // 更多库存记录
    ]
  }
  ```

### 4.7 员工API

#### 4.7.1 创建员工
- **接口路径**：`/api/v1/employees`
- **方法**：`POST`
- **描述**：创建新员工
- **请求参数**：
  ```json
  {
    "name": "李四",
    "phone": "13900139000",
    "email": "lisi@example.com",
    "position": "咖啡师",
    "storeId": 1,
    "role": 3,
    "hireDate": "2023-01-01T09:00:00Z"
  }
  ```

- **响应数据**：
  ```json
  {
    "id": 1,
    "name": "李四",
    "phone": "13900139000",
    "email": "lisi@example.com",
    "position": "咖啡师",
    "storeId": 1,
    "storeName": "Solo Coffee旗舰店",
    "role": 3,
    "roleName": "咖啡师",
    "status": 1,
    "hireDate": "2023-01-01T09:00:00Z",
    "createdAt": "2023-01-01T09:00:00Z"
  }
  ```

#### 4.7.2 查询员工详情
- **接口路径**：`/api/v1/employees/{employeeId}`
- **方法**：`GET`
- **描述**：查询员工详情
- **请求参数**：
  | 参数名 | 类型 | 必需 | 描述 |
  |--------|------|------|------|
  | employeeId | Long | 是 | 员工ID |

- **响应数据**：
  ```json
  {
    "id": 1,
    "name": "李四",
    "phone": "13900139000",
    "email": "lisi@example.com",
    "position": "咖啡师",
    "storeId": 1,
    "storeName": "Solo Coffee旗舰店",
    "role": 3,
    "roleName": "咖啡师",
    "status": 1,
    "hireDate": "2023-01-01T09:00:00Z",
    "createdAt": "2023-01-01T09:00:00Z",
    "updatedAt": "2023-01-10T15:30:00Z"
  }
  ```

### 4.8 数据分析API

#### 4.8.1 查询销售数据
- **接口路径**：`/api/v1/analytics/sales`
- **方法**：`GET`
- **描述**：查询销售数据，支持时间范围和门店筛选
- **请求参数**：
  | 参数名 | 类型 | 必需 | 描述 |
  |--------|------|------|------|
  | startTime | String | 是 | 开始时间，格式：YYYY-MM-DD HH:mm:ss |
  | endTime | String | 是 | 结束时间，格式：YYYY-MM-DD HH:mm:ss |
  | storeId | Long | 否 | 门店ID |
  | granularity | String | 否 | 时间粒度：hour、day、week、month |

- **响应数据**：
  ```json
  {
    "totalAmount": 10000.00,
    "totalOrders": 100,
    "averageAmount": 100.00,
    "salesData": [
      {
        "time": "2023-01-01",
        "amount": 1500.00,
        "orders": 15
      }
      // 更多销售数据
    ],
    "topProducts": [
      {
        "productId": 1,
        "productName": "美式咖啡",
        "salesAmount": 3000.00,
        "salesQuantity": 100
      }
      // 更多热销商品
    ]
  }
  ```

### 4.9 个性化推荐API

#### 4.9.1 获取个性化推荐
- **接口路径**：`/api/v1/recommendations/personalized`
- **方法**：`GET`
- **描述**：获取基于用户历史消费和偏好的个性化咖啡推荐
- **请求参数**：
  | 参数名 | 类型 | 必需 | 描述 |
  |--------|------|------|------|
  | customerId | Long | 否 | 顾客ID |
  | limit | Integer | 否 | 推荐数量，默认10 |

- **响应数据**：
  ```json
  {
    "recommendations": [
      {
        "productId": 1,
        "productName": "美式咖啡",
        "imageUrl": "/images/coffee.jpg",
        "price": 32.00,
        "score": 0.95,
        "reason": "您经常在上午购买美式咖啡"
      },
      {
        "productId": 3,
        "productName": "拿铁咖啡",
        "imageUrl": "/images/latte.jpg",
        "price": 36.00,
        "score": 0.85,
        "reason": "您喜欢的美式咖啡的相似产品"
      }
      // 更多推荐
    ]
  }
  ```

#### 4.9.2 获取历史订单快速复购
- **接口路径**：`/api/v1/recommendations/quick-reorder`
- **方法**：`GET`
- **描述**：获取历史订单中最常购买的组合，支持快速复购
- **请求参数**：
  | 参数名 | 类型 | 必需 | 描述 |
  |--------|------|------|------|
  | customerId | Long | 否 | 顾客ID |
  | limit | Integer | 否 | 推荐数量，默认5 |

- **响应数据**：
  ```json
  {
    "quickReorderItems": [
      {
        "productId": 1,
        "productName": "美式咖啡",
        "quantity": 1,
        "unitPrice": 32.00,
        "customizations": [
          {
            "optionId": 1,
            "value": "少糖"
          }
        ]
      },
      {
        "productId": 5,
        "productName": "牛角包",
        "quantity": 1,
        "unitPrice": 12.00
      }
      // 更多快速复购选项
    ]
  }
  ```

#### 4.9.3 获取推荐理由
- **接口路径**：`/api/v1/recommendations/reason`
- **方法**：`GET`
- **描述**：获取特定产品推荐的理由
- **请求参数**：
  | 参数名 | 类型 | 必需 | 描述 |
  |--------|------|------|------|
  | customerId | Long | 否 | 顾客ID |
  | productId | Long | 是 | 产品ID |

- **响应数据**：
  ```json
  {
    "productId": 1,
    "reasons": ["您经常在上午购买美式咖啡", "您喜欢的咖啡类型"]
  }
  ```

### 4.10 智能预点单API

#### 4.10.1 获取订单预测
- **接口路径**：`/api/v1/prediction/predict`
- **方法**：`POST`
- **描述**：基于历史消费模式和当前时间，生成订单预测
- **请求参数**：
  ```json
  {
    "customerId": 1
  }
  ```

- **响应数据**：
  ```json
  {
    "predictedOrder": {
      "items": [
        {
          "productId": 1,
          "productName": "美式咖啡",
          "quantity": 1,
          "unitPrice": 32.00,
          "customizations": [
            {
              "optionId": 1,
              "value": "少糖"
            }
          ]
        }
      ],
      "totalAmount": 32.00,
      "predictionConfidence": 0.92,
      "reason": "您通常在周一上午8:30左右购买美式咖啡"
    },
    "predictionId": 1
  }
  ```

#### 4.10.2 一键确认预点单
- **接口路径**：`/api/v1/prediction/{predictionId}/confirm`
- **方法**：`POST`
- **描述**：一键确认并生成订单
- **请求参数**：
  | 参数名 | 类型 | 必需 | 描述 |
  |--------|------|------|------|
  | predictionId | Long | 是 | 预测订单ID |

- **响应数据**：
  ```json
  {
    "id": 10,
    "orderNo": "SO202301160001",
    "orderStatus": 1,
    "qrCode": "https://example.com/qrcode/10",
    "paymentUrl": "https://pay.example.com/10",
    "status": "confirmed"
  }
  ```

#### 4.10.3 获取预测订单列表
- **接口路径**：`/api/v1/prediction/orders`
- **方法**：`GET`
- **描述**：获取用户的预测订单列表
- **请求参数**：
  | 参数名 | 类型 | 必需 | 描述 |
  |--------|------|------|------|
  | customerId | Long | 否 | 顾客ID |
  | page | Integer | 否 | 页码，默认1 |
  | size | Integer | 否 | 每页大小，默认10 |

- **响应数据**：
  ```json
  {
    "predictions": [
      {
        "predictionId": 1,
        "predictedOrder": {
          "items": [
            {
              "productId": 1,
              "productName": "美式咖啡",
              "quantity": 1,
              "unitPrice": 32.00
            }
          ],
          "totalAmount": 32.00
        },
        "predictionConfidence": 0.92,
        "createdAt": "2023-01-16T08:00:00Z"
      }
      // 更多预测订单
    ],
    "total": 5,
    "page": 1,
    "size": 10,
    "totalPages": 1
  }
  ```

#### 4.10.4 取消预测订单
- **接口路径**：`/api/v1/prediction/{predictionId}/cancel`
- **方法**：`POST`
- **描述**：取消预测订单
- **请求参数**：
  | 参数名 | 类型 | 必需 | 描述 |
  |--------|------|------|------|
  | predictionId | Long | 是 | 预测订单ID |

- **响应数据**：
  ```json
  {
    "predictionId": 1,
    "status": "cancelled",
    "message": "预测订单取消成功"
  }
  ```

### 4.11 语音点单API

#### 4.11.1 语音点单
- **接口路径**：`/api/v1/voice/order`
- **方法**：`POST`
- **描述**：语音输入点单，将语音转换为订单信息
- **请求参数**：
  ```json
  {
    "voiceInput": "一杯少糖美式咖啡",
    "customerId": 1
  }
  ```

- **响应数据**：
  ```json
  {
    "success": true,
    "recognizedText": "一杯少糖美式咖啡",
    "orderItems": [
      {
        "productId": 1,
        "productName": "美式咖啡",
        "quantity": 1,
        "unitPrice": 32.00,
        "customizations": [
          {
            "optionId": 1,
            "value": "少糖"
          }
        ]
      }
    ],
    "totalAmount": 32.00,
    "confidence": 0.9,
    "message": "语音点单成功"
  }
  ```

#### 4.11.2 获取支持的语音命令
- **接口路径**：`/api/v1/voice/commands`
- **方法**：`GET`
- **描述**：获取系统支持的语音命令列表

- **响应数据**：
  ```json
  {
    "commands": [
      {
        "command": "一杯美式咖啡",
        "description": "点一杯美式咖啡"
      },
      {
        "command": "两杯拿铁，少糖",
        "description": "点两杯少糖拿铁"
      },
      {
        "command": "我的订单",
        "description": "查询我的订单"
      }
    ]
  }
  ```

### 4.12 订单评价API

#### 4.12.1 创建订单评价
- **接口路径**：`/api/v1/reviews`
- **方法**：`POST`
- **描述**：对订单进行评价
- **请求参数**：
  ```json
  {
    "orderId": 1,
    "rating": 5,
    "comment": "咖啡很好喝，服务很周到",
    "customerId": 1,
    "productReviews": [
      {
        "productId": 1,
        "rating": 5,
        "comment": "美式咖啡味道很正宗"
      }
    ]
  }
  ```

- **响应数据**：
  ```json
  {
    "success": true,
    "reviewId": 1,
    "orderId": 1,
    "rating": 5,
    "comment": "咖啡很好喝，服务很周到",
    "createdAt": "2023-01-16T11:00:00Z"
  }
  ```

#### 4.12.2 查询订单评价
- **接口路径**：`/api/v1/reviews/order/{orderId}`
- **方法**：`GET`
- **描述**：查询订单的评价信息
- **请求参数**：
  | 参数名 | 类型 | 必需 | 描述 |
  |--------|------|------|------|
  | orderId | Long | 是 | 订单ID |

- **响应数据**：
  ```json
  {
    "reviews": [
      {
        "reviewId": 1,
        "orderId": 1,
        "customerId": 1,
        "productId": 1,
        "rating": 5,
        "comment": "咖啡很好喝，服务很周到",
        "createdAt": "2023-01-16T11:00:00Z"
      }
    ]
  }
  ```

#### 4.12.3 查询用户评价列表
- **接口路径**：`/api/v1/reviews/customer/{customerId}`
- **方法**：`GET`
- **描述**：查询用户的评价列表
- **请求参数**：
  | 参数名 | 类型 | 必需 | 描述 |
  |--------|------|------|------|
  | customerId | Long | 是 | 用户ID |
  | page | Integer | 否 | 页码，默认1 |
  | size | Integer | 否 | 每页大小，默认10 |

- **响应数据**：
  ```json
  {
    "reviews": [
      {
        "reviewId": 1,
        "orderId": 1,
        "customerId": 1,
        "productId": 1,
        "rating": 5,
        "comment": "咖啡很好喝，服务很周到",
        "createdAt": "2023-01-16T11:00:00Z"
      }
    ],
    "total": 1,
    "page": 1,
    "size": 10,
    "totalPages": 1
  }
  ```

#### 4.12.4 删除评价
- **接口路径**：`/api/v1/reviews/{reviewId}`
- **方法**：`DELETE`
- **描述**：删除评价
- **请求参数**：
  | 参数名 | 类型 | 必需 | 描述 |
  |--------|------|------|------|
  | reviewId | Long | 是 | 评价ID |
  | customerId | Long | 否 | 用户ID（可选，用于验证权限） |

- **响应数据**：
  ```json
  {
    "success": true,
    "reviewId": 1,
    "message": "评价删除成功"
  }
  ```

### 4.13 优惠券API

#### 4.13.1 查询可用优惠券
- **接口路径**：`/api/v1/coupons/available`
- **方法**：`GET`
- **描述**：查询用户可用的优惠券列表
- **请求参数**：
  | 参数名 | 类型 | 必需 | 描述 |
  |--------|------|------|------|
  | storeId | Long | 否 | 门店ID，默认当前选择的门店 |

- **响应数据**：
  ```json
  {
    "coupons": [
      {
        "id": 1,
        "name": "满30减5元优惠券",
        "type": 1,
        "value": 5.00,
        "minAmount": 30.00,
        "validFrom": "2023-01-01T00:00:00Z",
        "validTo": "2023-01-31T23:59:59Z",
        "status": 1,
        "isUsable": true
      },
      {
        "id": 2,
        "name": "新用户立减10元",
        "type": 1,
        "value": 10.00,
        "minAmount": 20.00,
        "validFrom": "2023-01-01T00:00:00Z",
        "validTo": "2023-01-31T23:59:59Z",
        "status": 1,
        "isUsable": true
      }
      // 更多优惠券
    ]
  }
  ```

#### 4.13.2 查询优惠券详情
- **接口路径**：`/api/v1/coupons/{couponId}`
- **方法**：`GET`
- **描述**：查询优惠券的详细信息
- **请求参数**：
  | 参数名 | 类型 | 必需 | 描述 |
  |--------|------|------|------|
  | couponId | Long | 是 | 优惠券ID |

- **响应数据**：
  ```json
  {
    "id": 1,
    "name": "满30减5元优惠券",
    "type": 1,
    "value": 5.00,
    "minAmount": 30.00,
    "validFrom": "2023-01-01T00:00:00Z",
    "validTo": "2023-01-31T23:59:59Z",
    "status": 1,
    "isUsable": true,
    "usageRules": "单笔消费满30元可使用",
    "applicableStores": [
      {
        "id": 1,
        "name": "Solo Coffee旗舰店"
      }
    ]
  }
  ```

### 4.14 地址管理API

#### 4.14.1 查询地址列表
- **接口路径**：`/api/v1/addresses`
- **方法**：`GET`
- **描述**：查询用户的收货地址列表
- **请求参数**：无

- **响应数据**：
  ```json
  {
    "addresses": [
      {
        "id": 1,
        "name": "张三",
        "phone": "13800138000",
        "province": "北京市",
        "city": "北京市",
        "district": "朝阳区",
        "detail": "建国路88号SOHO现代城A座1001室",
        "latitude": 39.9087,
        "longitude": 116.3975,
        "isDefault": true
      },
      {
        "id": 2,
        "name": "张三",
        "phone": "13800138000",
        "province": "北京市",
        "city": "北京市",
        "district": "海淀区",
        "detail": "中关村大街1号海龙大厦1501室",
        "latitude": 39.9840,
        "longitude": 116.3075,
        "isDefault": false
      }
      // 更多地址
    ]
  }
  ```

#### 4.14.2 创建地址
- **接口路径**：`/api/v1/addresses`
- **方法**：`POST`
- **描述**：创建新的收货地址
- **请求参数**：
  ```json
  {
    "name": "张三",
    "phone": "13800138000",
    "province": "北京市",
    "city": "北京市",
    "district": "朝阳区",
    "detail": "建国路88号SOHO现代城A座1001室",
    "latitude": 39.9087,
    "longitude": 116.3975,
    "isDefault": true
  }
  ```

- **响应数据**：
  ```json
  {
    "id": 3,
    "name": "张三",
    "phone": "13800138000",
    "province": "北京市",
    "city": "北京市",
    "district": "朝阳区",
    "detail": "建国路88号SOHO现代城A座1001室",
    "latitude": 39.9087,
    "longitude": 116.3975,
    "isDefault": true,
    "createdAt": "2023-01-16T11:30:00Z"
  }
  ```

#### 4.14.3 更新地址
- **接口路径**：`/api/v1/addresses/{addressId}`
- **方法**：`PUT`
- **描述**：更新收货地址
- **请求参数**：
  ```json
  {
    "name": "张三",
    "phone": "13800138000",
    "province": "北京市",
    "city": "北京市",
    "district": "朝阳区",
    "detail": "建国路88号SOHO现代城A座1002室",
    "latitude": 39.9087,
    "longitude": 116.3975,
    "isDefault": true
  }
  ```

- **响应数据**：
  ```json
  {
    "id": 1,
    "name": "张三",
    "phone": "13800138000",
    "province": "北京市",
    "city": "北京市",
    "district": "朝阳区",
    "detail": "建国路88号SOHO现代城A座1002室",
    "latitude": 39.9087,
    "longitude": 116.3975,
    "isDefault": true,
    "updatedAt": "2023-01-16T11:45:00Z"
  }
  ```

#### 4.14.4 删除地址
- **接口路径**：`/api/v1/addresses/{addressId}`
- **方法**：`DELETE`
- **描述**：删除收货地址
- **请求参数**：
  | 参数名 | 类型 | 必需 | 描述 |
  |--------|------|------|------|
  | addressId | Long | 是 | 地址ID |

- **响应数据**：`null`

### 4.15 消息通知API

#### 4.15.1 查询通知列表
- **接口路径**：`/api/v1/notifications`
- **方法**：`GET`
- **描述**：查询用户的消息通知列表
- **请求参数**：
  | 参数名 | 类型 | 必需 | 描述 |
  |--------|------|------|------|
  | page | Integer | 否 | 页码，默认1 |
  | size | Integer | 否 | 每页大小，默认10 |
  | status | Integer | 否 | 通知状态（1：未读，2：已读） |
  | type | Integer | 否 | 通知类型（1：订单通知，2：促销通知，3：积分通知） |

- **响应数据**：
  ```json
  {
    "total": 10,
    "pages": 1,
    "current": 1,
    "size": 10,
    "records": [
      {
        "id": 1,
        "title": "订单已完成",
        "content": "您的订单SO202301160001已完成制作，请前往门店取餐",
        "type": 1,
        "status": 1,
        "createdAt": "2023-01-16T12:00:00Z"
      },
      {
        "id": 2,
        "title": "积分到账",
        "content": "您的订单SO202301160001获得32积分",
        "type": 3,
        "status": 2,
        "createdAt": "2023-01-16T11:30:00Z"
      }
      // 更多通知
    ]
  }
  ```

#### 4.15.2 标记通知为已读
- **接口路径**：`/api/v1/notifications/{notificationId}/read`
- **方法**：`PUT`
- **描述**：标记单个通知为已读
- **请求参数**：
  | 参数名 | 类型 | 必需 | 描述 |
  |--------|------|------|------|
  | notificationId | Long | 是 | 通知ID |

- **响应数据**：
  ```json
  {
    "id": 1,
    "status": 2,
    "updatedAt": "2023-01-16T12:30:00Z"
  }
  ```

#### 4.15.3 标记所有通知为已读
- **接口路径**：`/api/v1/notifications/read-all`
- **方法**：`PUT`
- **描述**：标记所有通知为已读
- **请求参数**：无

- **响应数据**：
  ```json
  {
    "updatedCount": 5
  }
  ```

## 5. 安全设计

### 5.1 认证与授权
- **认证方式**：基于JWT的无状态认证
- **授权方式**：基于RBAC的细粒度权限控制
- **登录方式**：支持手机号验证码登录、微信登录等多种方式

### 5.2 数据安全
- **数据加密**：敏感数据加密存储（如会员手机号、地址等）
- **密码安全**：使用BCrypt算法对密码进行哈希处理
- **传输安全**：所有API接口使用HTTPS协议

### 5.3 接口安全
- **API签名**：对外部API请求进行签名验证
- **接口限流**：使用Spring Cloud Gateway实现接口限流
- **防SQL注入**：使用参数化查询防止SQL注入攻击
- **防XSS攻击**：对用户输入进行XSS过滤

## 6. API调用示例

### 6.1 认证API调用示例

#### 6.1.1 用户登录
```javascript
// 用户登录API调用示例
const login = async (username, password, rememberMe = false) => {
  try {
    const response = await axios.post('/api/v1/auth/login', {
      username,
      password,
      rememberMe
    });
    
    // 保存认证信息
    localStorage.setItem('token', response.data.token);
    localStorage.setItem('refreshToken', response.data.refreshToken);
    localStorage.setItem('user', JSON.stringify(response.data.user));
    
    return response.data;
  } catch (error) {
    console.error('登录失败:', error);
    throw error;
  }
};

// 调用登录API
login('13800138000', 'password123', true)
  .then(data => {
    console.log('登录成功:', data);
    // 跳转到首页
    router.push('/');
  })
  .catch(error => {
    message.error('登录失败，请检查用户名和密码');
  });
```

### 6.2 订单API调用示例

#### 6.2.1 查询订单列表
```javascript
// 查询订单列表API调用示例
const getOrderList = async (params = {}) => {
  try {
    const response = await axios.get('/api/v1/orders', {
      params: {
        page: 1,
        size: 10,
        ...params
      }
    });
    
    return response.data;
  } catch (error) {
    console.error('查询订单列表失败:', error);
    throw error;
  }
};

// 调用查询订单列表API
const fetchOrders = async () => {
  try {
    const orders = await getOrderList({
      startTime: '2023-01-01 00:00:00',
      endTime: '2023-01-31 23:59:59',
      storeId: 1
    });
    
    console.log('订单列表:', orders);
  } catch (error) {
    message.error('查询订单列表失败');
  }
};
```
