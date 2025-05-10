// 环境配置
const ENV = {
    DEV: 'development',
    PROD: 'production'
}

// 当前环境
const CURRENT_ENV = ENV.DEV

// 基础URL
export const BASE_URL = {
    [ENV.DEV]: 'http://localhost:8080/api/v1',
    [ENV.PROD]: 'https://api.example.com/api/v1'
}[CURRENT_ENV]

// 微信相关配置
export const WEIXIN_CONFIG = {
    APP_ID: ''
}

// 缓存Key
export const STORAGE_KEY = {
    TOKEN: 'ylToken',
    REFRESH_TOKEN: 'ylRefreshToken',
    USER_INFO: 'ylUserInfo'
}

// 请求配置
export const REQUEST_CONFIG = {
    TIMEOUT: 30000,
    RETRY_COUNT: 3,
    RETRY_DELAY: 1500
}

// 应用配置
export const APP_CONFIG = {
    APP_NAME: '语撩AI',
    APP_VERSION: '1.0.0',
    SUPPORT_EMAIL: 'support@example.com',
    PRIVACY_URL: 'https://example.com/privacy',
    TERMS_URL: 'https://example.com/terms'
}

export default {
    BASE_URL,
    WEIXIN_CONFIG,
    STORAGE_KEY,
    REQUEST_CONFIG,
    APP_CONFIG
} 