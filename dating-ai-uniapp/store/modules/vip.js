const state = {
    isVip: false,
    vipExpireTime: null,
    vipLevel: 0,
    vipBenefits: [],
    vipPlans: []
}

const mutations = {
    SET_VIP_STATUS(state, isVip) {
        state.isVip = isVip
    },
    SET_VIP_EXPIRE_TIME(state, expireTime) {
        state.vipExpireTime = expireTime
    },
    SET_VIP_LEVEL(state, level) {
        state.vipLevel = level
    },
    SET_VIP_BENEFITS(state, benefits) {
        state.vipBenefits = benefits
    },
    SET_VIP_PLANS(state, plans) {
        state.vipPlans = plans
    }
}

const actions = {
    async getVipInfo({ commit }) {
        try {
            // Would make API call in real app
            const mockVipInfo = {
                isVip: true,
                expireTime: new Date(Date.now() + 30 * 24 * 60 * 60 * 1000).toISOString(),
                level: 1,
                benefits: [
                    '无限制使用生成功能',
                    '解锁全部高级风格',
                    'VIP专属客服'
                ]
            }

            commit('SET_VIP_STATUS', mockVipInfo.isVip)
            commit('SET_VIP_EXPIRE_TIME', mockVipInfo.expireTime)
            commit('SET_VIP_LEVEL', mockVipInfo.level)
            commit('SET_VIP_BENEFITS', mockVipInfo.benefits)

            return mockVipInfo
        } catch (error) {
            console.error('Failed to get VIP info', error)
            return null
        }
    },

    async getVipPlans({ commit }) {
        try {
            // Would make API call in real app
            const mockPlans = [
                {
                    id: 'monthly',
                    name: '月度会员',
                    price: 39.99,
                    originalPrice: 59.99,
                    period: 30,
                    features: [
                        '解锁全部风格',
                        '每日200次使用量',
                        '高级会话分析'
                    ]
                },
                {
                    id: 'quarterly',
                    name: '季度会员',
                    price: 99.99,
                    originalPrice: 129.99,
                    period: 90,
                    features: [
                        '解锁全部风格',
                        '每日200次使用量',
                        '高级会话分析',
                        '会员特别活动'
                    ]
                },
                {
                    id: 'yearly',
                    name: '年度会员',
                    price: 299.99,
                    originalPrice: 499.99,
                    period: 365,
                    features: [
                        '解锁全部风格',
                        '无限使用次数',
                        '高级会话分析',
                        '会员特别活动',
                        '专属客服通道'
                    ]
                }
            ]

            commit('SET_VIP_PLANS', mockPlans)
            return mockPlans
        } catch (error) {
            console.error('Failed to get VIP plans', error)
            return []
        }
    }
}

const getters = {
    isVip: state => state.isVip,
    vipExpireTime: state => state.vipExpireTime,
    vipLevel: state => state.vipLevel,
    vipBenefits: state => state.vipBenefits,
    vipPlans: state => state.vipPlans,

    // Calculate days remaining for VIP
    vipDaysRemaining: state => {
        if (!state.isVip || !state.vipExpireTime) return 0

        const now = new Date().getTime()
        const expireTime = new Date(state.vipExpireTime).getTime()
        const diffDays = Math.max(0, Math.ceil((expireTime - now) / (1000 * 60 * 60 * 24)))

        return diffDays
    }
}

export default {
    namespaced: true,
    state,
    mutations,
    actions,
    getters
} 