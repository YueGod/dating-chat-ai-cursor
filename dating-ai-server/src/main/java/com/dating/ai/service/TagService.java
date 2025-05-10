package com.dating.ai.service;

import com.dating.ai.domain.Tag;

import java.util.List;

/**
 * Service interface for tag operations
 *
 * @author dating-ai
 */
public interface TagService {

    /**
     * Get all tags for the current user
     *
     * @return List of tags
     */
    List<Tag> getTags();

    /**
     * Get tag by ID
     *
     * @param tagId Tag ID
     * @return Tag
     */
    Tag getTagById(String tagId);

    /**
     * Create a new tag
     *
     * @param tag Tag to create
     * @return Created tag
     */
    Tag createTag(Tag tag);

    /**
     * Update an existing tag
     *
     * @param tagId Tag ID
     * @param tag   Tag data to update
     * @return Updated tag
     */
    Tag updateTag(String tagId, Tag tag);

    /**
     * Delete a tag
     *
     * @param tagId Tag ID
     */
    void deleteTag(String tagId);
}