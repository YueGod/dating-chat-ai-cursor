/**
 * 聊天模块状态管理
 * 管理会话列表、当前会话和未读消息状态
 */

// 初始状态
const state = {
    conversations: [],        // 会话列表
    currentConversation: null, // 当前选中的会话
    unreadMessages: 0,        // 未读消息总数
    loadingConversations: false // 是否正在加载会话列表
}

// 修改状态的方法
const mutations = {
    /**
     * 设置会话列表
     * @param {Object} state - Vuex状态
     * @param {Array} conversations - 会话列表
     */
    SET_CONVERSATIONS(state, conversations) {
        state.conversations = conversations
    },

    /**
     * 设置当前选中的会话
     * @param {Object} state - Vuex状态
     * @param {Object} conversation - 会话对象
     */
    SET_CURRENT_CONVERSATION(state, conversation) {
        state.currentConversation = conversation
    },

    /**
     * 设置未读消息总数
     * @param {Object} state - Vuex状态
     * @param {Number} count - 未读数量
     */
    SET_UNREAD_MESSAGES(state, count) {
        state.unreadMessages = count
    },

    /**
     * 设置加载状态
     * @param {Object} state - Vuex状态
     * @param {Boolean} loading - 是否加载中
     */
    SET_LOADING_CONVERSATIONS(state, loading) {
        state.loadingConversations = loading
    },

    /**
     * 添加新会话
     * @param {Object} state - Vuex状态
     * @param {Object} conversation - 会话对象
     */
    ADD_CONVERSATION(state, conversation) {
        // 新会话添加到列表开头
        state.conversations = [conversation, ...state.conversations]
    },

    /**
     * 更新会话信息
     * @param {Object} state - Vuex状态
     * @param {Object} payload - 更新数据
     * @param {String} payload.id - 会话ID
     * @param {Object} payload.data - 更新的字段
     */
    UPDATE_CONVERSATION(state, { id, data }) {
        const index = state.conversations.findIndex(conv => conv.id === id)
        if (index !== -1) {
            // 合并原会话数据和新数据
            state.conversations[index] = { ...state.conversations[index], ...data }
        }
    },

    /**
     * 删除会话
     * @param {Object} state - Vuex状态
     * @param {String} id - 会话ID
     */
    DELETE_CONVERSATION(state, id) {
        // 从会话列表中过滤掉要删除的会话
        state.conversations = state.conversations.filter(conv => conv.id !== id)

        // 如果删除的是当前选中的会话，清空当前会话
        if (state.currentConversation && state.currentConversation.id === id) {
            state.currentConversation = null
        }
    }
}

// 包含业务逻辑的异步操作
const actions = {
    /**
     * 加载会话列表
     * @param {Object} context - Vuex上下文
     * @param {Function} context.commit - 提交mutation的函数
     * @returns {Promise} 会话列表
     */
    loadConversations({ commit }) {
        commit('SET_LOADING_CONVERSATIONS', true)

        // API调用（实际项目中会通过API获取）
        // 这里使用Mock数据
        const mockConversations = [
            {
                id: '1',
                title: '与小红的对话',
                lastMessage: '好的，明天见！',
                timestamp: Date.now() - 10000,
                unread: 0
            },
            {
                id: '2',
                title: '与小明的对话',
                lastMessage: '期待下次聊天',
                timestamp: Date.now() - 3600000,
                unread: 2
            }
        ]

        commit('SET_CONVERSATIONS', mockConversations)
        commit('SET_LOADING_CONVERSATIONS', false)
        return mockConversations
    },

    /**
     * 设置当前会话
     * @param {Object} context - Vuex上下文
     * @param {Function} context.commit - 提交mutation的函数
     * @param {Object} conversation - 会话对象
     */
    setCurrentConversation({ commit }, conversation) {
        commit('SET_CURRENT_CONVERSATION', conversation)
    },

    /**
     * 更新未读消息总数
     * @param {Object} context - Vuex上下文
     * @param {Function} context.commit - 提交mutation的函数
     * @param {Object} context.state - Vuex状态
     */
    updateUnreadMessages({ commit, state }) {
        // 计算所有会话的未读消息总和
        const unread = state.conversations.reduce((acc, conv) => acc + conv.unread, 0)
        commit('SET_UNREAD_MESSAGES', unread)
    },

    /**
     * 创建新会话
     * @param {Object} context - Vuex上下文
     * @param {Function} context.commit - 提交mutation的函数
     * @param {Object} data - 会话数据
     * @returns {Object} 创建的会话
     */
    createConversation({ commit }, data) {
        // 创建新会话对象
        const newConversation = {
            id: Date.now().toString(), // 临时ID，实际应使用后端返回的ID
            timestamp: Date.now(),     // 创建时间戳
            unread: 0,                 // 初始无未读消息
            ...data                    // 合并其他数据
        }

        // 提交到store
        commit('ADD_CONVERSATION', newConversation)
        return newConversation
    },

    /**
     * 更新会话信息
     * @param {Object} context - Vuex上下文
     * @param {Function} context.commit - 提交mutation的函数
     * @param {Object} payload - 更新数据
     */
    updateConversation({ commit }, payload) {
        commit('UPDATE_CONVERSATION', payload)
    },

    /**
     * 删除会话
     * @param {Object} context - Vuex上下文
     * @param {Function} context.commit - 提交mutation的函数
     * @param {String} id - 会话ID
     */
    deleteConversation({ commit }, id) {
        commit('DELETE_CONVERSATION', id)
    }
}

// 从state派生出的计算属性
const getters = {
    // 会话列表
    conversations: state => state.conversations,

    // 当前选中的会话
    currentConversation: state => state.currentConversation,

    // 未读消息总数
    unreadMessages: state => state.unreadMessages,

    // 是否加载中
    loadingConversations: state => state.loadingConversations,

    /**
     * 根据ID获取会话
     * @param {Object} state - Vuex状态
     * @returns {Function} 接收ID返回会话对象的函数
     */
    getConversationById: state => id => {
        return state.conversations.find(conv => conv.id === id)
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