package com.job.job_studio.dto;

import lombok.Data;
import java.util.List;

@Data
public class AmapGeocodeResponse {
    private String status;
    private String info;
    private List<Geocode> geocodes;

    @Data
    public static class Geocode {
        private String formattedAddress;
        private String location; // 经纬度字符串，如 "116.397428,39.90923"
        private String level;    // 地址级别，如 "省", "市"
    }
}