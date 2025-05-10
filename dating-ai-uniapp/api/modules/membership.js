import request from '@/utils/request'

export default {
    // 获取会员信息
    getMembershipInfo() {
        return request({
            url: '/membership',
            method: 'get'
        })
    },

    // 获取会员价格
    getMembershipPlans() {
        return request({
            url: '/membership/plans',
            method: 'get'
        })
    },

    // 获取会员特权
    getMembershipBenefits() {
        return request({
            url: '/membership/benefits',
            method: 'get'
        })
    },

    // 更新自动续费状态
    updateAutoRenew(data) {
        return request({
            url: '/membership/autorenew',
            method: 'put',
            data
        })
    },

    // 取消会员
    cancelMembership() {
        return request({
            url: '/membership/cancel',
            method: 'post'
        })
    },

    // 验证优惠券
    verifyCoupon(data) {
        return request({
            url: '/membership/coupon/verify',
            method: 'post',
            data
        })
    }
} 