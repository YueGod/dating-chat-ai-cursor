import request from '@/utils/request'

export default {
    // 生成回复
    generateReply(data) {
        return request({
            url: '/assistant/generate',
            method: 'post',
            data
        })
    },

    // 记录复制操作
    trackCopy(data) {
        return request({
            url: '/assistant/track/copy',
            method: 'post',
            data
        })
    },

    // 提交反馈
    feedback(data) {
        return request({
            url: '/assistant/feedback',
            method: 'post',
            data
        })
    },

    // 获取生成历史
    getHistory(params) {
        return request({
            url: '/assistant/history',
            method: 'get',
            params
        })
    },

    // 获取常用提示词
    getPrompts() {
        return request({
            url: '/assistant/prompts',
            method: 'get'
        })
    },

    // 保存提示词
    savePrompt(data) {
        return request({
            url: '/assistant/prompts',
            method: 'post',
            data
        })
    }
} 