//// ==========================================
//// FILE: config/KafkaConfig.java
//// ==========================================
//package com.svrmslk.tenant.config;
//
//import org.apache.kafka.clients.admin.NewTopic;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.kafka.config.TopicBuilder;
//
//@Configuration
//public class KafkaConfig {
//
//    @Bean
//    public NewTopic tenantEventsTopic() {
//        return TopicBuilder.name("tenant-events")
//                .partitions(3)
//                .replicas(1)
//                .build();
//    }
//
//    @Bean
//    public NewTopic subscriptionEventsTopic() {
//        return TopicBuilder.name("subscription-events")
//                .partitions(3)
//                .replicas(1)
//                .build();
//    }
//}