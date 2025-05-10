import request from '@/utils/request'

export const getCoupons = (params) => {
    return request({
        url: '/coupons',
        method: 'get',
        params
    })
}

export const verifyCoupon = (code) => {
    return request({
        url: '/coupons/verify',
        method: 'post',
        data: { code }
    })
}

export const applyCoupon = (couponId, orderId) => {
    return request({
        url: '/coupons/apply',
        method: 'post',
        data: { couponId, orderId }
    })
}

export default {
    getCoupons,
    verifyCoupon,
    applyCoupon
} 