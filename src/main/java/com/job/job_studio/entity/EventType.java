package com.job.job_studio.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Table(name = "event_types")
@Data
public class EventType {

    // 对应 event_type_id (INT PRIMARY KEY)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long eventTypeId;

    // 对应 event_type_name (VARCHAR(50) UNIQUE)
    @Column(name = "event_type_name", unique = true, nullable = false)
    private String eventTypeName;

    // 对应 description (TEXT)
    @Column(columnDefinition = "TEXT")
    private String description;

    /* 关系映射：一个事件类型下有多个事件实例 */
    @OneToMany(mappedBy = "eventType")
    private List<AlumniEvent> alumniEvents;

    // 省略：构造函数、Getter/Setter（由 @Data 提供）
}