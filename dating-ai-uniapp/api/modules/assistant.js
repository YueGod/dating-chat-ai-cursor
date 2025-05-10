/**
 * AI助手相关API
 * 处理AI回复生成、使用统计和反馈相关功能
 */
import request from '@/utils/request'

export default {
    /**
     * 生成回复内容
     * @param {Object} data - 生成参数
     * @param {String} data.receivedMessage - 收到的消息内容
     * @param {Object} data.preferences - 生成偏好设置
     * @param {Array} data.preferences.styles - 风格列表，如['friendly', 'caring']
     * @param {String} data.preferences.length - 长度，如'short', 'medium', 'long'
     * @param {String} data.preferences.emotionalTone - 情感基调，如'positive', 'neutral'
     * @returns {Promise} 生成的回复内容列表和使用情况
     */
    generateReply(data) {
        return request({
            url: '/assistant/generate',
            method: 'post',
            data,
            loading: '生成回复中...'
        })
    },

    /**
     * 记录复制操作
     * 用于统计和分析用户行为
     * @param {Object} data - 复制记录
     * @param {String} data.replyId - 回复ID
     * @param {String} data.style - 回复风格
     * @returns {Promise} 记录结果
     */
    trackCopy(data) {
        return request({
            url: '/assistant/track/copy',
            method: 'post',
            data
        })
    },

    /**
     * 提交回复反馈
     * @param {Object} data - 反馈数据
     * @param {String} data.replyId - 回复ID
     * @param {String} data.feedback - 反馈类型，如'like', 'dislike'
     * @param {String} data.comment - 反馈评论（可选）
     * @returns {Promise} 提交结果
     */
    feedback(data) {
        return request({
            url: '/assistant/feedback',
            method: 'post',
            data
        })
    },

    /**
     * 获取生成历史记录
     * @param {Object} params - 查询参数
     * @param {Number} params.page - 页码
     * @param {Number} params.size - 每页数量
     * @param {String} params.style - 按风格筛选
     * @returns {Promise} 历史记录列表及分页信息
     */
    getHistory(params) {
        return request({
            url: '/assistant/history',
            method: 'get',
            params
        })
    },

    /**
     * 获取常用提示词
     * @returns {Promise} 提示词列表
     */
    getPrompts() {
        return request({
            url: '/assistant/prompts',
            method: 'get'
        })
    },

    /**
     * 保存自定义提示词
     * @param {Object} data - 提示词数据
     * @param {String} data.content - 提示词内容
     * @param {String} data.title - 提示词标题
     * @param {Array} data.tags - 提示词标签
     * @returns {Promise} 保存结果
     */
    savePrompt(data) {
        return request({
            url: '/assistant/prompts',
            method: 'post',
            data,
            loading: '保存中...'
        })
    }
}