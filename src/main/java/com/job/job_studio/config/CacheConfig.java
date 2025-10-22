package com.job.job_studio.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List; // 确保这个导入存在
import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * 配置 Caffeine 缓存管理器，用于地理编码结果，以节约高德 API 配额。
     */
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        // 设置缓存规约：最大容量 10000 条，写入后 7 天过期
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .maximumSize(10000)
                .expireAfterWrite(7, TimeUnit.DAYS)
                .recordStats());

        // 定义缓存名称，在 @Cacheable 中使用
        cacheManager.setCacheNames(List.of("geocache"));
        return cacheManager;
    }

    /**
     * WebClient Bean (用于外部 API 调用)
     * Spring Boot 的自动配置会提供一个 WebClient.Builder Bean，我们用它来构建最终的 WebClient。
     * 如果这个方法没有被执行，Spring 容器中就不会有 WebClient Bean。
     */
    @Bean
    public WebClient webClient(WebClient.Builder webClientBuilder) {
        return webClientBuilder.build();
    }
}