# Dating AI API实现框架

以下是基于我们定义的API接口和数据库结构的后端实现框架，包括各个Controller、Service和DAO层的设计。

## 1. Controller层设计

### 1.1 认证控制器 (AuthController.java)

```java
package com.dating.ai.controller;

import com.dating.ai.dto.LoginRequestDTO;
import com.dating.ai.dto.RefreshTokenRequestDTO;
import com.dating.ai.service.AuthService;
import com.dating.ai.vo.ResultVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@Api(tags = "认证相关接口")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthService authService;
    
    @PostMapping("/login")
    @ApiOperation("微信登录接口")
    public ResultVO<?> login(@RequestBody LoginRequestDTO loginRequest) {
        return ResultVO.success(authService.login(loginRequest));
    }
    
    @PostMapping("/refresh")
    @ApiOperation("刷新Token接口")
    public ResultVO<?> refreshToken(@RequestBody RefreshTokenRequestDTO refreshRequest) {
        return ResultVO.success(authService.refreshToken(refreshRequest.getRefreshToken()));
    }
    
    @PostMapping("/logout")
    @ApiOperation("退出登录接口")
    public ResultVO<?> logout() {
        return ResultVO.success(authService.logout());
    }
}
```

### 1.2 用户控制器 (UserController.java)

```java
package com.dating.ai.controller;

import com.dating.ai.dto.UpdateProfileRequestDTO;
import com.dating.ai.dto.UpdateSettingsRequestDTO;
import com.dating.ai.service.UserService;
import com.dating.ai.vo.ResultVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
@Api(tags = "用户相关接口")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    
    @GetMapping("/profile")
    @ApiOperation("获取用户信息")
    public ResultVO<?> getUserProfile() {
        return ResultVO.success(userService.getUserProfile());
    }
    
    @PutMapping("/profile")
    @ApiOperation("更新用户信息")
    public ResultVO<?> updateUserProfile(@RequestBody UpdateProfileRequestDTO updateRequest) {
        return ResultVO.success(userService.updateUserProfile(updateRequest));
    }
    
    @PutMapping("/settings")
    @ApiOperation("更新用户设置")
    public ResultVO<?> updateUserSettings(@RequestBody UpdateSettingsRequestDTO settingsRequest) {
        return ResultVO.success(userService.updateUserSettings(settingsRequest));
    }
    
    @GetMapping("/usage-stats")
    @ApiOperation("获取用户使用统计")
    public ResultVO<?> getUserUsageStats(@RequestParam(required = false, defaultValue = "day") String period) {
        return ResultVO.success(userService.getUserUsageStats(period));
    }
    
    @GetMapping("/limits")
    @ApiOperation("检查用户限制")
    public ResultVO<?> getUserLimits() {
        return ResultVO.success(userService.getUserLimits());
    }
}
```

### 1.3 会话控制器 (ConversationController.java)

```java
package com.dating.ai.controller;

import com.dating.ai.dto.ConversationRequest;
import com.dating.ai.dto.MessageRequest;
import com.dating.ai.dto.TagOperationRequestDTO;
import com.dating.ai.service.ConversationService;
import com.dating.ai.vo.ResultVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/conversations")
@Api(tags = "会话相关接口")
@RequiredArgsConstructor
public class ConversationController {
    
    private final ConversationService conversationService;
    
    @GetMapping
    @ApiOperation("获取会话列表")
    public ResultVO<?> getConversations(
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer size,
            @RequestParam(required = false) String tag,
            @RequestParam(required = false) String lastUpdated,
            @RequestParam(required = false, defaultValue = "lastMessageTime") String sortBy,
            @RequestParam(required = false, defaultValue = "desc") String sortOrder) {
        return ResultVO.success(conversationService.getConversations(page, size, tag, lastUpdated, sortBy, sortOrder));
    }
    
    @PostMapping
    @ApiOperation("创建新会话")
    public ResultVO<?> createConversation(@RequestBody ConversationRequest request) {
        return ResultVO.success(conversationService.createConversation(request));
    }
    
    @GetMapping("/{conversationId}/messages")
    @ApiOperation("获取会话消息")
    public ResultVO<?> getMessages(
            @PathVariable String conversationId,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "30") Integer size,
            @RequestParam(required = false) String before,
            @RequestParam(required = false) String after) {
        return ResultVO.success(conversationService.getMessages(conversationId, page, size, before, after));
    }
    
    @PostMapping("/{conversationId}/messages")
    @ApiOperation("添加消息")
    public ResultVO<?> addMessage(
            @PathVariable String conversationId,
            @RequestBody MessageRequest request) {
        return ResultVO.success(conversationService.addMessage(conversationId, request));
    }
    
    @PostMapping("/{conversationId}/tags")
    @ApiOperation("管理会话标签")
    public ResultVO<?> manageTags(
            @PathVariable String conversationId,
            @RequestBody TagOperationRequestDTO request) {
        return ResultVO.success(conversationService.manageTags(conversationId, request));
    }
    
    @DeleteMapping("/{conversationId}")
    @ApiOperation("删除会话")
    public ResultVO<?> deleteConversation(@PathVariable String conversationId) {
        return ResultVO.success(conversationService.deleteConversation(conversationId));
    }
}
```

### 1.4 AI助手控制器 (AssistantController.java)

```java
package com.dating.ai.controller;

import com.dating.ai.dto.FeedbackRequestDTO;
import com.dating.ai.dto.GenerateRequestDTO;
import com.dating.ai.service.AssistantService;
import com.dating.ai.vo.ResultVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/assistant")
@Api(tags = "AI助手相关接口")
@RequiredArgsConstructor
public class AssistantController {
    
    private final AssistantService assistantService;
    
    @PostMapping("/generate")
    @ApiOperation("生成回复")
    public ResultVO<?> generateReplies(@RequestBody GenerateRequestDTO request) {
        return ResultVO.success(assistantService.generateReplies(request));
    }
    
    @PostMapping("/feedback")
    @ApiOperation("回复反馈")
    public ResultVO<?> provideFeedback(@RequestBody FeedbackRequestDTO request) {
        return ResultVO.success(assistantService.provideFeedback(request));
    }
}
```

### 1.5 风格控制器 (StyleController.java)

```java
package com.dating.ai.controller;

import com.dating.ai.service.StyleService;
import com.dating.ai.vo.ResultVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/styles")
@Api(tags = "回复风格相关接口")
@RequiredArgsConstructor
public class StyleController {
    
    private final StyleService styleService;
    
    @GetMapping
    @ApiOperation("获取回复风格列表")
    public ResultVO<?> getStyles(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Boolean isVipOnly) {
        return ResultVO.success(styleService.getStyles(category, isVipOnly));
    }
}
```

### 1.6 会员与支付控制器 (MembershipController.java)

```java
package com.dating.ai.controller;

import com.dating.ai.dto.CancelRenewalRequestDTO;
import com.dating.ai.dto.OrderRequestDTO;
import com.dating.ai.service.MembershipService;
import com.dating.ai.service.OrderService;
import com.dating.ai.vo.ResultVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@Api(tags = "会员与支付相关接口")
@RequiredArgsConstructor
public class MembershipController {
    
    private final MembershipService membershipService;
    private final OrderService orderService;
    
    @GetMapping("/membership/plans")
    @ApiOperation("获取会员方案")
    public ResultVO<?> getMembershipPlans(@RequestParam(required = false) String platform) {
        return ResultVO.success(membershipService.getMembershipPlans(platform));
    }
    
    @PostMapping("/orders")
    @ApiOperation("创建订单")
    public ResultVO<?> createOrder(@RequestBody OrderRequestDTO request) {
        return ResultVO.success(orderService.createOrder(request));
    }
    
    @GetMapping("/orders/{orderId}")
    @ApiOperation("查询订单状态")
    public ResultVO<?> getOrderStatus(@PathVariable String orderId) {
        return ResultVO.success(orderService.getOrderStatus(orderId));
    }
    
    @PostMapping("/membership/cancel-renewal")
    @ApiOperation("取消自动续费")
    public ResultVO<?> cancelRenewal(@RequestBody CancelRenewalRequestDTO request) {
        return ResultVO.success(membershipService.cancelRenewal(request.getCancelReason()));
    }
}
```

### 1.7 标签控制器 (TagController.java)

```java
package com.dating.ai.controller;

import com.dating.ai.service.TagService;
import com.dating.ai.vo.ResultVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/tags")
@Api(tags = "标签相关接口")
@RequiredArgsConstructor
public class TagController {
    
    private final TagService tagService;
    
    @GetMapping
    @ApiOperation("获取用户标签列表")
    public ResultVO<?> getTags() {
        return ResultVO.success(tagService.getUserTags());
    }
}
```

## 2. Service层设计

### 2.1 认证服务 (AuthService.java)

```java
package com.dating.ai.service;

import com.dating.ai.dto.LoginRequestDTO;
import com.dating.ai.dto.LoginResponseDTO;

public interface AuthService {
    
    /**
     * 用户登录
     * @param loginRequest 登录请求
     * @return 登录响应信息
     */
    LoginResponseDTO login(LoginRequestDTO loginRequest);
    
    /**
     * 刷新令牌
     * @param refreshToken 刷新令牌
     * @return 刷新后的令牌信息
     */
    LoginResponseDTO refreshToken(String refreshToken);
    
    /**
     * 退出登录
     * @return 退出登录结果
     */
    Object logout();
}
```

```java
package com.dating.ai.service.impl;

import com.dating.ai.dto.LoginRequestDTO;
import com.dating.ai.dto.LoginResponseDTO;
import com.dating.ai.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    
    // 实现代码...
}
```

### 2.2 用户服务 (UserService.java)

```java
package com.dating.ai.service;

import com.dating.ai.dto.UpdateProfileRequestDTO;
import com.dating.ai.dto.UpdateSettingsRequestDTO;
import com.dating.ai.dto.UserDTO;
import com.dating.ai.dto.UserLimitsDTO;
import com.dating.ai.dto.UserUsageStatsDTO;

public interface UserService {
    
    /**
     * 获取用户信息
     * @return 用户信息
     */
    UserDTO getUserProfile();
    
    /**
     * 更新用户信息
     * @param updateRequest 更新请求
     * @return 更新后的用户信息
     */
    UserDTO updateUserProfile(UpdateProfileRequestDTO updateRequest);
    
    /**
     * 更新用户设置
     * @param settingsRequest 设置请求
     * @return 更新后的设置信息
     */
    Object updateUserSettings(UpdateSettingsRequestDTO settingsRequest);
    
    /**
     * 获取用户使用统计
     * @param period 统计周期
     * @return 使用统计信息
     */
    UserUsageStatsDTO getUserUsageStats(String period);
    
    /**
     * 检查用户限制
     * @return 用户限制信息
     */
    UserLimitsDTO getUserLimits();
}
```

```java
package com.dating.ai.service.impl;

import com.dating.ai.dao.UserDao;
import com.dating.ai.dto.UpdateProfileRequestDTO;
import com.dating.ai.dto.UpdateSettingsRequestDTO;
import com.dating.ai.dto.UserDTO;
import com.dating.ai.dto.UserLimitsDTO;
import com.dating.ai.dto.UserUsageStatsDTO;
import com.dating.ai.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    
    private final UserDao userDao;
    
    // 实现代码...
}
```

### 2.3 会话服务 (ConversationService.java)

```java
package com.dating.ai.service;

import com.dating.ai.dto.ConversationDTO;
import com.dating.ai.dto.ConversationRequest;
import com.dating.ai.dto.MessageDTO;
import com.dating.ai.dto.MessageRequest;
import com.dating.ai.dto.TagOperationRequestDTO;

import java.util.List;
import java.util.Map;

public interface ConversationService {
    
    /**
     * 获取会话列表
     * @param page 页码
     * @param size 每页大小
     * @param tag 标签
     * @param lastUpdated 最后更新时间
     * @param sortBy 排序字段
     * @param sortOrder 排序方向
     * @return 会话列表和分页信息
     */
    Map<String, Object> getConversations(Integer page, Integer size, String tag, 
                                         String lastUpdated, String sortBy, String sortOrder);
    
    /**
     * 创建新会话
     * @param request 创建请求
     * @return 创建的会话信息
     */
    ConversationDTO createConversation(ConversationRequest request);
    
    /**
     * 获取会话消息
     * @param conversationId 会话ID
     * @param page 页码
     * @param size 每页大小
     * @param before 指定消息ID之前的消息
     * @param after 指定消息ID之后的消息
     * @return 消息列表和会话信息
     */
    Map<String, Object> getMessages(String conversationId, Integer page, 
                                   Integer size, String before, String after);
    
    /**
     * 添加消息
     * @param conversationId 会话ID
     * @param request 消息请求
     * @return 添加的消息信息
     */
    Map<String, Object> addMessage(String conversationId, MessageRequest request);
    
    /**
     * 管理会话标签
     * @param conversationId 会话ID
     * @param request 标签操作请求
     * @return 更新后的标签信息
     */
    Map<String, Object> manageTags(String conversationId, TagOperationRequestDTO request);
    
    /**
     * 删除会话
     * @param conversationId 会话ID
     * @return 删除结果
     */
    Map<String, Object> deleteConversation(String conversationId);
}
```

```java
package com.dating.ai.service.impl;

import com.dating.ai.dao.ConversationDao;
import com.dating.ai.dao.MessageDao;
import com.dating.ai.dto.ConversationDTO;
import com.dating.ai.dto.ConversationRequest;
import com.dating.ai.dto.MessageDTO;
import com.dating.ai.dto.MessageRequest;
import com.dating.ai.dto.TagOperationRequestDTO;
import com.dating.ai.service.ConversationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ConversationServiceImpl implements ConversationService {
    
    private final ConversationDao conversationDao;
    private final MessageDao messageDao;
    
    // 实现代码...
}
```

### 2.4 AI助手服务 (AssistantService.java)

```java
package com.dating.ai.service;

import com.dating.ai.dto.FeedbackRequestDTO;
import com.dating.ai.dto.GenerateRequestDTO;
import com.dating.ai.dto.GenerateResponseDTO;

import java.util.Map;

public interface AssistantService {
    
    /**
     * 生成回复
     * @param request 生成请求
     * @return 生成的回复列表
     */
    GenerateResponseDTO generateReplies(GenerateRequestDTO request);
    
    /**
     * 提供回复反馈
     * @param request 反馈请求
     * @return 反馈结果
     */
    Map<String, Object> provideFeedback(FeedbackRequestDTO request);
}
```

```java
package com.dating.ai.service.impl;

import com.dating.ai.dao.GeneratedReplyDao;
import com.dating.ai.dto.FeedbackRequestDTO;
import com.dating.ai.dto.GenerateRequestDTO;
import com.dating.ai.dto.GenerateResponseDTO;
import com.dating.ai.service.AIClientService;
import com.dating.ai.service.AssistantService;
import com.dating.ai.service.ConversationService;
import com.dating.ai.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AssistantServiceImpl implements AssistantService {
    
    private final AIClientService aiClientService;
    private final UserService userService;
    private final ConversationService conversationService;
    private final GeneratedReplyDao generatedReplyDao;
    
    // 实现代码...
}
```

### 2.5 风格服务 (StyleService.java)

```java
package com.dating.ai.service;

import java.util.Map;

public interface StyleService {
    
    /**
     * 获取回复风格列表
     * @param category 分类
     * @param isVipOnly 是否仅VIP
     * @return 风格列表
     */
    Map<String, Object> getStyles(String category, Boolean isVipOnly);
}
```

```java
package com.dating.ai.service.impl;

import com.dating.ai.dao.ReplyStyleDao;
import com.dating.ai.service.StyleService;
import com.dating.ai.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class StyleServiceImpl implements StyleService {
    
    private final ReplyStyleDao replyStyleDao;
    private final UserService userService;
    
    // 实现代码...
}
```

### 2.6 会员服务 (MembershipService.java)

```java
package com.dating.ai.service;

import java.util.Map;

public interface MembershipService {
    
    /**
     * 获取会员方案
     * @param platform 平台
     * @return 会员方案列表
     */
    Map<String, Object> getMembershipPlans(String platform);
    
    /**
     * 取消自动续费
     * @param cancelReason 取消原因
     * @return 取消结果
     */
    Map<String, Object> cancelRenewal(String cancelReason);
}
```

```java
package com.dating.ai.service.impl;

import com.dating.ai.dao.MembershipPlanDao;
import com.dating.ai.dao.PromotionDao;
import com.dating.ai.dao.UserMembershipDao;
import com.dating.ai.service.MembershipService;
import com.dating.ai.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class MembershipServiceImpl implements MembershipService {
    
    private final MembershipPlanDao membershipPlanDao;
    private final PromotionDao promotionDao;
    private final UserMembershipDao userMembershipDao;
    private final UserService userService;
    
    // 实现代码...
}
```

### 2.7 订单服务 (OrderService.java)

```java
package com.dating.ai.service;

import com.dating.ai.dto.OrderRequestDTO;

import java.util.Map;

public interface OrderService {
    
    /**
     * 创建订单
     * @param request 订单请求
     * @return 创建的订单信息
     */
    Map<String, Object> createOrder(OrderRequestDTO request);
    
    /**
     * 获取订单状态
     * @param orderId 订单ID
     * @return 订单状态信息
     */
    Map<String, Object> getOrderStatus(String orderId);
}
```

```java
package com.dating.ai.service.impl;

import com.dating.ai.dao.CouponDao;
import com.dating.ai.dao.OrderDao;
import com.dating.ai.dao.PromotionDao;
import com.dating.ai.dto.OrderRequestDTO;
import com.dating.ai.service.MembershipService;
import com.dating.ai.service.OrderService;
import com.dating.ai.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    
    private final OrderDao orderDao;
    private final CouponDao couponDao;
    private final PromotionDao promotionDao;
    private final PaymentService paymentService;
    private final MembershipService membershipService;
    
    // 实现代码...
}
```

### 2.8 标签服务 (TagService.java)

```java
package com.dating.ai.service;

import java.util.Map;

public interface TagService {
    
    /**
     * 获取用户标签列表
     * @return 标签列表
     */
    Map<String, Object> getUserTags();
}
```

```java
package com.dating.ai.service.impl;

import com.dating.ai.dao.TagDao;
import com.dating.ai.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {
    
    private final TagDao tagDao;
    
    // 实现代码...
}
```

## 3. DAO层设计

我们使用MongoDB Repository接口来实现DAO层，以下是主要DAO接口的设计：

### 3.1 用户DAO (UserDao.java)

```java
package com.dating.ai.dao;

import com.dating.ai.domain.meta.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserDao extends MongoRepository<User, String> {
    
    Optional<User> findByUserId(String userId);
    
    Optional<User> findByOpenId(String openId);
    
    boolean existsByPhone(String phone);
    
    boolean existsByEmail(String email);
}
```

### 3.2 用户会员DAO (UserMembershipDao.java)

```java
package com.dating.ai.dao;

import com.dating.ai.domain.meta.UserMembership;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserMembershipDao extends MongoRepository<UserMembership, String> {
    
    Optional<UserMembership> findByUserIdAndIsActiveTrue(String userId);
}
```

### 3.3 会话DAO (ConversationDao.java)

```java
package com.dating.ai.dao;

import com.dating.ai.domain.meta.Conversation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ConversationDao extends MongoRepository<Conversation, String> {
    
    Optional<Conversation> findByConversationId(String conversationId);
    
    Page<Conversation> findByUserIdAndStatusAndUpdatedAtGreaterThan(
            String userId, String status, LocalDateTime lastUpdated, Pageable pageable);
    
    Page<Conversation> findByUserIdAndStatusAndTagsContaining(
            String userId, String status, String tag, Pageable pageable);
    
    long countByUserIdAndStatus(String userId, String status);
    
    long countByUserIdAndStatusAndIsStarredTrue(String userId, String status);
}
```

### 3.4 消息DAO (MessageDao.java)

```java
package com.dating.ai.dao;

import com.dating.ai.domain.meta.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MessageDao extends MongoRepository<Message, String> {
    
    Optional<Message> findByMessageId(String messageId);
    
    Page<Message> findByConversationIdAndStatusNot(
            String conversationId, String status, Pageable pageable);
    
    List<Message> findByConversationIdAndTimestampBeforeAndStatusNot(
            String conversationId, LocalDateTime timestamp, String status, Pageable pageable);
    
    List<Message> findByConversationIdAndTimestampAfterAndStatusNot(
            String conversationId, LocalDateTime timestamp, String status, Pageable pageable);
    
    long countByConversationIdAndStatusNot(String conversationId, String status);
}
```

### 3.5 生成回复DAO (GeneratedReplyDao.java)

```java
package com.dating.ai.dao;

import com.dating.ai.domain.meta.GeneratedReply;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GeneratedReplyDao extends MongoRepository<GeneratedReply, String> {
    
    Optional<GeneratedReply> findByRequestId(String requestId);
    
    Page<GeneratedReply> findByUserIdOrderByCreatedAtDesc(String userId, Pageable pageable);
    
    Optional<GeneratedReply> findByConversationIdAndReceivedMessageId(
            String conversationId, String receivedMessageId);
}
```

### 3.6 回复风格DAO (ReplyStyleDao.java)

```java
package com.dating.ai.dao;

import com.dating.ai.domain.meta.ReplyStyle;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReplyStyleDao extends MongoRepository<ReplyStyle, String> {
    
    Optional<ReplyStyle> findByStyleId(String styleId);
    
    List<ReplyStyle> findByCategory(String category, Sort sort);
    
    List<ReplyStyle> findByIsVipOnly(Boolean isVipOnly, Sort sort);
    
    List<ReplyStyle> findByCategoryAndIsVipOnly(String category, Boolean isVipOnly, Sort sort);
}
```

### 3.7 标签DAO (TagDao.java)

```java
package com.dating.ai.dao;

import com.dating.ai.domain.meta.Tag;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagDao extends MongoRepository<Tag, String> {
    
    Optional<Tag> findByTagId(String tagId);
    
    List<Tag> findByUserId(String userId, Sort sort);
    
    Optional<Tag> findByUserIdAndName(String userId, String name);
}
```

### 3.8 订单DAO (OrderDao.java)

```java
package com.dating.ai.dao;

import com.dating.ai.domain.meta.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderDao extends MongoRepository<Order, String> {
    
    Optional<Order> findByOrderId(String orderId);
    
    Page<Order> findByUserIdOrderByCreateTimeDesc(String userId, Pageable pageable);
    
    Optional<Order> findByPaymentTransactionId(String paymentTransactionId);
}
```

## 4. 配置和工具类设计

### 4.1 全局异常处理器 (GlobalExceptionHandler.java)

```java
package com.dating.ai.config;

import com.dating.ai.exception.BusinessException;
import com.dating.ai.vo.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    
    @ExceptionHandler(BusinessException.class)
    public ResultVO<?> handleBusinessException(BusinessException e) {
        log.error("业务异常", e);
        return ResultVO.error(e.getErrorCode(), e.getErrorMessage());
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResultVO<?> handleValidationException(MethodArgumentNotValidException e) {
        log.error("参数校验异常", e);
        String errorMessage = e.getBindingResult().getFieldError().getDefaultMessage();
        return ResultVO.error("000400", errorMessage);
    }
    
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResultVO<?> handleBindException(BindException e) {
        log.error("参数绑定异常", e);
        String errorMessage = e.getBindingResult().getFieldError().getDefaultMessage();
        return ResultVO.error("000400", errorMessage);
    }
    
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResultVO<?> handleException(Exception e) {
        log.error("系统异常", e);
        return ResultVO.error("000500", "服务端异常，请稍候重试");
    }
}
```

### 4.2 拦截器配置 (WebMvcConfig.java)

```java
package com.dating.ai.config;

import com.dating.ai.interceptor.AuthInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {
    
    private final AuthInterceptor authInterceptor;
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/api/v1/**")
                .excludePathPatterns("/api/v1/auth/login", "/api/v1/auth/refresh");
    }
}
```

### 4.3 认证拦截器 (AuthInterceptor.java)

```java
package com.dating.ai.interceptor;

import com.dating.ai.exception.BusinessException;
import com.dating.ai.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {
    
    private final JwtUtils jwtUtils;
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String token = getTokenFromRequest(request);
        
        if (!StringUtils.hasText(token) || !jwtUtils.validateToken(token)) {
            throw new BusinessException("000401", "未授权或令牌已过期");
        }
        
        // 设置用户上下文
        String userId = jwtUtils.getUserIdFromToken(token);
        UserContext.setUserId(userId);
        
        return true;
    }
    
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 清除用户上下文
        UserContext.clear();
    }
    
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
```

### 4.4 用户上下文 (UserContext.java)

```java
package com.dating.ai.utils;

public class UserContext {
    
    private static final ThreadLocal<String> USER_ID = new ThreadLocal<>();
    
    public static void setUserId(String userId) {
        USER_ID.set(userId);
    }
    
    public static String getUserId() {
        return USER_ID.get();
    }
    
    public static void clear() {
        USER_ID.remove();
    }
}
```

### 4.5 JWT工具类 (JwtUtils.java)

```java
package com.dating.ai.utils;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class JwtUtils {
    
    @Value("${jwt.secret}")
    private String jwtSecret;
    
    @Value("${jwt.expiration}")
    private int jwtExpiration;
    
    @Value("${jwt.refresh-expiration}")
    private int refreshExpiration;
    
    public String generateToken(String userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpiration * 1000))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }
    
    public String generateRefreshToken(String userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("isRefreshToken", true);
        
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + refreshExpiration * 1000))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }
    
    public String getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();
        
        return claims.get("userId", String.class);
    }
    
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
            return true;
        } catch (SignatureException e) {
            log.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        }
        
        return false;
    }
}
```

### 4.6 异常类 (BusinessException.java)

```java
package com.dating.ai.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    
    private final String errorCode;
    private final String errorMessage;
    
    public BusinessException(String errorCode, String errorMessage) {
        super(errorMessage);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
}
```

## 5. Swagger配置

```java
package com.dating.ai.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

import java.util.Collections;
import java.util.List;

@Configuration
@EnableSwagger2WebMvc
public class SwaggerConfig {
    
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.dating.ai.controller"))
                .paths(PathSelectors.any())
                .build()
                .securitySchemes(Collections.singletonList(apiKey()))
                .securityContexts(Collections.singletonList(securityContext()));
    }
    
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Dating AI API文档")
                .description("Dating AI 应用程序接口文档")
                .version("1.0.0")
                .build();
    }
    
    private ApiKey apiKey() {
        return new ApiKey("Bearer", "Authorization", "header");
    }
    
    private SecurityContext securityContext() {
        return SecurityContext.builder()
                .securityReferences(defaultAuth())
                .forPaths(PathSelectors.any())
                .build();
    }
    
    private List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return Collections.singletonList(new SecurityReference("Bearer", authorizationScopes));
    }
}
```

## 6. 主要配置文件 (application.yml)

```yaml
server:
  port: 8080
  servlet:
    context-path: /

spring:
  application:
    name: dating-ai
  data:
    mongodb:
      uri: mongodb://localhost:27017/dating_ai
  jackson:
    date-format: yyyy-MM-dd HH:mm:ss
    time-zone: GMT+8
    default-property-inclusion: non_null

jwt:
  secret: your-jwt-secret-key-should-be-very-long-and-secure
  expiration: 3600  # 1小时
  refresh-expiration: 2592000  # 30天

ai:
  service:
    url: https://api.openai.com/v1
    key: your-openai-api-key
    model: gpt-3.5-turbo
    timeout: 30000  # 30秒

qdrant:
  host: localhost
  port: 6333
  collections:
    style-embeddings:
      dimension: 1536
      distance: Cosine
    conversation-embeddings:
      dimension: 1536
      distance: Cosine
    reply-templates:
      dimension: 1536
      distance: Cosine
    user-preference-embeddings:
      dimension: 1536
      distance: Cosine

logging:
  level:
    com.dating.ai: INFO
    org.springframework.data.mongodb: INFO
  file:
    name: logs/dating-ai.log
    max-size: 10MB
    max-history: 7
``` 