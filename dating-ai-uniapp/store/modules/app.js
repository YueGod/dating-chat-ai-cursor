/**
 * 应用状态模块
 */

// 初始状态
const state = {
    // 系统信息
    systemInfo: null,
    // 状态栏高度
    statusBarHeight: 0,
    // 网络类型
    networkType: 'unknown',
    // 是否联网
    isConnected: true,
    // 是否展示加载中
    loading: false,
    // 全局主题
    theme: 'light'
}

// Getters
const getters = {
    // 获取系统信息
    systemInfo: state => state.systemInfo,
    // 获取状态栏高度
    statusBarHeight: state => state.statusBarHeight,
    // 获取网络类型
    networkType: state => state.networkType,
    // 是否联网
    isConnected: state => state.isConnected,
    // 是否展示加载中
    loading: state => state.loading,
    // 获取全局主题
    theme: state => state.theme
}

// Mutations
const mutations = {
    // 设置系统信息
    SET_SYSTEM_INFO(state, systemInfo) {
        state.systemInfo = systemInfo
    },
    // 设置状态栏高度
    SET_STATUS_BAR_HEIGHT(state, height) {
        state.statusBarHeight = height
    },
    // 设置网络类型
    SET_NETWORK_TYPE(state, networkType) {
        state.networkType = networkType
        state.isConnected = networkType !== 'none'
    },
    // 设置加载状态
    SET_LOADING(state, loading) {
        state.loading = loading
    },
    // 设置主题
    SET_THEME(state, theme) {
        state.theme = theme
        // 持久化主题设置
        uni.setStorageSync('theme', theme)
    }
}

// Actions
const actions = {
    // 初始化应用
    initApp({ commit }) {
        // 获取系统信息
        const systemInfo = uni.getSystemInfoSync()
        commit('SET_SYSTEM_INFO', systemInfo)

        // 设置状态栏高度
        commit('SET_STATUS_BAR_HEIGHT', systemInfo.statusBarHeight)

        // 获取网络状态
        uni.getNetworkType({
            success: (res) => {
                commit('SET_NETWORK_TYPE', res.networkType)
            }
        })

        // 恢复主题设置
        const savedTheme = uni.getStorageSync('theme')
        if (savedTheme) {
            commit('SET_THEME', savedTheme)
        }
    },

    // 切换主题
    toggleTheme({ commit, state }) {
        const newTheme = state.theme === 'light' ? 'dark' : 'light'
        commit('SET_THEME', newTheme)
    }
}

export default {
    namespaced: true,
    state,
    getters,
    mutations,
    actions
} 