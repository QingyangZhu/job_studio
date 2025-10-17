package com.job.job_studio.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebConfig {

    // 显式创建 WebClient.Builder Bean，供其他服务注入使用
    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }
}