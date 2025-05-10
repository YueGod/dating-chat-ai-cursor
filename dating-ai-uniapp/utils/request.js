/**
 * 网络请求工具类
 * 统一处理请求、响应拦截、错误处理和Mock数据
 */

import store from '../store'
import { BASE_URL } from '../config'

// 请求状态码
const CODE = {
    SUCCESS: '000000',
    UNAUTHORIZED: '000401',
    FORBIDDEN: '000403',
    ERROR: '000500'
}

// 接口基础URL
const baseURL = BASE_URL

// 是否启用Mock数据（本地开发使用Mock数据，生产环境连接真实API）
const useMock = process.env.NODE_ENV === 'development'

/**
 * 统一请求方法
 * @param {Object} options - 请求配置
 * @param {string} options.url - 请求路径
 * @param {string} options.method - 请求方法，默认为get
 * @param {Object} options.data - 请求数据，用于post、put等方法
 * @param {Object} options.params - URL参数，用于get请求
 * @param {string} options.loading - 加载提示文本，不传则不显示加载提示
 * @returns {Promise} Promise对象
 */
const request = (options) => {
    const { url, method = 'get', data, params, loading } = options

    // 显示加载提示
    if (loading) {
        uni.showLoading({
            title: loading,
            mask: true
        })
    }

    // 如果启用了Mock并且存在对应的Mock处理函数，则使用Mock数据
    if (useMock && mockHandlers[url]) {
        return new Promise((resolve) => {
            // 模拟网络延迟
            setTimeout(() => {
                if (loading) uni.hideLoading()

                // 调用Mock处理函数获取数据
                const mockData = mockHandlers[url]({
                    method,
                    data,
                    params
                })

                // 返回统一格式的响应
                resolve({
                    code: '000000',
                    data: mockData,
                    msg: 'success'
                })
            }, 300) // 模拟300ms网络延迟
        })
    }

    // 实际请求
    return new Promise((resolve, reject) => {
        uni.request({
            url: baseURL + url,
            method: method.toUpperCase(),
            data: method.toLowerCase() === 'get' ? params : data,
            header: getHeaders(),
            success: (res) => {
                if (loading) uni.hideLoading()

                // 统一处理响应
                if (res.statusCode >= 200 && res.statusCode < 300) {
                    resolve(res.data)
                } else {
                    handleError(res)
                    reject(res)
                }
            },
            fail: (err) => {
                if (loading) uni.hideLoading()

                // 网络错误处理
                uni.showToast({
                    title: '网络请求失败',
                    icon: 'none'
                })
                reject(err)
            }
        })
    })
}

/**
 * 获取请求头
 * @returns {Object} 请求头对象
 */
const getHeaders = () => {
    const token = uni.getStorageSync('ylToken')

    return {
        'Content-Type': 'application/json',
        'Authorization': token ? `Bearer ${token}` : ''
    }
}

/**
 * 错误处理
 * @param {Object} res - 错误响应
 */
const handleError = (res) => {
    // 根据状态码处理不同错误
    switch (res.statusCode) {
        case 401:
            // 登录过期处理
            uni.showToast({
                title: '登录已过期，请重新登录',
                icon: 'none'
            })
            // 清除登录信息并跳转到登录页
            uni.removeStorageSync('ylToken')
            uni.removeStorageSync('userInfo')
            uni.navigateTo({
                url: '/pages/login/login'
            })
            break
        case 403:
            uni.showToast({
                title: '没有操作权限',
                icon: 'none'
            })
            break
        case 404:
            uni.showToast({
                title: '请求的资源不存在',
                icon: 'none'
            })
            break
        case 500:
        case 502:
        case 503:
            uni.showToast({
                title: '服务器异常，请稍后重试',
                icon: 'none'
            })
            break
        default:
            uni.showToast({
                title: res.data?.msg || '请求失败',
                icon: 'none'
            })
    }
}

/**
 * 初始化请求拦截器
 * 保留此函数以兼容现有代码
 */
export const setupInterceptors = () => {
    console.log('请求拦截器已配置')
    return {
        request: (config) => {
            // 请求拦截处理
            return config
        },
        response: (response) => {
            // 响应拦截处理
            return response
        }
    }
}

/**
 * GET请求简便方法
 * @param {String} url - 请求地址
 * @param {Object} params - URL参数
 * @param {Object} options - 其他选项
 * @returns {Promise} Promise对象
 */
export const get = (url, params = {}, options = {}) => {
    return request({
        url,
        method: 'GET',
        params,
        ...options
    })
}

/**
 * POST请求简便方法
 * @param {String} url - 请求地址
 * @param {Object} data - 请求数据
 * @param {Object} options - 其他选项
 * @returns {Promise} Promise对象
 */
export const post = (url, data = {}, options = {}) => {
    return request({
        url,
        method: 'POST',
        data,
        ...options
    })
}

/**
 * PUT请求简便方法
 * @param {String} url - 请求地址
 * @param {Object} data - 请求数据
 * @param {Object} options - 其他选项
 * @returns {Promise} Promise对象
 */
export const put = (url, data = {}, options = {}) => {
    return request({
        url,
        method: 'PUT',
        data,
        ...options
    })
}

/**
 * DELETE请求简便方法
 * @param {String} url - 请求地址
 * @param {Object} data - 请求数据
 * @param {Object} options - 其他选项
 * @returns {Promise} Promise对象
 */
export const del = (url, data = {}, options = {}) => {
    return request({
        url,
        method: 'DELETE',
        data,
        ...options
    })
}

// --------------------- Mock数据处理函数 ---------------------
// key为API路径，value为处理函数
const mockHandlers = {
    // 用户接口
    '/user/info': () => {
        return {
            id: '1001',
            nickname: '小恋爱',
            avatar: '/static/images/default-avatar.png',
            gender: 'female',
            birthday: '1995-01-01',
            phone: '138****1234',
            email: 'test@example.com',
            registrationDate: '2023-01-15T08:30:00.000Z',
            vip: {
                isVip: true,
                expireTime: new Date(Date.now() + 30 * 24 * 60 * 60 * 1000).toISOString(),
                level: 1
            },
            settings: {
                notification: true,
                privacy: 'standard',
                chatHistoryRetention: 30
            }
        }
    },

    // 会话列表
    '/conversations': ({ params }) => {
        const conversations = [
            {
                id: '1',
                contactName: '小红',
                avatar: '/static/images/default-avatar.png',
                lastMessage: '好的，明天见！',
                timestamp: Date.now() - 1000 * 60 * 30, // 30分钟前
                unread: 0,
                platform: 'manual'
            },
            {
                id: '2',
                contactName: '小明',
                avatar: '/static/images/default-avatar.png',
                lastMessage: '下次见面聊聊！',
                timestamp: Date.now() - 1000 * 60 * 60 * 2, // 2小时前
                unread: 2,
                platform: 'wechat'
            },
            {
                id: '3',
                contactName: '小李',
                avatar: '/static/images/default-avatar.png',
                lastMessage: '期待下次遇见~',
                timestamp: Date.now() - 1000 * 60 * 60 * 24, // 1天前
                unread: 0,
                platform: 'manual'
            }
        ]

        // 处理分页
        if (params && params.page && params.size) {
            const { page, size } = params
            const start = (page - 1) * size
            const end = start + size

            return {
                conversations: conversations.slice(start, end),
                pagination: {
                    total: conversations.length,
                    page,
                    size,
                    hasMore: end < conversations.length
                }
            }
        }

        return {
            conversations,
            pagination: {
                total: conversations.length,
                page: 1,
                size: conversations.length,
                hasMore: false
            }
        }
    },

    // 会话详情
    '/conversations/:id': ({ method, data }) => {
        // 使用正则提取ID
        const idMatch = /\/conversations\/([^\/]+)/.exec(method.url)
        const id = idMatch ? idMatch[1] : '1'

        const conversations = {
            '1': {
                id: '1',
                contactName: '小红',
                avatar: '/static/images/default-avatar.png',
                platform: 'manual',
                createdAt: '2023-05-01T10:30:00.000Z'
            },
            '2': {
                id: '2',
                contactName: '小明',
                avatar: '/static/images/default-avatar.png',
                platform: 'wechat',
                createdAt: '2023-05-02T14:20:00.000Z'
            },
            '3': {
                id: '3',
                contactName: '小李',
                avatar: '/static/images/default-avatar.png',
                platform: 'manual',
                createdAt: '2023-05-03T09:15:00.000Z'
            }
        }

        return conversations[id] || conversations['1']
    },

    // 消息列表
    '/conversations/:id/messages': ({ params }) => {
        const mockMessages = []
        const now = Date.now()

        // 生成模拟消息数据，三种类型：接收、发送、AI生成
        for (let i = 1; i <= 20; i++) {
            let type = i % 3 === 0 ? 'sent' : (i % 5 === 0 ? 'generated' : 'received')

            mockMessages.push({
                messageId: `msg_${i}`,
                type,
                content: type === 'received'
                    ? '这是一条收到的消息，用于测试显示效果。你今天过得怎么样？'
                    : (type === 'generated'
                        ? '这是AI生成的消息，可以模拟不同风格的回复内容。很高兴能和你聊天！'
                        : '这是我发送的消息，希望今天过得愉快！'),
                timestamp: new Date(now - (20 - i) * 1000 * 60 * 30).toISOString() // 每隔30分钟一条
            })
        }

        // 处理分页
        if (params && params.page && params.size) {
            const { page, size } = params
            const start = (page - 1) * size
            const end = start + size

            return {
                messages: mockMessages.slice(start, end).reverse(),
                pagination: {
                    total: mockMessages.length,
                    page,
                    size,
                    hasMore: end < mockMessages.length
                }
            }
        }

        return {
            messages: mockMessages.reverse(),
            pagination: {
                total: mockMessages.length,
                page: 1,
                size: mockMessages.length,
                hasMore: false
            }
        }
    },

    // 会员信息
    '/membership': () => {
        return {
            isVip: true,
            plan: 'monthly',
            expireTime: new Date(Date.now() + 30 * 24 * 60 * 60 * 1000).toISOString(),
            autoRenew: true,
            benefits: [
                '无限制使用生成功能',
                '解锁全部高级风格',
                'VIP专属客服'
            ]
        }
    },

    // 会员套餐
    '/membership/plans': () => {
        return [
            {
                id: 'monthly',
                name: '月度会员',
                price: 39.99,
                originalPrice: 59.99,
                period: 'month',
                periodText: '月',
                features: [
                    '解锁全部风格',
                    '每日200次使用量',
                    '高级会话分析'
                ],
                recommended: false
            },
            {
                id: 'quarterly',
                name: '季度会员',
                price: 99.99,
                originalPrice: 129.99,
                period: 'quarter',
                periodText: '季',
                features: [
                    '解锁全部风格',
                    '每日200次使用量',
                    '高级会话分析',
                    '会员特别活动'
                ],
                recommended: true
            },
            {
                id: 'yearly',
                name: '年度会员',
                price: 299.99,
                originalPrice: 499.99,
                period: 'year',
                periodText: '年',
                features: [
                    '解锁全部风格',
                    '无限使用次数',
                    '高级会话分析',
                    '会员特别活动',
                    '专属客服通道'
                ],
                recommended: false
            }
        ]
    },

    // 聊天风格
    '/styles': () => {
        return [
            {
                id: 'friendly',
                name: '友好',
                description: '亲切、适度热情',
                vipOnly: false,
                icon: 'friendly.png'
            },
            {
                id: 'casual',
                name: '随性',
                description: '轻松自然的交流',
                vipOnly: false,
                icon: 'casual.png'
            },
            {
                id: 'caring',
                name: '体贴',
                description: '展现关怀和理解',
                vipOnly: false,
                icon: 'caring.png'
            },
            {
                id: 'humorous',
                name: '幽默',
                description: '轻松风趣的表达',
                vipOnly: true,
                icon: 'humorous.png'
            },
            {
                id: 'romantic',
                name: '浪漫',
                description: '富有情感的表达',
                vipOnly: true,
                icon: 'romantic.png'
            },
            {
                id: 'mysterious',
                name: '神秘',
                description: '保持适当神秘感',
                vipOnly: true,
                icon: 'mysterious.png'
            }
        ]
    },

    // AI生成回复
    '/assistant/generate': ({ data }) => {
        const styles = {
            'friendly': '很高兴认识你！我今天过得很充实，刚刚结束了一天的工作。你呢？最近有什么有趣的事情发生吗？',
            'casual': '嗨，这边刚忙完。最近怎么样？有啥新鲜事不？',
            'caring': '你今天看起来心情不错呢，工作顺利吗？记得照顾好自己，多喝水哦。',
            'humorous': '哈！今天我差点把咖啡洒在键盘上，差点成了"水上键盘侠"！你今天有什么搞笑的事情吗？',
            'romantic': '看到你的消息，整个下午都变得明媚起来。就像阳光穿过窗帘，温暖了整个房间。期待能再多了解你一些。',
            'mysterious': '有些事情，看似偶然，实则是命中注定。我们的相遇，或许也是如此。你相信命运吗？'
        }

        // 默认风格
        let style = 'friendly'

        // 根据请求参数选择风格
        if (data && data.preferences && data.preferences.styles && data.preferences.styles.length > 0) {
            style = data.preferences.styles[0]
        }

        return {
            replies: [
                {
                    replyId: 'reply_' + Date.now(),
                    content: styles[style] || styles.friendly,
                    style,
                    timestamp: new Date().toISOString()
                }
            ],
            usage: {
                total: 30,
                remaining: 20,
                used: 10
            }
        }
    },

    // 使用统计
    '/user/usage-stats': () => {
        return {
            totalGeneration: 182,
            totalCopied: 135,
            successRate: 74,
            dailyAverage: 8,
            mostUsedStyle: 'friendly'
        }
    },

    // 登录接口
    '/auth/login': ({ data }) => {
        console.log('Mock登录接口收到数据:', data)

        // 根据用户信息构建响应
        const userInfo = data.userInfo || {
            nickName: '默认用户',
            avatarUrl: '/static/images/default-avatar.png',
            gender: 1
        }

        // 为测试使用，强制设置为新用户
        const isNewUser = false

        // 生成一个假的token和用户信息
        return {
            token: 'mock_token_' + Date.now(),
            refreshToken: 'mock_refresh_token_' + Date.now(),
            userInfo: {
                userId: 'user_' + Date.now(),
                nickname: userInfo.nickName,
                avatar: userInfo.avatarUrl,
                gender: userInfo.gender === 1 ? 'male' : 'female',
                newUser: isNewUser
            },
            newUser: isNewUser
        }
    },

    // 用户个人资料
    '/user/profile': () => {
        // 确保返回完整的用户数据结构
        return {
            userId: 'user_12345',
            nickname: '小恋爱',
            avatar: '/static/images/default-avatar.png',
            gender: 'female',
            birthday: '1995-01-01',
            phone: '138****1234',
            email: 'test@example.com',
            registrationDate: '2023-01-15T08:30:00.000Z',
            membershipInfo: {
                isActive: true,
                expireTime: new Date(Date.now() + 30 * 24 * 60 * 60 * 1000).toISOString(),
                level: 1
            },
            settings: {
                notification: true,
                privacy: 'standard',
                chatHistoryRetention: 30,
                language: 'zh-CN'
            }
        }
    },

    // 用户使用限制
    '/user/limits': () => {
        return {
            dailyGeneration: {
                total: 30,
                used: 8,
                remaining: 22
            },
            monthlyDownload: {
                total: 100,
                used: 25,
                remaining: 75
            },
            featuresAccess: {
                advancedStyles: true,
                bulkGeneration: false,
                customPrompts: true
            }
        }
    }
}

// 暴露请求方法
export default request

// 设置基础URL和获取请求头的方法，方便上传API等场景使用
request.baseURL = baseURL
request.getHeaders = getHeaders