import request from '@/utils/request'

export const getMessages = (params) => {
    return request({
        url: '/messages',
        method: 'get',
        params
    })
}

export const sendMessage = (data) => {
    return request({
        url: '/messages',
        method: 'post',
        data
    })
}

export const deleteMessage = (id) => {
    return request({
        url: `/messages/${id}`,
        method: 'delete'
    })
}

export default {
    getMessages,
    sendMessage,
    deleteMessage
} 