/**
 * 用户相关API
 * 处理用户资料、设置和统计数据
 */
import request, { get, post, put } from '../../utils/request'

/**
 * 获取用户个人资料
 * @returns {Promise} 用户资料
 */
export const getUserProfile = () => {
    return get('/user/profile', {}, {
        loading: '加载中...'
    })
}

/**
 * 更新用户个人资料
 * @param {Object} data 用户资料数据
 * @returns {Promise} 更新结果
 */
export const updateUserProfile = (data) => {
    return put('/user/profile', data, {
        loading: '更新中...'
    })
}

/**
 * 更新用户设置
 * @param {Object} data 设置数据
 * @returns {Promise} 更新结果
 */
export const updateUserSettings = (data) => {
    return put('/user/settings', data)
}

/**
 * 获取用户使用统计
 * @param {String} period 统计周期，如'day', 'week', 'month'
 * @returns {Promise} 统计数据
 */
export const getUserUsageStats = (period = 'week') => {
    return get('/user/usage-stats', { period })
}

/**
 * 获取用户限制信息
 * @returns {Promise} 限制信息
 */
export const getUserLimits = () => {
    return get('/user/limits')
}

/**
 * 上传用户头像
 * @param {String} filePath 本地文件路径
 * @returns {Promise} 上传结果
 */
export const uploadAvatar = (filePath) => {
    return new Promise((resolve, reject) => {
        uni.uploadFile({
            url: request.baseURL + '/user/avatar',
            filePath,
            name: 'file',
            header: request.getHeaders(),
            success: (res) => {
                if (res.statusCode === 200) {
                    const data = JSON.parse(res.data)
                    resolve(data)
                } else {
                    reject(new Error('上传失败'))
                }
            },
            fail: (err) => {
                reject(err)
            }
        })
    })
} 