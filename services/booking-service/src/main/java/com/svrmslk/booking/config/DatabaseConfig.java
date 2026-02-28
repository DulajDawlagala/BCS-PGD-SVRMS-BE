package com.svrmslk.booking.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(basePackages = "com.svrmslk.booking")
@EnableTransactionManagement
public class DatabaseConfig {
}