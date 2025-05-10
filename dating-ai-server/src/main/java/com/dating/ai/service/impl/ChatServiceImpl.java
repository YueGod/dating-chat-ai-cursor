package com.dating.ai.service.impl;

import com.dating.ai.dao.ChatMessageRepository;
import com.dating.ai.dao.ConversationRepository;
import com.dating.ai.domain.ChatMessage;
import com.dating.ai.domain.Conversation;
import com.dating.ai.service.AssistantService;
import com.dating.ai.service.ChatService;
import com.dating.ai.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.UUID;

/**
 * Implementation of the ChatService interface
 *
 * @author dating-ai
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ChatServiceImpl implements ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final ConversationRepository conversationRepository;
    private final AssistantService assistantService;
    private final UserService userService;

    @Override
    @Transactional
    public ChatMessage processUserMessage(String userId, String message) {
        log.info("Processing user message for user: {}", userId);

        // Create user message
        ChatMessage userMessage = new ChatMessage();
        userMessage.setUserId(userId);
        userMessage.setMessageType(ChatMessage.MessageType.USER);
        userMessage.setContent(message);
        userMessage.setCreateTime(new Date());

        // Check if conversation exists, if not create a new one
        String conversationId = getLatestConversationId(userId);
        if (conversationId == null) {
            conversationId = startNewConversation(userId);
        }
        userMessage.setConversationId(conversationId);

        // Save user message
        chatMessageRepository.save(userMessage);

        // TODO: Implement AI response generation using AssistantService
        // For now, create a simple AI response
        ChatMessage aiResponse = new ChatMessage();
        aiResponse.setUserId(userId);
        aiResponse.setMessageType(ChatMessage.MessageType.AI);
        aiResponse.setContent("This is a placeholder AI response to: " + message);
        aiResponse.setConversationId(conversationId);
        aiResponse.setCreateTime(new Date());
        aiResponse.setEmotionScore(70); // Placeholder score

        // Save AI response
        chatMessageRepository.save(aiResponse);

        // Update conversation with last message
        updateConversationLastMessage(conversationId, aiResponse);

        return aiResponse;
    }

    @Override
    public Page<ChatMessage> getChatHistory(String userId, String conversationId, Pageable pageable) {
        if (conversationId != null) {
            return chatMessageRepository.findByUserIdAndConversationIdOrderByCreateTimeDesc(
                    userId, conversationId, pageable);
        } else {
            return chatMessageRepository.findByUserIdOrderByCreateTimeDesc(userId, pageable);
        }
    }

    @Override
    @Transactional
    public void deleteConversation(String userId, String conversationId) {
        // Verify conversation belongs to user
        conversationRepository.findByConversationId(conversationId)
                .filter(conv -> conv.getUserId().equals(userId))
                .ifPresent(conversation -> {
                    // Mark conversation as deleted
                    conversation.setStatus("DELETED");
                    conversation.setDeletedAt(new Date());
                    conversationRepository.save(conversation);

                    // Delete messages
                    chatMessageRepository.deleteByConversationId(conversationId);
                });
    }

    @Override
    @Transactional
    public String startNewConversation(String userId) {
        String conversationId = UUID.randomUUID().toString();

        Conversation conversation = new Conversation();
        conversation.setConversationId(conversationId);
        conversation.setUserId(userId);
        conversation.setStatus("ACTIVE");
        conversation.setMessageCount(0);
        conversation.setUnreadCount(0);
        conversation.setIsStarred(false);
        conversation.setCreateTime(new Date());

        // Setting contact info with placeholder data
        Conversation.ContactInfo contactInfo = new Conversation.ContactInfo();
        contactInfo.setName("New Conversation");
        contactInfo.setRelationship("unknown");
        conversation.setContactInfo(contactInfo);

        conversationRepository.save(conversation);

        return conversationId;
    }

    /**
     * Get the ID of the latest active conversation for a user
     *
     * @param userId User ID
     * @return Conversation ID or null if none exists
     */
    private String getLatestConversationId(String userId) {
        return conversationRepository.findByUserIdAndStatus(userId, "ACTIVE",
                Pageable.ofSize(1))
                .getContent()
                .stream()
                .findFirst()
                .map(Conversation::getConversationId)
                .orElse(null);
    }

    /**
     * Update the last message information in a conversation
     *
     * @param conversationId Conversation ID
     * @param message        Last message
     */
    private void updateConversationLastMessage(String conversationId, ChatMessage message) {
        conversationRepository.findByConversationId(conversationId)
                .ifPresent(conversation -> {
                    Conversation.LastMessage lastMessage = new Conversation.LastMessage();
                    lastMessage.setMessageId(message.getId());
                    lastMessage.setContent(message.getContent());
                    lastMessage.setType(message.getMessageType().toString().toLowerCase());
                    lastMessage.setTimestamp(message.getCreateTime());

                    conversation.setLastMessage(lastMessage);
                    conversation.setMessageCount(conversation.getMessageCount() + 1);
                    conversation.setUpdateTime(new Date());

                    conversationRepository.save(conversation);
                });
    }
}