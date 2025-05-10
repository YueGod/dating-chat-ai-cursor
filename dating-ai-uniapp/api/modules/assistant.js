/**
 * AI助手相关API
 */
import { post } from '../../utils/request'

/**
 * 生成回复
 * @param {Object} data 生成参数
 * @param {String} data.conversationId 会话ID
 * @param {String} data.receivedMessage 接收到的消息
 * @param {Array} data.styles 回复风格
 * @param {Array} data.previousMessages 历史消息
 * @param {Object} data.context 上下文信息
 * @returns {Promise} Promise对象
 */
export const generateReplies = (data) => {
    return post('/assistant/generate', data, { loading: '生成回复中...' })
}

/**
 * 回复反馈
 * @param {Object} data 反馈信息
 * @param {String} data.replyId 回复ID
 * @param {String} data.feedbackType 反馈类型，可选值：like、dislike、copy、used
 * @param {String} data.comment 反馈内容
 * @returns {Promise} Promise对象
 */
export const provideFeedback = (data) => {
    return post('/assistant/feedback', data)
} 