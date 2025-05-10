# Dating AI 接口设计文档

## 1. 用户认证与管理

### 1.1 登录接口

**请求**
```
POST /api/v1/auth/login
Content-Type: application/json

{
  "code": "微信登录授权code",
  "platform": "WECHAT_MINI_PROGRAM"
}
```

**响应**
```json
{
  "code": "000000",
  "msg": "成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "expiresIn": 3600,
    "userId": "user123456",
    "newUser": false
  }
}
```

### 1.2 刷新Token接口

**请求**
```
POST /api/v1/auth/refresh
Content-Type: application/json

{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**响应**
```json
{
  "code": "000000",
  "msg": "成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "expiresIn": 3600
  }
}
```

### 1.3 获取用户信息接口

**请求**
```
GET /api/v1/user/profile
Authorization: Bearer {token}
```

**响应**
```json
{
  "code": "000000",
  "msg": "成功",
  "data": {
    "userId": "user123456",
    "nickname": "用户昵称",
    "avatar": "https://example.com/avatars/user123.jpg",
    "gender": "male",
    "phone": "137****8910",
    "membershipInfo": {
      "level": 1,
      "isActive": true,
      "expireDate": "2023-12-31T23:59:59Z",
      "daysRemaining": 127,
      "autoRenew": true,
      "benefits": ["无限次数", "全部风格", "高级分析"]
    },
    "statistics": {
      "totalUsage": 365,
      "favoriteStyles": ["幽默风趣", "浪漫甜蜜"],
      "successRate": 78
    },
    "settings": {
      "notification": true,
      "privacy": "standard",
      "chatHistoryRetention": 30,
      "language": "zh-CN"
    }
  }
}
```

### 1.4 更新用户信息接口

**请求**
```
PUT /api/v1/user/profile
Content-Type: application/json
Authorization: Bearer {token}

{
  "nickname": "新昵称",
  "avatar": "base64编码的头像图片", // 可选
  "gender": "female"
}
```

**响应**
```json
{
  "code": "000000",
  "msg": "成功",
  "data": {
    "userId": "user123456",
    "nickname": "新昵称",
    "avatar": "https://example.com/avatars/user123_new.jpg",
    "gender": "female",
    "updatedAt": "2023-06-18T10:25:30Z"
  }
}
```

### 1.5 更新用户设置接口

**请求**
```
PUT /api/v1/user/settings
Content-Type: application/json
Authorization: Bearer {token}

{
  "notification": false,
  "privacy": "strict",
  "chatHistoryRetention": 90,
  "language": "en-US"
}
```

**响应**
```json
{
  "code": "000000",
  "msg": "成功",
  "data": {
    "settings": {
      "notification": false,
      "privacy": "strict",
      "chatHistoryRetention": 90,
      "language": "en-US"
    },
    "updatedAt": "2023-06-18T11:30:15Z"
  }
}
```

## 2. 会话和消息管理

### 2.1 获取会话列表接口

**请求**
```
GET /api/v1/conversations
?page={pageNumber}
&size={pageSize}
&tag={tagId}
&lastUpdated={timestamp}
&sortBy={field}
&sortOrder={asc|desc}
Authorization: Bearer {token}
```

**响应**
```json
{
  "code": "000000",
  "msg": "成功",
  "data": {
    "conversations": [
      {
        "conversationId": "conv123",
        "contactName": "张三",
        "contactAvatar": "https://example.com/avatars/zhangsan.jpg",
        "lastMessage": {
          "content": "明天有空一起吃饭吗？",
          "type": "received",
          "timestamp": "2023-06-15T14:30:45Z"
        },
        "unreadCount": 0,
        "tags": [
          {
            "tagId": "tag1",
            "name": "朋友",
            "color": "#4287f5"
          }
        ],
        "isStarred": true,
        "messageCount": 24,
        "updatedAt": "2023-06-15T14:30:45Z"
      }
    ],
    "pagination": {
      "currentPage": 1,
      "pageSize": 20,
      "totalItems": 38,
      "totalPages": 2
    }
  }
}
```

### 2.2 获取会话消息接口

**请求**
```
GET /api/v1/conversations/{conversationId}/messages
?page={pageNumber}
&size={pageSize}
&before={messageId}
&after={messageId}
Authorization: Bearer {token}
```

**响应**
```json
{
  "code": "000000",
  "msg": "成功",
  "data": {
    "conversationInfo": {
      "conversationId": "conv123",
      "contactName": "张三",
      "contactAvatar": "https://example.com/avatars/zhangsan.jpg",
      "relationship": "friend",
      "lastActiveTime": "2023-06-15T14:30:45Z",
      "tags": [
        {
          "tagId": "tag1",
          "name": "朋友",
          "color": "#4287f5"
        }
      ],
      "isStarred": true
    },
    "messages": [
      {
        "messageId": "msg123",
        "type": "received",
        "content": "你好，最近怎么样？",
        "timestamp": "2023-06-15T14:25:12Z",
        "status": "read",
        "metadata": {
          "platform": "wechat"
        }
      },
      {
        "messageId": "msg124",
        "type": "generated",
        "content": "嗨！最近挺好的，工作比较忙。你呢？有什么新鲜事吗？",
        "timestamp": "2023-06-15T14:26:30Z",
        "style": "casual",
        "status": "generated"
      }
    ],
    "pagination": {
      "hasMore": true,
      "oldestMessageId": "msg123",
      "newestMessageId": "msg124",
      "totalInRange": 2
    }
  }
}
```

### 2.3 创建新会话接口

**请求**
```
POST /api/v1/conversations
Content-Type: application/json
Authorization: Bearer {token}

{
  "contactName": "王五",
  "contactAvatar": "https://example.com/avatars/wangwu.jpg",
  "relationship": "colleague",
  "metadata": {
    "platform": "wechat",
    "contactId": "wxid_12345"
  },
  "tags": ["tag2"]
}
```

**响应**
```json
{
  "code": "000000",
  "msg": "成功",
  "data": {
    "conversationId": "conv125",
    "contactName": "王五",
    "contactAvatar": "https://example.com/avatars/wangwu.jpg",
    "createdAt": "2023-06-16T10:00:15Z",
    "tags": [
      {
        "tagId": "tag2",
        "name": "工作",
        "color": "#f54242"
      }
    ]
  }
}
```

### 2.4 添加消息接口

**请求**
```
POST /api/v1/conversations/{conversationId}/messages
Content-Type: application/json
Authorization: Bearer {token}

{
  "content": "你好，我想问一下关于项目进度的事情",
  "type": "received",
  "timestamp": "2023-06-16T09:45:30Z",
  "metadata": {
    "platform": "wechat",
    "messageFormat": "text"
  }
}
```

**响应**
```json
{
  "code": "000000",
  "msg": "成功",
  "data": {
    "messageId": "msg127",
    "status": "stored",
    "timestamp": "2023-06-16T09:45:30Z",
    "conversationId": "conv123",
    "recommendation": {
      "suggestedStyles": ["professional", "friendly"],
      "emotionAnalysis": "neutral"
    }
  }
}
```

### 2.5 管理会话标签接口

**请求**
```
POST /api/v1/conversations/{conversationId}/tags
Content-Type: application/json
Authorization: Bearer {token}

{
  "action": "add",  // add, remove, set
  "tags": ["tag1", "tag3"]
}
```

**响应**
```json
{
  "code": "000000",
  "msg": "成功",
  "data": {
    "conversationId": "conv123",
    "tags": [
      {
        "tagId": "tag1",
        "name": "朋友",
        "color": "#4287f5"
      },
      {
        "tagId": "tag3",
        "name": "重要",
        "color": "#42f5a7"
      }
    ],
    "updatedAt": "2023-06-16T10:30:20Z"
  }
}
```

### 2.6 删除会话接口

**请求**
```
DELETE /api/v1/conversations/{conversationId}
Authorization: Bearer {token}
```

**响应**
```json
{
  "code": "000000",
  "msg": "成功",
  "data": {
    "conversationId": "conv123",
    "deletedAt": "2023-06-16T11:15:45Z"
  }
}
```

## 3. AI回复生成

### 3.1 生成回复接口

**请求**
```
POST /api/v1/assistant/generate
Content-Type: application/json
Authorization: Bearer {token}

{
  "conversationId": "conv123",
  "receivedMessage": "你好，周末有空一起去看电影吗？",
  "styles": ["casual", "humorous", "romantic"],
  "previousMessages": [
    {
      "role": "received",
      "content": "嗨，最近怎么样？",
      "timestamp": "2023-06-15T14:10:30Z"
    },
    {
      "role": "sent",
      "content": "还不错，工作有点忙。你呢？",
      "timestamp": "2023-06-15T14:15:45Z"
    }
  ],
  "context": {
    "relationshipStage": "getting_to_know",
    "receiverGender": "female",
    "receiverAgeRange": "20s"
  }
}
```

**响应**
```json
{
  "code": "000000",
  "msg": "成功",
  "data": {
    "requestId": "req-7890",
    "replies": [
      {
        "replyId": "reply-1234",
        "styleId": "casual",
        "styleName": "随性自然",
        "content": "周末确实有空，一起看电影听起来不错！有什么想看的片子吗？",
        "metadata": {
          "wordCount": 25,
          "emotionalIndex": 70
        },
        "quality": {
          "relevanceScore": 0.92,
          "styleMatchScore": 0.88,
          "totalScore": 0.90
        }
      },
      {
        "replyId": "reply-1235",
        "styleId": "humorous",
        "styleName": "幽默风趣",
        "content": "哇！电影约会邀请！我的爆米花瘾已经开始犯了。😁 你有什么片子推荐吗？或者我们要不要来场"随机电影冒险"？",
        "metadata": {
          "wordCount": 42,
          "emotionalIndex": 85
        },
        "quality": {
          "relevanceScore": 0.88,
          "styleMatchScore": 0.95,
          "totalScore": 0.91
        }
      },
      {
        "replyId": "reply-1236",
        "styleId": "romantic",
        "styleName": "浪漫甜蜜",
        "content": "和你一起看电影的周末，听起来已经很完美了✨ 我很期待见到你。你有什么想看的电影类型吗？",
        "metadata": {
          "wordCount": 38,
          "emotionalIndex": 90
        },
        "quality": {
          "relevanceScore": 0.90,
          "styleMatchScore": 0.94,
          "totalScore": 0.92
        }
      }
    ],
    "analysis": {
      "messageType": "invitation",
      "emotion": "positive",
      "interestLevel": "high",
      "topics": ["weekend", "movie", "date"],
      "suggestedResponseTone": "enthusiastic"
    },
    "generationTime": 1260  // 毫秒
  }
}
```

### 3.2 获取回复风格列表接口

**请求**
```
GET /api/v1/styles
?category={category}
&isVipOnly={true|false}
Authorization: Bearer {token}
```

**响应**
```json
{
  "code": "000000",
  "msg": "成功",
  "data": {
    "styles": [
      {
        "styleId": "casual",
        "name": "随性自然",
        "description": "轻松自然的对话风格，像老朋友一样交流",
        "longDescription": "随性自然风格模拟日常轻松交流的语言特点，语气亲切，像与熟悉的朋友交谈一样，没有过多修饰，直接而真诚。",
        "tags": ["日常", "轻松", "通用"],
        "category": "基础风格",
        "popularity": 4.8,
        "isVipOnly": false,
        "iconUrl": "https://example.com/styles/casual.png",
        "examples": [
          {
            "receivedMessage": "最近工作怎么样？",
            "replyExample": "还行吧，有点忙但能应付。你那边怎么样？"
          }
        ]
      },
      {
        "styleId": "romantic",
        "name": "浪漫甜蜜",
        "description": "充满柔情和甜蜜的表达，适合暧昧关系",
        "longDescription": "浪漫甜蜜风格充满感性和柔情的表达方式，善用比喻和温柔的措辞，传递情感连接和亲密感，适合提升关系温度。",
        "tags": ["约会", "情感", "亲密"],
        "category": "约会风格",
        "popularity": 4.9,
        "isVipOnly": true,
        "iconUrl": "https://example.com/styles/romantic.png",
        "examples": [
          {
            "receivedMessage": "今天的夕阳真美",
            "replyExample": "没有你在我身边欣赏，再美的夕阳也失去了一半魅力。真希望此刻能和你一起分享这美景✨"
          }
        ]
      }
    ],
    "categories": [
      {
        "id": "basic",
        "name": "基础风格",
        "count": 3
      },
      {
        "id": "dating",
        "name": "约会风格",
        "count": 5
      }
    ]
  }
}
```

### 3.3 回复反馈接口

**请求**
```
POST /api/v1/assistant/feedback
Content-Type: application/json
Authorization: Bearer {token}

{
  "replyId": "reply-1236",
  "feedbackType": "like",  // like, dislike, copy, used
  "comment": "这个回复很适合我的风格" // 可选
}
```

**响应**
```json
{
  "code": "000000",
  "msg": "成功",
  "data": {
    "replyId": "reply-1236",
    "feedbackSaved": true,
    "recommendations": {
      "similarStyles": ["sweet", "gentle"],
      "improvedStyles": ["casual_romantic"]
    }
  }
}
```

## 4. 会员与支付

### 4.1 获取会员方案接口

**请求**
```
GET /api/v1/membership/plans
?platform={platform}
Authorization: Bearer {token}
```

**响应**
```json
{
  "code": "000000",
  "msg": "成功",
  "data": {
    "currentPlan": {
      "level": 0,
      "expireDate": null,
      "autoRenew": false
    },
    "plans": [
      {
        "planId": "monthly",
        "planName": "月度会员",
        "planDescription": "解锁全部高级功能，每月自动续费",
        "price": 28.00,
        "originalPrice": 38.00,
        "duration": 30,
        "durationType": "day",
        "planType": "vip",
        "isRecommended": false,
        "features": [
          "无限回复生成",
          "全部回复风格",
          "高级情感分析"
        ]
      },
      {
        "planId": "yearly",
        "planName": "年度会员",
        "planDescription": "解锁全部高级功能，年度付费，超值优惠",
        "price": 198.00,
        "originalPrice": 298.00,
        "duration": 365,
        "durationType": "day",
        "planType": "vip",
        "isRecommended": true,
        "features": [
          "无限回复生成",
          "全部回复风格",
          "高级情感分析",
          "优先客服支持",
          "专属新功能抢先体验"
        ]
      }
    ],
    "promotions": [
      {
        "promotionId": "newyear2023",
        "title": "新年特惠",
        "description": "新年限时8折优惠",
        "discountType": "percentage",
        "discountValue": 20,
        "startTime": "2023-01-01T00:00:00Z",
        "endTime": "2023-01-15T23:59:59Z"
      }
    ]
  }
}
```

### 4.2 创建订单接口

**请求**
```
POST /api/v1/orders
Content-Type: application/json
Authorization: Bearer {token}

{
  "planId": "yearly",
  "paymentMethod": "wechat",
  "couponCode": "WELCOME20",  // 可选
  "promotionId": "newyear2023", // 可选
  "autoRenew": true
}
```

**响应**
```json
{
  "code": "000000",
  "msg": "成功",
  "data": {
    "orderId": "order123456",
    "amount": 198.00,
    "discountAmount": 39.60,
    "finalAmount": 158.40,
    "planId": "yearly",
    "planName": "年度会员",
    "orderStatus": "created",
    "paymentInfo": {
      "method": "wechat",
      "appId": "wx123456789",
      "timeStamp": "1623498765",
      "nonceStr": "abcdefghijk",
      "package": "prepay_id=wx12345678901234567890123456",
      "signType": "MD5",
      "paySign": "ABCDEFGHIJKLMNOPQRSTUVWXYZ123456"
    },
    "createTime": "2023-06-16T15:30:45Z",
    "expireTime": "2023-06-16T15:45:45Z"
  }
}
```

### 4.3 查询订单状态接口

**请求**
```
GET /api/v1/orders/{orderId}
Authorization: Bearer {token}
```

**响应**
```json
{
  "code": "000000",
  "msg": "成功",
  "data": {
    "orderId": "order123456",
    "orderStatus": "paid",
    "paymentTime": "2023-06-16T15:32:10Z",
    "membershipInfo": {
      "planId": "yearly",
      "planName": "年度会员",
      "startTime": "2023-06-16T15:32:10Z",
      "expireTime": "2024-06-16T15:32:10Z",
      "autoRenew": true
    }
  }
}
```

### 4.4 取消自动续费接口

**请求**
```
POST /api/v1/membership/cancel-renewal
Content-Type: application/json
Authorization: Bearer {token}

{
  "cancelReason": "价格过高" // 可选
}
```

**响应**
```json
{
  "code": "000000",
  "msg": "成功",
  "data": {
    "membershipInfo": {
      "planId": "yearly",
      "planName": "年度会员",
      "autoRenew": false,
      "expireTime": "2024-06-16T15:32:10Z",
      "willExpire": true
    },
    "cancelTime": "2023-06-18T10:20:30Z"
  }
}
```

## 5. 使用统计与限制

### 5.1 获取用户使用统计接口

**请求**
```
GET /api/v1/user/usage-stats
?period={day|week|month|all}
Authorization: Bearer {token}
```

**响应**
```json
{
  "code": "000000",
  "msg": "成功",
  "data": {
    "totalGeneration": 256,
    "totalCopied": 128,
    "totalLiked": 98,
    "totalDisliked": 12,
    "usedStyles": [
      {
        "styleId": "casual",
        "name": "随性自然",
        "count": 65,
        "percentage": 25.4
      },
      {
        "styleId": "romantic",
        "name": "浪漫甜蜜",
        "count": 58,
        "percentage": 22.7
      }
    ],
    "dailyLimit": {
      "limit": 20,
      "used": 3,
      "remaining": 17,
      "resetTime": "2023-06-17T00:00:00Z"
    },
    "periodicStats": [
      {
        "date": "2023-06-10",
        "generation": 12,
        "copied": 8,
        "liked": 6
      },
      {
        "date": "2023-06-11",
        "generation": 15,
        "copied": 10,
        "liked": 7
      }
    ]
  }
}
```

### 5.2 检查用户限制接口

**请求**
```
GET /api/v1/user/limits
Authorization: Bearer {token}
```

**响应**
```json
{
  "code": "000000",
  "msg": "成功",
  "data": {
    "dailyGeneration": {
      "limit": 20,
      "used": 3,
      "remaining": 17,
      "resetTime": "2023-06-17T00:00:00Z"
    },
    "accessibleStyles": {
      "total": 25,
      "accessible": 8,
      "vipOnly": 17
    },
    "conversationRetention": {
      "days": 30,
      "vipDays": 365
    },
    "vipUpgradeInfo": {
      "sampleBenefits": [
        "每日生成无限制",
        "解锁全部25种回复风格",
        "365天会话记录保存"
      ],
      "recommendedPlan": "yearly",
      "discount": "限时7折"
    }
  }
}
```

## 5. 错误码定义

| 错误码  | 描述                   | 说明                           |
|--------|------------------------|------------------------------|
| 000000 | 成功                   | 请求成功                       |
| 000400 | 请求参数错误           | 请求参数格式错误或缺少必要参数  |
| 000401 | 未授权                 | 用户未登录或token已过期        |
| 000403 | 权限不足               | 用户无权访问该资源             |
| 000404 | 资源不存在             | 请求的资源不存在               |
| 000429 | 请求过于频繁           | 超出API调用频率限制            |
| 000500 | 服务器内部错误         | 服务器处理请求时发生错误       |
| 000501 | AI服务暂时不可用       | AI生成服务暂时不可用           |
| 001001 | 会员权限不足           | 需要会员才能访问此功能         |
| 001002 | 使用次数已达上限       | 免费用户达到每日使用次数上限    |
| 002001 | 支付创建失败           | 创建支付订单失败               |
| 002002 | 订单已过期             | 支付订单已过期                 |
| 002003 | 重复支付               | 订单已支付，请勿重复支付       | 