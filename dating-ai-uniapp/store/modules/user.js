/**
 * 用户状态模块
 * 管理用户信息、登录状态、会员信息等
 */
import * as userApi from '../../api/modules/user'
import * as authApi from '../../api/modules/auth'
import { BIZ_CONSTANTS } from '../../config'

// 初始状态
const state = {
    // 用户信息
    userInfo: null,
    // 是否已登录
    isLoggedIn: false,
    // 是否新用户
    isNewUser: false,
    // 会员信息
    membershipInfo: null,
    // 用户设置
    settings: {
        notification: true,
        privacy: 'standard',
        chatHistoryRetention: 30,
        language: 'zh-CN'
    },
    // 使用统计
    usageStats: null,
    // 使用限制
    limits: null
}

// Getters
const getters = {
    // 获取用户信息
    userInfo: state => state.userInfo,
    // 是否已登录
    isLoggedIn: state => state.isLoggedIn,
    // 是否新用户
    isNewUser: state => state.isNewUser,
    // 获取会员信息
    membershipInfo: state => state.membershipInfo,
    // 是否是会员
    isVip: state => state.membershipInfo && state.membershipInfo.isActive,
    // 会员等级
    vipLevel: state => state.membershipInfo ? state.membershipInfo.level : 0,
    // 获取用户设置
    settings: state => state.settings,
    // 获取使用统计
    usageStats: state => state.usageStats,
    // 获取使用限制
    limits: state => state.limits,
    // 今日剩余使用次数
    dailyRemainingCount: state => state.limits ? state.limits.dailyGeneration.remaining : 0
}

// Mutations
const mutations = {
    // 设置用户信息
    SET_USER_INFO(state, userInfo) {
        state.userInfo = userInfo
        state.isLoggedIn = !!userInfo
    },
    // 设置新用户状态
    SET_NEW_USER(state, isNewUser) {
        state.isNewUser = isNewUser
    },
    // 设置会员信息
    SET_MEMBERSHIP_INFO(state, membershipInfo) {
        state.membershipInfo = membershipInfo
    },
    // 设置用户设置
    SET_SETTINGS(state, settings) {
        state.settings = { ...state.settings, ...settings }
    },
    // 设置使用统计
    SET_USAGE_STATS(state, usageStats) {
        state.usageStats = usageStats
    },
    // 设置使用限制
    SET_LIMITS(state, limits) {
        state.limits = limits
    }
}

// Actions
const actions = {
    /**
     * 获取用户信息
     * @param {Object} context Vuex上下文
     * @param {Function} context.commit 提交mutation方法
     * @returns {Promise} 用户信息
     */
    getUserInfo({ commit }) {
        return userApi.getUserProfile().then(data => {
            console.log('获取到用户信息:', data)
            commit('SET_USER_INFO', data)

            if (data.membershipInfo) {
                commit('SET_MEMBERSHIP_INFO', data.membershipInfo)
            }

            if (data.settings) {
                commit('SET_SETTINGS', data.settings)
            }

            return data
        })
    },

    /**
     * 更新用户信息
     * @param {Object} context Vuex上下文
     * @param {Function} context.commit 提交mutation方法
     * @param {Object} context.state 当前状态
     * @param {Object} data 用户资料数据
     * @returns {Promise} 更新结果
     */
    updateUserProfile({ commit, state }, data) {
        return userApi.updateUserProfile(data).then(response => {
            // 合并现有用户信息和更新的数据
            const updatedUserInfo = { ...state.userInfo, ...response }
            commit('SET_USER_INFO', updatedUserInfo)
            return response
        })
    },

    /**
     * 更新用户设置
     * @param {Object} context Vuex上下文
     * @param {Function} context.commit 提交mutation方法 
     * @param {Object} data 设置数据
     * @returns {Promise} 更新结果
     */
    updateUserSettings({ commit }, data) {
        return userApi.updateUserSettings(data).then(response => {
            if (response.settings) {
                commit('SET_SETTINGS', response.settings)
            }
            return response
        })
    },

    /**
     * 获取用户使用统计
     * @param {Object} context Vuex上下文
     * @param {Function} context.commit 提交mutation方法
     * @param {String} period 统计周期，如'day', 'week', 'month'
     * @returns {Promise} 统计数据
     */
    getUserUsageStats({ commit }, period) {
        return userApi.getUserUsageStats(period).then(data => {
            commit('SET_USAGE_STATS', data)
            return data
        })
    },

    /**
     * 获取用户限制信息
     * @param {Object} context Vuex上下文
     * @param {Function} context.commit 提交mutation方法
     * @returns {Promise} 限制信息
     */
    getUserLimits({ commit }) {
        return userApi.getUserLimits().then(data => {
            commit('SET_LIMITS', data)
            return data
        })
    },

    /**
     * 登录
     * @param {Object} context Vuex上下文
     * @param {Function} context.commit 提交mutation方法
     * @param {Function} context.dispatch 派发action方法
     * @param {Object} data 登录数据
     * @returns {Promise} 登录结果
     */
    login({ commit, dispatch }, data) {
        console.log('开始登录，数据:', data)
        return authApi.login(data).then(response => {
            console.log('登录响应:', response)

            // 设置新用户状态
            commit('SET_NEW_USER', response.newUser || false)

            // 如果API直接返回了用户信息，先设置
            if (response.userInfo) {
                commit('SET_USER_INFO', response.userInfo)
            }

            // 获取更完整的用户信息
            return dispatch('getUserInfo')
        })
    },

    /**
     * 退出登录
     * @param {Object} context Vuex上下文
     * @param {Function} context.commit 提交mutation方法
     * @returns {Promise} 登出结果
     */
    logout({ commit }) {
        return authApi.logout().then(() => {
            // 清除状态
            commit('SET_USER_INFO', null)
            commit('SET_MEMBERSHIP_INFO', null)
            commit('SET_USAGE_STATS', null)
            commit('SET_LIMITS', null)
            commit('SET_NEW_USER', false)

            return true
        })
    }
}

export default {
    namespaced: true,
    state,
    getters,
    mutations,
    actions
} 