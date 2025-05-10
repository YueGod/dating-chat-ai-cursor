package com.dating.ai.domain;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;

import java.io.Serializable;
import java.util.Date;

/**
 * Base entity class with common fields for all entities
 *
 * @author dating-ai
 */
@Data
public abstract class BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Unique identifier
     */
    @Id
    private String id;

    /**
     * Creation time
     */
    @CreatedDate
    private Date createTime;

    /**
     * Last update time
     */
    @LastModifiedDate
    private Date updateTime;
}