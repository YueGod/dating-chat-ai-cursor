package com.dating.ai.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Tag entity for conversation organization
 *
 * @author dating-ai
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Document(collection = "tags")
public class Tag extends BaseEntity {

    private String tagId;
    private String userId;
    private String name;
    private String color;
    private Integer sortOrder;
    private Integer count;
}