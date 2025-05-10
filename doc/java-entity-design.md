# Dating AI Java实体类设计

根据我们的MongoDB和Milvus数据库设计，以下是对应的Java实体类设计。这些类将用于与数据库交互并在后端服务中处理数据。

## 1. 用户相关实体类

### 1.1 用户实体类 (User.java)

```java
package com.dating.ai.domain.meta;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "users")
public class User {
    @Id
    private String id;
    
    private String userId;
    private String openId;
    private String unionId;
    private String nickname;
    private String avatar;
    private String gender;
    private Integer age;
    private String province;
    private String city;
    private String occupation;
    private String phone;
    private String email;
    private LocalDateTime registrationDate;
    private LocalDateTime lastLoginDate;
    private String userStatus;
    
    private UserSettings settings;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    @Data
    public static class UserSettings {
        private Boolean notification;
        private String privacy;
        private Integer chatHistoryRetention;
        private String language;
    }
}
```

### 1.2 用户登录历史实体类 (UserLoginHistory.java)

```java
package com.dating.ai.domain.meta;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "user_login_history")
public class UserLoginHistory {
    @Id
    private String id;
    
    private String userId;
    private LocalDateTime loginTime;
    private String ipAddress;
    private String deviceType;
    private String deviceId;
    private String osType;
    private String osVersion;
    private String appVersion;
    private String loginMethod;
    private String loginStatus;
    private String failureReason;
    private LocalDateTime createdAt;
}
```

### 1.3 用户设备实体类 (UserDevice.java)

```java
package com.dating.ai.domain.meta;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "user_devices")
public class UserDevice {
    @Id
    private String id;
    
    private String userId;
    private String deviceIdentifier;
    private String deviceName;
    private String deviceType;
    private String osVersion;
    private String appVersion;
    private String pushToken;
    private Boolean isActive;
    private LocalDateTime lastActiveTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

### 1.4 用户会员实体类 (UserMembership.java)

```java
package com.dating.ai.domain.meta;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Document(collection = "user_memberships")
public class UserMembership {
    @Id
    private String id;
    
    private String userId;
    private String planId;
    private String planName;
    private LocalDateTime startTime;
    private LocalDateTime expireTime;
    private Boolean isActive;
    private Boolean autoRenew;
    private String originalOrderId;
    private LocalDateTime nextBillingDate;
    private BigDecimal nextBillingAmount;
    private String cancelReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

### 1.5 用户使用统计实体类 (UserUsageStat.java)

```java
package com.dating.ai.domain.meta;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Document(collection = "user_usage_stats")
public class UserUsageStat {
    @Id
    private String id;
    
    private String userId;
    private LocalDate date;
    private Integer generationCount;
    private Integer copyCount;
    private Integer likeCount;
    private Integer dislikeCount;
    private Integer dailyLimit;
    private List<StyleUsage> usedStyles;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    @Data
    public static class StyleUsage {
        private String styleId;
        private Integer count;
    }
}
```

### 1.6 用户回复偏好实体类 (UserReplyPreference.java)

```java
package com.dating.ai.domain.meta;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Document(collection = "user_reply_preferences")
public class UserReplyPreference {
    @Id
    private String id;
    
    private String userId;
    private List<String> stylePreferences;
    private String lengthPreference;
    private String emotionExpression;
    private List<SampleRating> sampleRatings;
    private LearningData learningData;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    @Data
    public static class SampleRating {
        private String replyId;
        private Integer rating;
    }
    
    @Data
    public static class LearningData {
        private List<StyleCount> mostCopiedStyles;
        private List<StyleCount> mostLikedStyles;
        private List<ContextSuccess> topSuccessfulContexts;
        
        @Data
        public static class StyleCount {
            private String styleId;
            private Integer count;
        }
        
        @Data
        public static class ContextSuccess {
            private String context;
            private Double successRate;
        }
    }
}
```

## 2. 会话和消息相关实体类

### 2.1 会话实体类 (Conversation.java)

```java
package com.dating.ai.domain.meta;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Document(collection = "conversations")
public class Conversation {
    @Id
    private String id;
    
    private String conversationId;
    private String userId;
    private ContactInfo contactInfo;
    private List<String> tags;
    private Boolean isStarred;
    private LastMessage lastMessage;
    private Integer messageCount;
    private Integer unreadCount;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    
    @Data
    public static class ContactInfo {
        private String name;
        private String avatar;
        private String relationship;
        private String platform;
        private String externalId;
    }
    
    @Data
    public static class LastMessage {
        private String messageId;
        private String content;
        private String type;
        private LocalDateTime timestamp;
    }
}
```

### 2.2 消息实体类 (Message.java)

```java
package com.dating.ai.domain.meta;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Document(collection = "messages")
public class Message {
    @Id
    private String id;
    
    private String messageId;
    private String conversationId;
    private String userId;
    private String type;
    private String content;
    private LocalDateTime timestamp;
    private String status;
    private Map<String, Object> metadata;
    private String originalMessageId;
    private String style;
    private MessageAnalysis analysis;
    private Boolean isStarred;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    
    @Data
    public static class MessageAnalysis {
        private String emotion;
        private String intent;
        private List<String> topics;
        private Integer emotionScore;
    }
}
```

### 2.3 标签实体类 (Tag.java)

```java
package com.dating.ai.domain.meta;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "tags")
public class Tag {
    @Id
    private String id;
    
    private String tagId;
    private String userId;
    private String name;
    private String color;
    private Integer sortOrder;
    private Integer count;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

## 3. AI回复生成相关实体类

### 3.1 生成回复实体类 (GeneratedReply.java)

```java
package com.dating.ai.domain.meta;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Document(collection = "generated_replies")
public class GeneratedReply {
    @Id
    private String id;
    
    private String requestId;
    private String userId;
    private String conversationId;
    private String receivedMessageId;
    private List<Reply> replies;
    private GenerationParams generationParams;
    private MessageAnalysis analysis;
    private Performance performance;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    @Data
    public static class Reply {
        private String replyId;
        private String styleId;
        private String styleName;
        private String content;
        private Map<String, Object> metadata;
        private Quality quality;
        private UserFeedback userFeedback;
        
        @Data
        public static class Quality {
            private Double relevanceScore;
            private Double styleMatchScore;
            private Double totalScore;
        }
        
        @Data
        public static class UserFeedback {
            private Boolean liked;
            private Boolean copied;
            private Boolean used;
            private String feedback;
            private LocalDateTime feedbackTime;
        }
    }
    
    @Data
    public static class GenerationParams {
        private String model;
        private Double temperature;
        private List<String> styles;
        private Integer contextTurns;
    }
    
    @Data
    public static class MessageAnalysis {
        private String messageType;
        private String emotion;
        private String interestLevel;
        private List<String> topics;
        private String suggestedResponseTone;
    }
    
    @Data
    public static class Performance {
        private Integer requestTime;
        private Integer processingTime;
        private Integer totalTime;
    }
}
```

### 3.2 回复风格实体类 (ReplyStyle.java)

```java
package com.dating.ai.domain.meta;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Document(collection = "reply_styles")
public class ReplyStyle {
    @Id
    private String id;
    
    private String styleId;
    private String name;
    private String description;
    private String longDescription;
    private List<String> tags;
    private Boolean isVipOnly;
    private String category;
    private Double popularity;
    private String iconUrl;
    private Integer displayOrder;
    private Map<String, String> parameters;
    private List<String> patternExamples;
    private List<String> avoidPatterns;
    private String promptModifiers;
    private List<StyleExample> examples;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    @Data
    public static class StyleExample {
        private String receivedMessage;
        private String replyExample;
    }
}
```

### 3.3 会话上下文实体类 (ConversationContext.java)

```java
package com.dating.ai.domain.meta;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Document(collection = "conversation_contexts")
public class ConversationContext {
    @Id
    private String id;
    
    private String userId;
    private String conversationId;
    private String currentTopic;
    private String currentStage;
    private String relationshipType;
    private List<RecentMessage> recentMessages;
    private Personas personas;
    private InteractionMetrics interactionMetrics;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    @Data
    public static class RecentMessage {
        private String messageId;
        private String role;
        private String content;
        private LocalDateTime timestamp;
    }
    
    @Data
    public static class Personas {
        private ReceiverPersona receiver;
        
        @Data
        public static class ReceiverPersona {
            private String gender;
            private String estimatedAge;
            private String communicationStyle;
            private String interestShown;
        }
    }
    
    @Data
    public static class InteractionMetrics {
        private Integer totalExchanges;
        private Integer averageResponseTime;
        private Integer topicShiftCount;
        private LocalDateTime conversationStartTime;
        private LocalDateTime lastInteractionTime;
    }
}
```

## 4. 支付相关实体类

### 4.1 订单实体类 (Order.java)

```java
package com.dating.ai.domain.meta;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Document(collection = "orders")
public class Order {
    @Id
    private String id;
    
    private String orderId;
    private String userId;
    private String planId;
    private String planName;
    private BigDecimal amount;
    private BigDecimal discountAmount;
    private BigDecimal finalAmount;
    private String couponCode;
    private String promotionId;
    private String paymentMethod;
    private String paymentTransactionId;
    private String orderStatus;
    private Boolean autoRenew;
    private String source;
    private LocalDateTime createTime;
    private LocalDateTime paymentTime;
    private LocalDateTime expireTime;
    private LocalDateTime refundTime;
    private String refundReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

### 4.2 会员计划实体类 (MembershipPlan.java)

```java
package com.dating.ai.domain.meta;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Document(collection = "membership_plans")
public class MembershipPlan {
    @Id
    private String id;
    
    private String planId;
    private String planName;
    private String planDescription;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private Integer duration;
    private String durationType;
    private String planType;
    private Boolean isRecommended;
    private Boolean isActive;
    private List<String> features;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

### 4.3 促销活动实体类 (Promotion.java)

```java
package com.dating.ai.domain.meta;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Document(collection = "promotions")
public class Promotion {
    @Id
    private String id;
    
    private String promotionId;
    private String title;
    private String description;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String discountType;
    private Double discountValue;
    private List<String> applicablePlans;
    private Integer maxUses;
    private Integer currentUses;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

### 4.4 优惠券实体类 (Coupon.java)

```java
package com.dating.ai.domain.meta;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Document(collection = "coupons")
public class Coupon {
    @Id
    private String id;
    
    private String couponCode;
    private String promotionId;
    private String couponType;
    private Double discountValue;
    private BigDecimal minPurchase;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer maxUses;
    private Integer currentUses;
    private Boolean isActive;
    private List<String> applicablePlans;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

### 4.5 用户优惠券实体类 (UserCoupon.java)

```java
package com.dating.ai.domain.meta;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "user_coupons")
public class UserCoupon {
    @Id
    private String id;
    
    private String userId;
    private String couponCode;
    private Boolean isUsed;
    private LocalDateTime usedTime;
    private String orderId;
    private LocalDateTime expireTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

## 5. DTO对象设计

除了数据库实体类，我们还需要设计用于与前端通信的DTO（数据传输对象）。下面是几个核心DTO的设计：

### 5.1 用户DTO

```java
package com.dating.ai.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserDTO {
    private String userId;
    private String nickname;
    private String avatar;
    private String gender;
    private String phone;
    private MembershipDTO membershipInfo;
    private StatisticsDTO statistics;
    private SettingsDTO settings;
    
    @Data
    public static class MembershipDTO {
        private Integer level;
        private Boolean isActive;
        private LocalDateTime expireDate;
        private Integer daysRemaining;
        private Boolean autoRenew;
        private String[] benefits;
    }
    
    @Data
    public static class StatisticsDTO {
        private Integer totalUsage;
        private String[] favoriteStyles;
        private Integer successRate;
    }
    
    @Data
    public static class SettingsDTO {
        private Boolean notification;
        private String privacy;
        private Integer chatHistoryRetention;
        private String language;
    }
}
```

### 5.2 会话DTO

```java
package com.dating.ai.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ConversationDTO {
    private String conversationId;
    private String contactName;
    private String contactAvatar;
    private LastMessageDTO lastMessage;
    private Integer unreadCount;
    private List<TagDTO> tags;
    private Boolean isStarred;
    private Integer messageCount;
    private LocalDateTime updatedAt;
    
    @Data
    public static class LastMessageDTO {
        private String content;
        private String type;
        private LocalDateTime timestamp;
    }
    
    @Data
    public static class TagDTO {
        private String tagId;
        private String name;
        private String color;
    }
}
```

### 5.3 消息DTO

```java
package com.dating.ai.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
public class MessageDTO {
    private String messageId;
    private String type;
    private String content;
    private LocalDateTime timestamp;
    private String status;
    private Map<String, Object> metadata;
    private String style;
}
```

### 5.4 生成回复DTO

```java
package com.dating.ai.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
public class GenerateRequestDTO {
    private String conversationId;
    private String receivedMessage;
    private List<String> styles;
    private List<PreviousMessageDTO> previousMessages;
    private ContextDTO context;
    
    @Data
    public static class PreviousMessageDTO {
        private String role;
        private String content;
        private LocalDateTime timestamp;
    }
    
    @Data
    public static class ContextDTO {
        private String relationshipStage;
        private String receiverGender;
        private String receiverAgeRange;
    }
}

@Data
public class GenerateResponseDTO {
    private String requestId;
    private List<ReplyDTO> replies;
    private AnalysisDTO analysis;
    private Integer generationTime;
    
    @Data
    public static class ReplyDTO {
        private String replyId;
        private String styleId;
        private String styleName;
        private String content;
        private Map<String, Object> metadata;
        private QualityDTO quality;
        
        @Data
        public static class QualityDTO {
            private Double relevanceScore;
            private Double styleMatchScore;
            private Double totalScore;
        }
    }
    
    @Data
    public static class AnalysisDTO {
        private String messageType;
        private String emotion;
        private String interestLevel;
        private List<String> topics;
        private String suggestedResponseTone;
    }
}
```

### 5.5 会员计划DTO

```java
package com.dating.ai.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class MembershipPlanDTO {
    private String planId;
    private String planName;
    private String planDescription;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private Integer duration;
    private String durationType;
    private String planType;
    private Boolean isRecommended;
    private List<String> features;
}

@Data
public class PlansResponseDTO {
    private CurrentPlanDTO currentPlan;
    private List<MembershipPlanDTO> plans;
    private List<PromotionDTO> promotions;
    
    @Data
    public static class CurrentPlanDTO {
        private Integer level;
        private LocalDateTime expireDate;
        private Boolean autoRenew;
    }
    
    @Data
    public static class PromotionDTO {
        private String promotionId;
        private String title;
        private String description;
        private String discountType;
        private Double discountValue;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
    }
}
```

## 6. VO对象设计

VO（视图对象）用于封装返回给前端的响应数据，遵循统一响应格式：

```java
package com.dating.ai.vo;

import lombok.Data;

@Data
public class ResultVO<T> {
    private String code;
    private String msg;
    private T data;
    
    public static <T> ResultVO<T> success(T data) {
        ResultVO<T> resultVO = new ResultVO<>();
        resultVO.setCode("000000");
        resultVO.setMsg("成功");
        resultVO.setData(data);
        return resultVO;
    }
    
    public static <T> ResultVO<T> error(String code, String msg) {
        ResultVO<T> resultVO = new ResultVO<>();
        resultVO.setCode(code);
        resultVO.setMsg(msg);
        return resultVO;
    }
}
``` 