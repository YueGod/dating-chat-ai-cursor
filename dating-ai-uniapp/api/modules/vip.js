import request from '@/utils/request'

export const getVipInfo = () => {
    return request({
        url: '/vip/info',
        method: 'get'
    })
}

export const getVipPlans = () => {
    return request({
        url: '/vip/plans',
        method: 'get'
    })
}

export const createOrder = (data) => {
    return request({
        url: '/vip/orders',
        method: 'post',
        data
    })
}

export const verifyPayment = (orderId) => {
    return request({
        url: `/vip/orders/${orderId}/verify`,
        method: 'post'
    })
}

export const getMembershipInfo = () => {
    return request({
        url: '/vip/membership',
        method: 'get'
    })
}

export default {
    getVipInfo,
    getVipPlans,
    createOrder,
    verifyPayment,
    getMembershipInfo
} 