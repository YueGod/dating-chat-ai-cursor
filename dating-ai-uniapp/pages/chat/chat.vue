<template>
  <view class="chat-container">
    <!-- 状态栏高度占位 -->
    <view class="status-bar" :style="{ height: statusBarHeight + 'px' }"></view>
    
    <!-- 顶部搜索区域 -->
    <view class="search-header">
      <view class="search-bar">
        <view class="search-bar__icon">
          <image src="/static/images/icons/search.png" mode="aspectFit"></image>
        </view>
        <input 
          class="search-bar__input" 
          type="text" 
          placeholder="搜索聊天记录" 
          confirm-type="search"
          v-model="searchKeyword"
          @confirm="handleSearch"
        />
      </view>
      <view class="add-chat" @tap="createNewChat">
        <image src="/static/images/icons/add.png" mode="aspectFit"></image>
      </view>
    </view>
    
    <!-- 会话列表 -->
    <scroll-view 
      class="chat-list"
      scroll-y
      :refresher-enabled="true"
      :refresher-triggered="refreshing"
      @refresherrefresh="onRefresh"
      @scrolltolower="loadMore"
    >
      <!-- 空数据展示 -->
      <view v-if="conversations.length === 0 && !loading" class="empty-state">
        <image src="/static/images/empty-chat.png" mode="aspectFit" class="empty-state__image"></image>
        <text class="empty-state__text">暂无聊天记录</text>
        <view class="btn btn-primary mt-3" @tap="navigateTo('/pages/assistant/assistant')">开始聊天</view>
      </view>
      
      <!-- 会话列表项 -->
      <view 
        v-for="(item, index) in conversations" 
        :key="item.conversationId" 
        class="chat-item"
        @tap="navigateToChat(item.conversationId)"
      >
        <!-- 滑动操作区域 -->
        <view class="chat-item__left-actions" v-if="item.showLeftActions">
          <view 
            class="action-btn action-btn--star" 
            :class="{'action-btn--active': item.isStarred}"
            @tap.stop="toggleStar(item)"
          >
            <image :src="item.isStarred ? '/static/images/icons/star-filled.png' : '/static/images/icons/star.png'" mode="aspectFit"></image>
          </view>
        </view>
        
        <!-- 会话内容 -->
        <view class="chat-item__content">
          <view class="chat-item__avatar">
            <image :src="item.contactAvatar || '/static/images/default-avatar.png'" mode="aspectFill"></image>
          </view>
          <view class="chat-item__main">
            <view class="chat-item__header">
              <text class="chat-item__name">{{item.contactName}}</text>
              <text class="chat-item__time">{{formatTime(item.lastMessage.timestamp)}}</text>
            </view>
            <view class="chat-item__info">
              <text class="chat-item__message">{{item.lastMessage.content}}</text>
              <view class="chat-item__badge" v-if="item.unreadCount > 0">{{item.unreadCount > 99 ? '99+' : item.unreadCount}}</view>
            </view>
            <view class="chat-item__tags" v-if="item.tags && item.tags.length > 0">
              <view 
                v-for="tag in item.tags.slice(0, 2)" 
                :key="tag.tagId" 
                class="chat-tag"
                :style="{ backgroundColor: tag.color + '20', color: tag.color }"
              >
                {{tag.name}}
              </view>
            </view>
          </view>
        </view>
        
        <!-- 滑动操作区域 -->
        <view class="chat-item__right-actions" v-if="item.showRightActions">
          <view 
            class="action-btn action-btn--tag" 
            @tap.stop="showTagsModal(item)"
          >
            <image src="/static/images/icons/tag.png" mode="aspectFit"></image>
          </view>
          <view 
            class="action-btn action-btn--delete" 
            @tap.stop="confirmDelete(item)"
          >
            <image src="/static/images/icons/delete.png" mode="aspectFit"></image>
          </view>
        </view>
      </view>
      
      <!-- 加载更多 -->
      <view class="loading-more" v-if="hasMoreData && conversations.length > 0">
        <text v-if="loadingMore">加载中...</text>
        <text v-else @tap="loadMore">加载更多</text>
      </view>
    </scroll-view>
    
    <!-- 标签选择弹窗 -->
    <view class="tags-modal" v-if="showTagModal">
      <view class="tags-modal__mask" @tap="hideTagsModal"></view>
      <view class="tags-modal__content">
        <view class="tags-modal__header">
          <text class="tags-modal__title">选择标签</text>
          <view class="tags-modal__close" @tap="hideTagsModal">
            <image src="/static/images/icons/close.png" mode="aspectFit"></image>
          </view>
        </view>
        <view class="tags-modal__body">
          <view class="tags-list">
            <view 
              v-for="tag in allTags" 
              :key="tag.tagId" 
              class="tag-item"
              :class="{'tag-item--selected': isTagSelected(tag.tagId)}"
              @tap="toggleTag(tag.tagId)"
            >
              <view class="tag-item__color" :style="{ backgroundColor: tag.color }"></view>
              <text class="tag-item__name">{{tag.name}}</text>
              <view class="tag-item__check" v-if="isTagSelected(tag.tagId)">
                <image src="/static/images/icons/check.png" mode="aspectFit"></image>
              </view>
            </view>
          </view>
          <view class="btn btn-primary full-width mt-4" @tap="saveTagChanges">确认</view>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import { mapGetters } from 'vuex'
import { formatRelativeTime } from '@/utils/common'

export default {
  data() {
    return {
      conversations: [],
      allTags: [],
      searchKeyword: '',
      loading: false,
      refreshing: false,
      loadingMore: false,
      hasMoreData: true,
      currentPage: 1,
      pageSize: 20,
      showTagModal: false,
      currentConversation: null,
      selectedTags: []
    }
  },
  computed: {
    ...mapGetters({
      statusBarHeight: 'app/statusBarHeight'
    })
  },
  onLoad() {
    this.loadConversations()
    this.loadTags()
  },
  onShow() {
    // 每次页面显示都刷新会话列表
    this.refreshConversations()
  },
  methods: {
    // 加载会话列表
    async loadConversations(refresh = false) {
      if (refresh) {
        this.currentPage = 1
        this.hasMoreData = true
      }
      
      if (!this.hasMoreData && !refresh) return
      
      this.loading = true
      
      try {
        const params = {
          page: this.currentPage,
          size: this.pageSize,
          sortBy: 'lastMessageTime',
          sortOrder: 'desc'
        }
        
        if (this.searchKeyword) {
          params.keyword = this.searchKeyword
        }
        
        const result = await this.$api.conversation.getConversations(params)
        
        const { conversations, pagination } = result
        
        if (refresh) {
          this.conversations = conversations || []
        } else {
          this.conversations = [...this.conversations, ...(conversations || [])]
        }
        
        this.hasMoreData = pagination.currentPage < pagination.totalPages
        
        if (this.hasMoreData) {
          this.currentPage++
        }
      } catch (error) {
        this.$log.error('加载会话列表失败', error)
        this.$toast('加载失败，请重试')
      } finally {
        this.loading = false
        this.refreshing = false
        this.loadingMore = false
      }
    },
    
    // 刷新会话列表
    refreshConversations() {
      this.loadConversations(true)
    },
    
    // 下拉刷新
    onRefresh() {
      this.refreshing = true
      this.refreshConversations()
    },
    
    // 加载更多
    loadMore() {
      if (this.loadingMore || !this.hasMoreData) return
      
      this.loadingMore = true
      this.loadConversations()
    },
    
    // 加载标签列表
    async loadTags() {
      try {
        const result = await this.$api.tag.getTags()
        this.allTags = result || []
      } catch (error) {
        this.$log.error('加载标签列表失败', error)
      }
    },
    
    // 搜索会话
    handleSearch() {
      this.refreshConversations()
    },
    
    // 创建新会话
    createNewChat() {
      uni.navigateTo({
        url: '/pages/assistant/assistant'
      })
    },
    
    // 导航到聊天详情
    navigateToChat(conversationId) {
      uni.navigateTo({
        url: `/pages/chat/chat-detail?id=${conversationId}`
      })
    },
    
    // 切换会话星标状态
    async toggleStar(conversation) {
      const isStarred = !conversation.isStarred
      try {
        await this.$api.conversation.updateConversation(conversation.conversationId, { isStarred })
        
        // 更新本地数据
        const index = this.conversations.findIndex(c => c.conversationId === conversation.conversationId)
        if (index !== -1) {
          this.conversations[index].isStarred = isStarred
        }
      } catch (error) {
        this.$log.error('更新星标状态失败', error)
        this.$toast('操作失败，请重试')
      }
    },
    
    // 显示标签弹窗
    showTagsModal(conversation) {
      this.currentConversation = conversation
      this.selectedTags = conversation.tags.map(t => t.tagId)
      this.showTagModal = true
    },
    
    // 隐藏标签弹窗
    hideTagsModal() {
      this.showTagModal = false
      this.currentConversation = null
      this.selectedTags = []
    },
    
    // 判断标签是否已选中
    isTagSelected(tagId) {
      return this.selectedTags.includes(tagId)
    },
    
    // 切换标签选中状态
    toggleTag(tagId) {
      const index = this.selectedTags.indexOf(tagId)
      if (index !== -1) {
        this.selectedTags.splice(index, 1)
      } else {
        this.selectedTags.push(tagId)
      }
    },
    
    // 保存标签更改
    async saveTagChanges() {
      if (!this.currentConversation) return
      
      try {
        await this.$api.conversation.manageTags(this.currentConversation.conversationId, {
          action: 'set',
          tags: this.selectedTags
        })
        
        // 更新本地数据
        const index = this.conversations.findIndex(c => c.conversationId === this.currentConversation.conversationId)
        if (index !== -1) {
          const updatedTags = this.allTags.filter(t => this.selectedTags.includes(t.tagId))
          this.conversations[index].tags = updatedTags
        }
        
        this.hideTagsModal()
        this.$toast('标签已更新')
      } catch (error) {
        this.$log.error('更新标签失败', error)
        this.$toast('操作失败，请重试')
      }
    },
    
    // 确认删除会话
    async confirmDelete(conversation) {
      const result = await this.$modal('确定删除此会话吗？删除后无法恢复。')
      
      if (result) {
        try {
          await this.$api.conversation.deleteConversation(conversation.conversationId)
          
          // 更新本地数据
          const index = this.conversations.findIndex(c => c.conversationId === conversation.conversationId)
          if (index !== -1) {
            this.conversations.splice(index, 1)
          }
          
          this.$toast('删除成功')
        } catch (error) {
          this.$log.error('删除会话失败', error)
          this.$toast('删除失败，请重试')
        }
      }
    },
    
    // 格式化时间
    formatTime(time) {
      return formatRelativeTime(time)
    },
    
    // 页面导航
    navigateTo(url) {
      uni.navigateTo({ url })
    }
  }
}
</script>

<style lang="scss">
.chat-container {
  min-height: 100vh;
  background-color: #f5f5f5;
  display: flex;
  flex-direction: column;
}

.search-header {
  padding: 20rpx 30rpx;
  background-color: #fff;
  display: flex;
  align-items: center;
  box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
  position: relative;
  z-index: 100;
}

.search-bar {
  flex: 1;
  height: 70rpx;
  background-color: #f5f5f5;
  border-radius: 35rpx;
  display: flex;
  align-items: center;
  padding: 0 20rpx;
  
  &__icon {
    width: 40rpx;
    height: 40rpx;
    margin-right: 10rpx;
    
    image {
      width: 100%;
      height: 100%;
    }
  }
  
  &__input {
    flex: 1;
    height: 100%;
    font-size: 28rpx;
  }
}

.add-chat {
  width: 70rpx;
  height: 70rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-left: 20rpx;
  
  image {
    width: 48rpx;
    height: 48rpx;
  }
}

.chat-list {
  flex: 1;
  background-color: #f5f5f5;
}

.chat-item {
  background-color: #fff;
  margin-bottom: 2rpx;
  position: relative;
  display: flex;
  overflow: hidden;
  
  &__left-actions,
  &__right-actions {
    position: absolute;
    top: 0;
    height: 100%;
    display: flex;
    align-items: center;
  }
  
  &__left-actions {
    left: 0;
    transform: translateX(-100%);
  }
  
  &__right-actions {
    right: 0;
    transform: translateX(100%);
  }
  
  &__content {
    width: 100%;
    padding: 20rpx 30rpx;
    display: flex;
    align-items: center;
    z-index: 1;
    background: #fff;
  }
  
  &__avatar {
    width: 80rpx;
    height: 80rpx;
    border-radius: 50%;
    overflow: hidden;
    margin-right: 20rpx;
    
    image {
      width: 100%;
      height: 100%;
    }
  }
  
  &__main {
    flex: 1;
    overflow: hidden;
  }
  
  &__header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 8rpx;
  }
  
  &__name {
    font-size: 30rpx;
    font-weight: 500;
    color: #333;
  }
  
  &__time {
    font-size: 24rpx;
    color: #999;
  }
  
  &__info {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 8rpx;
  }
  
  &__message {
    flex: 1;
    font-size: 26rpx;
    color: #666;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
  
  &__badge {
    min-width: 40rpx;
    height: 40rpx;
    border-radius: 20rpx;
    background-color: #fc466b;
    color: #fff;
    font-size: 24rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 0 8rpx;
    margin-left: 10rpx;
  }
  
  &__tags {
    display: flex;
    flex-wrap: wrap;
  }
}

.chat-tag {
  font-size: 22rpx;
  padding: 2rpx 10rpx;
  border-radius: 4rpx;
  margin-right: 10rpx;
  max-width: 120rpx;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.action-btn {
  width: 120rpx;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  
  image {
    width: 44rpx;
    height: 44rpx;
  }
  
  &--star {
    background-color: #ffbb33;
    
    &.action-btn--active {
      background-color: #ff9500;
    }
  }
  
  &--tag {
    background-color: #33b5e5;
  }
  
  &--delete {
    background-color: #ff4444;
  }
}

.empty-state {
  padding: 100rpx 30rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  
  &__image {
    width: 240rpx;
    height: 240rpx;
    margin-bottom: 30rpx;
  }
  
  &__text {
    font-size: 30rpx;
    color: #999;
    margin-bottom: 40rpx;
  }
}

.loading-more {
  padding: 20rpx 0;
  text-align: center;
  font-size: 26rpx;
  color: #999;
}

.tags-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 1000;
  
  &__mask {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background-color: rgba(0, 0, 0, 0.5);
  }
  
  &__content {
    position: absolute;
    left: 0;
    right: 0;
    bottom: 0;
    background-color: #fff;
    border-radius: 20rpx 20rpx 0 0;
    overflow: hidden;
    padding-bottom: calc(env(safe-area-inset-bottom) + 30rpx);
  }
  
  &__header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 30rpx;
    border-bottom: 1rpx solid #eee;
  }
  
  &__title {
    font-size: 32rpx;
    font-weight: 500;
    color: #333;
  }
  
  &__close {
    width: 44rpx;
    height: 44rpx;
    
    image {
      width: 100%;
      height: 100%;
    }
  }
  
  &__body {
    padding: 30rpx;
  }
}

.tags-list {
  display: flex;
  flex-wrap: wrap;
  margin: 0 -10rpx;
}

.tag-item {
  width: calc(33.333% - 20rpx);
  height: 80rpx;
  margin: 10rpx;
  border: 1rpx solid #eee;
  border-radius: 8rpx;
  display: flex;
  align-items: center;
  padding: 0 15rpx;
  position: relative;
  
  &--selected {
    border-color: #fc466b;
    background-color: rgba(252, 70, 107, 0.05);
  }
  
  &__color {
    width: 20rpx;
    height: 20rpx;
    border-radius: 4rpx;
    margin-right: 10rpx;
  }
  
  &__name {
    flex: 1;
    font-size: 26rpx;
    color: #333;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
  
  &__check {
    width: 36rpx;
    height: 36rpx;
    
    image {
      width: 100%;
      height: 100%;
    }
  }
}
</style> 