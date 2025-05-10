const state = {
    dailyRemainingCount: 20,
    dailyTotalCount: 30,
    recentPrompts: [],
    favoritePrompts: [],
    settings: {
        style: 'friendly',
        tone: 'positive',
        length: 'medium'
    }
}

const mutations = {
    SET_DAILY_REMAINING_COUNT(state, count) {
        state.dailyRemainingCount = count
    },
    SET_DAILY_TOTAL_COUNT(state, count) {
        state.dailyTotalCount = count
    },
    SET_RECENT_PROMPTS(state, prompts) {
        state.recentPrompts = prompts
    },
    SET_FAVORITE_PROMPTS(state, prompts) {
        state.favoritePrompts = prompts
    },
    ADD_RECENT_PROMPT(state, prompt) {
        if (!state.recentPrompts.some(p => p.id === prompt.id)) {
            state.recentPrompts = [prompt, ...state.recentPrompts].slice(0, 10)
        }
    },
    TOGGLE_FAVORITE_PROMPT(state, promptId) {
        const index = state.favoritePrompts.findIndex(p => p.id === promptId)
        if (index !== -1) {
            state.favoritePrompts.splice(index, 1)
        } else {
            const prompt = state.recentPrompts.find(p => p.id === promptId)
            if (prompt) {
                state.favoritePrompts.push(prompt)
            }
        }
    },
    UPDATE_SETTINGS(state, settings) {
        state.settings = { ...state.settings, ...settings }
    }
}

const actions = {
    async getUserLimits({ commit }) {
        try {
            // API call would go here
            // For now, return mock data
            const result = {
                remaining: 18,
                total: 30
            }

            commit('SET_DAILY_REMAINING_COUNT', result.remaining)
            commit('SET_DAILY_TOTAL_COUNT', result.total)
            return result
        } catch (error) {
            console.error('Failed to get user limits', error)
            return null
        }
    },

    setRecentPrompts({ commit }, prompts) {
        commit('SET_RECENT_PROMPTS', prompts)
    },

    setFavoritePrompts({ commit }, prompts) {
        commit('SET_FAVORITE_PROMPTS', prompts)
    },

    addRecentPrompt({ commit }, prompt) {
        commit('ADD_RECENT_PROMPT', prompt)
    },

    toggleFavoritePrompt({ commit }, promptId) {
        commit('TOGGLE_FAVORITE_PROMPT', promptId)
    },

    updateSettings({ commit }, settings) {
        commit('UPDATE_SETTINGS', settings)
    }
}

const getters = {
    dailyRemainingCount: state => state.dailyRemainingCount,
    dailyTotalCount: state => state.dailyTotalCount,
    recentPrompts: state => state.recentPrompts,
    favoritePrompts: state => state.favoritePrompts,
    settings: state => state.settings,
    isPromptFavorited: state => promptId => {
        return state.favoritePrompts.some(p => p.id === promptId)
    }
}

export default {
    namespaced: true,
    state,
    mutations,
    actions,
    getters
} 