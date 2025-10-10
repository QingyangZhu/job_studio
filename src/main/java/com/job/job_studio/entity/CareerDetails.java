package com.job.job_studio.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Table(name = "career_details")
@Data
public class CareerDetails {

    // 对应 career_id (INT PRIMARY KEY)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long careerId;

    // 对应 company_name (VARCHAR(100))
    @Column(name = "company_name", nullable = false)
    private String companyName;

    // 对应 industry (VARCHAR(50))
    private String industry;

    // 对应 job_title (VARCHAR(100))
    @Column(name = "job_title")
    private String jobTitle;

    // 对应 employment_type (VARCHAR(50))
    @Column(name = "employment_type")
    private String employmentType;

    // 对应 job_start_date (DATE)
    @Column(name = "job_start_date", nullable = false)
    private LocalDate jobStartDate;

    // 对应 job_end_date (DATE)
    @Column(name = "job_end_date")
    private LocalDate jobEndDate;

    // 对应 work_city (VARCHAR(50))
    @Column(name = "work_city")
    private String workCity;

    // 对应 job_description (TEXT)
    @Column(name = "job_description", columnDefinition = "TEXT")
    private String jobDescription;

    /* 关系映射：多对一（多条工作记录属于一个校友）*/
    // 尽管目前我们假设一个校友一条记录，但使用 ManyToOne 提高了模型的灵活性
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "alumni_id", nullable = false)
    private AlumniInfo alumniInfo;

    // 省略：构造函数、Getter/Setter（由 @Data 提供）
}