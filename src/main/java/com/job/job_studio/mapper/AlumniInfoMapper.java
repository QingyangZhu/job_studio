package com.job.job_studio.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.job.job_studio.entity.AlumniInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AlumniInfoMapper extends BaseMapper<AlumniInfo> {
    // BaseMapper 提供了基本的 CRUD 方法，如：
    // insert(AlumniInfo entity);
    // selectById(Long id);
    // selectList(Wrapper<AlumniInfo> queryWrapper);

    // 如果需要自定义查询（如根据学号查询），需要在这里定义方法，并使用 @Select 注解或 XML 文件实现
    // AlumniInfo selectByStudentId(String studentId);
}