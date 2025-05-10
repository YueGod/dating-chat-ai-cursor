<template>
  <view class="profile-container">
    <!-- 状态栏高度占位 -->
    <view class="status-bar" :style="{ height: statusBarHeight + 'px' }"></view>
    
    <!-- 顶部导航栏 -->
    <view class="navbar">
      <view class="navbar__title">个人中心</view>
    </view>
    
    <!-- 用户信息卡片 -->
    <view class="user-card">
      <view class="user-info">
        <view class="user-avatar" @tap="changeAvatar">
          <image :src="userInfo.avatar || '/static/images/default-avatar.png'" mode="aspectFill"></image>
          <view class="user-avatar__edit">
            <image src="/static/images/icons/camera.png" mode="aspectFit"></image>
          </view>
        </view>
        <view class="user-details">
          <view class="user-name">
            <text>{{userInfo.nickname || '未设置昵称'}}</text>
            <view class="user-badge" v-if="isVip">
              <image src="/static/images/icons/vip-badge.png" mode="aspectFit"></image>
            </view>
          </view>
          <view class="user-meta">
            <text>注册于 {{formatRegDate}}</text>
          </view>
        </view>
        <view class="edit-btn" @tap="navigateToProfileSetup">
          <image src="/static/images/icons/edit.png" mode="aspectFit"></image>
        </view>
      </view>
      
      <!-- 会员状态展示 -->
      <view class="membership-status" @tap="navigateToVip">
        <view class="membership-icon">
          <image :src="isVip ? '/static/images/icons/vip-crown.png' : '/static/images/icons/vip.png'" mode="aspectFit"></image>
        </view>
        <view class="membership-info">
          <view class="membership-title">
            <text>{{isVip ? 'VIP会员' : '开通VIP'}}</text>
            <view class="membership-tag" v-if="isVip">剩余{{vipDaysRemaining}}天</view>
          </view>
          <view class="membership-desc">{{isVip ? '尊享VIP特权，畅享全部功能' : '解锁全部高级功能和风格'}}</view>
        </view>
        <view class="membership-action">
          <text>{{isVip ? '续费' : '开通'}}</text>
          <image src="/static/images/icons/arrow-right.png" mode="aspectFit"></image>
        </view>
      </view>
    </view>
    
    <!-- 功能卡片区域 -->
    <view class="section-container">
      <!-- 使用统计卡片 -->
      <view class="card">
        <view class="card-header">
          <text class="card-title">使用统计</text>
        </view>
        <view class="stat-grid">
          <view class="stat-item">
            <text class="stat-value">{{usageStats.totalGeneration || 0}}</text>
            <text class="stat-label">生成回复</text>
          </view>
          <view class="stat-item">
            <text class="stat-value">{{usageStats.totalCopied || 0}}</text>
            <text class="stat-label">使用回复</text>
          </view>
          <view class="stat-item">
            <text class="stat-value">{{usageStats.successRateText || '0%'}}</text>
            <text class="stat-label">有效率</text>
          </view>
        </view>
      </view>
      
      <!-- 账户设置卡片 -->
      <view class="card">
        <view class="card-header">
          <text class="card-title">账户设置</text>
        </view>
        <view class="settings-list">
          <view class="settings-item" @tap="navigateToProfileSetup">
            <view class="settings-icon">
              <image src="/static/images/icons/profile.png" mode="aspectFit"></image>
            </view>
            <view class="settings-content">
              <text class="settings-name">个人资料</text>
            </view>
            <view class="settings-arrow">
              <image src="/static/images/icons/arrow-right.png" mode="aspectFit"></image>
            </view>
          </view>
          <view class="settings-item settings-item--switch">
            <view class="settings-icon">
              <image src="/static/images/icons/bell.png" mode="aspectFit"></image>
            </view>
            <view class="settings-content">
              <text class="settings-name">消息通知</text>
            </view>
            <switch :checked="settings.notification" @change="toggleNotification" color="#fc466b" />
          </view>
          <view class="settings-item settings-item--switch">
            <view class="settings-icon">
              <image src="/static/images/icons/privacy.png" mode="aspectFit"></image>
            </view>
            <view class="settings-content">
              <text class="settings-name">隐私保护</text>
              <text class="settings-desc">{{settings.privacy === 'strict' ? '严格' : '标准'}}</text>
            </view>
            <switch :checked="settings.privacy === 'strict'" @change="togglePrivacy" color="#fc466b" />
          </view>
        </view>
      </view>
      
      <!-- 历史记录与存储卡片 -->
      <view class="card">
        <view class="card-header">
          <text class="card-title">历史记录与存储</text>
        </view>
        <view class="settings-list">
          <view class="settings-item" @tap="navigateToChat">
            <view class="settings-icon">
              <image src="/static/images/icons/chat-history.png" mode="aspectFit"></image>
            </view>
            <view class="settings-content">
              <text class="settings-name">聊天记录</text>
            </view>
            <view class="settings-arrow">
              <image src="/static/images/icons/arrow-right.png" mode="aspectFit"></image>
            </view>
          </view>
          <picker @change="changeChatRetention" :value="getRetentionIndex()" :range="retentionOptions">
            <view class="settings-item">
              <view class="settings-icon">
                <image src="/static/images/icons/time.png" mode="aspectFit"></image>
              </view>
              <view class="settings-content">
                <text class="settings-name">保存时长</text>
                <text class="settings-desc">{{getRetentionText()}}</text>
              </view>
              <view class="settings-arrow">
                <image src="/static/images/icons/arrow-right.png" mode="aspectFit"></image>
              </view>
            </view>
          </picker>
          <view class="settings-item" @tap="clearCache">
            <view class="settings-icon">
              <image src="/static/images/icons/clear.png" mode="aspectFit"></image>
            </view>
            <view class="settings-content">
              <text class="settings-name">清除缓存</text>
              <text class="settings-desc">{{cacheSize}}</text>
            </view>
            <view class="settings-arrow">
              <image src="/static/images/icons/arrow-right.png" mode="aspectFit"></image>
            </view>
          </view>
        </view>
      </view>
      
      <!-- 帮助与支持卡片 -->
      <view class="card">
        <view class="card-header">
          <text class="card-title">帮助与支持</text>
        </view>
        <view class="settings-list">
          <view class="settings-item" @tap="navigateToFaq">
            <view class="settings-icon">
              <image src="/static/images/icons/faq.png" mode="aspectFit"></image>
            </view>
            <view class="settings-content">
              <text class="settings-name">常见问题</text>
            </view>
            <view class="settings-arrow">
              <image src="/static/images/icons/arrow-right.png" mode="aspectFit"></image>
            </view>
          </view>
          <view class="settings-item" @tap="contactSupport">
            <view class="settings-icon">
              <image src="/static/images/icons/support.png" mode="aspectFit"></image>
            </view>
            <view class="settings-content">
              <text class="settings-name">联系客服</text>
              <text class="settings-desc" v-if="isVip">VIP优先响应</text>
            </view>
            <view class="settings-arrow">
              <image src="/static/images/icons/arrow-right.png" mode="aspectFit"></image>
            </view>
          </view>
          <view class="settings-item" @tap="rateApp">
            <view class="settings-icon">
              <image src="/static/images/icons/star.png" mode="aspectFit"></image>
            </view>
            <view class="settings-content">
              <text class="settings-name">评分鼓励</text>
            </view>
            <view class="settings-arrow">
              <image src="/static/images/icons/arrow-right.png" mode="aspectFit"></image>
            </view>
          </view>
          <view class="settings-item" @tap="aboutApp">
            <view class="settings-icon">
              <image src="/static/images/icons/info.png" mode="aspectFit"></image>
            </view>
            <view class="settings-content">
              <text class="settings-name">关于语撩AI</text>
              <text class="settings-desc">版本 1.0.0</text>
            </view>
            <view class="settings-arrow">
              <image src="/static/images/icons/arrow-right.png" mode="aspectFit"></image>
            </view>
          </view>
        </view>
      </view>
      
      <!-- 退出登录按钮 -->
      <view class="logout-btn" @tap="confirmLogout">退出登录</view>
    </view>
  </view>
</template>

<script>
import { mapGetters } from 'vuex'
import { formatDate } from '@/utils/common'

export default {
  data() {
    return {
      loading: false,
      cacheSize: '0KB',
      usageStats: {},
      settings: {
        notification: true,
        privacy: 'standard',
        chatHistoryRetention: 30
      },
      retentionOptions: ['7天', '30天', '90天', '365天']
    }
  },
  computed: {
    ...mapGetters({
      statusBarHeight: 'app/statusBarHeight',
      userInfo: 'user/userInfo',
      isVip: 'user/isVip',
      vipExpireTime: 'user/vipExpireTime'
    }),
    
    // 注册日期格式化
    formatRegDate() {
      if (!this.userInfo || !this.userInfo.registrationDate) return ''
      return formatDate(this.userInfo.registrationDate, 'YYYY年MM月DD日')
    },
    
    // 计算VIP剩余天数
    vipDaysRemaining() {
      if (!this.isVip || !this.vipExpireTime) return 0
      
      const now = new Date().getTime()
      const expire = new Date(this.vipExpireTime).getTime()
      const diffDays = Math.max(0, Math.ceil((expire - now) / (1000 * 60 * 60 * 24)))
      
      return diffDays
    }
  },
  onShow() {
    this.loadUserProfile()
    this.loadUserSettings()
    this.loadUsageStats()
    this.getStorageInfo()
  },
  methods: {
    // 加载用户资料
    async loadUserProfile() {
      try {
        await this.$store.dispatch('user/getUserInfo')
      } catch (error) {
        this.$log.error('加载用户资料失败', error)
      }
    },
    
    // 加载用户设置
    async loadUserSettings() {
      try {
        const result = await this.$api.user.getSettings()
        if (result) {
          this.settings = {
            ...this.settings,
            ...result
          }
        }
      } catch (error) {
        this.$log.error('加载用户设置失败', error)
      }
    },
    
    // 加载使用统计
    async loadUsageStats() {
      try {
        const stats = await this.$api.user.getUsageStats()
        if (stats) {
          this.usageStats = {
            ...stats,
            successRateText: stats.successRate ? `${stats.successRate}%` : '0%'
          }
        }
      } catch (error) {
        this.$log.error('加载使用统计失败', error)
      }
    },
    
    // 获取存储信息
    getStorageInfo() {
      uni.getStorageInfo({
        success: (res) => {
          const sizeInKB = Math.ceil(res.currentSize)
          
          if (sizeInKB < 1024) {
            this.cacheSize = sizeInKB + 'KB'
          } else {
            this.cacheSize = (sizeInKB / 1024).toFixed(2) + 'MB'
          }
        }
      })
    },
    
    // 改变头像
    changeAvatar() {
      uni.chooseImage({
        count: 1,
        sizeType: ['compressed'],
        sourceType: ['album', 'camera'],
        success: (res) => {
          const tempFilePath = res.tempFilePaths[0]
          
          // 跳转到裁剪页面或直接上传头像
          this.uploadAvatar(tempFilePath)
        }
      })
    },
    
    // 上传头像
    async uploadAvatar(filePath) {
      try {
        this.$toast('头像上传中...')
        
        const result = await this.$api.user.uploadAvatar(filePath)
        
        if (result && result.avatar) {
          // 更新本地用户信息
          this.$store.commit('user/SET_USER_AVATAR', result.avatar)
          this.$toast('头像上传成功')
        }
      } catch (error) {
        this.$log.error('头像上传失败', error)
        this.$toast('头像上传失败')
      }
    },
    
    // 切换通知设置
    async toggleNotification(e) {
      try {
        const value = e.detail.value
        
        await this.$api.user.updateSettings({
          notification: value
        })
        
        this.settings.notification = value
      } catch (error) {
        this.$log.error('更新设置失败', error)
        this.$toast('设置更新失败')
      }
    },
    
    // 切换隐私设置
    async togglePrivacy(e) {
      try {
        const value = e.detail.value
        const privacy = value ? 'strict' : 'standard'
        
        await this.$api.user.updateSettings({
          privacy
        })
        
        this.settings.privacy = privacy
      } catch (error) {
        this.$log.error('更新设置失败', error)
        this.$toast('设置更新失败')
      }
    },
    
    // 修改聊天记录保存时长
    async changeChatRetention(e) {
      try {
        const index = e.detail.value
        const days = this.getRetentionDays(index)
        
        await this.$api.user.updateSettings({
          chatHistoryRetention: days
        })
        
        this.settings.chatHistoryRetention = days
        this.$toast('设置已更新')
      } catch (error) {
        this.$log.error('更新设置失败', error)
        this.$toast('设置更新失败')
      }
    },
    
    // 获取保存时长选项索引
    getRetentionIndex() {
      const days = this.settings.chatHistoryRetention
      
      if (days <= 7) return 0
      if (days <= 30) return 1
      if (days <= 90) return 2
      return 3
    },
    
    // 获取保存时长文本
    getRetentionText() {
      const days = this.settings.chatHistoryRetention
      
      if (days <= 7) return '7天'
      if (days <= 30) return '30天'
      if (days <= 90) return '90天'
      return '365天'
    },
    
    // 根据索引获取保存天数
    getRetentionDays(index) {
      const options = [7, 30, 90, 365]
      return options[index] || 30
    },
    
    // 清除缓存
    clearCache() {
      uni.showModal({
        title: '确认清除',
        content: '清除缓存将删除本地临时数据，不会影响您的账户信息和聊天记录',
        success: (res) => {
          if (res.confirm) {
            uni.clearStorageSync()
            this.getStorageInfo()
            this.$toast('缓存已清除')
          }
        }
      })
    },
    
    // 退出登录确认
    confirmLogout() {
      uni.showModal({
        title: '确认退出',
        content: '确定要退出当前账号吗？',
        success: (res) => {
          if (res.confirm) {
            this.logout()
          }
        }
      })
    },
    
    // 退出登录
    async logout() {
      try {
        await this.$store.dispatch('user/logout')
        
        // 跳转到登录页
        uni.reLaunch({
          url: '/pages/login/login'
        })
      } catch (error) {
        this.$log.error('退出登录失败', error)
        this.$toast('退出失败，请重试')
      }
    },
    
    // 导航到个人资料设置
    navigateToProfileSetup() {
      uni.navigateTo({
        url: '/pages/profile/profile-setup'
      })
    },
    
    // 导航到VIP页面
    navigateToVip() {
      uni.navigateTo({
        url: '/pages/vip/vip'
      })
    },
    
    // 导航到聊天记录页面
    navigateToChat() {
      uni.switchTab({
        url: '/pages/chat/chat'
      })
    },
    
    // 导航到FAQ页面
    navigateToFaq() {
      uni.navigateTo({
        url: '/pages/about/faq'
      })
    },
    
    // 联系客服
    contactSupport() {
      // 实现联系客服功能，如跳转到客服会话
      uni.showToast({
        title: '正在跳转到客服...',
        icon: 'none'
      })
    },
    
    // 应用评分
    rateApp() {
      // 跳转到评分页面
      uni.showToast({
        title: '谢谢您的鼓励！',
        icon: 'none'
      })
    },
    
    // 关于应用
    aboutApp() {
      uni.navigateTo({
        url: '/pages/about/about'
      })
    }
  }
}
</script>

<style lang="scss">
.profile-container {
  min-height: 100vh;
  background-color: #f5f5f5;
}

.navbar {
  padding: 10rpx 30rpx;
  background: linear-gradient(135deg, #ff6b6b, #fc466b);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  z-index: 100;
  
  &__title {
    font-size: 36rpx;
    font-weight: bold;
  }
}

.user-card {
  background: linear-gradient(135deg, #ff6b6b, #fc466b);
  color: #fff;
  padding: 30rpx;
  padding-bottom: 40rpx;
}

.user-info {
  display: flex;
  align-items: center;
  padding-bottom: 30rpx;
}

.user-avatar {
  width: 120rpx;
  height: 120rpx;
  border-radius: 60rpx;
  background-color: #fff;
  position: relative;
  margin-right: 30rpx;
  
  image {
    width: 100%;
    height: 100%;
    border-radius: 60rpx;
  }
  
  &__edit {
    position: absolute;
    right: -5rpx;
    bottom: -5rpx;
    width: 40rpx;
    height: 40rpx;
    background-color: #fff;
    border-radius: 20rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.2);
    
    image {
      width: 24rpx;
      height: 24rpx;
    }
  }
}

.user-details {
  flex: 1;
}

.user-name {
  display: flex;
  align-items: center;
  font-size: 36rpx;
  font-weight: bold;
  margin-bottom: 10rpx;
}

.user-badge {
  margin-left: 15rpx;
  
  image {
    width: 40rpx;
    height: 40rpx;
  }
}

.user-meta {
  font-size: 24rpx;
  opacity: 0.8;
}

.edit-btn {
  width: 60rpx;
  height: 60rpx;
  
  image {
    width: 40rpx;
    height: 40rpx;
  }
}

.membership-status {
  background-color: rgba(255, 255, 255, 0.15);
  border-radius: 12rpx;
  padding: 20rpx;
  display: flex;
  align-items: center;
}

.membership-icon {
  margin-right: 20rpx;
  
  image {
    width: 60rpx;
    height: 60rpx;
  }
}

.membership-info {
  flex: 1;
}

.membership-title {
  display: flex;
  align-items: center;
  font-weight: bold;
  font-size: 30rpx;
  margin-bottom: 6rpx;
}

.membership-tag {
  margin-left: 15rpx;
  background-color: #fff;
  color: #fc466b;
  font-size: 20rpx;
  padding: 2rpx 10rpx;
  border-radius: 10rpx;
}

.membership-desc {
  font-size: 24rpx;
  opacity: 0.9;
}

.membership-action {
  display: flex;
  align-items: center;
  font-size: 26rpx;
  
  image {
    width: 30rpx;
    height: 30rpx;
    margin-left: 6rpx;
  }
}

.section-container {
  padding: 30rpx;
  transform: translateY(-20rpx);
}

.card {
  background-color: #fff;
  border-radius: 12rpx;
  overflow: hidden;
  margin-bottom: 30rpx;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.05);
}

.card-header {
  padding: 20rpx 30rpx;
  border-bottom: 1rpx solid #f0f0f0;
}

.card-title {
  font-size: 30rpx;
  font-weight: 500;
  color: #333;
}

.stat-grid {
  display: flex;
  padding: 30rpx;
}

.stat-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.stat-value {
  font-size: 40rpx;
  font-weight: bold;
  color: #fc466b;
  margin-bottom: 10rpx;
}

.stat-label {
  font-size: 24rpx;
  color: #999;
}

.settings-list {
  padding: 10rpx 0;
}

.settings-item {
  display: flex;
  align-items: center;
  padding: 20rpx 30rpx;
  
  &:active {
    background-color: #f9f9f9;
  }
  
  &--switch:active {
    background-color: transparent;
  }
}

.settings-icon {
  margin-right: 20rpx;
  
  image {
    width: 44rpx;
    height: 44rpx;
  }
}

.settings-content {
  flex: 1;
}

.settings-name {
  font-size: 28rpx;
  color: #333;
}

.settings-desc {
  font-size: 24rpx;
  color: #999;
  margin-top: 6rpx;
}

.settings-arrow {
  image {
    width: 30rpx;
    height: 30rpx;
  }
}

.logout-btn {
  background-color: #fff;
  height: 90rpx;
  border-radius: 12rpx;
  margin-top: 60rpx;
  margin-bottom: 60rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 30rpx;
  color: #fc466b;
  
  &:active {
    opacity: 0.8;
  }
}
</style> 