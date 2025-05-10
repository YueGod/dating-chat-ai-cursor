package com.dating.ai.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * User entity
 *
 * @author dating-ai
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Document(collection = "users")
public class User extends BaseEntity {

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
    private Date registrationDate;
    private Date lastLoginDate;
    private String userStatus;

    private UserSettings settings;

    /**
     * User settings
     */
    @Data
    public static class UserSettings {
        private Boolean notification;
        private String privacy;
        private Integer chatHistoryRetention;
        private String language;
    }
}