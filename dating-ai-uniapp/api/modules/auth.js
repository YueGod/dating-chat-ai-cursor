/**
 * 认证相关API
 */
import request, { post } from '../../utils/request'
import { BIZ_CONSTANTS } from '../../config'

/**
 * 微信登录
 * @param {Object} data 登录信息
 * @param {String} data.code 微信登录授权code
 * @param {String} data.platform 登录平台，如'WECHAT_MINI_PROGRAM'
 * @param {Object} data.userInfo 用户信息对象
 * @returns {Promise} Promise对象
 */
export const login = (data) => {
    return post('/auth/login', data, { noAuth: true, loading: '登录中...' }).then(response => {
        // 存储令牌和用户信息
        if (response && response.data && response.data.token) {
            // 存储token
            uni.setStorageSync(BIZ_CONSTANTS.STORAGE_KEYS.TOKEN, response.data.token)

            // 存储用户基本信息
            if (response.data.userInfo) {
                uni.setStorageSync(BIZ_CONSTANTS.STORAGE_KEYS.USER_INFO, JSON.stringify(response.data.userInfo))
            }

            console.log('登录成功，已保存用户信息')
        }
        return response.data
    })
}

/**
 * 刷新令牌
 * @param {String} refreshToken 刷新令牌
 * @returns {Promise} Promise对象
 */
export const refreshToken = (refreshToken) => {
    return post('/auth/refresh', { refreshToken }, { noAuth: true }).then(response => {
        // 更新令牌
        if (response && response.data && response.data.token) {
            uni.setStorageSync(BIZ_CONSTANTS.STORAGE_KEYS.TOKEN, response.data.token)
        }
        return response.data
    })
}

/**
 * 退出登录
 * @returns {Promise} Promise对象
 */
export const logout = () => {
    return post('/auth/logout').finally(() => {
        // 清除本地存储
        uni.removeStorageSync(BIZ_CONSTANTS.STORAGE_KEYS.TOKEN)
        uni.removeStorageSync(BIZ_CONSTANTS.STORAGE_KEYS.USER_INFO)
    })
} 