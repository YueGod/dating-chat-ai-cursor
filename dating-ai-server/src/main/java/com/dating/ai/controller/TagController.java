package com.dating.ai.controller;

import com.dating.ai.dto.CommonResponse;
import com.dating.ai.service.TagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for tag-related endpoints
 *
 * @author dating-ai
 */
@RestController
@RequestMapping("/tags")
@RequiredArgsConstructor
@io.swagger.v3.oas.annotations.tags.Tag(name = "Tags API", description = "Conversation tagging endpoints")
public class TagController {

    private final TagService tagService;

    @GetMapping
    @Operation(summary = "Get tags", description = "Get all tags for the current user")
    public CommonResponse<?> getTags() {
        return CommonResponse.success(tagService.getTags());
    }

    @GetMapping("/{tagId}")
    @Operation(summary = "Get tag by ID", description = "Get tag by ID")
    public CommonResponse<?> getTagById(
            @Parameter(description = "Tag ID") @PathVariable String tagId) {
        return CommonResponse.success(tagService.getTagById(tagId));
    }

    @PostMapping
    @Operation(summary = "Create tag", description = "Create a new tag")
    public CommonResponse<?> createTag(@RequestBody com.dating.ai.domain.Tag tag) {
        return CommonResponse.success(tagService.createTag(tag));
    }

    @PutMapping("/{tagId}")
    @Operation(summary = "Update tag", description = "Update an existing tag")
    public CommonResponse<?> updateTag(
            @Parameter(description = "Tag ID") @PathVariable String tagId,
            @RequestBody com.dating.ai.domain.Tag tag) {
        return CommonResponse.success(tagService.updateTag(tagId, tag));
    }

    @DeleteMapping("/{tagId}")
    @Operation(summary = "Delete tag", description = "Delete a tag")
    public CommonResponse<?> deleteTag(
            @Parameter(description = "Tag ID") @PathVariable String tagId) {
        tagService.deleteTag(tagId);
        return CommonResponse.success(null);
    }
}