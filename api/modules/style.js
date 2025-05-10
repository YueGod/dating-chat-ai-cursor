import request from '@/utils/request'

export default {
    // 获取所有风格列表
    getStyles() {
        return request({
            url: '/styles',
            method: 'get'
        })
    },

    // 获取特定风格详情
    getStyleDetail(id) {
        return request({
            url: `/styles/${id}`,
            method: 'get'
        })
    },

    // 获取推荐风格
    getRecommendedStyles() {
        return request({
            url: '/styles/recommended',
            method: 'get'
        })
    },

    // 获取用户常用风格
    getUserFrequentStyles() {
        return request({
            url: '/styles/user/frequent',
            method: 'get'
        })
    },

    // 保存用户对风格的偏好
    saveStylePreference(data) {
        return request({
            url: '/styles/preference',
            method: 'post',
            data
        })
    }
} 