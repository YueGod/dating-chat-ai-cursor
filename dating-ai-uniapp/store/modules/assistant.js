/**
 * AI助手模块状态管理
 * 管理AI助手相关的状态、计数和设置
 */

// 初始状态
const state = {
    dailyRemainingCount: 20, // 每日剩余使用次数
    dailyTotalCount: 30,     // 每日总使用次数
    recentPrompts: [],       // 最近使用的提示词
    favoritePrompts: [],     // 收藏的提示词
    settings: {              // 助手设置
        style: 'friendly',   // 默认风格：友好
        tone: 'positive',    // 默认语气：积极
        length: 'medium'     // 默认长度：中等
    }
}

// 修改状态的方法
const mutations = {
    /**
     * 设置每日剩余使用次数
     * @param {Object} state - Vuex状态
     * @param {Number} count - 剩余次数
     */
    SET_DAILY_REMAINING_COUNT(state, count) {
        state.dailyRemainingCount = count
    },

    /**
     * 设置每日总使用次数
     * @param {Object} state - Vuex状态
     * @param {Number} count - 总次数
     */
    SET_DAILY_TOTAL_COUNT(state, count) {
        state.dailyTotalCount = count
    },

    /**
     * 设置最近使用的提示词
     * @param {Object} state - Vuex状态
     * @param {Array} prompts - 提示词列表
     */
    SET_RECENT_PROMPTS(state, prompts) {
        state.recentPrompts = prompts
    },

    /**
     * 设置收藏的提示词
     * @param {Object} state - Vuex状态
     * @param {Array} prompts - 提示词列表
     */
    SET_FAVORITE_PROMPTS(state, prompts) {
        state.favoritePrompts = prompts
    },

    /**
     * 添加提示词到最近使用
     * @param {Object} state - Vuex状态
     * @param {Object} prompt - 提示词对象
     */
    ADD_RECENT_PROMPT(state, prompt) {
        // 如果不存在相同ID的提示词，则添加
        if (!state.recentPrompts.some(p => p.id === prompt.id)) {
            // 添加到开头并限制最多保留10个
            state.recentPrompts = [prompt, ...state.recentPrompts].slice(0, 10)
        }
    },

    /**
     * 切换提示词收藏状态
     * @param {Object} state - Vuex状态
     * @param {String} promptId - 提示词ID
     */
    TOGGLE_FAVORITE_PROMPT(state, promptId) {
        const index = state.favoritePrompts.findIndex(p => p.id === promptId)
        if (index !== -1) {
            // 如果已收藏，则移除
            state.favoritePrompts.splice(index, 1)
        } else {
            // 如果未收藏，在最近使用中找到该提示词并添加到收藏
            const prompt = state.recentPrompts.find(p => p.id === promptId)
            if (prompt) {
                state.favoritePrompts.push(prompt)
            }
        }
    },

    /**
     * 更新助手设置
     * @param {Object} state - Vuex状态
     * @param {Object} settings - 设置对象
     */
    UPDATE_SETTINGS(state, settings) {
        state.settings = { ...state.settings, ...settings }
    }
}

// 包含业务逻辑的异步操作
const actions = {
    /**
     * 获取用户使用限制信息
     * @param {Object} context - Vuex上下文
     * @param {Function} context.commit - 提交mutation的函数
     * @returns {Promise} 限制信息
     */
    async getUserLimits({ commit }) {
        try {
            // API调用（实际项目中会通过API获取）
            // 这里使用Mock数据
            const result = {
                remaining: 18,
                total: 30
            }

            // 更新状态
            commit('SET_DAILY_REMAINING_COUNT', result.remaining)
            commit('SET_DAILY_TOTAL_COUNT', result.total)
            return result
        } catch (error) {
            console.error('获取使用限制失败', error)
            return null
        }
    },

    /**
     * 设置最近使用的提示词
     * @param {Object} context - Vuex上下文
     * @param {Function} context.commit - 提交mutation的函数
     * @param {Array} prompts - 提示词列表
     */
    setRecentPrompts({ commit }, prompts) {
        commit('SET_RECENT_PROMPTS', prompts)
    },

    /**
     * 设置收藏的提示词
     * @param {Object} context - Vuex上下文
     * @param {Function} context.commit - 提交mutation的函数
     * @param {Array} prompts - 提示词列表
     */
    setFavoritePrompts({ commit }, prompts) {
        commit('SET_FAVORITE_PROMPTS', prompts)
    },

    /**
     * 添加提示词到最近使用
     * @param {Object} context - Vuex上下文
     * @param {Function} context.commit - 提交mutation的函数
     * @param {Object} prompt - 提示词对象
     */
    addRecentPrompt({ commit }, prompt) {
        commit('ADD_RECENT_PROMPT', prompt)
    },

    /**
     * 切换提示词收藏状态
     * @param {Object} context - Vuex上下文
     * @param {Function} context.commit - 提交mutation的函数
     * @param {String} promptId - 提示词ID
     */
    toggleFavoritePrompt({ commit }, promptId) {
        commit('TOGGLE_FAVORITE_PROMPT', promptId)
    },

    /**
     * 更新助手设置
     * @param {Object} context - Vuex上下文
     * @param {Function} context.commit - 提交mutation的函数
     * @param {Object} settings - 设置对象
     */
    updateSettings({ commit }, settings) {
        commit('UPDATE_SETTINGS', settings)
    }
}

// 从state派生出的计算属性
const getters = {
    // 每日剩余使用次数
    dailyRemainingCount: state => state.dailyRemainingCount,

    // 每日总使用次数
    dailyTotalCount: state => state.dailyTotalCount,

    // 最近使用的提示词
    recentPrompts: state => state.recentPrompts,

    // 收藏的提示词
    favoritePrompts: state => state.favoritePrompts,

    // 助手设置
    settings: state => state.settings,

    /**
     * 判断提示词是否已收藏
     * @param {Object} state - Vuex状态
     * @returns {Function} 接收promptId返回布尔值的函数
     */
    isPromptFavorited: state => promptId => {
        return state.favoritePrompts.some(p => p.id === promptId)
    }
}

// 导出Vuex模块
export default {
    namespaced: true, // 启用命名空间
    state,
    mutations,
    actions,
    getters
} 