package com.dating.ai.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * 聊天响应DTO
 * 用于返回AI生成的回复
 *
 * @author dating-ai
 */
@Data
@Schema(description = "聊天响应")
public class ChatResponseDTO {

    /**
     * 会话ID
     * 标识该回复所属的会话
     */
    @Schema(description = "会话ID", example = "conv-123456")
    private String conversationId;

    /**
     * 消息ID
     * 唯一标识该回复
     */
    @Schema(description = "消息ID", example = "msg-789012")
    private String messageId;

    /**
     * 消息内容
     * AI生成的回复内容
     */
    @Schema(description = "消息内容")
    private String message;

    /**
     * 时间戳
     * 回复生成的时间
     */
    @Schema(description = "回复生成时间")
    private Date timestamp;

    /**
     * 语音URL
     * 如果生成了语音，则包含语音文件的URL
     */
    @Schema(description = "语音URL")
    private String audioUrl;

    /**
     * 聊天情绪
     * AI对当前对话情绪的评估
     */
    @Schema(description = "聊天情绪评估")
    private String emotion;
}