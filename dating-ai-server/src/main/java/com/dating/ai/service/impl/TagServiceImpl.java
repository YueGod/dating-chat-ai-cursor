package com.dating.ai.service.impl;

import com.dating.ai.dao.TagRepository;
import com.dating.ai.domain.Tag;
import com.dating.ai.service.TagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Implementation of the TagService interface
 *
 * @author dating-ai
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;

    // TODO: Replace with actual user context mechanism
    private static final String CURRENT_USER_ID = "user123456";

    @Override
    public List<Tag> getTags() {
        return tagRepository.findByUserId(CURRENT_USER_ID, Sort.by(Sort.Direction.ASC, "sortOrder"));
    }

    @Override
    public Tag getTagById(String tagId) {
        return tagRepository.findByTagId(tagId)
                .filter(tag -> tag.getUserId().equals(CURRENT_USER_ID))
                .orElseThrow(() -> new RuntimeException("Tag not found: " + tagId));
    }

    @Override
    @Transactional
    public Tag createTag(Tag tag) {
        // Check if tag name already exists for this user
        if (tagRepository.findByUserIdAndName(CURRENT_USER_ID, tag.getName()).isPresent()) {
            throw new RuntimeException("Tag name already exists: " + tag.getName());
        }

        // Set tag ID and user ID
        tag.setTagId(UUID.randomUUID().toString());
        tag.setUserId(CURRENT_USER_ID);

        // Set default values if not provided
        if (tag.getColor() == null) {
            tag.setColor("#4287f5"); // Default blue color
        }

        if (tag.getSortOrder() == null) {
            // Find highest sort order and add 1
            Integer maxSortOrder = tagRepository.findByUserId(CURRENT_USER_ID,
                    Sort.by(Sort.Direction.DESC, "sortOrder"))
                    .stream()
                    .findFirst()
                    .map(Tag::getSortOrder)
                    .orElse(0);
            tag.setSortOrder(maxSortOrder + 1);
        }

        // Initialize count
        tag.setCount(0);

        // Set creation time
        tag.setCreateTime(new Date());
        tag.setUpdateTime(new Date());

        return tagRepository.save(tag);
    }

    @Override
    @Transactional
    public Tag updateTag(String tagId, Tag tagData) {
        Tag existingTag = getTagById(tagId);

        // Update name if provided and not duplicate
        if (tagData.getName() != null && !tagData.getName().equals(existingTag.getName())) {
            tagRepository.findByUserIdAndName(CURRENT_USER_ID, tagData.getName())
                    .ifPresent(tag -> {
                        throw new RuntimeException("Tag name already exists: " + tagData.getName());
                    });
            existingTag.setName(tagData.getName());
        }

        // Update color if provided
        if (tagData.getColor() != null) {
            existingTag.setColor(tagData.getColor());
        }

        // Update sort order if provided
        if (tagData.getSortOrder() != null) {
            existingTag.setSortOrder(tagData.getSortOrder());
        }

        // Update timestamp
        existingTag.setUpdateTime(new Date());

        return tagRepository.save(existingTag);
    }

    @Override
    @Transactional
    public void deleteTag(String tagId) {
        // Verify tag exists and belongs to user
        getTagById(tagId);

        // Delete tag
        tagRepository.deleteByTagIdAndUserId(tagId, CURRENT_USER_ID);
    }
}