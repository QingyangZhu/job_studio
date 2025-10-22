package com.job.job_studio.service;

import com.job.job_studio.dto.LocationJobCountDTO;
import com.job.job_studio.mapper.CareerDetailsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class JobDistributionService {

    private final CareerDetailsMapper careerDetailsMapper;
    private final AmapGeocodeService amapGeocodeService;

    @Autowired
    public JobDistributionService(CareerDetailsMapper careerDetailsMapper, AmapGeocodeService amapGeocodeService) {
        this.careerDetailsMapper = careerDetailsMapper;
        this.amapGeocodeService = amapGeocodeService;
    }

    public Map<String, Object> getAggregatedJobDistribution() {
        List<LocationJobCountDTO> rawData = careerDetailsMapper.aggregateJobDistribution();
        return aggregateDataByLocation(rawData);
    }

    private Map<String, Object> aggregateDataByLocation(List<LocationJobCountDTO> rawData) {
        Map<String, Object> result = new HashMap<>();

        Map<String, List<LocationJobCountDTO>> provinceMap = rawData.stream()
                .filter(dto -> StringUtils.hasText(dto.getProvince()))
                .collect(Collectors.groupingBy(LocationJobCountDTO::getProvince));

        List<Map<String, Object>> provinceData = new ArrayList<>();
        Map<String, Object> cityDetailsMap = new HashMap<>();

        for (Map.Entry<String, List<LocationJobCountDTO>> entry : provinceMap.entrySet()) {
            String province = entry.getKey();
            List<LocationJobCountDTO> provinceRecords = entry.getValue();

            Optional<String> coordStr = amapGeocodeService.geocodeAddress(province);
            if (coordStr.isEmpty()) continue;

            long totalCount = provinceRecords.stream().mapToLong(LocationJobCountDTO::getJobCount).sum();

            Map<String, List<LocationJobCountDTO>> cityGroup = provinceRecords.stream()
                    .filter(dto -> StringUtils.hasText(dto.getCity()))
                    .collect(Collectors.groupingBy(LocationJobCountDTO::getCity));

            List<String> cityNames = new ArrayList<>();

            for (Map.Entry<String, List<LocationJobCountDTO>> cityEntry : cityGroup.entrySet()) {
                String city = cityEntry.getKey();
                List<LocationJobCountDTO> cityRecords = cityEntry.getValue();
                Optional<String> cityCoordStr = amapGeocodeService.geocodeAddress(city);

                List<Map<String, Object>> jobData = cityRecords.stream()
                        .map(dto -> Map.<String, Object>of("name", dto.getJobTitle(), "value", dto.getJobCount()))
                        .collect(Collectors.toList());

                if (cityCoordStr.isPresent()) {
                    // [修复] 简化键名，并确保数据结构与前端期望一致
                    cityDetailsMap.put(city, Map.of(
                            "name", city,
                            "coord", cityCoordStr.get(), // 坐标字符串
                            "data", jobData,
                            "total", cityRecords.stream().mapToLong(LocationJobCountDTO::getJobCount).sum()
                    ));
                    cityNames.add(city);
                }
            }

            // [修复] 确保省份数据点也包含 `coord` 字符串
            provinceData.add(Map.of(
                    "name", province,
                    "coord", coordStr.get(), // 坐标字符串
                    "total", totalCount,
                    "cityNames", cityNames
            ));
        }

        result.put("provinceData", provinceData);
        result.put("cityDetailsMap", cityDetailsMap);
        return result;
    }
}