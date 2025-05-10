import request from '@/utils/request'

export default {
    // 获取会话列表
    getConversations(params) {
        return request({
            url: '/conversations',
            method: 'get',
            params
        })
    },

    // 获取单个会话详情
    getConversationInfo(id) {
        return request({
            url: `/conversations/${id}`,
            method: 'get'
        })
    },

    // 创建新会话
    createConversation(data) {
        return request({
            url: '/conversations',
            method: 'post',
            data
        })
    },

    // 更新会话信息
    updateConversation(id, data) {
        return request({
            url: `/conversations/${id}`,
            method: 'put',
            data
        })
    },

    // 删除会话
    deleteConversation(id) {
        return request({
            url: `/conversations/${id}`,
            method: 'delete'
        })
    },

    // 获取会话消息列表
    getMessages(conversationId, params) {
        return request({
            url: `/conversations/${conversationId}/messages`,
            method: 'get',
            params
        })
    },

    // 添加消息到会话
    addMessage(conversationId, data) {
        return request({
            url: `/conversations/${conversationId}/messages`,
            method: 'post',
            data
        })
    },

    // 删除消息
    deleteMessage(conversationId, messageId) {
        return request({
            url: `/conversations/${conversationId}/messages/${messageId}`,
            method: 'delete'
        })
    }
}