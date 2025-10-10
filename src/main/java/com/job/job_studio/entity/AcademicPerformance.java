package com.job.job_studio.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Entity
@Table(name = "academic_performance")
@Data
public class AcademicPerformance {

    // 对应 gpa_id (INT PRIMARY KEY)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long gpaId;

    // 对应 academic_year (VARCHAR(20))
    @Column(name = "academic_year", nullable = false)
    private String academicYear;

    // 对应 semester (VARCHAR(20))
    @Column(name = "semester", nullable = false)
    private String semester;

    // 对应 overall_gpa (DECIMAL(4,2))
    @Column(name = "overall_gpa", precision = 4, scale = 2)
    private BigDecimal overallGpa;

    // 对应 major_gpa (DECIMAL(4,2))
    // 主修科目GPA，是我们分析生涯曲线的关键指标 [2]
    @Column(name = "major_gpa", precision = 4, scale = 2)
    private BigDecimal majorGpa;

    /* 关系映射：多对一（多条 GPA 记录属于一个校友）*/
    @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn 指定了外键列名，即 academic_performance 表中的 alumni_id
    @JoinColumn(name = "alumni_id", nullable = false)
    private AlumniInfo alumniInfo;

    // 省略：构造函数、Getter/Setter（由 @Data 提供）
}