<template>
  <view class="index-container">
    <!-- 状态栏高度占位 -->
    <view class="status-bar" :style="{ height: statusBarHeight + 'px' }"></view>
    
    <!-- 顶部导航栏 -->
    <view class="navbar">
      <view class="navbar__title">语撩AI</view>
    </view>
    
    <!-- 页面内容 -->
    <view class="content">
      <!-- 欢迎信息 -->
      <view class="welcome-card card">
        <view class="welcome-card__title">
          <text class="text-2xl text-bold">你好，{{userInfo ? userInfo.nickname : '新朋友'}}</text>
          <text v-if="isVip" class="tag tag-primary ml-2">VIP会员</text>
        </view>
        <view class="welcome-card__subtitle mt-2">AI驱动的恋爱聊天助手，让聊天更轻松</view>
        
        <!-- 使用次数统计 -->
        <view class="usage-stats mt-3">
          <view class="usage-stats__item">
            <text class="usage-stats__value text-xl text-primary text-bold">{{dailyRemainingCount}}</text>
            <text class="usage-stats__label text-sm">今日剩余次数</text>
          </view>
          <view class="usage-stats__item">
            <text class="usage-stats__value text-xl text-bold">{{usageStats ? usageStats.totalGeneration : 0}}</text>
            <text class="usage-stats__label text-sm">累计生成回复</text>
          </view>
          <view class="usage-stats__item">
            <text class="usage-stats__value text-xl text-bold">{{usageStats ? usageStats.totalCopied : 0}}</text>
            <text class="usage-stats__label text-sm">已使用回复</text>
          </view>
        </view>
      </view>
      
      <!-- 快捷功能区 -->
      <view class="feature-grid mt-4">
        <view class="feature-card" @tap="navigateTo('/pages/chat/chat')">
          <view class="feature-card__icon">
            <image src="/static/images/icons/chat.png" mode="aspectFit"></image>
          </view>
          <text class="feature-card__title">聊天记录</text>
        </view>
        <view class="feature-card" @tap="navigateTo('/pages/assistant/assistant')">
          <view class="feature-card__icon">
            <image src="/static/images/icons/assistant.png" mode="aspectFit"></image>
          </view>
          <text class="feature-card__title">AI助手</text>
        </view>
        <view class="feature-card" @tap="navigateTo('/pages/vip/vip')">
          <view class="feature-card__icon">
            <image src="/static/images/icons/vip.png" mode="aspectFit"></image>
          </view>
          <text class="feature-card__title">会员中心</text>
        </view>
        <view class="feature-card" @tap="navigateTo('/pages/profile/profile')">
          <view class="feature-card__icon">
            <image src="/static/images/icons/profile.png" mode="aspectFit"></image>
          </view>
          <text class="feature-card__title">个人中心</text>
        </view>
      </view>
      
      <!-- 最近聊天 -->
      <view class="recent-chats mt-4">
        <view class="section-header">
          <text class="section-header__title">最近聊天</text>
          <text class="section-header__more" @tap="navigateTo('/pages/chat/chat')">查看全部</text>
        </view>
        
        <view v-if="conversations && conversations.length > 0" class="recent-chat-list">
          <view 
            v-for="(conversation, index) in conversations.slice(0, 3)" 
            :key="index" 
            class="chat-item" 
            @tap="navigateToChat(conversation.conversationId)"
          >
            <view class="chat-item__avatar">
              <image :src="conversation.contactAvatar || '/static/images/default-avatar.png'" mode="aspectFill"></image>
            </view>
            <view class="chat-item__content">
              <view class="chat-item__header">
                <text class="chat-item__name">{{conversation.contactName}}</text>
                <text class="chat-item__time">{{formatTime(conversation.lastMessage.timestamp)}}</text>
              </view>
              <view class="chat-item__message text-ellipsis">{{conversation.lastMessage.content}}</view>
            </view>
          </view>
        </view>
        
        <view v-else class="empty-state">
          <image src="/static/images/empty-chat.png" mode="aspectFit" class="empty-state__image"></image>
          <text class="empty-state__text">暂无聊天记录</text>
          <view class="btn btn-primary mt-3" @tap="navigateTo('/pages/assistant/assistant')">开始聊天</view>
        </view>
      </view>
      
      <!-- 风格推荐 -->
      <view class="style-recommendations mt-4">
        <view class="section-header">
          <text class="section-header__title">风格推荐</text>
          <text class="section-header__more" @tap="navigateTo('/pages/assistant/assistant')">更多风格</text>
        </view>
        
        <scroll-view scroll-x class="style-scroll-view" show-scrollbar="false">
          <view class="style-card" @tap="navigateToAssistant('casual')">
            <view class="style-card__icon">
              <image src="/static/images/styles/casual.png" mode="aspectFit"></image>
            </view>
            <text class="style-card__title">随性自然</text>
          </view>
          <view class="style-card" @tap="navigateToAssistant('humorous')">
            <view class="style-card__icon">
              <image src="/static/images/styles/humorous.png" mode="aspectFit"></image>
            </view>
            <text class="style-card__title">幽默风趣</text>
          </view>
          <view class="style-card" @tap="navigateToAssistant('romantic')">
            <view class="style-card__icon">
              <image src="/static/images/styles/romantic.png" mode="aspectFit"></image>
            </view>
            <text class="style-card__title">浪漫甜蜜</text>
            <view class="style-card__badge" v-if="!isVip">VIP</view>
          </view>
          <view class="style-card" @tap="navigateToAssistant('intellectual')">
            <view class="style-card__icon">
              <image src="/static/images/styles/intellectual.png" mode="aspectFit"></image>
            </view>
            <text class="style-card__title">才华横溢</text>
            <view class="style-card__badge" v-if="!isVip">VIP</view>
          </view>
        </scroll-view>
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
      loading: false
    }
  },
  computed: {
    ...mapGetters({
      userInfo: 'user/userInfo',
      isVip: 'user/isVip',
      statusBarHeight: 'app/statusBarHeight',
      usageStats: 'user/usageStats',
      dailyRemainingCount: 'user/dailyRemainingCount'
    })
  },
  onLoad() {
    this.initData()
  },
  onShow() {
    // 刷新数据
    this.loadConversations()
    this.loadUserStats()
  },
  methods: {
    // 初始化数据
    async initData() {
      this.loading = true
      try {
        await Promise.all([
          this.loadConversations(),
          this.loadUserStats()
        ])
      } catch (error) {
        this.$log.error('初始化数据失败', error)
      } finally {
        this.loading = false
      }
    },
    
    // 加载会话列表
    async loadConversations() {
      try {
        const result = await this.$api.conversation.getConversations(1, 3)
        this.conversations = result.conversations || []
      } catch (error) {
        this.$log.error('加载会话列表失败', error)
      }
    },
    
    // 加载用户使用统计
    loadUserStats() {
      return Promise.all([
        this.$store.dispatch('user/getUserUsageStats'),
        this.$store.dispatch('user/getUserLimits')
      ])
    },
    
    // 页面导航
    navigateTo(url) {
      uni.navigateTo({ url })
    },
    
    // 导航到聊天详情
    navigateToChat(conversationId) {
      uni.navigateTo({
        url: `/pages/chat/chat-detail?id=${conversationId}`
      })
    },
    
    // 导航到助手并设置风格
    navigateToAssistant(style) {
      uni.navigateTo({
        url: `/pages/assistant/assistant?style=${style}`
      })
    },
    
    // 格式化时间
    formatTime(time) {
      return formatRelativeTime(time)
    }
  }
}
</script>

<style lang="scss">
.index-container {
  min-height: 100vh;
  background-color: #f5f5f5;
}

.navbar {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 44px;
  background: linear-gradient(135deg, #ff6b6b, #fc466b);
  color: #ffffff;
  position: relative;
  z-index: 100;
  
  &__title {
    font-size: 18px;
    font-weight: bold;
  }
}

.content {
  padding: 20rpx;
}

.welcome-card {
  background: #ffffff;
  padding: 30rpx;
  
  &__title {
    display: flex;
    align-items: center;
  }
  
  &__subtitle {
    color: #666666;
    font-size: 28rpx;
  }
}

.usage-stats {
  display: flex;
  justify-content: space-around;
  margin-top: 20rpx;
  
  &__item {
    display: flex;
    flex-direction: column;
    align-items: center;
  }
  
  &__value {
    margin-bottom: 6rpx;
  }
  
  &__label {
    color: #999999;
  }
}

.feature-grid {
  display: flex;
  flex-wrap: wrap;
  margin: 0 -10rpx;
}

.feature-card {
  width: calc(50% - 20rpx);
  margin: 10rpx;
  height: 180rpx;
  background: #ffffff;
  border-radius: 12rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  
  &__icon {
    width: 80rpx;
    height: 80rpx;
    margin-bottom: 16rpx;
    
    image {
      width: 100%;
      height: 100%;
    }
  }
  
  &__title {
    font-size: 28rpx;
    color: #333333;
  }
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20rpx;
  
  &__title {
    font-size: 32rpx;
    font-weight: bold;
    color: #333333;
  }
  
  &__more {
    font-size: 24rpx;
    color: #999999;
  }
}

.recent-chat-list {
  background: #ffffff;
  border-radius: 12rpx;
  overflow: hidden;
}

.chat-item {
  display: flex;
  padding: 20rpx;
  border-bottom: 1px solid #f5f5f5;
  
  &:last-child {
    border-bottom: none;
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
  
  &__content {
    flex: 1;
    overflow: hidden;
  }
  
  &__header {
    display: flex;
    justify-content: space-between;
    margin-bottom: 6rpx;
  }
  
  &__name {
    font-size: 28rpx;
    font-weight: bold;
    color: #333333;
  }
  
  &__time {
    font-size: 24rpx;
    color: #999999;
  }
  
  &__message {
    font-size: 26rpx;
    color: #666666;
    width: 100%;
  }
}

.empty-state {
  background: #ffffff;
  border-radius: 12rpx;
  padding: 60rpx 30rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  
  &__image {
    width: 200rpx;
    height: 200rpx;
    margin-bottom: 30rpx;
  }
  
  &__text {
    font-size: 28rpx;
    color: #999999;
    margin-bottom: 30rpx;
  }
}

.style-scroll-view {
  white-space: nowrap;
  padding: 10rpx 0;
}

.style-card {
  display: inline-block;
  width: 200rpx;
  height: 240rpx;
  background: #ffffff;
  border-radius: 12rpx;
  margin-right: 20rpx;
  padding: 20rpx;
  position: relative;
  
  &__icon {
    width: 100rpx;
    height: 100rpx;
    margin: 0 auto 20rpx;
    
    image {
      width: 100%;
      height: 100%;
    }
  }
  
  &__title {
    font-size: 28rpx;
    color: #333333;
    text-align: center;
    display: block;
  }
  
  &__badge {
    position: absolute;
    top: 10rpx;
    right: 10rpx;
    background: linear-gradient(135deg, #ff6b6b, #fc466b);
    color: #ffffff;
    font-size: 20rpx;
    padding: 4rpx 10rpx;
    border-radius: 10rpx;
  }
}
</style> 