const state = {
    conversations: [],
    currentConversation: null,
    unreadMessages: 0,
    loadingConversations: false
}

const mutations = {
    SET_CONVERSATIONS(state, conversations) {
        state.conversations = conversations
    },
    SET_CURRENT_CONVERSATION(state, conversation) {
        state.currentConversation = conversation
    },
    SET_UNREAD_MESSAGES(state, count) {
        state.unreadMessages = count
    },
    SET_LOADING_CONVERSATIONS(state, loading) {
        state.loadingConversations = loading
    },
    ADD_CONVERSATION(state, conversation) {
        state.conversations = [conversation, ...state.conversations]
    },
    UPDATE_CONVERSATION(state, { id, data }) {
        const index = state.conversations.findIndex(conv => conv.id === id)
        if (index !== -1) {
            state.conversations[index] = { ...state.conversations[index], ...data }
        }
    },
    DELETE_CONVERSATION(state, id) {
        state.conversations = state.conversations.filter(conv => conv.id !== id)
        if (state.currentConversation && state.currentConversation.id === id) {
            state.currentConversation = null
        }
    }
}

const actions = {
    loadConversations({ commit }) {
        commit('SET_LOADING_CONVERSATIONS', true)
        // API call would go here
        // Return example data
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

    setCurrentConversation({ commit }, conversation) {
        commit('SET_CURRENT_CONVERSATION', conversation)
    },

    updateUnreadMessages({ commit, state }) {
        const unread = state.conversations.reduce((acc, conv) => acc + conv.unread, 0)
        commit('SET_UNREAD_MESSAGES', unread)
    },

    createConversation({ commit }, data) {
        const newConversation = {
            id: Date.now().toString(),
            timestamp: Date.now(),
            unread: 0,
            ...data
        }
        commit('ADD_CONVERSATION', newConversation)
        return newConversation
    },

    updateConversation({ commit }, payload) {
        commit('UPDATE_CONVERSATION', payload)
    },

    deleteConversation({ commit }, id) {
        commit('DELETE_CONVERSATION', id)
    }
}

const getters = {
    conversations: state => state.conversations,
    currentConversation: state => state.currentConversation,
    unreadMessages: state => state.unreadMessages,
    loadingConversations: state => state.loadingConversations,
    getConversationById: state => id => {
        return state.conversations.find(conv => conv.id === id)
    }
}

export default {
    namespaced: true,
    state,
    mutations,
    actions,
    getters
} 