package com.dating.ai.config;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.document.MetadataMode;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.openai.OpenAiEmbeddingOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAI配置类
 * 提供AI模型客户端的配置
 * @author wap
 */
@Configuration
public class OpenAIConfig {

	@Value("${ai.openai.api-key}")
	private String apiKey;

	@Value("${ai.openai.base-url:https://api.openai.com}")
	private String baseUrl;

	@Value("${ai.openai.chat.options.model:gpt-3.5-turbo}")
	private String chatModel;

	@Value("${ai.openai.embedding.options.model:text-embedding-ada-002}")
	private String embeddingModel;

	@Value("${ai.openai.chat.options.temperature:0.7}")
	private Double temperature;

	@Value("${ai.openai.chat.options.max-tokens:800}")
	private Integer maxTokens;

	/**
	 * 创建OpenAI API客户端
	 */
	@Bean
	public OpenAiApi openAiApi() {
		return OpenAiApi.builder()
				.apiKey(apiKey)
				.baseUrl(baseUrl)
				.build();
	}

	/**
	 * 创建聊天模型客户端
	 */
	@Bean
	public ChatModel chatModel() {
		return OpenAiChatModel.builder()
				.openAiApi(openAiApi())
				.defaultOptions(OpenAiChatOptions.builder()
						.model(chatModel)
						.maxTokens(maxTokens)
						.temperature(temperature)
						.build())
				.build();
	}

	/**
	 * 创建嵌入模型客户端
	 */
	@Bean
	public EmbeddingModel embeddingModel() {
		return new OpenAiEmbeddingModel(openAiApi(), MetadataMode.EMBED,
				OpenAiEmbeddingOptions.builder()
						.model(embeddingModel)
						.build());
	}
}