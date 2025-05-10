package com.dating.ai.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Knife4j configuration for API documentation
 * 
 * @author dating-ai
 */
@Configuration
public class Knife4jConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Dating AI API Documentation")
                        .description("API documentation for Dating AI backend services")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Dating AI Team")
                                .email("contact@dating-ai.com"))
                        .license(new License()
                                .name("Private License")
                                .url("https://dating-ai.com/license")));
    }
}