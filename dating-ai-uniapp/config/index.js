/**
 * 全局配置文件
 * 统一管理各环境配置和常量
 */

// 获取当前环境
const ENV = process.env.NODE_ENV || 'development'

// 接口基础URL
const BASE_URL_CONFIG = {
    development: 'https://dev-api.dating-ai.com',  // 开发环境
    test: 'https://test-api.dating-ai.com',        // 测试环境
    production: 'https://api.dating-ai.com'        // 生产环境
}

// 导出基础URL
export const BASE_URL = BASE_URL_CONFIG[ENV]

// 应用配置
export const APP_CONFIG = {
    // 应用名称
    APP_NAME: '恋爱导师',

    // 默认超时时间（毫秒）
    TIMEOUT: 30000,

    // 版本号
    VERSION: '1.0.0',

    // 请求头
    HEADERS: {
        'Content-Type': 'application/json;charset=UTF-8'
    }
}

// 业务常量
export const BIZ_CONSTANTS = {
    // 存储键名
    STORAGE_KEYS: {
        TOKEN: 'ylToken',
        USER_INFO: 'userInfo',
        SETTINGS: 'settings'
    },

    // 聊天类型
    CHAT_TYPES: {
        SENT: 'sent',         // 用户发送
        RECEIVED: 'received', // 用户接收
        GENERATED: 'generated' // AI生成
    },

    // 消息状态
    MESSAGE_STATUS: {
        SENDING: 'sending',
        SUCCESS: 'success',
        FAILED: 'failed'
    }
}

// 请求状态码
export const CODE = {
    SUCCESS: '000000',       // 成功
    UNAUTHORIZED: '000401',  // 未授权
    FORBIDDEN: '000403',     // 禁止访问
    NOT_FOUND: '000404',     // 资源不存在
    INTERNAL_ERROR: '000500' // 服务器错误
}

// 默认导出所有配置
export default {
    BASE_URL,
    APP_CONFIG,
    BIZ_CONSTANTS,
    CODE,
    ENV
} 