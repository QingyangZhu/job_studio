package com.job.job_studio.dto;

import com.job.job_studio.entity.StudentInfo;
import lombok.Data;

import java.io.Serializable;

/**
 * 学生信息列表 DTO：用于后台管理系统 (BMS) 列表展示
 */
@Data
public class StudentInfoDTO implements Serializable {
    private Long studentId;
    private String name;
    private String major;
    private String className;
    private Integer enrollmentYear;
    private String contactEmail;
    // 可以添加最新 GPA 等聚合信息，但此处保持基础信息，避免复杂联表查询

    // 静态工厂方法，用于从 Entity 转换
    public static StudentInfoDTO fromEntity(StudentInfo entity) {
        StudentInfoDTO dto = new StudentInfoDTO();
        dto.setStudentId(entity.getStudentId());
        dto.setName(entity.getName());
        dto.setMajor(entity.getMajor());
        dto.setClassName(entity.getClassName());
        dto.setEnrollmentYear(entity.getEnrollmentYear());
        dto.setContactEmail(entity.getContactEmail());
        return dto;
    }
}