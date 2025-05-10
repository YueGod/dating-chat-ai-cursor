/**
 * 认证相关API
 */
import { post } from '../../utils/request'
import { STORAGE_KEY } from '../../config'

/**
 * 微信登录
 * @param {Object} data 登录信息
 * @param {String} data.code 微信登录授权code
 * @param {String} data.platform 登录平台，如'WECHAT_MINI_PROGRAM'
 * @returns {Promise} Promise对象
 */
export const login = (data) => {
    return post('/auth/login', data, { noAuth: true }).then(response => {
        // 存储令牌和用户信息
        if (response && response.token) {
            uni.setStorageSync(STORAGE_KEY.TOKEN, response.token)
            uni.setStorageSync(STORAGE_KEY.REFRESH_TOKEN, response.refreshToken)
            uni.setStorageSync(STORAGE_KEY.USER_INFO, JSON.stringify({
                userId: response.userId,
                newUser: response.newUser
            }))
        }
        return response
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
        if (response && response.token) {
            uni.setStorageSync(STORAGE_KEY.TOKEN, response.token)
        }
        return response
    })
}

/**
 * 退出登录
 * @returns {Promise} Promise对象
 */
export const logout = () => {
    return post('/auth/logout').finally(() => {
        // 清除本地存储
        uni.removeStorageSync(STORAGE_KEY.TOKEN)
        uni.removeStorageSync(STORAGE_KEY.REFRESH_TOKEN)
        uni.removeStorageSync(STORAGE_KEY.USER_INFO)
    })
} 