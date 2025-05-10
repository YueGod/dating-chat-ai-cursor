package com.dating.ai.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 聊天请求DTO
 * 用于接收用户的聊天请求
 *
 * @author dating-ai
 */
@Data
@Schema(description = "聊天请求")
public class ChatRequestDTO {

    /**
     * 会话ID
     * 用于标识一个连续的对话
     */
    @Schema(description = "会话ID", example = "conv-123456")
    private String conversationId;

    /**
     * 风格ID
     * 用于指定聊天风格/角色
     */
    @Schema(description = "风格ID", example = "style-romantic")
    private String styleId;

    /**
     * 消息内容
     * 用户发送的消息
     */
    @Schema(description = "消息内容", required = true)
    private String message;

    /**
     * 参考消息ID
     * 可选，用于引用之前的消息
     */
    @Schema(description = "参考消息ID")
    private String referenceMessageId;
}