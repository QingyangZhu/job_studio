package com.job.job_studio.service.impl;

import com.job.job_studio.entity.CareerDetails;
import com.job.job_studio.mapper.CareerDetailsMapper;
import com.job.job_studio.service.CareerDetailsService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CareerDetailsServiceImpl extends ServiceImpl<CareerDetailsMapper, CareerDetails> implements CareerDetailsService {

    private final CareerDetailsMapper careerDetailsMapper;

    @Autowired
    public CareerDetailsServiceImpl(CareerDetailsMapper careerDetailsMapper) {
        this.careerDetailsMapper = careerDetailsMapper;
    }

    @Override
    public List<CareerDetails> getJobsByIndustry(String industry) {
        // 根据行业名称筛选就业记录
        QueryWrapper<CareerDetails> wrapper = new QueryWrapper<>();
        if (industry!= null &&!industry.isEmpty()) {
            wrapper.eq("industry", industry);
        }
        return careerDetailsMapper.selectList(wrapper);
    }

    @Override
    public List<CareerDetails> getAllJobPlacements() {
        // 获取所有工作去向记录
        return this.list();
    }
}