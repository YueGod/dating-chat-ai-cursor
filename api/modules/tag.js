import request from '@/utils/request'

export default {
    // 获取所有标签
    getTags() {
        return request({
            url: '/tags',
            method: 'get'
        })
    },

    // 获取热门标签
    getHotTags() {
        return request({
            url: '/tags/hot',
            method: 'get'
        })
    },

    // 按类别获取标签
    getTagsByCategory(category) {
        return request({
            url: `/tags/category/${category}`,
            method: 'get'
        })
    },

    // 为对话添加标签
    addTagToConversation(conversationId, tagId) {
        return request({
            url: `/conversations/${conversationId}/tags/${tagId}`,
            method: 'post'
        })
    },

    // 从对话移除标签
    removeTagFromConversation(conversationId, tagId) {
        return request({
            url: `/conversations/${conversationId}/tags/${tagId}`,
            method: 'delete'
        })
    },

    // 创建自定义标签
    createTag(data) {
        return request({
            url: '/tags',
            method: 'post',
            data
        })
    },

    // 删除自定义标签
    deleteTag(id) {
        return request({
            url: `/tags/${id}`,
            method: 'delete'
        })
    }
} 