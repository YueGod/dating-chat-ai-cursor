import request from '@/utils/request'

export const createPayment = (data) => {
    return request({
        url: '/payments',
        method: 'post',
        data
    })
}

export const getPaymentStatus = (paymentId) => {
    return request({
        url: `/payments/${paymentId}/status`,
        method: 'get'
    })
}

export const getPaymentHistory = (params) => {
    return request({
        url: '/payments/history',
        method: 'get',
        params
    })
}

export default {
    createPayment,
    getPaymentStatus,
    getPaymentHistory
} 