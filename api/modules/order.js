import request from '@/utils/request'

export default {
    // 创建订单
    createOrder(data) {
        return request({
            url: '/orders',
            method: 'post',
            data
        })
    },

    // 获取订单详情
    getOrderDetail(orderId) {
        return request({
            url: `/orders/${orderId}`,
            method: 'get'
        })
    },

    // 获取订单列表
    getOrders(params) {
        return request({
            url: '/orders',
            method: 'get',
            params
        })
    },

    // 取消订单
    cancelOrder(orderId) {
        return request({
            url: `/orders/${orderId}/cancel`,
            method: 'post'
        })
    },

    // 验证支付结果
    verifyPayment(orderId) {
        return request({
            url: `/orders/${orderId}/verify`,
            method: 'post'
        })
    }
} 