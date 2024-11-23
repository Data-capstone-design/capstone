package com.technote.storage.mongo.core.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages = "com.technote.storage.mongo.core")
public class MongoConfig {
}
