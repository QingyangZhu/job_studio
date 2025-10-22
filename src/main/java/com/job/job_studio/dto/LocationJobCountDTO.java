package com.job.job_studio.dto;

import lombok.Data;

/**
 * 聚合结果 DTO：用于接收按地理位置和岗位类型分组后的计数
 */
@Data
public class LocationJobCountDTO {
    private String province;
    private String city;
    private String jobTitle;
    private Long jobCount;
}