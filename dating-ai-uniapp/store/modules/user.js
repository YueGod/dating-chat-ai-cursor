/**
 * 用户状态模块
 */
import * as api from '../../api/modules/user'
import * as authApi from '../../api/modules/auth'
import { STORAGE_KEY } from '../../config'

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
    // 获取用户信息
    getUserInfo({ commit }) {
        return api.getUserProfile().then(data => {
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

    // 更新用户信息
    updateUserProfile({ commit }, data) {
        return api.updateUserProfile(data).then(response => {
            commit('SET_USER_INFO', { ...state.userInfo, ...response })
            return response
        })
    },

    // 更新用户设置
    updateUserSettings({ commit }, data) {
        return api.updateUserSettings(data).then(response => {
            commit('SET_SETTINGS', response.settings)
            return response
        })
    },

    // 获取用户使用统计
    getUserUsageStats({ commit }, period) {
        return api.getUserUsageStats(period).then(data => {
            commit('SET_USAGE_STATS', data)
            return data
        })
    },

    // 获取用户限制
    getUserLimits({ commit }) {
        return api.getUserLimits().then(data => {
            commit('SET_LIMITS', data)
            return data
        })
    },

    // 登录
    login({ commit, dispatch }, data) {
        return authApi.login(data).then(response => {
            // 初始用户状态
            commit('SET_NEW_USER', response.newUser)

            // 获取完整用户信息
            return dispatch('getUserInfo')
        })
    },

    // 退出登录
    logout({ commit }) {
        return authApi.logout().then(() => {
            // 清除状态
            commit('SET_USER_INFO', null)
            commit('SET_MEMBERSHIP_INFO', null)
            commit('SET_USAGE_STATS', null)
            commit('SET_LIMITS', null)

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