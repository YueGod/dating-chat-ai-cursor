<template>
  <view class="vip-container">
    <!-- 状态栏高度占位 -->
    <view class="status-bar" :style="{ height: statusBarHeight + 'px' }"></view>
    
    <!-- 顶部导航栏 -->
    <view class="navbar">
      <view class="navbar__back" @tap="navigateBack">
        <image src="/static/images/icons/back.png" mode="aspectFit"></image>
      </view>
      <view class="navbar__title">会员服务</view>
      <view class="navbar__placeholder"></view>
    </view>
    
    <!-- 页面内容区域 -->
    <scroll-view class="content" scroll-y>
      <!-- 顶部会员横幅 -->
      <view class="vip-banner">
        <view class="vip-banner__content">
          <image src="/static/images/vip-banner.png" class="vip-banner__image" mode="aspectFill"></image>
          <view class="vip-banner__info">
            <view class="vip-banner__title">{{isVip ? '尊贵VIP会员' : '升级VIP会员'}}</view>
            <view class="vip-banner__subtitle">{{isVip ? `有效期至 ${formatVipExpireTime}` : '解锁所有高级功能'}}</view>
          </view>
        </view>
      </view>
      
      <!-- 会员权益 -->
      <view class="benefits-section">
        <view class="section-title">会员尊享特权</view>
        
        <view class="benefits-grid">
          <view class="benefit-item" v-for="(benefit, index) in benefits" :key="index">
            <image :src="benefit.icon" class="benefit-item__icon" mode="aspectFit"></image>
            <view class="benefit-item__info">
              <view class="benefit-item__title">{{benefit.title}}</view>
              <view class="benefit-item__desc">{{benefit.description}}</view>
            </view>
          </view>
        </view>
      </view>
      
      <!-- 会员套餐选择 -->
      <view class="plans-section" v-if="!isVip">
        <view class="section-title">选择套餐</view>
        
        <view class="plans-container">
          <view 
            v-for="(plan, index) in membershipPlans" 
            :key="plan.id"
            class="plan-card"
            :class="{
              'plan-card--selected': selectedPlan === plan.id,
              'plan-card--recommend': plan.recommended
            }"
            @tap="selectPlan(plan.id)"
          >
            <view class="plan-card__tag" v-if="plan.recommended">
              <text>推荐</text>
            </view>
            <view class="plan-card__header">
              <view class="plan-card__title">{{plan.name}}</view>
              <view class="plan-card__price">
                <text class="plan-card__price-value">¥{{plan.price}}</text>
                <text class="plan-card__price-unit">/{{plan.periodText}}</text>
              </view>
              <view class="plan-card__original" v-if="plan.originalPrice">
                <text>原价¥{{plan.originalPrice}}</text>
              </view>
            </view>
            <view class="plan-card__body">
              <view class="plan-card__feature" v-for="(feature, idx) in plan.features" :key="idx">
                <image src="/static/images/icons/check-circle.png" mode="aspectFit"></image>
                <text>{{feature}}</text>
              </view>
            </view>
          </view>
        </view>
      </view>
      
      <!-- 优惠券输入区域 -->
      <view class="coupon-section" v-if="!isVip">
        <view class="coupon-input">
          <input 
            class="coupon-input__field" 
            v-model="couponCode" 
            placeholder="输入优惠码" 
            :disabled="loading"
          />
          <view class="coupon-input__btn" @tap="verifyCoupon" v-if="!couponCode || !couponVerified">验证</view>
          <view class="coupon-input__btn coupon-input__btn--verified" v-else>
            <image src="/static/images/icons/check.png" mode="aspectFit"></image>
            <text>已应用</text>
          </view>
        </view>
        <view class="coupon-message" v-if="couponMessage">{{couponMessage}}</view>
      </view>
      
      <!-- 会员对比表 -->
      <view class="compare-section">
        <view class="section-title">会员权益对比</view>
        
        <view class="compare-table">
          <view class="compare-table__header">
            <view class="compare-table__cell"></view>
            <view class="compare-table__cell">免费用户</view>
            <view class="compare-table__cell">VIP会员</view>
          </view>
          
          <view class="compare-table__row" v-for="(feature, index) in featureComparison" :key="index">
            <view class="compare-table__cell">{{feature.name}}</view>
            <view class="compare-table__cell">
              <view class="compare-table__value">{{feature.free}}</view>
            </view>
            <view class="compare-table__cell">
              <view class="compare-table__value compare-table__value--vip">{{feature.vip}}</view>
            </view>
          </view>
        </view>
      </view>
      
      <!-- 会员常见问题 -->
      <view class="faq-section">
        <view class="section-title">常见问题</view>
        
        <view class="faq-list">
          <view 
            class="faq-item" 
            v-for="(faq, index) in faqs" 
            :key="index"
            :class="{'faq-item--active': activeFaq === index}"
            @tap="toggleFaq(index)"
          >
            <view class="faq-item__header">
              <text class="faq-item__question">{{faq.question}}</text>
              <image 
                :src="activeFaq === index ? '/static/images/icons/arrow-up.png' : '/static/images/icons/arrow-down.png'" 
                mode="aspectFit"
              ></image>
            </view>
            <view class="faq-item__body" v-if="activeFaq === index">
              <text class="faq-item__answer">{{faq.answer}}</text>
            </view>
          </view>
        </view>
      </view>
      
      <!-- 购买按钮区域 -->
      <view class="purchase-section" v-if="!isVip">
        <view class="purchase-summary">
          <view class="purchase-summary__title">总计</view>
          <view class="purchase-summary__price">
            <text class="purchase-summary__original" v-if="discountedPrice !== selectedPlanPrice">¥{{selectedPlanPrice}}</text>
            <text class="purchase-summary__value">¥{{discountedPrice}}</text>
          </view>
        </view>
        
        <button 
          class="purchase-btn" 
          :loading="loading" 
          :disabled="loading || !selectedPlan" 
          @tap="purchaseVip"
        >
          立即开通
        </button>
        
        <view class="purchase-terms">
          <text>开通即表示同意</text>
          <text class="purchase-terms__link" @tap="showTerms">《会员服务条款》</text>
        </view>
      </view>
      
      <!-- 续费按钮区域 -->
      <view class="purchase-section" v-if="isVip">
        <button 
          class="purchase-btn" 
          @tap="showRenewPlans"
        >
          {{autoRenew ? '管理自动续费' : '立即续费'}}
        </button>
        
        <view class="purchase-tips" v-if="autoRenew">
          <text>已开启自动续费，将于到期前24小时自动扣款</text>
        </view>
      </view>
    </scroll-view>
  </view>
</template>

<script>
import { mapGetters } from 'vuex'
import { formatDate } from '@/utils/common'

export default {
  data() {
    return {
      loading: false,
      selectedPlan: 'monthly',
      couponCode: '',
      couponVerified: false,
      couponDiscount: 0,
      couponMessage: '',
      activeFaq: null,
      autoRenew: false,
      
      membershipPlans: [
        {
          id: 'monthly',
          name: '月度会员',
          price: 39.99,
          originalPrice: 59.99,
          period: 'month',
          periodText: '月',
          features: [
            '解锁全部风格',
            '每日200次使用量',
            '高级会话分析'
          ],
          recommended: false
        },
        {
          id: 'quarterly',
          name: '季度会员',
          price: 99.99,
          originalPrice: 129.99,
          period: 'quarter',
          periodText: '季',
          features: [
            '解锁全部风格',
            '每日200次使用量',
            '高级会话分析',
            '会员特别活动'
          ],
          recommended: true
        },
        {
          id: 'yearly',
          name: '年度会员',
          price: 299.99,
          originalPrice: 499.99,
          period: 'year',
          periodText: '年',
          features: [
            '解锁全部风格',
            '无限使用次数',
            '高级会话分析',
            '会员特别活动',
            '专属客服通道'
          ],
          recommended: false
        }
      ],
      
      benefits: [
        {
          icon: '/static/images/icons/vip-style.png',
          title: '高级聊天风格',
          description: '解锁15+种高级聊天风格'
        },
        {
          icon: '/static/images/icons/vip-unlimited.png',
          title: '增加使用次数',
          description: '每日高达200次，年费无限制'
        },
        {
          icon: '/static/images/icons/vip-analytics.png',
          title: '会话情感分析',
          description: '深度解析聊天情感与建议'
        },
        {
          icon: '/static/images/icons/vip-support.png',
          title: '优先客服支持',
          description: 'VIP专属客服通道'
        }
      ],
      
      featureComparison: [
        {
          name: '基础聊天风格',
          free: '3种',
          vip: '全部'
        },
        {
          name: '高级聊天风格',
          free: '不支持',
          vip: '15+种'
        },
        {
          name: '每日使用次数',
          free: '20次',
          vip: '200次'
        },
        {
          name: '情感分析',
          free: '不支持',
          vip: '支持'
        },
        {
          name: '个性化推荐',
          free: '不支持',
          vip: '支持'
        },
        {
          name: '客服响应',
          free: '普通通道',
          vip: '优先通道'
        }
      ],
      
      faqs: [
        {
          question: '如何开通VIP会员？',
          answer: '选择您喜欢的套餐，点击"立即开通"按钮，使用微信支付完成付款即可立即开通VIP会员服务。'
        },
        {
          question: '会员服务有哪些特权？',
          answer: 'VIP会员可享受解锁全部高级聊天风格、每日高达200次(年费无限)的使用次数、会话情感分析、VIP专属客服等多种权益。'
        },
        {
          question: '会员到期后数据会丢失吗？',
          answer: '会员到期后，您的历史聊天记录和生成的内容将会保留，但新的使用会受到免费用户限制。'
        },
        {
          question: '如何取消自动续费？',
          answer: '在个人中心-会员服务中点击"管理自动续费"，按照提示操作即可取消自动续费。'
        },
        {
          question: '可以申请退款吗？',
          answer: '根据服务条款，会员服务开通后不支持退款。如有特殊情况，请联系客服解决。'
        }
      ]
    }
  },
  computed: {
    ...mapGetters({
      statusBarHeight: 'app/statusBarHeight',
      userInfo: 'user/userInfo',
      isVip: 'user/isVip',
      vipExpireTime: 'user/vipExpireTime'
    }),
    
    // 格式化VIP到期时间
    formatVipExpireTime() {
      if (!this.vipExpireTime) return ''
      return formatDate(this.vipExpireTime, 'YYYY年MM月DD日')
    },
    
    // 当前选中套餐原价
    selectedPlanPrice() {
      const plan = this.membershipPlans.find(p => p.id === this.selectedPlan)
      return plan ? plan.price : 0
    },
    
    // 计算优惠后价格
    discountedPrice() {
      if (!this.couponVerified || this.couponDiscount <= 0) {
        return this.selectedPlanPrice
      }
      
      // 计算折扣价格，保留两位小数
      return Math.max(0.01, (this.selectedPlanPrice * (1 - this.couponDiscount / 100)).toFixed(2))
    }
  },
  onLoad(options) {
    // 默认选中推荐套餐
    const recommendedPlan = this.membershipPlans.find(p => p.recommended)
    if (recommendedPlan) {
      this.selectedPlan = recommendedPlan.id
    }
    
    // 检查是否已经是会员
    this.checkVipStatus()
    
    // 如果有优惠码参数，自动填入
    if (options.coupon) {
      this.couponCode = options.coupon
      this.verifyCoupon()
    }
  },
  methods: {
    // 检查会员状态
    async checkVipStatus() {
      try {
        await this.$store.dispatch('user/getUserInfo')
        
        // 如果是会员，检查自动续费状态
        if (this.isVip) {
          const memberInfo = await this.$api.vip.getMembershipInfo()
          if (memberInfo && memberInfo.autoRenew !== undefined) {
            this.autoRenew = memberInfo.autoRenew
          }
        }
      } catch (error) {
        this.$log.error('检查会员状态失败', error)
      }
    },
    
    // 选择套餐
    selectPlan(planId) {
      this.selectedPlan = planId
    },
    
    // 验证优惠券
    async verifyCoupon() {
      if (!this.couponCode || this.loading) return
      
      this.loading = true
      this.couponMessage = ''
      
      try {
        const result = await this.$api.vip.verifyCoupon({
          couponCode: this.couponCode,
          planId: this.selectedPlan
        })
        
        if (result && result.valid) {
          this.couponVerified = true
          this.couponDiscount = result.discountValue || 0
          this.couponMessage = `优惠券有效，已享受${result.discountValue}%折扣`
        } else {
          this.couponVerified = false
          this.couponDiscount = 0
          this.couponMessage = '优惠券无效或已过期'
        }
      } catch (error) {
        this.$log.error('验证优惠券失败', error)
        this.couponVerified = false
        this.couponDiscount = 0
        this.couponMessage = '验证优惠券失败，请重试'
      } finally {
        this.loading = false
      }
    },
    
    // 切换FAQ展开状态
    toggleFaq(index) {
      this.activeFaq = this.activeFaq === index ? null : index
    },
    
    // 显示会员条款
    showTerms() {
      uni.navigateTo({
        url: '/pages/about/terms'
      })
    },
    
    // 购买VIP
    async purchaseVip() {
      if (this.loading || !this.selectedPlan) return
      
      this.loading = true
      
      try {
        // 准备订单数据
        const orderData = {
          planId: this.selectedPlan,
          paymentMethod: 'wechat',
          autoRenew: true
        }
        
        // 如果有验证的优惠券，添加到订单
        if (this.couponVerified && this.couponCode) {
          orderData.couponCode = this.couponCode
        }
        
        // 创建订单
        const orderResult = await this.$api.vip.createOrder(orderData)
        
        if (orderResult && orderResult.paymentInfo) {
          // 调起支付
          uni.requestPayment({
            ...orderResult.paymentInfo,
            success: () => {
              // 支付成功，验证订单状态
              this.verifyPayment(orderResult.orderId)
            },
            fail: (err) => {
              this.$log.error('支付失败', err)
              this.$toast('支付已取消')
              this.loading = false
            }
          })
        } else {
          this.$toast('创建订单失败，请重试')
          this.loading = false
        }
      } catch (error) {
        this.$log.error('购买VIP失败', error)
        this.$toast('购买失败，请重试')
        this.loading = false
      }
    },
    
    // 验证支付结果
    async verifyPayment(orderId) {
      try {
        const result = await this.$api.vip.verifyPayment(orderId)
        
        if (result && result.status === 'paid') {
          // 更新本地会员状态
          await this.$store.dispatch('user/getUserInfo')
          
          // 显示成功提示
          uni.showModal({
            title: '开通成功',
            content: '恭喜您成功开通VIP会员！现在就去体验全部功能吧',
            showCancel: false,
            success: () => {
              // 返回首页
              uni.switchTab({
                url: '/pages/index/index'
              })
            }
          })
        } else {
          this.$toast('支付验证失败，如已扣款请联系客服')
        }
      } catch (error) {
        this.$log.error('验证支付结果失败', error)
        this.$toast('验证支付失败，如已扣款请联系客服')
      } finally {
        this.loading = false
      }
    },
    
    // 显示续费套餐
    showRenewPlans() {
      uni.showActionSheet({
        itemList: ['续费会员', '管理自动续费', '取消'],
        success: (res) => {
          if (res.tapIndex === 0) {
            // 打开续费界面
            this.isVip = false
          } else if (res.tapIndex === 1) {
            // 管理自动续费
            this.toggleAutoRenew()
          }
        }
      })
    },
    
    // 切换自动续费
    async toggleAutoRenew() {
      try {
        await this.$api.vip.updateAutoRenew({
          autoRenew: !this.autoRenew
        })
        
        this.autoRenew = !this.autoRenew
        this.$toast(this.autoRenew ? '已开启自动续费' : '已关闭自动续费')
      } catch (error) {
        this.$log.error('更新自动续费状态失败', error)
        this.$toast('操作失败，请重试')
      }
    },
    
    // 返回上一页
    navigateBack() {
      uni.navigateBack()
    }
  }
}
</script>

<style lang="scss">
.vip-container {
  min-height: 100vh;
  background-color: #f7f7f7;
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
    
    image {
      width: 40rpx;
      height: 40rpx;
    }
  }
  
  &__title {
    flex: 1;
    text-align: center;
    font-size: 36rpx;
    font-weight: bold;
  }
}

.content {
  flex: 1;
  padding-bottom: 40rpx;
}

.vip-banner {
  background: linear-gradient(135deg, #ff6b6b, #fc466b);
  padding: 40rpx 30rpx;
}

.vip-banner__content {
  display: flex;
  align-items: center;
}

.vip-banner__image {
  width: 100rpx;
  height: 100rpx;
  margin-right: 30rpx;
}

.vip-banner__info {
  flex: 1;
}

.vip-banner__title {
  font-size: 36rpx;
  font-weight: bold;
  color: #fff;
  margin-bottom: 10rpx;
}

.vip-banner__subtitle {
  font-size: 28rpx;
  color: rgba(255, 255, 255, 0.9);
}

.section-title {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 30rpx;
}

.benefits-section {
  padding: 40rpx 30rpx;
  background-color: #fff;
  margin-bottom: 20rpx;
}

.benefits-grid {
  display: flex;
  flex-wrap: wrap;
  margin: 0 -10rpx;
}

.benefit-item {
  width: calc(50% - 20rpx);
  margin: 10rpx;
  display: flex;
  align-items: flex-start;
  
  &__icon {
    width: 70rpx;
    height: 70rpx;
    margin-right: 15rpx;
  }
  
  &__info {
    flex: 1;
  }
  
  &__title {
    font-size: 28rpx;
    font-weight: 500;
    color: #333;
    margin-bottom: 6rpx;
  }
  
  &__desc {
    font-size: 24rpx;
    color: #666;
  }
}

.plans-section {
  padding: 40rpx 30rpx;
  background-color: #fff;
  margin-bottom: 20rpx;
}

.plans-container {
  display: flex;
  justify-content: space-between;
  flex-wrap: wrap;
}

.plan-card {
  width: calc(33.333% - 20rpx);
  border-radius: 12rpx;
  background-color: #f9f9f9;
  border: 2rpx solid #f0f0f0;
  overflow: hidden;
  padding-bottom: 20rpx;
  position: relative;
  
  &--selected {
    border-color: #fc466b;
    background-color: #fff;
    box-shadow: 0 4rpx 20rpx rgba(252, 70, 107, 0.1);
  }
  
  &--recommend {
    box-shadow: 0 4rpx 20rpx rgba(252, 70, 107, 0.1);
    transform: scale(1.05);
    z-index: 1;
    background-color: #fff;
  }
  
  &__tag {
    position: absolute;
    top: 10rpx;
    right: 10rpx;
    background: linear-gradient(135deg, #ff6b6b, #fc466b);
    color: #fff;
    font-size: 20rpx;
    padding: 4rpx 12rpx;
    border-radius: 20rpx;
  }
  
  &__header {
    padding: 20rpx;
    background: linear-gradient(135deg, #ff6b6b, #fc466b);
    color: #fff;
    text-align: center;
    margin-bottom: 20rpx;
  }
  
  &__title {
    font-size: 28rpx;
    font-weight: bold;
    margin-bottom: 10rpx;
  }
  
  &__price {
    display: flex;
    align-items: baseline;
    justify-content: center;
  }
  
  &__price-value {
    font-size: 36rpx;
    font-weight: bold;
  }
  
  &__price-unit {
    font-size: 24rpx;
  }
  
  &__original {
    font-size: 22rpx;
    text-decoration: line-through;
    opacity: 0.8;
    margin-top: 6rpx;
  }
  
  &__body {
    padding: 0 20rpx;
  }
  
  &__feature {
    display: flex;
    align-items: center;
    margin-bottom: 12rpx;
    
    image {
      width: 28rpx;
      height: 28rpx;
      margin-right: 10rpx;
    }
    
    text {
      font-size: 24rpx;
      color: #666;
    }
  }
}

.coupon-section {
  padding: 20rpx 30rpx;
  background-color: #fff;
  margin-bottom: 20rpx;
}

.coupon-input {
  display: flex;
  height: 80rpx;
  border: 2rpx solid #eee;
  border-radius: 40rpx;
  overflow: hidden;
  
  &__field {
    flex: 1;
    height: 100%;
    padding: 0 30rpx;
    font-size: 28rpx;
  }
  
  &__btn {
    height: 100%;
    background-color: #f5f5f5;
    color: #666;
    padding: 0 30rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 28rpx;
    
    &--verified {
      background-color: #e8f5e9;
      color: #4caf50;
      
      image {
        width: 28rpx;
        height: 28rpx;
        margin-right: 6rpx;
      }
    }
  }
}

.coupon-message {
  font-size: 24rpx;
  color: #fc466b;
  margin-top: 10rpx;
  padding-left: 20rpx;
}

.compare-section {
  padding: 40rpx 30rpx;
  background-color: #fff;
  margin-bottom: 20rpx;
}

.compare-table {
  border-radius: 12rpx;
  overflow: hidden;
  border: 1rpx solid #eee;
}

.compare-table__header {
  display: flex;
  background-color: #f5f5f5;
}

.compare-table__row {
  display: flex;
  border-top: 1rpx solid #eee;
}

.compare-table__cell {
  flex: 1;
  padding: 20rpx;
  text-align: center;
  font-size: 26rpx;
  color: #333;
  display: flex;
  align-items: center;
  justify-content: center;
  
  &:first-child {
    text-align: left;
    justify-content: flex-start;
  }
}

.compare-table__value {
  &--vip {
    color: #fc466b;
    font-weight: 500;
  }
}

.faq-section {
  padding: 40rpx 30rpx;
  background-color: #fff;
  margin-bottom: 20rpx;
}

.faq-item {
  border-bottom: 1rpx solid #eee;
  
  &:last-child {
    border-bottom: none;
  }
  
  &__header {
    padding: 24rpx 0;
    display: flex;
    justify-content: space-between;
    align-items: center;
    
    image {
      width: 28rpx;
      height: 28rpx;
      transition: all 0.3s;
    }
  }
  
  &__question {
    font-size: 28rpx;
    color: #333;
    flex: 1;
  }
  
  &__body {
    padding-bottom: 24rpx;
  }
  
  &__answer {
    font-size: 26rpx;
    color: #666;
    line-height: 1.5;
  }
}

.purchase-section {
  padding: 30rpx;
  background-color: #fff;
  border-top: 1rpx solid #eee;
}

.purchase-summary {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30rpx;
  
  &__title {
    font-size: 28rpx;
    color: #333;
  }
  
  &__price {
    display: flex;
    align-items: center;
  }
  
  &__original {
    font-size: 24rpx;
    color: #999;
    text-decoration: line-through;
    margin-right: 10rpx;
  }
  
  &__value {
    font-size: 36rpx;
    font-weight: bold;
    color: #fc466b;
  }
}

.purchase-btn {
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
  margin-bottom: 20rpx;
  
  &:disabled {
    opacity: 0.6;
  }
}

.purchase-terms {
  text-align: center;
  font-size: 24rpx;
  color: #999;
  
  &__link {
    color: #fc466b;
  }
}

.purchase-tips {
  text-align: center;
  font-size: 24rpx;
  color: #999;
  margin-top: 20rpx;
}

@media screen and (max-width: 375px) {
  .plan-card {
    width: 100%;
    margin-bottom: 20rpx;
    
    &--recommend {
      transform: none;
    }
  }
}
</style> 