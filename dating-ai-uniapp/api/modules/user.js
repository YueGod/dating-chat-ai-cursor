/**
 * 用户相关API
 */
import { get, put } from '../../utils/request'

/**
 * 获取用户信息
 * @returns {Promise} Promise对象
 */
export const getUserProfile = () => {
    return get('/user/profile')
}

/**
 * 更新用户信息
 * @param {Object} data 用户信息
 * @param {String} data.nickname 昵称
 * @param {String} data.avatar 头像
 * @param {String} data.gender 性别
 * @returns {Promise} Promise对象
 */
export const updateUserProfile = (data) => {
    return put('/user/profile', data)
}

/**
 * 更新用户设置
 * @param {Object} data 用户设置
 * @param {Boolean} data.notification 通知设置
 * @param {String} data.privacy 隐私设置
 * @param {Number} data.chatHistoryRetention 聊天记录保留天数
 * @param {String} data.language 语言设置
 * @returns {Promise} Promise对象
 */
export const updateUserSettings = (data) => {
    return put('/user/settings', data)
}

/**
 * 获取用户使用统计
 * @param {String} period 统计周期，可选值：day、week、month、all
 * @returns {Promise} Promise对象
 */
export const getUserUsageStats = (period = 'day') => {
    return get('/user/usage-stats', { period })
}

/**
 * 检查用户限制
 * @returns {Promise} Promise对象
 */
export const getUserLimits = () => {
    return get('/user/limits')
} 