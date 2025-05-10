package com.dating.ai.service.impl;

import com.dating.ai.dao.ReplyStyleRepository;
import com.dating.ai.dao.UserMembershipRepository;
import com.dating.ai.dao.UserRepository;
import com.dating.ai.dao.UserUsageStatRepository;
import com.dating.ai.domain.ReplyStyle;
import com.dating.ai.domain.User;
import com.dating.ai.domain.UserMembership;
import com.dating.ai.domain.UserUsageStat;
import com.dating.ai.dto.UserDTO;
import com.dating.ai.dto.UserLimitsDTO;
import com.dating.ai.dto.UserUsageStatsDTO;
import com.dating.ai.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of the UserService interface
 *
 * @author dating-ai
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMembershipRepository membershipRepository;
    private final UserUsageStatRepository usageStatRepository;
    private final ReplyStyleRepository styleRepository;

    // TODO: Replace with actual user context mechanism
    private static final String CURRENT_USER_ID = "user123456";

    @Override
    public UserDTO getUserProfile() {
        User user = getUserOrThrow();

        UserDTO userDTO = new UserDTO();
        userDTO.setUserId(user.getUserId());
        userDTO.setNickname(user.getNickname());
        userDTO.setAvatar(user.getAvatar());
        userDTO.setGender(user.getGender());
        userDTO.setPhone(maskPhone(user.getPhone()));

        // Set membership info
        UserDTO.MembershipDTO membershipDTO = getMembershipDTO();
        userDTO.setMembershipInfo(membershipDTO);

        // Set statistics
        UserDTO.StatisticsDTO statisticsDTO = getStatisticsDTO();
        userDTO.setStatistics(statisticsDTO);

        // Set settings
        if (user.getSettings() != null) {
            UserDTO.SettingsDTO settingsDTO = new UserDTO.SettingsDTO();
            BeanUtils.copyProperties(user.getSettings(), settingsDTO);
            userDTO.setSettings(settingsDTO);
        }

        return userDTO;
    }

    @Override
    @Transactional
    public UserDTO updateUserProfile(UserDTO userDTO) {
        User user = getUserOrThrow();

        if (userDTO.getNickname() != null) {
            user.setNickname(userDTO.getNickname());
        }

        if (userDTO.getAvatar() != null) {
            user.setAvatar(userDTO.getAvatar());
        }

        if (userDTO.getGender() != null) {
            user.setGender(userDTO.getGender());
        }

        user.setUpdateTime(new Date());
        userRepository.save(user);

        return getUserProfile();
    }

    @Override
    @Transactional
    public UserDTO.SettingsDTO updateUserSettings(UserDTO.SettingsDTO settings) {
        User user = getUserOrThrow();

        if (user.getSettings() == null) {
            user.setSettings(new User.UserSettings());
        }

        if (settings.getNotification() != null) {
            user.getSettings().setNotification(settings.getNotification());
        }

        if (settings.getPrivacy() != null) {
            user.getSettings().setPrivacy(settings.getPrivacy());
        }

        if (settings.getChatHistoryRetention() != null) {
            user.getSettings().setChatHistoryRetention(settings.getChatHistoryRetention());
        }

        if (settings.getLanguage() != null) {
            user.getSettings().setLanguage(settings.getLanguage());
        }

        user.setUpdateTime(new Date());
        userRepository.save(user);

        UserDTO.SettingsDTO result = new UserDTO.SettingsDTO();
        BeanUtils.copyProperties(user.getSettings(), result);
        return result;
    }

    @Override
    public UserUsageStatsDTO getUserUsageStats(String period) {
        LocalDate today = LocalDate.now();
        Date startDate;

        // Determine start date based on period
        switch (period.toLowerCase()) {
            case "week":
                startDate = Date.from(today.minusDays(7).atStartOfDay(ZoneId.systemDefault()).toInstant());
                break;
            case "month":
                startDate = Date.from(today.minusMonths(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
                break;
            case "all":
                startDate = null;
                break;
            case "day":
            default:
                startDate = Date.from(today.atStartOfDay(ZoneId.systemDefault()).toInstant());
                break;
        }

        List<UserUsageStat> stats;
        if (startDate == null) {
            stats = usageStatRepository.findByUserIdOrderByDateDesc(CURRENT_USER_ID);
        } else {
            Date endDate = Date.from(today.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
            stats = usageStatRepository.findByUserIdAndDateBetweenOrderByDateDesc(
                    CURRENT_USER_ID, startDate, endDate);
        }

        return buildUserUsageStatsDTO(stats);
    }

    @Override
    public UserLimitsDTO getUserLimits() {
        UserLimitsDTO limitsDTO = new UserLimitsDTO();

        // Set daily generation limits
        UserLimitsDTO.DailyGenerationDTO dailyGeneration = getDailyGenerationDTO();
        limitsDTO.setDailyGeneration(dailyGeneration);

        // Set accessible styles
        UserLimitsDTO.AccessibleStylesDTO accessibleStyles = getAccessibleStylesDTO();
        limitsDTO.setAccessibleStyles(accessibleStyles);

        // Set conversation retention
        UserLimitsDTO.ConversationRetentionDTO retentionDTO = getConversationRetentionDTO();
        limitsDTO.setConversationRetention(retentionDTO);

        // Set VIP upgrade info
        UserLimitsDTO.VipUpgradeInfoDTO vipUpgradeInfo = getVipUpgradeInfoDTO();
        limitsDTO.setVipUpgradeInfo(vipUpgradeInfo);

        return limitsDTO;
    }

    /**
     * Get user by ID or throw exception
     *
     * @return User
     */
    private User getUserOrThrow() {
        // TODO: Replace with actual user context mechanism
        return userRepository.findByUserId(CURRENT_USER_ID)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    /**
     * Mask phone number for privacy
     *
     * @param phone Phone number
     * @return Masked phone number
     */
    private String maskPhone(String phone) {
        if (phone == null || phone.length() < 7) {
            return phone;
        }
        return phone.substring(0, 3) + "****" + phone.substring(phone.length() - 4);
    }

    /**
     * Get membership DTO
     *
     * @return MembershipDTO
     */
    private UserDTO.MembershipDTO getMembershipDTO() {
        UserDTO.MembershipDTO membershipDTO = new UserDTO.MembershipDTO();

        Optional<UserMembership> membershipOpt = membershipRepository.findByUserIdAndIsActiveTrue(CURRENT_USER_ID);

        if (membershipOpt.isPresent()) {
            UserMembership membership = membershipOpt.get();
            membershipDTO.setLevel(1); // Default to level 1 for now
            membershipDTO.setIsActive(true);
            membershipDTO.setExpireDate(membership.getExpireTime());

            // Calculate days remaining
            long diffInMillies = membership.getExpireTime().getTime() - new Date().getTime();
            int daysRemaining = (int) (diffInMillies / (24 * 60 * 60 * 1000));
            membershipDTO.setDaysRemaining(Math.max(0, daysRemaining));

            membershipDTO.setAutoRenew(membership.getAutoRenew());
            membershipDTO.setBenefits(new String[] {
                    "无限次数",
                    "全部风格",
                    "高级分析"
            });
        } else {
            membershipDTO.setLevel(0);
            membershipDTO.setIsActive(false);
            membershipDTO.setDaysRemaining(0);
            membershipDTO.setAutoRenew(false);
        }

        return membershipDTO;
    }

    /**
     * Get statistics DTO
     *
     * @return StatisticsDTO
     */
    private UserDTO.StatisticsDTO getStatisticsDTO() {
        UserDTO.StatisticsDTO statisticsDTO = new UserDTO.StatisticsDTO();

        List<UserUsageStat> allStats = usageStatRepository.findByUserIdOrderByDateDesc(CURRENT_USER_ID);

        // Calculate total usage
        int totalUsage = allStats.stream()
                .mapToInt(UserUsageStat::getGenerationCount)
                .sum();
        statisticsDTO.setTotalUsage(totalUsage);

        // Get favorite styles
        Map<String, Integer> styleUsageCount = new HashMap<>();
        allStats.forEach(stat -> {
            if (stat.getUsedStyles() != null) {
                stat.getUsedStyles().forEach(styleUsage -> {
                    styleUsageCount.merge(styleUsage.getStyleId(), styleUsage.getCount(), Integer::sum);
                });
            }
        });

        List<Map.Entry<String, Integer>> sortedStyles = styleUsageCount.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .collect(Collectors.toList());

        String[] favoriteStyles = sortedStyles.stream()
                .limit(3)
                .map(entry -> {
                    // Get style name from repository
                    Optional<ReplyStyle> style = styleRepository.findByStyleId(entry.getKey());
                    return style.map(ReplyStyle::getName).orElse(entry.getKey());
                })
                .toArray(String[]::new);

        statisticsDTO.setFavoriteStyles(favoriteStyles);

        // Set success rate (placeholder for now)
        statisticsDTO.setSuccessRate(78);

        return statisticsDTO;
    }

    /**
     * Build UserUsageStatsDTO from UserUsageStat list
     *
     * @param stats List of UserUsageStat
     * @return UserUsageStatsDTO
     */
    private UserUsageStatsDTO buildUserUsageStatsDTO(List<UserUsageStat> stats) {
        UserUsageStatsDTO dto = new UserUsageStatsDTO();

        // Calculate totals
        int totalGeneration = 0;
        int totalCopied = 0;
        int totalLiked = 0;
        int totalDisliked = 0;

        Map<String, Integer> styleUsageCount = new HashMap<>();
        List<UserUsageStatsDTO.PeriodicStatDTO> periodicStats = new ArrayList<>();

        for (UserUsageStat stat : stats) {
            totalGeneration += stat.getGenerationCount() != null ? stat.getGenerationCount() : 0;
            totalCopied += stat.getCopyCount() != null ? stat.getCopyCount() : 0;
            totalLiked += stat.getLikeCount() != null ? stat.getLikeCount() : 0;
            totalDisliked += stat.getDislikeCount() != null ? stat.getDislikeCount() : 0;

            // Collect style usage
            if (stat.getUsedStyles() != null) {
                stat.getUsedStyles().forEach(styleUsage -> {
                    styleUsageCount.merge(styleUsage.getStyleId(), styleUsage.getCount(), Integer::sum);
                });
            }

            // Add to periodic stats
            UserUsageStatsDTO.PeriodicStatDTO periodicStat = new UserUsageStatsDTO.PeriodicStatDTO();
            periodicStat.setDate(stat.getDate().toString());
            periodicStat.setGeneration(stat.getGenerationCount());
            periodicStat.setCopied(stat.getCopyCount());
            periodicStat.setLiked(stat.getLikeCount());
            periodicStats.add(periodicStat);
        }

        dto.setTotalGeneration(totalGeneration);
        dto.setTotalCopied(totalCopied);
        dto.setTotalLiked(totalLiked);
        dto.setTotalDisliked(totalDisliked);
        dto.setPeriodicStats(periodicStats);

        // Set style usage
        List<UserUsageStatsDTO.StyleUsageDTO> styleUsages = new ArrayList<>();
        int finalTotalGeneration = totalGeneration;
        styleUsageCount.forEach((styleId, count) -> {
            UserUsageStatsDTO.StyleUsageDTO styleUsage = new UserUsageStatsDTO.StyleUsageDTO();
            styleUsage.setStyleId(styleId);

            // Get style name
            Optional<ReplyStyle> style = styleRepository.findByStyleId(styleId);
            styleUsage.setName(style.map(ReplyStyle::getName).orElse(styleId));

            styleUsage.setCount(count);

            // Calculate percentage
            double percentage = finalTotalGeneration > 0 ? (count * 100.0) / finalTotalGeneration : 0;
            styleUsage.setPercentage(Math.round(percentage * 10) / 10.0);

            styleUsages.add(styleUsage);
        });

        // Sort by count descending
        styleUsages.sort((a, b) -> b.getCount().compareTo(a.getCount()));
        dto.setUsedStyles(styleUsages);

        // Set daily limit
        UserUsageStatsDTO.DailyLimitDTO dailyLimit = new UserUsageStatsDTO.DailyLimitDTO();

        // Get today's usage if available
        Optional<UserUsageStat> todayStat = stats.stream()
                .filter(stat -> {
                    LocalDate statDate = stat.getDate().toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate();
                    return statDate.equals(LocalDate.now());
                })
                .findFirst();

        int dailyLimitValue = todayStat.map(UserUsageStat::getDailyLimit).orElse(20);
        int used = todayStat.map(UserUsageStat::getGenerationCount).orElse(0);

        dailyLimit.setLimit(dailyLimitValue);
        dailyLimit.setUsed(used);
        dailyLimit.setRemaining(dailyLimitValue - used);

        // Calculate reset time (next day midnight)
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        dailyLimit.setResetTime(tomorrow.atStartOfDay(ZoneId.systemDefault()).toString());

        dto.setDailyLimit(dailyLimit);

        return dto;
    }

    /**
     * Get daily generation DTO
     *
     * @return DailyGenerationDTO
     */
    private UserLimitsDTO.DailyGenerationDTO getDailyGenerationDTO() {
        UserLimitsDTO.DailyGenerationDTO dailyGeneration = new UserLimitsDTO.DailyGenerationDTO();

        // Get today's usage if available
        LocalDate today = LocalDate.now();
        Date startDate = Date.from(today.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(today.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());

        List<UserUsageStat> todayStats = usageStatRepository.findByUserIdAndDateBetweenOrderByDateDesc(
                CURRENT_USER_ID, startDate, endDate);

        Optional<UserUsageStat> todayStat = todayStats.isEmpty() ? Optional.empty() : Optional.of(todayStats.get(0));

        // Determine limit based on membership
        int limit = 20; // Default for free users
        Optional<UserMembership> membership = membershipRepository.findByUserIdAndIsActiveTrue(CURRENT_USER_ID);
        if (membership.isPresent()) {
            limit = 100; // Higher limit for VIP users
        }

        int used = todayStat.map(UserUsageStat::getGenerationCount).orElse(0);

        dailyGeneration.setLimit(limit);
        dailyGeneration.setUsed(used);
        dailyGeneration.setRemaining(limit - used);

        // Set reset time (next day midnight)
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        dailyGeneration.setResetTime(Date.from(tomorrow.atStartOfDay(ZoneId.systemDefault()).toInstant()));

        return dailyGeneration;
    }

    /**
     * Get accessible styles DTO
     *
     * @return AccessibleStylesDTO
     */
    private UserLimitsDTO.AccessibleStylesDTO getAccessibleStylesDTO() {
        UserLimitsDTO.AccessibleStylesDTO accessibleStyles = new UserLimitsDTO.AccessibleStylesDTO();

        // Count total styles
        long totalStyles = styleRepository.count();
        accessibleStyles.setTotal((int) totalStyles);

        // Count VIP-only styles
        List<ReplyStyle> vipStyles = styleRepository.findByIsVipOnly(true, Sort.unsorted());
        int vipStylesCount = vipStyles.size();

        // Determine accessible styles based on membership
        boolean isVip = membershipRepository.findByUserIdAndIsActiveTrue(CURRENT_USER_ID).isPresent();

        if (isVip) {
            accessibleStyles.setAccessible((int) totalStyles);
            accessibleStyles.setVipOnly(0);
        } else {
            accessibleStyles.setAccessible((int) (totalStyles - vipStylesCount));
            accessibleStyles.setVipOnly(vipStylesCount);
        }

        return accessibleStyles;
    }

    /**
     * Get conversation retention DTO
     *
     * @return ConversationRetentionDTO
     */
    private UserLimitsDTO.ConversationRetentionDTO getConversationRetentionDTO() {
        UserLimitsDTO.ConversationRetentionDTO retentionDTO = new UserLimitsDTO.ConversationRetentionDTO();

        // Default retention periods
        retentionDTO.setDays(30);
        retentionDTO.setVipDays(365);

        // Check if settings override default
        User user = getUserOrThrow();
        if (user.getSettings() != null && user.getSettings().getChatHistoryRetention() != null) {
            retentionDTO.setDays(user.getSettings().getChatHistoryRetention());
        }

        return retentionDTO;
    }

    /**
     * Get VIP upgrade info DTO
     *
     * @return VipUpgradeInfoDTO
     */
    private UserLimitsDTO.VipUpgradeInfoDTO getVipUpgradeInfoDTO() {
        UserLimitsDTO.VipUpgradeInfoDTO vipUpgradeInfo = new UserLimitsDTO.VipUpgradeInfoDTO();

        List<String> benefits = Arrays.asList(
                "每日生成无限制",
                "解锁全部风格",
                "365天会话记录保存");
        vipUpgradeInfo.setSampleBenefits(benefits);

        vipUpgradeInfo.setRecommendedPlan("yearly");
        vipUpgradeInfo.setDiscount("限时7折");

        return vipUpgradeInfo;
    }
}