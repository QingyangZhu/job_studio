package com.job.job_studio.service;

import com.job.job_studio.dto.AmapGeocodeResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils; // 引入 Spring 工具类
import org.springframework.web.reactive.function.client.WebClient;
import java.util.Optional;

@Service
public class AmapGeocodeService {

    @Value("${amap.api.key}")
    private String amapApiKey;

    private final WebClient webClient;
    private static final String AMAP_BASE_URL = "https://restapi.amap.com/v3/geocode/geo";

    public AmapGeocodeService(WebClient webClient) {
        this.webClient = webClient;
    }

    @Cacheable(cacheNames = "geocache", key = "#address")
    public Optional<String> geocodeAddress(String address) {
        // [修复] 使用 StringUtils.hasText 进行健壮性检查
        if (!StringUtils.hasText(address)) {
            return Optional.empty();
        }

        try {
            AmapGeocodeResponse response = webClient.get()
                    .uri(AMAP_BASE_URL, uriBuilder -> uriBuilder
                            .queryParam("key", amapApiKey)
                            .queryParam("address", address)
                            .build())
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(AmapGeocodeResponse.class)
                    .block();

            // [修复] 增加对 getGeocodes() 的 null 安全检查
            if (response != null && "1".equals(response.getStatus()) && response.getGeocodes() != null && !response.getGeocodes().isEmpty()) {
                return Optional.ofNullable(response.getGeocodes().get(0).getLocation());
            }

        } catch (Exception e) {
            System.err.println("高德地理编码调用失败 (" + address + "): " + e.getMessage());
        }
        return Optional.empty();
    }
}