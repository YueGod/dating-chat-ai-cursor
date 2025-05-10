/**
 * 会话相关API
 * 处理会话列表、会话详情和消息相关功能
 */
import request from '@/utils/request'

export default {
    /**
     * 获取会话列表
     * @param {Object} params - 查询参数
     * @param {Number} params.page - 页码
     * @param {Number} params.size - 每页数量
     * @param {String} params.keyword - 搜索关键词
     * @returns {Promise} 会话列表及分页信息
     */
    getConversations(params) {
        return request({
            url: '/conversations',
            method: 'get',
            params,
            loading: '加载中...'
        })
    },

    /**
     * 获取单个会话详情
     * @param {String} id - 会话ID
     * @returns {Promise} 会话详细信息
     */
    getConversationInfo(id) {
        return request({
            url: `/conversations/${id}`,
            method: 'get'
        })
    },

    /**
     * 创建新会话
     * @param {Object} data - 会话数据
     * @param {String} data.contactName - 联系人名称
     * @param {String} data.platform - 平台类型，如'manual', 'wechat'等
     * @returns {Promise} 创建的会话信息
     */
    createConversation(data) {
        return request({
            url: '/conversations',
            method: 'post',
            data,
            loading: '创建会话中...'
        })
    },

    /**
     * 更新会话信息
     * @param {String} id - 会话ID
     * @param {Object} data - 更新的会话数据
     * @returns {Promise} 更新后的会话信息
     */
    updateConversation(id, data) {
        return request({
            url: `/conversations/${id}`,
            method: 'put',
            data
        })
    },

    /**
     * 删除会话
     * @param {String} id - 会话ID
     * @returns {Promise} 删除结果
     */
    deleteConversation(id) {
        return request({
            url: `/conversations/${id}`,
            method: 'delete',
            loading: '删除中...'
        })
    },

    /**
     * 获取会话消息列表
     * @param {String} conversationId - 会话ID
     * @param {Object} params - 查询参数
     * @param {Number} params.page - 页码
     * @param {Number} params.size - 每页数量
     * @param {String} params.before - 获取此消息ID之前的消息
     * @returns {Promise} 消息列表及分页信息
     */
    getMessages(conversationId, params) {
        return request({
            url: `/conversations/${conversationId}/messages`,
            method: 'get',
            params
        })
    },

    /**
     * 添加消息到会话
     * @param {String} conversationId - 会话ID
     * @param {Object} data - 消息数据
     * @param {String} data.content - 消息内容
     * @param {String} data.type - 消息类型，如'sent', 'received', 'generated'
     * @returns {Promise} 添加的消息信息
     */
    addMessage(conversationId, data) {
        return request({
            url: `/conversations/${conversationId}/messages`,
            method: 'post',
            data,
            loading: '保存中...'
        })
    },

    /**
     * 删除消息
     * @param {String} conversationId - 会话ID
     * @param {String} messageId - 消息ID
     * @returns {Promise} 删除结果
     */
    deleteMessage(conversationId, messageId) {
        return request({
            url: `/conversations/${conversationId}/messages/${messageId}`,
            method: 'delete',
            loading: '删除中...'
        })
    }
} 