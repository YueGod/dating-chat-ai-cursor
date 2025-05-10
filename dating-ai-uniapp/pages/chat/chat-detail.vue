<template>
  <view class="chat-detail-container">
    <!-- 状态栏高度占位 -->
    <view class="status-bar" :style="{ height: statusBarHeight + 'px' }"></view>
    
    <!-- 顶部导航栏 -->
    <view class="navbar">
      <view class="navbar__back" @tap="navigateBack">
        <image src="/static/images/icons/back.png" mode="aspectFit"></image>
      </view>
      <view class="navbar__info">
        <text class="navbar__title">{{conversationInfo.contactName || '对话详情'}}</text>
        <text class="navbar__subtitle" v-if="conversationInfo.contactName">{{statusText}}</text>
      </view>
      <view class="navbar__actions">
        <view class="navbar__action" @tap="showMoreActions">
          <image src="/static/images/icons/more.png" mode="aspectFit"></image>
        </view>
      </view>
    </view>
    
    <!-- 消息列表 -->
    <scroll-view 
      class="message-list" 
      scroll-y 
      :scroll-into-view="scrollToMessageId"
      @scrolltoupper="loadMoreMessages"
      :refresher-enabled="true"
      :refresher-triggered="refreshing"
      @refresherrefresh="onRefresh"
      scroll-anchoring
      :style="{ paddingBottom: inputHeight + 'px' }"
    >
      <!-- 加载更多提示 -->
      <view class="loading-more" v-if="loadingMore">
        <view class="loading-more__spinner"></view>
        <text class="loading-more__text">加载更多消息...</text>
      </view>
      
      <!-- 消息日期分组 -->
      <block v-for="(group, date) in groupedMessages" :key="date">
        <!-- 日期分割线 -->
        <view class="date-divider">
          <text class="date-divider__text">{{formatMessageDate(date)}}</text>
        </view>
        
        <!-- 消息气泡 -->
        <view 
          v-for="message in group" 
          :key="message.messageId" 
          :id="'msg-' + message.messageId"
          class="message-item"
          :class="[message.type === 'received' ? 'message-item--received' : 'message-item--sent']"
        >
          <!-- 消息时间 -->
          <text class="message-item__time">{{formatMessageTime(message.timestamp)}}</text>
          
          <!-- 消息内容 -->
          <view 
            class="message-item__bubble" 
            :class="[
              message.type === 'received' ? 'message-item__bubble--received' : 'message-item__bubble--sent',
              message.type === 'generated' ? 'message-item__bubble--generated' : ''
            ]"
            @longpress="handleMessageLongPress(message)"
          >
            <!-- 生成标记 -->
            <view class="message-item__badge" v-if="message.type === 'generated'">
              <text class="message-item__badge-text">AI生成</text>
            </view>
            
            <!-- 消息文本 -->
            <text class="message-item__content">{{message.content}}</text>
          </view>
          
          <!-- 消息操作按钮 -->
          <view class="message-item__actions" v-if="message.type === 'received'">
            <view class="message-item__action" @tap="generateReply(message)">
              <image src="/static/images/icons/robot.png" mode="aspectFit"></image>
            </view>
          </view>
        </view>
      </block>
      
      <!-- 空消息提示 -->
      <view class="empty-messages" v-if="messages.length === 0 && !loading">
        <image src="/static/images/empty-message.png" mode="aspectFit" class="empty-messages__image"></image>
        <text class="empty-messages__text">暂无消息记录</text>
        <text class="empty-messages__tip">点击下方"+"添加消息</text>
      </view>
    </scroll-view>
    
    <!-- 底部输入区域 -->
    <view 
      class="input-area" 
      :class="{'input-area--focused': inputFocused}"
      :style="{ bottom: keyboardHeight + 'px' }"
      @touchmove.stop.prevent
    >
      <view class="input-area__actions">
        <view class="input-area__action" @tap="showMessageTypes">
          <image src="/static/images/icons/plus.png" mode="aspectFit"></image>
        </view>
      </view>
      <view class="input-area__middle">
        <textarea
          class="input-area__input"
          v-model="inputMessage"
          placeholder="粘贴接收到的消息或手动输入"
          :adjust-position="false"
          :cursor-spacing="10"
          :show-confirm-bar="false"
          :maxlength="-1"
          @focus="onInputFocus"
          @blur="onInputBlur"
          @linechange="onInputLineChange"
          confirm-type="send"
          @confirm="sendMessage"
        ></textarea>
      </view>
      <view class="input-area__actions">
        <view 
          class="input-area__action input-area__send"
          :class="{'input-area__send--active': inputMessage.trim().length > 0}"
          @tap="sendMessage"
        >
          <text>保存</text>
        </view>
      </view>
    </view>
    
    <!-- 消息类型选择菜单 -->
    <view class="message-types" v-if="showMessageTypesMenu" @touchmove.stop.prevent>
      <view class="message-types__mask" @tap="hideMessageTypes"></view>
      <view class="message-types__content">
        <view class="message-types__header">
          <text class="message-types__title">添加消息</text>
          <view class="message-types__close" @tap="hideMessageTypes">
            <image src="/static/images/icons/close.png" mode="aspectFit"></image>
          </view>
        </view>
        <view class="message-types__list">
          <view class="message-types__item" @tap="addReceivedMessage">
            <view class="message-types__icon message-types__icon--received">
              <image src="/static/images/icons/message-received.png" mode="aspectFit"></image>
            </view>
            <view class="message-types__info">
              <text class="message-types__name">收到的消息</text>
              <text class="message-types__desc">添加对方发送的消息</text>
            </view>
          </view>
          <view class="message-types__item" @tap="addSentMessage">
            <view class="message-types__icon message-types__icon--sent">
              <image src="/static/images/icons/message-sent.png" mode="aspectFit"></image>
            </view>
            <view class="message-types__info">
              <text class="message-types__name">发送的消息</text>
              <text class="message-types__desc">添加自己发送的消息</text>
            </view>
          </view>
          <view class="message-types__item" @tap="navigateToAssistant">
            <view class="message-types__icon message-types__icon--generate">
              <image src="/static/images/icons/robot.png" mode="aspectFit"></image>
            </view>
            <view class="message-types__info">
              <text class="message-types__name">生成回复</text>
              <text class="message-types__desc">使用AI助手生成回复</text>
            </view>
          </view>
        </view>
      </view>
    </view>
    
    <!-- 消息操作菜单 -->
    <view class="message-actions" v-if="showMessageActions" @touchmove.stop.prevent>
      <view class="message-actions__mask" @tap="hideMessageActions"></view>
      <view class="message-actions__content">
        <view class="message-actions__item" @tap="copyMessage">
          <image src="/static/images/icons/copy.png" mode="aspectFit"></image>
          <text>复制消息</text>
        </view>
        <view class="message-actions__item" @tap="generateReply">
          <image src="/static/images/icons/robot.png" mode="aspectFit"></image>
          <text>生成回复</text>
        </view>
        <view class="message-actions__item" @tap="deleteMessage">
          <image src="/static/images/icons/delete.png" mode="aspectFit"></image>
          <text>删除消息</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import { mapGetters } from 'vuex'
import { formatTime, formatDate } from '@/utils/common'

export default {
  data() {
    return {
      conversationId: '',
      conversationInfo: {},
      messages: [],
      groupedMessages: {},
      inputMessage: '',
      inputFocused: false,
      inputHeight: 120,
      keyboardHeight: 0,
      messageType: 'received',
      currentPage: 1,
      pageSize: 20,
      hasMoreMessages: true,
      loading: true,
      loadingMore: false,
      refreshing: false,
      lastMessageId: '',
      scrollToMessageId: '',
      showMessageTypesMenu: false,
      showMessageActions: false,
      selectedMessage: null,
      statusText: '在线'
    }
  },
  computed: {
    ...mapGetters({
      statusBarHeight: 'app/statusBarHeight',
      userInfo: 'user/userInfo'
    })
  },
  onLoad(options) {
    this.conversationId = options.id || ''
    this.loadConversation()
    this.loadMessages()
  },
  methods: {
    // 加载会话信息
    async loadConversation() {
      if (!this.conversationId) return
      
      try {
        const result = await this.$api.conversation.getConversationInfo(this.conversationId)
        this.conversationInfo = result || {}
      } catch (error) {
        this.$log.error('加载会话信息失败', error)
        this.$toast('加载失败，请重试')
      }
    },
    
    // 加载消息列表
    async loadMessages(refresh = false) {
      if (!this.conversationId) return
      
      if (refresh) {
        this.messages = []
        this.currentPage = 1
        this.hasMoreMessages = true
      }
      
      if (!this.hasMoreMessages && !refresh) return
      
      this.loading = true
      
      try {
        const params = {
          page: this.currentPage,
          size: this.pageSize
        }
        
        if (this.lastMessageId && !refresh) {
          params.before = this.lastMessageId
        }
        
        const result = await this.$api.conversation.getMessages(this.conversationId, params)
        
        if (result && result.messages) {
          if (refresh) {
            this.messages = result.messages
          } else {
            this.messages = [...result.messages, ...this.messages]
          }
          
          this.hasMoreMessages = result.pagination.hasMore
          
          if (this.hasMoreMessages) {
            this.currentPage++
          }
          
          if (result.messages.length > 0) {
            this.lastMessageId = result.messages[0].messageId
          }
          
          // 分组消息
          this.groupMessages()
          
          // 滚动到最新消息
          if (refresh && this.messages.length > 0) {
            this.scrollToBottom()
          }
        }
      } catch (error) {
        this.$log.error('加载消息失败', error)
        this.$toast('加载失败，请重试')
      } finally {
        this.loading = false
        this.refreshing = false
        this.loadingMore = false
      }
    },
    
    // 按日期分组消息
    groupMessages() {
      this.groupedMessages = {}
      
      this.messages.forEach(message => {
        const date = new Date(message.timestamp).toISOString().split('T')[0]
        
        if (!this.groupedMessages[date]) {
          this.groupedMessages[date] = []
        }
        
        this.groupedMessages[date].push(message)
      })
    },
    
    // 发送/保存消息
    async sendMessage() {
      if (!this.inputMessage.trim()) return
      
      const messageData = {
        type: this.messageType,
        content: this.inputMessage,
        timestamp: new Date().toISOString()
      }
      
      // 清空输入框
      const content = this.inputMessage
      this.inputMessage = ''
      
      try {
        let result
        
        if (!this.conversationId) {
          // 创建新会话
          const newConversation = await this.$api.conversation.createConversation({
            contactName: '新对话',
            platform: 'manual'
          })
          
          this.conversationId = newConversation.conversationId
          this.conversationInfo = newConversation
          
          // 添加消息
          result = await this.$api.conversation.addMessage(this.conversationId, messageData)
        } else {
          // 添加消息到现有会话
          result = await this.$api.conversation.addMessage(this.conversationId, messageData)
        }
        
        if (result) {
          // 刷新消息列表
          this.loadMessages(true)
        }
      } catch (error) {
        this.$log.error('保存消息失败', error)
        this.$toast('保存失败，请重试')
        // 恢复输入内容
        this.inputMessage = content
      }
    },
    
    // 生成回复
    generateReply(message) {
      const messageContent = message ? message.content : (this.selectedMessage ? this.selectedMessage.content : '')
      
      if (!messageContent) {
        this.$toast('请先选择一条消息')
        return
      }
      
      uni.navigateTo({
        url: `/pages/assistant/assistant?message=${encodeURIComponent(messageContent)}&conversationId=${this.conversationId}`
      })
      
      this.hideMessageActions()
    },
    
    // 添加收到的消息
    addReceivedMessage() {
      this.messageType = 'received'
      this.hideMessageTypes()
    },
    
    // 添加发送的消息
    addSentMessage() {
      this.messageType = 'sent'
      this.hideMessageTypes()
    },
    
    // 跳转到助手页面
    navigateToAssistant() {
      uni.navigateTo({
        url: `/pages/assistant/assistant?conversationId=${this.conversationId}`
      })
      this.hideMessageTypes()
    },
    
    // 复制消息
    copyMessage() {
      if (!this.selectedMessage) return
      
      uni.setClipboardData({
        data: this.selectedMessage.content,
        success: () => {
          this.$toast('已复制到剪贴板')
        }
      })
      
      this.hideMessageActions()
    },
    
    // 删除消息
    async deleteMessage() {
      if (!this.selectedMessage) return
      
      const result = await this.$modal('确定要删除这条消息吗？')
      
      if (result) {
        try {
          await this.$api.conversation.deleteMessage(
            this.conversationId, 
            this.selectedMessage.messageId
          )
          
          // 更新本地数据
          const index = this.messages.findIndex(m => m.messageId === this.selectedMessage.messageId)
          if (index !== -1) {
            this.messages.splice(index, 1)
            this.groupMessages()
          }
          
          this.$toast('删除成功')
        } catch (error) {
          this.$log.error('删除消息失败', error)
          this.$toast('删除失败，请重试')
        }
      }
      
      this.hideMessageActions()
    },
    
    // 加载更多消息
    loadMoreMessages() {
      if (this.loadingMore || !this.hasMoreMessages) return
      
      this.loadingMore = true
      this.loadMessages()
    },
    
    // 下拉刷新
    onRefresh() {
      this.refreshing = true
      this.loadMessages(true)
    },
    
    // 滚动到底部
    scrollToBottom() {
      if (this.messages.length === 0) return
      
      const lastMessage = this.messages[this.messages.length - 1]
      this.scrollToMessageId = 'msg-' + lastMessage.messageId
    },
    
    // 处理消息长按
    handleMessageLongPress(message) {
      this.selectedMessage = message
      this.showMessageActions = true
    },
    
    // 显示消息类型菜单
    showMessageTypes() {
      this.showMessageTypesMenu = true
    },
    
    // 隐藏消息类型菜单
    hideMessageTypes() {
      this.showMessageTypesMenu = false
    },
    
    // 隐藏消息操作菜单
    hideMessageActions() {
      this.showMessageActions = false
      this.selectedMessage = null
    },
    
    // 显示更多操作菜单
    showMoreActions() {
      // 实现更多操作菜单
    },
    
    // 输入框获取焦点
    onInputFocus(e) {
      this.inputFocused = true
      this.keyboardHeight = e.detail.height || 0
    },
    
    // 输入框失去焦点
    onInputBlur() {
      this.inputFocused = false
      this.keyboardHeight = 0
    },
    
    // 输入框行数变化
    onInputLineChange(e) {
      const lineHeight = 24
      const minHeight = 40
      const maxHeight = 120
      
      const height = Math.min(maxHeight, Math.max(minHeight, e.detail.height + 30))
      this.inputHeight = height + 30 // 添加内边距
    },
    
    // 返回上一页
    navigateBack() {
      uni.navigateBack()
    },
    
    // 格式化消息日期
    formatMessageDate(dateStr) {
      const date = new Date(dateStr)
      const today = new Date()
      today.setHours(0, 0, 0, 0)
      
      const yesterday = new Date(today)
      yesterday.setDate(yesterday.getDate() - 1)
      
      if (date.getTime() === today.getTime()) {
        return '今天'
      } else if (date.getTime() === yesterday.getTime()) {
        return '昨天'
      } else {
        return formatDate(date, 'MM月DD日')
      }
    },
    
    // 格式化消息时间
    formatMessageTime(timestamp) {
      return formatTime(timestamp, 'HH:mm')
    }
  }
}
</script>

<style lang="scss">
.chat-detail-container {
  min-height: 100vh;
  background-color: #f5f5f5;
  display: flex;
  flex-direction: column;
}

.navbar {
  padding: 10rpx 30rpx;
  background: linear-gradient(135deg, #ff6b6b, #fc466b);
  color: #fff;
  display: flex;
  align-items: center;
  position: relative;
  z-index: 100;
  
  &__back {
    width: 60rpx;
    height: 60rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    
    image {
      width: 40rpx;
      height: 40rpx;
    }
  }
  
  &__info {
    flex: 1;
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 0 20rpx;
  }
  
  &__title {
    font-size: 34rpx;
    font-weight: bold;
  }
  
  &__subtitle {
    font-size: 24rpx;
    opacity: 0.8;
  }
  
  &__actions {
    display: flex;
  }
  
  &__action {
    width: 60rpx;
    height: 60rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    
    image {
      width: 40rpx;
      height: 40rpx;
    }
  }
}

.message-list {
  flex: 1;
  padding: 20rpx 30rpx;
}

.date-divider {
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 30rpx 0;
  
  &__text {
    background-color: rgba(0, 0, 0, 0.1);
    color: #666;
    font-size: 24rpx;
    padding: 4rpx 20rpx;
    border-radius: 24rpx;
  }
}

.message-item {
  margin-bottom: 30rpx;
  position: relative;
  display: flex;
  flex-direction: column;
  
  &--received {
    align-items: flex-start;
  }
  
  &--sent {
    align-items: flex-end;
  }
  
  &__time {
    font-size: 24rpx;
    color: #999;
    margin-bottom: 10rpx;
  }
  
  &__bubble {
    max-width: 70%;
    padding: 20rpx;
    border-radius: 20rpx;
    position: relative;
    word-break: break-all;
    
    &--received {
      background-color: #fff;
      border-top-left-radius: 4rpx;
    }
    
    &--sent {
      background: linear-gradient(135deg, #ff6b6b, #fc466b);
      color: #fff;
      border-top-right-radius: 4rpx;
    }
    
    &--generated {
      background-color: #f5f9ff;
      border: 1px solid #dbeaff;
    }
  }
  
  &__badge {
    position: absolute;
    top: -10rpx;
    right: -10rpx;
    background-color: #3a86ff;
    border-radius: 20rpx;
    padding: 2rpx 12rpx;
    
    &-text {
      color: #fff;
      font-size: 20rpx;
    }
  }
  
  &__content {
    font-size: 28rpx;
    line-height: 1.5;
  }
  
  &__actions {
    margin-top: 10rpx;
    display: flex;
    justify-content: flex-end;
  }
  
  &__action {
    width: 60rpx;
    height: 60rpx;
    background-color: #fff;
    border-radius: 30rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.1);
    
    image {
      width: 40rpx;
      height: 40rpx;
    }
  }
}

.input-area {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: #fff;
  padding: 20rpx;
  display: flex;
  align-items: flex-end;
  box-shadow: 0 -2rpx 10rpx rgba(0, 0, 0, 0.05);
  z-index: 100;
  
  &--focused {
    box-shadow: 0 -2rpx 10rpx rgba(0, 0, 0, 0.1);
  }
  
  &__actions {
    display: flex;
    align-items: center;
  }
  
  &__action {
    width: 70rpx;
    height: 70rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    
    image {
      width: 44rpx;
      height: 44rpx;
    }
  }
  
  &__middle {
    flex: 1;
    margin: 0 20rpx;
  }
  
  &__input {
    background-color: #f5f5f5;
    border-radius: 10rpx;
    padding: 20rpx;
    width: 100%;
    min-height: 80rpx;
    max-height: 200rpx;
    font-size: 28rpx;
  }
  
  &__send {
    background-color: #e0e0e0;
    border-radius: 10rpx;
    padding: 0 30rpx;
    height: 70rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    
    text {
      color: #666;
      font-size: 28rpx;
    }
    
    &--active {
      background: linear-gradient(135deg, #ff6b6b, #fc466b);
      
      text {
        color: #fff;
      }
    }
  }
}

.loading-more {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20rpx 0;
  
  &__spinner {
    width: 40rpx;
    height: 40rpx;
    border: 4rpx solid #f3f3f3;
    border-top: 4rpx solid #fc466b;
    border-radius: 50%;
    animation: spin 0.8s linear infinite;
    margin-right: 10rpx;
  }
  
  &__text {
    font-size: 24rpx;
    color: #999;
  }
}

.empty-messages {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 100rpx 0;
  
  &__image {
    width: 240rpx;
    height: 240rpx;
    margin-bottom: 30rpx;
  }
  
  &__text {
    font-size: 32rpx;
    color: #666;
    margin-bottom: 10rpx;
  }
  
  &__tip {
    font-size: 28rpx;
    color: #999;
  }
}

.message-types, .message-actions {
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
}

.message-types {
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
  
  &__list {
    padding: 20rpx 0;
  }
  
  &__item {
    display: flex;
    align-items: center;
    padding: 20rpx 30rpx;
    
    &:active {
      background-color: #f5f5f5;
    }
  }
  
  &__icon {
    width: 80rpx;
    height: 80rpx;
    border-radius: 20rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-right: 20rpx;
    
    image {
      width: 44rpx;
      height: 44rpx;
    }
    
    &--received {
      background-color: #e3f2fd;
    }
    
    &--sent {
      background-color: #fff3e0;
    }
    
    &--generate {
      background-color: #e8f5e9;
    }
  }
  
  &__info {
    flex: 1;
  }
  
  &__name {
    font-size: 30rpx;
    font-weight: 500;
    color: #333;
    margin-bottom: 6rpx;
  }
  
  &__desc {
    font-size: 24rpx;
    color: #999;
  }
}

.message-actions {
  &__content {
    position: absolute;
    left: 50%;
    top: 50%;
    transform: translate(-50%, -50%);
    background-color: #fff;
    border-radius: 20rpx;
    width: 70%;
    overflow: hidden;
  }
  
  &__item {
    display: flex;
    align-items: center;
    padding: 30rpx;
    border-bottom: 1rpx solid #eee;
    
    &:last-child {
      border-bottom: none;
    }
    
    &:active {
      background-color: #f5f5f5;
    }
    
    image {
      width: 44rpx;
      height: 44rpx;
      margin-right: 20rpx;
    }
    
    text {
      font-size: 30rpx;
      color: #333;
    }
  }
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}
</style> 