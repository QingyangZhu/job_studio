package com.job.job_studio.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Table(name = "alumni_info")
@Data
public class AlumniInfo {

    // 对应 alumni_id (INT PRIMARY KEY)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long alumniId;

    // 对应 student_id (VARCHAR(20) UNIQUE)
    @Column(name = "student_id", unique = true, nullable = false)
    private String studentId;

    // 对应 name (VARCHAR(50))
    @Column(name = "name", nullable = false)
    private String name;

    // 对应 gender (VARCHAR(10))
    private String gender;

    // 对应 major (VARCHAR(50))
    private String major;

    // 对应 graduation_year (INT)
    @Column(name = "graduation_year")
    private Integer graduationYear;

    // 对应 contact_email (VARCHAR(100) UNIQUE)
    @Column(name = "contact_email", unique = true)
    private String contactEmail;

    /* 关系映射：一个校友有多条 GPA 记录 */
    // mappedBy 指向 AcademicPerformance 实体中关联该 AlumniInfo 的字段名
    // CascadeType.ALL 确保对 AlumniInfo 的操作（如删除）会级联到其关联的 GPA 记录
    @OneToMany(mappedBy = "alumniInfo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AcademicPerformance> gpaRecords;

    /* 关系映射：一个校友有多个特殊事件 */
    @OneToMany(mappedBy = "alumniInfo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AlumniEvent> alumniEvents;

    /* 关系映射：一个校友有一条工作详情记录 */
    // 假设 career_details 中 alumni_id 是唯一的，因此是一对一关系，此处使用 OneToMany 以便未来扩展
    @OneToMany(mappedBy = "alumniInfo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CareerDetails> careerDetails;

    // 省略：构造函数、Getter/Setter（由 @Data 提供）
}