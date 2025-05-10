package com.dating.ai.dao;

import com.dating.ai.domain.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for User entity
 *
 * @author dating-ai
 */
@Repository
public interface UserRepository extends MongoRepository<User, String> {

    /**
     * Find user by user ID
     *
     * @param userId User ID
     * @return Optional of User
     */
    Optional<User> findByUserId(String userId);

    /**
     * Find user by open ID
     *
     * @param openId Open ID
     * @return Optional of User
     */
    Optional<User> findByOpenId(String openId);

    /**
     * Check if user exists by phone
     *
     * @param phone Phone number
     * @return True if exists
     */
    boolean existsByPhone(String phone);

    /**
     * Check if user exists by email
     *
     * @param email Email address
     * @return True if exists
     */
    boolean existsByEmail(String email);
}