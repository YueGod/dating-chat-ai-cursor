<template>
  <view class="profile-setup-container">
    <!-- 状态栏高度占位 -->
    <view class="status-bar" :style="{ height: statusBarHeight + 'px' }"></view>
    
    <!-- 顶部导航栏 -->
    <view class="navbar">
      <view class="navbar__back" @tap="navigateBack">
        <image src="/static/images/icons/back.png" mode="aspectFit"></image>
      </view>
      <view class="navbar__title">个人资料设置</view>
      <view class="navbar__action" @tap="saveProfile" v-if="!saving">
        <text>保存</text>
      </view>
      <view class="navbar__action navbar__action--loading" v-else>
        <text>保存中...</text>
      </view>
    </view>
    
    <!-- 表单内容区 -->
    <scroll-view class="content" scroll-y>
      <!-- 头像修改 -->
      <view class="avatar-section">
        <view class="avatar-wrapper" @tap="changeAvatar">
          <image :src="profileForm.avatar || '/static/images/default-avatar.png'" mode="aspectFill" class="avatar-image"></image>
          <view class="avatar-edit">
            <image src="/static/images/icons/camera.png" mode="aspectFit"></image>
            <text>更换头像</text>
          </view>
        </view>
      </view>
      
      <!-- 表单项 -->
      <view class="form-section">
        <view class="form-item">
          <text class="form-item__label">昵称</text>
          <input 
            class="form-item__input" 
            v-model="profileForm.nickname" 
            placeholder="请输入昵称" 
            maxlength="20"
          />
        </view>
        
        <view class="form-item">
          <text class="form-item__label">性别</text>
          <picker 
            class="form-item__picker" 
            @change="handleGenderChange" 
            :value="genderIndex" 
            :range="genderOptions"
          >
            <view class="picker-value">
              <text>{{profileForm.gender ? genderOptions[genderIndex] : '请选择'}}</text>
              <image src="/static/images/icons/arrow-down.png" mode="aspectFit"></image>
            </view>
          </picker>
        </view>
        
        <view class="form-item">
          <text class="form-item__label">生日</text>
          <picker 
            class="form-item__picker" 
            mode="date" 
            @change="handleBirthdayChange" 
            :value="profileForm.birthday || '1995-01-01'"
            start="1950-01-01" 
            end="2010-01-01"
          >
            <view class="picker-value">
              <text>{{profileForm.birthday || '请选择'}}</text>
              <image src="/static/images/icons/arrow-down.png" mode="aspectFit"></image>
            </view>
          </picker>
        </view>
        
        <view class="form-item">
          <text class="form-item__label">邮箱</text>
          <input 
            class="form-item__input" 
            v-model="profileForm.email" 
            placeholder="请输入邮箱" 
            type="text"
          />
        </view>
        
        <view class="form-item">
          <text class="form-item__label">个人简介</text>
          <textarea 
            class="form-item__textarea" 
            v-model="profileForm.bio" 
            placeholder="请输入个人简介" 
            maxlength="100"
          ></textarea>
          <text class="textarea-counter">{{profileForm.bio ? profileForm.bio.length : 0}}/100</text>
        </view>
      </view>
      
      <!-- 兴趣爱好区域 -->
      <view class="interests-section">
        <view class="section-title">
          <text>兴趣爱好</text>
          <text class="section-subtitle">选择您感兴趣的话题（最多5个）</text>
        </view>
        
        <view class="interests-grid">
          <view 
            v-for="(interest, index) in interests" 
            :key="index"
            class="interest-tag"
            :class="{'interest-tag--selected': isInterestSelected(interest)}"
            @tap="toggleInterest(interest)"
          >
            <text>{{interest}}</text>
          </view>
        </view>
      </view>
      
      <!-- 提交按钮区 -->
      <view class="submit-section">
        <button 
          class="submit-btn" 
          :loading="saving" 
          :disabled="saving" 
          @tap="saveProfile"
        >
          保存资料
        </button>
      </view>
    </scroll-view>
  </view>
</template>

<script>
import { mapGetters } from 'vuex'

export default {
  data() {
    return {
      saving: false,
      isNewUser: false,
      profileForm: {
        nickname: '',
        gender: '',
        birthday: '',
        email: '',
        bio: '',
        avatar: '',
        interests: []
      },
      genderOptions: ['男', '女', '保密'],
      interests: [
        '音乐', '电影', '旅行', '美食', '阅读', '运动', '游戏', '摄影', 
        '艺术', '设计', '科技', '动漫', '宠物', '时尚', '健身', '购物',
        '社交', '手工', '教育', '投资'
      ]
    }
  },
  computed: {
    ...mapGetters({
      statusBarHeight: 'app/statusBarHeight',
      userInfo: 'user/userInfo'
    }),
    
    // 性别选择器索引
    genderIndex() {
      const gender = this.profileForm.gender
      if (gender === 'male') return 0
      if (gender === 'female') return 1
      if (gender === 'secret') return 2
      return 0
    }
  },
  onLoad(options) {
    // 检查是否新用户设置页面
    if (options.newUser === 'true') {
      this.isNewUser = true
    }
    
    // 加载用户资料
    this.loadUserProfile()
  },
  methods: {
    // 加载用户资料
    async loadUserProfile() {
      try {
        // 从 store 获取用户信息
        if (this.userInfo) {
          this.profileForm = {
            nickname: this.userInfo.nickname || '',
            gender: this.userInfo.gender || '',
            birthday: this.userInfo.birthday || '',
            email: this.userInfo.email || '',
            bio: this.userInfo.bio || '',
            avatar: this.userInfo.avatar || '',
            interests: this.userInfo.interests || []
          }
        } else {
          // 如果store中没有用户信息，尝试获取
          await this.$store.dispatch('user/getUserInfo')
          
          // 再次更新表单
          if (this.userInfo) {
            this.profileForm = {
              nickname: this.userInfo.nickname || '',
              gender: this.userInfo.gender || '',
              birthday: this.userInfo.birthday || '',
              email: this.userInfo.email || '',
              bio: this.userInfo.bio || '',
              avatar: this.userInfo.avatar || '',
              interests: this.userInfo.interests || []
            }
          }
        }
      } catch (error) {
        this.$log.error('加载用户资料失败', error)
        this.$toast('加载资料失败，请重试')
      }
    },
    
    // 改变头像
    changeAvatar() {
      uni.chooseImage({
        count: 1,
        sizeType: ['compressed'],
        sourceType: ['album', 'camera'],
        success: (res) => {
          const tempFilePath = res.tempFilePaths[0]
          
          // 直接更新本地表单
          this.profileForm.avatar = tempFilePath
          
          // 标记本地图片，后续保存时上传
          this.profileForm.avatarChanged = true
          this.profileForm.avatarPath = tempFilePath
        }
      })
    },
    
    // 处理性别更改
    handleGenderChange(e) {
      const index = e.detail.value
      const genderMap = ['male', 'female', 'secret']
      this.profileForm.gender = genderMap[index]
    },
    
    // 处理生日更改
    handleBirthdayChange(e) {
      this.profileForm.birthday = e.detail.value
    },
    
    // 判断兴趣是否已选择
    isInterestSelected(interest) {
      return this.profileForm.interests.includes(interest)
    },
    
    // 切换兴趣选择状态
    toggleInterest(interest) {
      const index = this.profileForm.interests.indexOf(interest)
      
      if (index === -1) {
        // 如果未选择，且选择数量未达到上限，则添加
        if (this.profileForm.interests.length < 5) {
          this.profileForm.interests.push(interest)
        } else {
          this.$toast('最多只能选择5个兴趣')
        }
      } else {
        // 如果已选择，则移除
        this.profileForm.interests.splice(index, 1)
      }
    },
    
    // 表单验证
    validateForm() {
      if (!this.profileForm.nickname) {
        this.$toast('请输入昵称')
        return false
      }
      
      if (!this.profileForm.gender) {
        this.$toast('请选择性别')
        return false
      }
      
      if (this.profileForm.email && !this.validateEmail(this.profileForm.email)) {
        this.$toast('邮箱格式不正确')
        return false
      }
      
      return true
    },
    
    // 邮箱格式验证
    validateEmail(email) {
      const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
      return regex.test(email)
    },
    
    // 保存用户资料
    async saveProfile() {
      if (!this.validateForm()) return
      
      this.saving = true
      
      try {
        // 准备提交数据
        const profileData = {
          nickname: this.profileForm.nickname,
          gender: this.profileForm.gender,
          birthday: this.profileForm.birthday,
          email: this.profileForm.email,
          bio: this.profileForm.bio,
          interests: this.profileForm.interests
        }
        
        // 如果头像已更改，先上传头像
        if (this.profileForm.avatarChanged && this.profileForm.avatarPath) {
          try {
            const result = await this.$api.user.uploadAvatar(this.profileForm.avatarPath)
            if (result && result.avatar) {
              profileData.avatar = result.avatar
            }
          } catch (error) {
            this.$log.error('头像上传失败', error)
            this.$toast('头像上传失败，其他资料将继续保存')
          }
        }
        
        // 提交资料更新
        const result = await this.$api.user.updateProfile(profileData)
        
        // 更新本地存储
        this.$store.commit('user/UPDATE_USER_INFO', result)
        
        this.$toast('资料保存成功')
        
        // 如果是新用户设置，保存后跳转到首页
        if (this.isNewUser) {
          uni.reLaunch({
            url: '/pages/index/index'
          })
        } else {
          this.navigateBack()
        }
      } catch (error) {
        this.$log.error('保存资料失败', error)
        this.$toast('保存失败，请重试')
      } finally {
        this.saving = false
      }
    },
    
    // 返回上一页
    navigateBack() {
      // 判断表单是否有变化
      const hasChanges = this.checkFormChanges()
      
      if (hasChanges) {
        uni.showModal({
          title: '放弃修改',
          content: '您的资料已修改但未保存，确定要离开吗？',
          success: (res) => {
            if (res.confirm) {
              uni.navigateBack()
            }
          }
        })
      } else {
        uni.navigateBack()
      }
    },
    
    // 检查表单是否有变化
    checkFormChanges() {
      if (!this.userInfo) return false
      
      return (
        this.profileForm.nickname !== this.userInfo.nickname ||
        this.profileForm.gender !== this.userInfo.gender ||
        this.profileForm.birthday !== this.userInfo.birthday ||
        this.profileForm.email !== this.userInfo.email ||
        this.profileForm.bio !== this.userInfo.bio ||
        this.profileForm.avatarChanged ||
        JSON.stringify(this.profileForm.interests) !== JSON.stringify(this.userInfo.interests || [])
      )
    }
  }
}
</script>

<style lang="scss">
.profile-setup-container {
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
  
  &__title {
    flex: 1;
    text-align: center;
    font-size: 32rpx;
    font-weight: bold;
  }
  
  &__action {
    font-size: 28rpx;
    
    &--loading {
      opacity: 0.7;
    }
  }
}

.content {
  flex: 1;
  padding-bottom: 40rpx;
}

.avatar-section {
  background-color: #fff;
  padding: 40rpx 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 20rpx;
}

.avatar-wrapper {
  position: relative;
  width: 160rpx;
  height: 160rpx;
  border-radius: 80rpx;
  overflow: hidden;
}

.avatar-image {
  width: 100%;
  height: 100%;
}

.avatar-edit {
  position: absolute;
  left: 0;
  right: 0;
  bottom: 0;
  height: 50rpx;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  
  image {
    width: 24rpx;
    height: 24rpx;
    margin-right: 6rpx;
  }
  
  text {
    font-size: 22rpx;
    color: #fff;
  }
}

.form-section {
  background-color: #fff;
  margin-bottom: 20rpx;
}

.form-item {
  padding: 30rpx;
  border-bottom: 1rpx solid #f5f5f5;
  position: relative;
  
  &:last-child {
    border-bottom: none;
  }
  
  &__label {
    font-size: 28rpx;
    color: #333;
    margin-bottom: 20rpx;
    display: block;
  }
  
  &__input {
    font-size: 30rpx;
    height: 80rpx;
    width: 100%;
  }
  
  &__picker {
    height: 80rpx;
    width: 100%;
  }
  
  &__textarea {
    width: 100%;
    height: 200rpx;
    font-size: 30rpx;
  }
}

.textarea-counter {
  position: absolute;
  right: 30rpx;
  bottom: 30rpx;
  font-size: 24rpx;
  color: #999;
}

.picker-value {
  height: 80rpx;
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 30rpx;
  
  image {
    width: 30rpx;
    height: 30rpx;
  }
}

.interests-section {
  background-color: #fff;
  padding: 30rpx;
  margin-bottom: 40rpx;
}

.section-title {
  font-size: 28rpx;
  color: #333;
  font-weight: 500;
  margin-bottom: 20rpx;
}

.section-subtitle {
  font-size: 24rpx;
  color: #999;
  font-weight: normal;
  margin-left: 10rpx;
}

.interests-grid {
  display: flex;
  flex-wrap: wrap;
  margin: 0 -10rpx;
}

.interest-tag {
  height: 70rpx;
  border-radius: 35rpx;
  padding: 0 30rpx;
  background-color: #f5f5f5;
  margin: 10rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  
  text {
    font-size: 26rpx;
    color: #666;
  }
  
  &--selected {
    background: linear-gradient(135deg, #ff6b6b, #fc466b);
    
    text {
      color: #fff;
    }
  }
}

.submit-section {
  padding: 0 30rpx;
}

.submit-btn {
  width: 100%;
  height: 90rpx;
  background: linear-gradient(135deg, #ff6b6b, #fc466b);
  color: #fff;
  border-radius: 45rpx;
  font-size: 32rpx;
  font-weight: bold;
  display: flex;
  align-items: center;
  justify-content: center;
  
  &:disabled {
    opacity: 0.6;
  }
}
</style> 