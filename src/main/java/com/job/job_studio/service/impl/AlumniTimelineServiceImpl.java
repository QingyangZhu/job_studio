package com.job.job_studio.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.job.job_studio.entity.AcademicPerformance;
import com.job.job_studio.entity.AlumniEvent;
import com.job.job_studio.entity.AlumniInfo;
import com.job.job_studio.mapper.AcademicPerformanceMapper;
import com.job.job_studio.mapper.AlumniEventMapper;
import com.job.job_studio.mapper.AlumniInfoMapper;
import com.job.job_studio.service.AlumniTimelineService;
import com.job.job_studio.vo.AlumniTimelineVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils; // [修复 5] 引入更健壮的字符串检查工具

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class AlumniTimelineServiceImpl implements AlumniTimelineService {

    private final AlumniInfoMapper alumniInfoMapper;
    private final AcademicPerformanceMapper academicPerformanceMapper;
    private final AlumniEventMapper alumniEventMapper;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Autowired
    public AlumniTimelineServiceImpl(AlumniInfoMapper alumniInfoMapper, AcademicPerformanceMapper academicPerformanceMapper, AlumniEventMapper alumniEventMapper) {
        this.alumniInfoMapper = alumniInfoMapper;
        this.academicPerformanceMapper = academicPerformanceMapper;
        this.alumniEventMapper = alumniEventMapper;
    }

    /**
     * 核心逻辑：聚合与分类校友数据以构建时间线
     */
    @Override
    public AlumniTimelineVO getAlumniTimelineData(Long alumniId) {
        // 1. 基础信息
        AlumniInfo info = alumniInfoMapper.selectById(alumniId);
        if (info == null) {
            // 更好的实践是抛出自定义异常或返回一个空的VO对象，而不是null
            // 但为保持原逻辑，此处暂时返回null
            return null;
        }

        // 2. 智育：GPA 序列 (主线图)
        QueryWrapper<AcademicPerformance> gpaWrapper = new QueryWrapper<>();
        gpaWrapper.eq("alumni_id", alumniId).orderByAsc("academic_year").orderByAsc("semester");
        List<AcademicPerformance> gpaRecords = academicPerformanceMapper.selectList(gpaWrapper);
        List<Map<String, Object>> gpaSeries = new ArrayList<>();

        for (AcademicPerformance gpa : gpaRecords) {
            String dateKey = generateGpaDate(gpa.getAcademicYear(), gpa.getSemester());
            // 使用不可变的Map.of，代码更简洁
            gpaSeries.add(Map.of(
                    "date", dateKey,
                    "gpa", Objects.requireNonNullElse(gpa.getMajorGpa(), 0.0) // [修复 4] 对可能为null的GPA进行安全处理
            ));
        }

        // 3. 德育/劳育等：持续时间区域和重大事件里程碑
        QueryWrapper<AlumniEvent> eventWrapper = new QueryWrapper<>();
        eventWrapper.eq("alumni_id", alumniId).orderByAsc("event_start_date");
        List<AlumniEvent> allEvents = alumniEventMapper.selectList(eventWrapper);

        List<Map<String, Object>> durationTenures = new ArrayList<>();
        List<Map<String, Object>> majorMilestones = new ArrayList<>();

        for (AlumniEvent event : allEvents) {
            String pillar = mapEventTypeToPillar(event.getEventName());

            // 处理持续性事件 (如干部任职、实习)
            if (event.getEventStartDate() != null && event.getEventEndDate() != null) {
                // [修复 1] 使用正确的逻辑或运算符 '||'
                if ("德育".equals(pillar) || "劳育".equals(pillar)) {
                    durationTenures.add(Map.of(
                            "startDate", event.getEventStartDate().format(DATE_FORMATTER),
                            "endDate", event.getEventEndDate().format(DATE_FORMATTER),
                            "role", event.getEventName(),
                            "pillar", pillar
                    ));
                }
            }

            // 处理离散的里程碑事件 (如竞赛获奖)
            // [修复 5] 使用 StringUtils.hasText 检查字符串是否为null、空或仅包含空白
            if (StringUtils.hasText(event.getOutcome())) {
                // [修复 4] 格式化日期前，确保 event.getEventStartDate() 不为 null
                String eventDate = (event.getEventStartDate() != null) ? event.getEventStartDate().format(DATE_FORMATTER) : "";

                // [修复 2] 使用正确的三元运算符为 level 提供默认值
                String level = StringUtils.hasText(event.getEventLevel()) ? event.getEventLevel() : "校级";

                majorMilestones.add(Map.of(
                        "date", eventDate,
                        "title", event.getEventName() + " (" + event.getOutcome() + ")",
                        "pillar", pillar,
                        "level", level
                ));
            }
        }

        // 4. 构建 VO
        AlumniTimelineVO vo = new AlumniTimelineVO();
        vo.setAlumniId(alumniId);
        vo.setAlumniName(info.getName());
        vo.setGraduationYear(String.valueOf(info.getGraduationYear()));
        vo.setGpaSeries(gpaSeries);
        vo.setDurationTenures(durationTenures);
        vo.setMajorMilestones(majorMilestones);

        return vo;
    }

    /**
     * 辅助函数：将学年学期映射到一个有代表性的日期
     */
    private String generateGpaDate(String academicYear, String semester) {
        // 对入参进行健壮性检查
        if (!StringUtils.hasText(academicYear) || !academicYear.contains("-")) {
            return "1970-01-01"; // 对于无效输入，返回一个默认的起始日期
        }

        // [修复 3] 正确处理 split() 方法返回的字符串数组
        String[] yearParts = academicYear.split("-");
        if (yearParts.length < 2) {
            return "1970-01-01"; // 处理格式不正确的学年字符串
        }
        String endYear = yearParts[1];

        if (semester != null && semester.contains("秋季")) {
            // 秋季学期在下一日历年结束，例如 "2021-2022" 秋季学期在 2022年1月结束
            return endYear + "-01-31";
        } else if (semester != null && semester.contains("春季")) {
            // 春季学期在同一日历年结束，例如 "2021-2022" 春季学期在 2022年6月结束
            return endYear + "-06-30";
        }

        // 为其他意外的学期值或null值提供一个默认回退
        return yearParts[0] + "-01-01";
    }

    /**
     * 辅助函数：将事件名称映射到德智体美劳维度
     */
    private String mapEventTypeToPillar(String eventName) {
        if (!StringUtils.hasText(eventName)) {
            return "其他";
        }

        // [修复 1] 使用正确的逻辑或运算符 '||'
        if (eventName.contains("竞赛") || eventName.contains("奖学金") || eventName.contains("项目")) {
            return "智育";
        }
        if (eventName.contains("学生会") || eventName.contains("团委") || eventName.contains("班级")) {
            return "德育";
        }
        if (eventName.contains("实习") || eventName.contains("实践") || eventName.contains("服务")) {
            return "劳育";
        }

        return "其他";
    }
}