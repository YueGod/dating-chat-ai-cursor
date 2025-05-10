package com.dating.ai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * Dating AI Application Main Entry
 *
 * @author dating-ai
 */
@SpringBootApplication
@EnableMongoRepositories
public class DatingAiApplication {

    public static void main(String[] args) {
        SpringApplication.run(DatingAiApplication.class, args);
    }
}