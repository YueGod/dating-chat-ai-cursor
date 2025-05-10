package com.dating.ai.dao;

import com.dating.ai.domain.UserUsageStat;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Repository for UserUsageStat entity
 *
 * @author dating-ai
 */
@Repository
public interface UserUsageStatRepository extends MongoRepository<UserUsageStat, String> {

    /**
     * Find usage stats by user ID and date
     *
     * @param userId User ID
     * @param date   Date
     * @return Optional of UserUsageStat
     */
    Optional<UserUsageStat> findByUserIdAndDate(String userId, Date date);

    /**
     * Find usage stats by user ID sorted by date
     *
     * @param userId User ID
     * @return List of UserUsageStat
     */
    List<UserUsageStat> findByUserIdOrderByDateDesc(String userId);

    /**
     * Find usage stats by user ID within date range
     *
     * @param userId    User ID
     * @param startDate Start date
     * @param endDate   End date
     * @return List of UserUsageStat
     */
    List<UserUsageStat> findByUserIdAndDateBetweenOrderByDateDesc(String userId, Date startDate, Date endDate);

    /**
     * Find recent usage stats by user ID
     *
     * @param userId User ID
     * @param limit  Limit
     * @return List of UserUsageStat
     */
    List<UserUsageStat> findByUserIdOrderByDateDesc(String userId, PageRequest limit);
}