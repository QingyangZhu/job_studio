package com.job.job_studio.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Table(name = "alumni_events")
@Data
public class AlumniEvent {

    // 对应 event_instance_id (INT PRIMARY KEY)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long eventInstanceId;

    // 对应 event_name (VARCHAR(100))
    @Column(name = "event_name", nullable = false)
    private String eventName;

    // 对应 event_start_date (DATE)
    @Column(name = "event_start_date")
    private LocalDate eventStartDate;

    // 对应 event_end_date (DATE)
    @Column(name = "event_end_date")
    private LocalDate eventEndDate;

    // 对应 outcome (VARCHAR(100)) - 成果/结果
    private String outcome;

    // 对应 event_level (VARCHAR(50)) - 级别，例如“国家级” [3]
    @Column(name = "event_level")
    private String eventLevel;

    /* 关系映射：多对一（多个事件属于一个校友）*/
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "alumni_id", nullable = false)
    private AlumniInfo alumniInfo;

    /* 关系映射：多对一（多个事件属于一个事件类型）*/
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_type_id", nullable = false)
    private EventType eventType;

    // 省略：构造函数、Getter/Setter（由 @Data 提供）
}