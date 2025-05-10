<template>
  <view class="assistant-container">
    <!-- 状态栏高度占位 -->
    <view class="status-bar" :style="{ height: statusBarHeight + 'px' }"></view>
    
    <!-- 顶部导航栏 -->
    <view class="navbar">
      <view class="navbar__back" @tap="navigateBack">
        <image src="/static/images/icons/back.png" mode="aspectFit"></image>
      </view>
      <view class="navbar__title">AI回复助手</view>
      <view class="navbar__placeholder"></view>
    </view>
    
    <!-- 页面内容区域 -->
    <scroll-view class="content" scroll-y>
      <!-- 用量指示器 -->
      <view class="usage-indicator">
        <view class="usage-indicator__bar">
          <view 
            class="usage-indicator__progress" 
            :style="{ width: usagePercentage + '%' }"
          ></view>
        </view>
        <view class="usage-indicator__text">
          <text>今日剩余：{{dailyRemainingCount}}/{{dailyTotalCount}}</text>
          <text v-if="isVip" class="usage-indicator__vip">VIP</text>
        </view>
      </view>
      
      <!-- 输入区域 -->
      <view class="input-card card">
        <view class="input-card__header">
          <text class="input-card__title">对方消息</text>
          <view class="input-card__actions">
            <view class="input-card__action" @tap="clearInput">清空</view>
            <view class="input-card__action" @tap="pasteFromClipboard">粘贴</view>
          </view>
        </view>
        <textarea 
          class="input-card__textarea" 
          v-model="receivedMessage" 
          placeholder="请输入或粘贴收到的消息内容..." 
          :maxlength="-1"
          :disabled="loading"
        ></textarea>
      </view>
      
      <!-- 风格选择区域 -->
      <view class="style-card card">
        <view class="style-card__header">
          <text class="style-card__title">选择风格</text>
        </view>
        <scroll-view class="style-scroll" scroll-x show-scrollbar="false">
          <view 
            v-for="style in styles" 
            :key="style.id" 
            class="style-item"
            :class="{ 'style-item--active': selectedStyle === style.id, 'style-item--vip': style.vipOnly && !isVip }"
            @tap="selectStyle(style)"
          >
            <image :src="getStyleIcon(style)" class="style-item__icon" mode="aspectFit"></image>
            <text class="style-item__name">{{style.name}}</text>
            <view class="style-item__tag" v-if="style.vipOnly && !isVip">VIP</view>
          </view>
        </scroll-view>
      </view>
      
      <!-- 生成按钮 -->
      <view class="generate-btn-container">
        <button 
          class="generate-btn" 
          :disabled="!canGenerate || loading" 
          :loading="loading"
          @tap="generateReply"
        >
          {{ loading ? '生成中...' : '生成回复' }}
        </button>
      </view>
      
      <!-- 回复结果区域 -->
      <view class="replies-container" v-if="replies.length > 0">
        <view class="card reply-card" v-for="(reply, index) in replies" :key="index">
          <view class="reply-card__header">
            <view class="reply-card__style">
              <image :src="getStyleIcon(getStyleById(reply.style))" class="reply-card__style-icon" mode="aspectFit"></image>
              <text class="reply-card__style-name">{{getStyleById(reply.style).name}}</text>
            </view>
            <view class="reply-card__actions">
              <view class="reply-card__action" @tap="copyReply(reply)">
                <image src="/static/images/icons/copy.png" mode="aspectFit"></image>
              </view>
              <view class="reply-card__action" @tap="likeReply(reply)">
                <image 
                  :src="reply.liked ? '/static/images/icons/like-filled.png' : '/static/images/icons/like.png'" 
                  mode="aspectFit"
                ></image>
              </view>
            </view>
          </view>
          <view class="reply-card__content">
            <text>{{reply.content}}</text>
          </view>
        </view>
      </view>
      
      <!-- VIP推广区域 -->
      <view class="vip-promo card" v-if="!isVip && showVipPromo">
        <view class="vip-promo__header">
          <image src="/static/images/icons/vip-crown.png" class="vip-promo__icon" mode="aspectFit"></image>
          <text class="vip-promo__title">解锁全部功能</text>
        </view>
        <view class="vip-promo__content">
          <view class="vip-promo__feature">
            <image src="/static/images/icons/check.png" mode="aspectFit"></image>
            <text>无限使用次数</text>
          </view>
          <view class="vip-promo__feature">
            <image src="/static/images/icons/check.png" mode="aspectFit"></image>
            <text>解锁高级风格 (15+)</text>
          </view>
          <view class="vip-promo__feature">
            <image src="/static/images/icons/check.png" mode="aspectFit"></image>
            <text>情感分析与建议</text>
          </view>
        </view>
        <button class="vip-promo__btn" @tap="navigateToVip">立即开通 VIP</button>
      </view>
    </scroll-view>
  </view>
</template>

<script>
import { mapGetters } from 'vuex'

export default {
  data() {
    return {
      receivedMessage: '',
      selectedStyle: 'friendly',
      loading: false,
      replies: [],
      showVipPromo: false,
      conversationId: '',
      styles: [
        {
          id: 'friendly',
          name: '友好',
          description: '亲切、适度热情',
          vipOnly: false,
          icon: 'friendly.png'
        },
        {
          id: 'casual',
          name: '随性',
          description: '轻松自然的交流',
          vipOnly: false,
          icon: 'casual.png'
        },
        {
          id: 'caring',
          name: '体贴',
          description: '展现关怀和理解',
          vipOnly: false,
          icon: 'caring.png'
        },
        {
          id: 'humorous',
          name: '幽默',
          description: '轻松风趣的表达',
          vipOnly: true,
          icon: 'humorous.png'
        },
        {
          id: 'romantic',
          name: '浪漫',
          description: '富有情感的表达',
          vipOnly: true,
          icon: 'romantic.png'
        },
        {
          id: 'mysterious',
          name: '神秘',
          description: '保持适当神秘感',
          vipOnly: true,
          icon: 'mysterious.png'
        },
        {
          id: 'intellectual',
          name: '知性',
          description: '展现智慧和思考',
          vipOnly: true,
          icon: 'intellectual.png'
        },
        {
          id: 'assertive',
          name: '自信',
          description: '直接、有主见',
          vipOnly: true,
          icon: 'assertive.png'
        }
      ]
    }
  },
  computed: {
    ...mapGetters({
      statusBarHeight: 'app/statusBarHeight',
      isVip: 'user/isVip',
      dailyRemainingCount: 'user/dailyRemainingCount',
      dailyTotalCount: 'user/dailyTotalCount'
    }),
    
    // 使用进度百分比
    usagePercentage() {
      return (this.dailyRemainingCount / this.dailyTotalCount) * 100
    },
    
    // 是否可以生成
    canGenerate() {
      // 消息不能为空且长度大于5
      const validMessage = this.receivedMessage.trim().length > 5
      
      // 会员无使用限制，非会员需要检查剩余次数
      const hasRemainingCount = this.isVip || this.dailyRemainingCount > 0
      
      // 如果选择了VIP专属风格，非会员不能使用
      const hasAccess = this.isVip || !this.getStyleById(this.selectedStyle).vipOnly
      
      return validMessage && hasRemainingCount && hasAccess
    }
  },
  onLoad(options) {
    // 从聊天页面传入的消息
    if (options.message) {
      this.receivedMessage = decodeURIComponent(options.message)
    }
    
    if (options.conversationId) {
      this.conversationId = options.conversationId
    }
    
    // 从聊天页面传入的风格
    if (options.style && this.isStyleValid(options.style)) {
      this.selectedStyle = options.style
    }
    
    // 加载用户使用统计
    this.loadUserStats()
  },
  methods: {
    // 加载用户使用统计
    loadUserStats() {
      this.$store.dispatch('user/getUserLimits')
    },
    
    // 生成回复
    async generateReply() {
      if (!this.canGenerate) return
      
      // 检查VIP限制
      const selectedStyleObj = this.getStyleById(this.selectedStyle)
      if (selectedStyleObj.vipOnly && !this.isVip) {
        this.showVipPromo = true
        this.$toast('该风格仅限VIP用户使用')
        return
      }
      
      // 检查使用限制
      if (!this.isVip && this.dailyRemainingCount <= 0) {
        this.showVipPromo = true
        this.$toast('今日使用次数已用完')
        return
      }
      
      this.loading = true
      this.showVipPromo = false
      
      try {
        // 准备请求参数
        const params = {
          receivedMessage: this.receivedMessage,
          preferences: {
            styles: [this.selectedStyle],
            length: "medium",
            emotionalTone: "positive"
          }
        }
        
        // 如果有会话ID，加入参数
        if (this.conversationId) {
          params.conversationId = this.conversationId
        }
        
        // 调用API生成回复
        const result = await this.$api.assistant.generateReply(params)
        
        if (result && result.replies) {
          this.replies = result.replies.map(reply => ({
            ...reply,
            liked: false
          }))
          
          // 更新用户剩余次数
          if (result.usage) {
            this.$store.commit('user/SET_DAILY_REMAINING_COUNT', result.usage.remaining)
          }
          
          // 显示VIP推广 (对于免费用户，已用超过50%且不是VIP)
          const usagePercentage = (this.dailyRemainingCount / this.dailyTotalCount) * 100
          if (!this.isVip && usagePercentage < 50) {
            this.showVipPromo = true
          }
        }
      } catch (error) {
        this.$log.error('生成回复失败', error)
        this.$toast('生成失败，请重试')
      } finally {
        this.loading = false
      }
    },
    
    // 复制回复内容
    copyReply(reply) {
      uni.setClipboardData({
        data: reply.content,
        success: () => {
          this.$toast('已复制到剪贴板')
          
          // 调用API更新使用统计
          this.$api.assistant.trackCopy({
            replyId: reply.replyId,
            style: reply.style
          }).catch(err => {
            this.$log.error('记录复制失败', err)
          })
        }
      })
    },
    
    // 喜欢回复
    async likeReply(reply) {
      // 更新UI状态
      const index = this.replies.findIndex(r => r.replyId === reply.replyId)
      if (index !== -1) {
        this.replies[index].liked = !this.replies[index].liked
        
        // 调用API提交反馈
        try {
          await this.$api.assistant.feedback({
            replyId: reply.replyId,
            feedback: this.replies[index].liked ? 'like' : 'dislike'
          })
        } catch (error) {
          this.$log.error('提交反馈失败', error)
        }
      }
    },
    
    // 选择风格
    selectStyle(style) {
      if (style.vipOnly && !this.isVip) {
        this.showVipPromo = true
        this.$toast('该风格需要开通VIP会员')
        return
      }
      
      this.selectedStyle = style.id
    },
    
    // 粘贴内容
    pasteFromClipboard() {
      uni.getClipboardData({
        success: (res) => {
          if (res.data) {
            this.receivedMessage = res.data
          }
        }
      })
    },
    
    // 清空输入
    clearInput() {
      this.receivedMessage = ''
    },
    
    // 根据风格ID获取风格对象
    getStyleById(styleId) {
      return this.styles.find(style => style.id === styleId) || this.styles[0]
    },
    
    // 获取风格图标
    getStyleIcon(style) {
      return `/static/images/styles/${style.icon}`
    },
    
    // 检查风格是否有效
    isStyleValid(styleId) {
      return this.styles.some(style => style.id === styleId)
    },
    
    // 导航到VIP页面
    navigateToVip() {
      uni.navigateTo({
        url: '/pages/vip/vip'
      })
    },
    
    // 返回上一页
    navigateBack() {
      uni.navigateBack()
    }
  }
}
</script>

<style lang="scss">
.assistant-container {
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
  
  &__back, &__placeholder {
    width: 60rpx;
    height: 60rpx;
    display: flex;
    align-items: center;
    justify-content: center;
  }
  
  &__title {
    flex: 1;
    text-align: center;
    font-size: 36rpx;
    font-weight: bold;
  }
  
  &__back {
    image {
      width: 40rpx;
      height: 40rpx;
    }
  }
}

.content {
  flex: 1;
  padding: 20rpx 30rpx;
}

.card {
  background-color: #fff;
  border-radius: 12rpx;
  margin-bottom: 30rpx;
  box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
  overflow: hidden;
}

.usage-indicator {
  margin-bottom: 30rpx;
  
  &__bar {
    height: 8rpx;
    background-color: rgba(252, 70, 107, 0.2);
    border-radius: 4rpx;
    overflow: hidden;
  }
  
  &__progress {
    height: 100%;
    background: linear-gradient(135deg, #ff6b6b, #fc466b);
  }
  
  &__text {
    display: flex;
    justify-content: space-between;
    font-size: 24rpx;
    color: #666;
    margin-top: 10rpx;
  }
  
  &__vip {
    color: #fc466b;
    font-weight: bold;
  }
}

.input-card {
  &__header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 20rpx 30rpx;
    border-bottom: 1rpx solid #f0f0f0;
  }
  
  &__title {
    font-size: 30rpx;
    font-weight: 500;
    color: #333;
  }
  
  &__actions {
    display: flex;
  }
  
  &__action {
    font-size: 28rpx;
    color: #666;
    margin-left: 30rpx;
    
    &:active {
      opacity: 0.7;
    }
  }
  
  &__textarea {
    width: 100%;
    height: 240rpx;
    padding: 30rpx;
    font-size: 28rpx;
    line-height: 1.5;
  }
}

.style-card {
  &__header {
    padding: 20rpx 30rpx;
    border-bottom: 1rpx solid #f0f0f0;
  }
  
  &__title {
    font-size: 30rpx;
    font-weight: 500;
    color: #333;
  }
}

.style-scroll {
  white-space: nowrap;
  padding: 30rpx;
}

.style-item {
  display: inline-block;
  width: 140rpx;
  height: 180rpx;
  border-radius: 12rpx;
  background-color: #f9f9f9;
  margin-right: 20rpx;
  text-align: center;
  padding: 20rpx 0;
  position: relative;
  
  &--active {
    background-color: rgba(252, 70, 107, 0.1);
    border: 2rpx solid #fc466b;
  }
  
  &--vip {
    opacity: 0.6;
  }
  
  &__icon {
    width: 80rpx;
    height: 80rpx;
    margin-bottom: 10rpx;
  }
  
  &__name {
    font-size: 26rpx;
    color: #333;
  }
  
  &__tag {
    position: absolute;
    top: 10rpx;
    right: 10rpx;
    background: linear-gradient(135deg, #ff6b6b, #fc466b);
    color: #fff;
    font-size: 20rpx;
    padding: 2rpx 8rpx;
    border-radius: 8rpx;
  }
}

.generate-btn-container {
  margin: 30rpx 0;
}

.generate-btn {
  width: 100%;
  height: 90rpx;
  border-radius: 45rpx;
  background: linear-gradient(135deg, #ff6b6b, #fc466b);
  color: #fff;
  font-size: 32rpx;
  font-weight: bold;
  display: flex;
  align-items: center;
  justify-content: center;
  
  &:active {
    opacity: 0.9;
  }
  
  &[disabled] {
    background: #cccccc;
    opacity: 0.6;
  }
}

.replies-container {
  margin-top: 50rpx;
}

.reply-card {
  margin-bottom: 40rpx;
  
  &__header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 20rpx 30rpx;
    border-bottom: 1rpx solid #f0f0f0;
  }
  
  &__style {
    display: flex;
    align-items: center;
  }
  
  &__style-icon {
    width: 40rpx;
    height: 40rpx;
    margin-right: 10rpx;
  }
  
  &__style-name {
    font-size: 28rpx;
    color: #333;
    font-weight: 500;
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
      width: 36rpx;
      height: 36rpx;
    }
    
    &:active {
      opacity: 0.7;
    }
  }
  
  &__content {
    padding: 30rpx;
    font-size: 30rpx;
    line-height: 1.6;
    color: #333;
  }
}

.vip-promo {
  padding: 30rpx;
  
  &__header {
    display: flex;
    align-items: center;
    margin-bottom: 20rpx;
  }
  
  &__icon {
    width: 50rpx;
    height: 50rpx;
    margin-right: 15rpx;
  }
  
  &__title {
    font-size: 32rpx;
    font-weight: bold;
    color: #333;
  }
  
  &__content {
    margin-bottom: 30rpx;
  }
  
  &__feature {
    display: flex;
    align-items: center;
    margin-bottom: 15rpx;
    
    image {
      width: 32rpx;
      height: 32rpx;
      margin-right: 15rpx;
    }
    
    text {
      font-size: 28rpx;
      color: #666;
    }
  }
  
  &__btn {
    width: 100%;
    height: 80rpx;
    border-radius: 40rpx;
    background: linear-gradient(135deg, #ff6b6b, #fc466b);
    color: #fff;
    font-size: 30rpx;
    font-weight: bold;
    display: flex;
    align-items: center;
    justify-content: center;
  }
}
</style> 