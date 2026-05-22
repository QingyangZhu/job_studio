# 智慧就业系统 — 技术分析报告

> 生成日期：2026-05-14

---

## 一、项目概况

智慧就业系统是面向高校就业工作室的全栈 Web 应用，核心功能包括学生能力测评、岗位知识图谱可视
化、校友成长曲线分析、就业地域分布展示以及 AI 生涯规划助手。

- **后端项目**：`job_studio`（Spring Boot）
- **前端项目**：`job_studio_front`（React + Vite）
- **数据库**：MySQL（库名 `job-studio`）
- **开发模式**：前后端分离，Vite 开发服务器通过代理转发 API 请求到 Spring Boot

---

## 二、后端技术栈

### 2.1 核心框架与版本

| 技术 | 版本 | 用途 |
|------|------|------|
| Java | 17 | 运行环境 |
| Spring Boot | 3.5.6 | 应用框架 |
| MyBatis-Plus | 3.5.6 | ORM / 数据库访问 |
| MySQL Connector | runtime | 数据库驱动 |
| Spring Security | (随 Boot 3.5.6) | 认证与授权 |
| jjwt | 0.11.5 | JWT Token 生成与校验 |
| Spring WebFlux | (随 Boot 3.5.6) | 外部 API 调用（WebClient） |
| Caffeine | (随 Boot) | 本地缓存（地理编码结果） |
| SpringDoc OpenAPI | 2.1.0 | API 文档（Swagger UI） |
| Lombok | (optional) | 样板代码生成 |
| Maven | — | 项目构建 |

### 2.2 外部服务集成

| 服务 | 用途 | 配置键 |
|------|------|--------|
| DeepSeek API | AI 生涯规划对话 | `deepseek.api.key`, `deepseek.base.url`, `deepseek.model` |
| 高德地图 API | 地址 → 经纬度地理编码 | `amap.api.key` |

### 2.3 项目分层架构

```
controller/     ← REST 接口层（@RestController）
    ├── AuthController        登录认证
    ├── UserController        用户资料管理
    ├── AdminController       后台仪表盘统计
    ├── AlumniDataController  校友数据 CRUD + 成长曲线
    ├── StudentAssessmentController  学生测评提交与管理
    ├── JobGraphController    岗位知识图谱
    ├── JobAnalysisController 就业分布分析
    └── ChatController        AI 对话接口

service/        ← 业务接口层
service/impl/   ← 业务实现层（extends ServiceImpl<Mapper, Entity>）
    ├── AlumniInfoServiceImpl
    ├── StudentInfoServiceImpl
    ├── JobGraphServiceImpl   ← 知识图谱 BFS 遍历 + ECharts 格式转换
    ├── AssessmentResultServiceImpl
    └── ...

mapper/         ← 数据访问层（MyBatis-Plus BaseMapper）
entity/         ← 数据库实体（@TableName 映射）
dto/            ← 数据传输对象（入参/出参）
vo/             ← 视图对象（复杂聚合结构，如 AlumniTimelineVO）
config/         ← Spring 配置类
    ├── SecurityConfig       安全策略 + CORS
    ├── AuthTokenFilter      JWT 过滤器（OncePerRequestFilter）
    ├── CacheConfig          Caffeine 缓存 + WebClient Bean
    ├── WebConfig            WebClient.Builder Bean
    ├── GraphDataInitializer 启动时自动初始化知识图谱种子数据
    └── ...
security/       ← 安全组件
    ├── LoginUser             实现 UserDetails，扩展 studentId/role
    └── UserDetailsServiceImpl  从 sys_user 表加载用户
utils/
    └── JwtUtils             JWT 生成/解析/验证
```

### 2.4 数据库实体（Entity）

| 实体类 | 表名 | 说明 |
|--------|------|------|
| `SysUser` | `sys_user` | 系统用户（用户名/密码/角色/关联学号） |
| `StudentInfo` | `student_info` | 学生基本信息（学号/姓名/专业/目标岗位/联系方式） |
| `AlumniInfo` | `alumni_info` | 校友基本信息（学号/姓名/专业/毕业年份） |
| `AcademicPerformance` | `academic_performance` | 校友 GPA 成绩记录（按学期） |
| `AlumniEvent` | `alumni_events` | 校友特殊事件（竞赛/干部/实习） |
| `CareerDetails` | `career_details` | 就业去向详情（公司/行业/岗位/城市） |
| `AssessmentResult` | `assessment_result` | 学生测评结果（综合分/目标岗位/技能 JSON） |
| `StudentExperience` | `student_experience` | 学生实践经历（竞赛/干部） |
| `KgNode` | `kg_node` | 知识图谱节点（岗位/技能） |
| `KgRelationship` | `kg_relationship` | 知识图谱关系边 |
| `EventType` | (event_type) | 事件类型字典 |

### 2.5 认证与安全

- **认证流程**：
  1. 客户端 `POST /api/auth/login` 提交用户名密码
  2. Spring Security `DaoAuthenticationProvider` 调用 `UserDetailsServiceImpl` 从 `sys_user` 表加载用户
  3. 密码使用 `BCryptPasswordEncoder` 比对
  4. 成功后返回 JWT Token（24h 有效期）、角色、studentId、测评完成状态
  5. 后续请求通过 `AuthTokenFilter` 从 `Authorization: Bearer xxx` 头解析 JWT 并注入 SecurityContext

- **API 路径策略**：通过 `server.servlet.context-path=/api` 全局配置统一添加 `/api` 前缀，所有 Controller 的 `@RequestMapping` 不含 `/api`（如 `@RequestMapping("/auth")`），由 Spring Boot 自动拼装为 `/api/auth`

- **权限模型**：基于角色的访问控制（`ROLE_STUDENT` / `ROLE_ADMIN`），SecurityConfig 中的 `requestMatchers` 与 Controller 路径一致（如 `/students/**` 对应 Controller 的 `/students`）
  - `/auth/**` — 公开
  - `/students/**` — STUDENT / ADMIN
  - `/admin/**` — ADMIN 独占
  - 其余接口需认证

- **CORS**：开发环境允许所有来源（`addAllowedOriginPattern("*")`）

### 2.6 代码风格要点

- **依赖注入**：混合使用构造器注入（推荐）和字段注入（`@Autowired`）。新建代码应统一使用构造器注入
- **实体**：统一使用 Lombok `@Data`，MyBatis-Plus `@TableName` / `@TableId` / `@TableField` 注解
- **Service**：实现类继承 `ServiceImpl<Mapper, Entity>` 获得 CRUD 能力，接口定义业务方法
- **Controller 返回**：统一使用 `ResponseEntity` 包装，返回体多为 `Map<String, Object>` 或实体/DTO 直接返回
- **注释风格**：中文注释为主，关键步骤有编号说明
- **异常处理**：Controller 层 try-catch + `e.printStackTrace()`（不够规范，建议后续引入全局异常处理器）
- **配置**：全部在 `application.properties`，无 YAML 文件

---

## 三、前端技术栈

### 3.1 核心依赖与版本

| 技术 | 版本 | 用途 |
|------|------|------|
| React | 19.2.0 | UI 框架 |
| React Router | 7.13.0 | 前端路由 |
| Vite | 7.2.1 | 构建工具 / 开发服务器 |
| Zustand | 5.0.8 | 全局状态管理 |
| Axios | 1.12.2 | HTTP 请求 |
| ECharts | 6.0.0 | 力导向图、地图等可视化 |
| D3.js | 7.9.0 | 校友成长时间线等自定义可视化 |

### 3.2 项目结构

```
src/
├── App.jsx                 主入口（路由定义、PrivateRoute 守卫）
├── index.jsx               ReactDOM 挂载点
├── index.css               全局样式
├── App.css                 应用级样式
├── store/
│   └── appStore.js          Zustand 全局状态（认证/学生/校友/图谱/地图/聊天）
├── router/
│   └── AppRouter.jsx        独立路由组件（备用，当前实际使用 App.jsx 中路由）
├── layouts/
│   ├── AdminLayout.jsx      后台管理布局（侧边栏 + Outlet）
│   └── BMSLayout.jsx        备用布局
├── pages/
│   ├── Login.jsx            登录页（暗色科技风）
│   ├── AssessmentForm.jsx   三步测评表单（通用能力→岗位选择→专项自评）
│   ├── UserProfile.jsx      个人中心（标签页：个人资料/密码修改/成长档案）
│   └── admin/
│       ├── Dashboard.jsx    后台仪表盘（统计卡片 + ECharts 柱状图）
│       ├── StudentList.jsx  学生列表管理
│       └── AlumniList.jsx   校友列表管理
├── components/
│   ├── DataDashboard.jsx    主数据大屏（九宫格布局，集成各子面板）
│   ├── JobCompetencyGraph.jsx  ECharts 力导向知识图谱
│   ├── AlumniGrowthTimeline.jsx  D3 校友成长时间线
│   ├── JobDistributionMap.jsx   中国地图就业分布
│   ├── AssessmentPanel.jsx      学生筛选与能力概览面板
│   ├── ChatAssistant.jsx        AI 对话助手（上下文感知）
│   ├── GapAnalysisPanel.jsx     能力差距分析面板
│   ├── RadarChart.jsx           雷达图
│   └── admin/
│       ├── AssessmentModal.jsx  测评详情弹窗
│       └── DataModal.jsx        数据编辑弹窗
├── utils/
│   └── mapUtils.js          地图工具函数
└── assets/
    └── geo_maps/            中国各省 GeoJSON 地图数据（34 个省级行政区）
```

### 3.3 路由设计

| 路径 | 组件 | 权限 | 说明 |
|------|------|------|------|
| `/login` | Login | 公开 | 登录页 |
| `/dashboard` | DataDashboard | 需登录 | 核心数据大屏 |
| `/assessment` | AssessmentForm | 需登录 | 三步能力测评 |
| `/profile` | UserProfile | 需登录 | 个人中心 |
| `/admin` | AdminLayout | 需登录 | 后台管理布局 |
| `/admin/dashboard` | Dashboard | 需登录 | 后台仪表盘 |
| `/admin/students` | StudentList | 需登录 | 学生管理 |
| `/admin/alumni` | AlumniList | 需登录 | 校友管理 |
| `/` | → `/dashboard` | — | 默认重定向 |
| `*` | → `/login` | — | 404 兜底 |

### 3.4 状态管理（Zustand）

`appStore.js` 采用单一 Store 模式，按功能域划分：

- **认证状态**：`user` / `token` / `isAuthenticated`，登录时持久化到 `localStorage`
- **业务状态**：`selectedStudentId` / `selectedAlumniId` / `selectedJobRole`
- **数据列表**：`studentList` / `alumniList`
- **详情数据**：`studentProfile` / `graphData` / `mapData` / `chatMessages`
- **加载态**：细粒度的 `loading` 对象（auth/studentList/alumniList 等各自独立）
- **Actions**：`login` / `logout` / `fetchStudentList` / `selectStudent` / `triggerGraphFetch` / `sendChatMessage`

### 3.5 API 代理配置

Vite 开发服务器将 `/api` 前缀的请求代理到 `http://localhost:8080`，并重写路径去掉 `/api` 前缀。后端通过 `server.servlet.context-path=/api` 统一管理前缀，前端所有 API 调用均使用单层 `/api` 前缀，路径策略一致：

```
浏览器请求: /api/auth/login  →  Vite 剥离 /api  →  后端接收 /auth/login  →  匹配 Controller @RequestMapping("/auth")
浏览器请求: /api/alumni/all  →  Vite 剥离 /api  →  后端接收 /alumni/all  →  匹配 Controller @RequestMapping("/alumni")
```

### 3.6 代码风格要点

- **样式方案**：组件内 `const styles = { ... }` 对象 + 内联 `style={}` 属性，无 CSS Modules / styled-components
- **配色体系**：统一的暗色科技风：背景 `#0a0b1f`，强调色 `#00c5c7`（青色），边框 `#005f73`
- **组件模式**：函数组件 + Hooks（useState / useEffect / useRef），无类组件
- **图表组件**：封装模式均为 `useEffect` 内初始化 ECharts/D3 实例，监听数据变化重渲染，监听 resize 自适应
- **路由守卫**：`PrivateRoute` 组件检查 `isAuthenticated`，未登录重定向到 `/login`
- **代码注释**：中文注释为主，关键逻辑有编号说明

---

## 四、核心业务模块分析

### 4.1 能力测评系统

- **前端**：三步向导式表单（通用素养→目标岗位选择→专项技能自评），滑块打分（0-100）
- **后端**：`POST /students/{studentId}/assessment/submit` 接收 `AssessmentSubmitDTO`，合并通用+专项技能为 JSON 存入 `skill_details` 字段，计算平均分存入 `general_score`

### 4.2 岗位知识图谱

- **数据来源**：`kg_node` / `kg_relationship` 表，启动时通过 `GraphDataInitializer`（CommandLineRunner）自动填充种子数据
- **图谱生成**：`JobGraphServiceImpl.generateForceGraphData()` — 从目标岗位节点出发 BFS 遍历 3 层关系，批量查询优化（IN 子句），转换为 ECharts force-graph 格式
- **技能差距标注**：结合学生最新测评结果，将已掌握的技能标记为绿色，未掌握的 HardSkill 标记为红色 GAP
- **节点分类**：JobRole(0) / HardSkill(1) / SoftSkill(2) / Threshold(3) / Differentiating(4)

### 4.3 AI 生涯规划助手

- **后端**：`DeepSeekService` 通过 WebClient 调用 DeepSeek Chat Completion API，携带系统提示词设定"就业工作室 AI 导师"角色
- **前端**：`ChatAssistant` 组件自动将当前选中的学生画像和校友榜样拼接为上下文 Prompt 一并发送

### 4.4 校友成长曲线

- **后端**：`AlumniTimelineService` 聚合 GPA 曲线（AcademicPerformance）、持续时间区间（干部/实习等）、重大里程碑事件，输出 `AlumniTimelineVO`
- **前端**：`AlumniGrowthTimeline` 组件使用 D3.js 渲染多维度时间线

### 4.5 就业地域分布

- **后端**：`JobDistributionService` 聚合 `career_details` 表的城市数据，调用 `AmapGeocodeService`（高德 API）获取经纬度，结果通过 Caffeine 缓存 7 天
- **前端**：`JobDistributionMap` 组件使用 ECharts 中国地图 + 各省 GeoJSON 渲染

---

## 五、待改进项与技术债

### 5.1 后端

| 问题 | 严重程度 | 建议 |
|------|----------|------|
| ~~API 路径前缀不统一~~ | ~~高~~ | ✅ 已修复：采用 `server.servlet.context-path=/api` 统一管理 |
| Controller 中异常处理用 `e.printStackTrace()` | 中 | 引入 `@ControllerAdvice` 全局异常处理器 |
| `application.properties` 中包含 API 密钥明文 | 高 | 使用环境变量或配置中心管理敏感信息 |
| 部分 Service 使用字段注入 | 低 | 统一为构造器注入 |
| `JobStudioApplication.main()` 中包含测试代码（打印 BCrypt 密码） | 低 | 移入测试类 |
| AdminController 中管理员登录使用硬编码账号密码 + 模拟 Token | 高 | 统一使用 Spring Security + JWT 认证体系 |
| DeepSeek API Key 硬编码在配置文件中 | 高 | 移入环境变量 |

### 5.2 前端

| 问题 | 严重程度 | 建议 |
|------|----------|------|
| ~~API 请求路径不一致（如 `/api/api/auth/login` 双前缀）~~ | ~~高~~ | ✅ 已修复：改为 `/api/auth/login`，与全局 context-path 对齐 |
| 内联样式对象散落在每个组件中 | 中 | 抽取公共主题对象到独立文件 |
| 存在两个路由定义（`App.jsx` 和 `AppRouter.jsx`），功能重复 | 中 | 只保留一处 |
| `appStore.sendChatMessage` 方法体为空 | 低 | 补充实现或移除 |
| 缺少 TypeScript | 低 | 后续可考虑迁移，提升类型安全 |
| `.env` 文件未使用（API 地址硬编码在 vite.config.js） | 低 | 使用环境变量管理不同环境的 API 地址 |

---

## 六、开发环境启动指南

### 后端

```bash
cd job_studio
# 确保 MySQL 运行，数据库 job-studio 已创建
./mvnw spring-boot:run
# 默认端口: 8080
# Swagger UI: http://localhost:8080/api/swagger-ui/index.html
```

### 前端

```bash
cd job_studio_front
npm install
npm start
# 默认端口: 5173
# 浏览器访问: http://localhost:5173
```

**默认测试账号**：用户名 `admin` / 密码 `123456`（BCrypt 加密后存储在 `sys_user` 表）

---

## 七、总结

该项目是一个功能较为完整的高校就业数据可视化平台。后端采用经典的 Spring Boot + MyBatis-Plus 三层架构，引入 Spring Security + JWT 实现无状态认证，集成 DeepSeek AI 和高德地图外部服务。前端基于 React 19 + Vite 7 + Zustand 状态管理，使用 ECharts 和 D3.js 实现丰富的交互式数据可视化。

当前处于 MVP/开发阶段，核心业务流程（登录→测评→图谱分析→AI 建议）已打通。API 路径前缀已通过 `server.servlet.context-path=/api` 统一，SecurityConfig 权限规则已对齐。后续需关注异常处理规范化、敏感信息安全加固等问题。
