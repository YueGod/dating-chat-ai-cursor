package com.dating.ai.controller;

import com.dating.ai.dto.CommonResponse;
import com.dating.ai.service.StyleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for reply style-related endpoints
 *
 * @author dating-ai
 */
@RestController
@RequestMapping("/styles")
@RequiredArgsConstructor
@Tag(name = "Styles API", description = "Endpoints for reply styles")
public class StyleController {

    private final StyleService styleService;

    @GetMapping
    @Operation(summary = "Get reply styles", description = "Retrieve available reply styles")
    public CommonResponse<?> getStyles(
            @Parameter(description = "Category filter") @RequestParam(required = false) String category,
            @Parameter(description = "VIP-only filter") @RequestParam(required = false) Boolean isVipOnly) {
        return CommonResponse.success(styleService.getStyles(category, isVipOnly));
    }

    @GetMapping("/{styleId}")
    @Operation(summary = "Get style details", description = "Retrieve details for a specific style")
    public CommonResponse<?> getStyleById(
            @Parameter(description = "Style ID") @PathVariable String styleId) {
        return CommonResponse.success(styleService.getStyleById(styleId));
    }

    @GetMapping("/categories")
    @Operation(summary = "Get style categories", description = "Retrieve available style categories")
    public CommonResponse<?> getCategories() {
        return CommonResponse.success(styleService.getCategories());
    }
}