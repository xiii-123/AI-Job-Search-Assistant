# AI Job Search Assistant

<div align="center">

![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.6-green)
![Spring AI](https://img.shields.io/badge/Spring%20AI%20Alibaba-1.0.0--M2-blue)
![License](https://img.shields.io/badge/License-MIT-yellow)

基于 Spring AI Alibaba 框架的智能求职助手

[特性](#特性) • [快速开始](#快速开始) • [API文档](#api文档) • [配置说明](#配置说明)

</div>

---

## 项目简介

AI Job Search Assistant 是一款基于 Spring AI Alibaba 和通义千问大模型的智能求职助手后端服务。通过 AI Agent 架构实现意图识别、技能匹配和智能对话，为求职者提供简历优化、面试准备、职业规划等全方位支持。

### 核心能力

- **🤖 AI Agent 智能编排** - 意图识别 → Skill匹配 → 回答生成
- **💬 多轮对话记忆** - 基于上下文的连续对话体验
- **🔐 JWT 安全认证** - BCrypt 密码加密 + Token 认证
- **📊 SQLite 本地存储** - 零配置数据库，开箱即用
- **🚀 Java 21 特性** - Record、Sealed Class、Pattern Matching
- **⚙️ 多环境配置** - dev / local / prod 环境隔离

---

## 特性

### 已实现功能

#### 🔑 用户系统
- 用户注册/登录/登出
- BCrypt 密码加密
- JWT Token 认证
- 用户信息管理

#### 🤖 AI 核心能力
- **意图识别** - 自动识别用户需求（简历优化/面试准备/求职搜索等）
- **Agent 编排** - 智能路由到对应 Skill
- **多轮对话** - 保持对话上下文记忆
- **流式响应** - 支持实时流式输出（可选）

#### 🛠️ 工程化能力
- 全局异常处理（Java 21 Sealed Classes）
- 启动配置校验
- CORS 跨域支持
- 健康检查端点
- 多环境配置管理

### 预留扩展

- 扩展 Advisor（日志监控、降级、限流）
- Tool Calling（函数回调、联网搜索）
- Skill 管理（CRUD 接口）
- PGVector 向量存储
- MCP 多工具联动

---

## 技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| **Java** | 21 | 使用 Record、Sealed Class、Pattern Matching 等新特性 |
| **Spring Boot** | 3.3.6 | 核心框架 |
| **Spring AI Alibaba** | 1.0.0-M2 | 阿里云通义千问集成 |
| **Spring Security** | 6.x | 安全认证框架 |
| **Spring Data JPA** | 3.x | ORM 框架 |
| **SQLite** | 3.x | 嵌入式数据库 |
| **Maven** | 3.8+ | 依赖管理工具 |

---

## 快速开始

### 1. 环境要求

- **JDK 21+** - [下载地址](https://adoptium.net/)
- **Maven 3.8+** - [下载地址](https://maven.apache.org/download.cgi)
- **通义千问 API Key** - [获取地址](https://bailian.console.aliyun.com/)

### 2. 克隆项目

```bash
git clone https://github.com/your-username/AI-Job-Search-Assistant.git
cd AI-Job-Search-Assistant
```

### 3. 配置 API Key

#### 方式一：使用配置文件（推荐）

```bash
# 1. 运行配置脚本
./scripts/setup-local.sh

# 2. 编辑生成的配置文件
vim src/main/resources/application-local.yaml

# 3. 填入 API Key 和 JWT Secret
spring:
  ai:
    dashscope:
      api-key: sk-your-api-key-here
  security:
    jwt:
      secret: your-jwt-secret-at-least-256-bits
```

#### 方式二：使用环境变量

```bash
export DASHSCOPE_API_KEY="sk-your-api-key-here"
export JWT_SECRET="your-jwt-secret-at-least-256-bits"
```

### 4. 运行项目

```bash
# 使用 local 配置（推荐）
./mvnw spring-boot:run -Dspring-boot.run.profiles=local

# 或使用 dev 配置
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

### 5. 验证运行

```bash
# 健康检查
curl http://localhost:8080/api/actuator/health

# 预期响应
{
  "status": "UP",
  "components": {
    "db": {"status": "UP"},
    "diskSpace": {"status": "UP"},
    "ping": {"status": "UP"}
  }
}
```

### 6. 测试接口

```bash
# 1. 注册用户
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "test123456",
    "displayName": "Test User"
  }'

# 2. 登录获取 Token
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"test123456"}' \
  | jq -r '.data.token')

# 3. 发送消息
curl -X POST http://localhost:8080/api/chat/agent \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"message": "请帮我准备Java后端开发的面试"}'
```

---

## API 文档

### 认证接口

#### 用户注册

```http
POST /api/auth/register
Content-Type: application/json

{
  "username": "string",
  "password": "string",
  "email": "string (可选)",
  "displayName": "string (可选)"
}
```

#### 用户登录

```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "string",
  "password": "string"
}
```

**响应示例：**

```json
{
  "code": 200,
  "msg": "Login successful",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "tokenType": "Bearer",
    "userId": 1,
    "username": "testuser",
    "displayName": "Test User",
    "role": "USER"
  },
  "timestamp": 1234567890123
}
```

#### 获取当前用户信息

```http
GET /api/auth/me
Authorization: Bearer {token}
```

### 聊天接口

#### Agent 交互（推荐）

```http
POST /api/chat/agent
Authorization: Bearer {token}
Content-Type: application/json

{
  "message": "请帮我优化简历",
  "conversationId": "string (可选)",
  "stream": false
}
```

**响应示例：**

```json
{
  "code": 200,
  "msg": "Operation successful",
  "data": {
    "content": "我建议从以下几个方面优化你的简历...",
    "conversationId": "uuid",
    "intent": "resume_optimization",
    "skill": "resume_optimizer",
    "model": "qwen-plus",
    "executionTime": 1841
  },
  "timestamp": 1234567890123
}
```

#### LLM 直接对话

```http
POST /api/chat/send
Authorization: Bearer {token}
Content-Type: application/json

{
  "message": "你好",
  "conversationId": "string (可选)",
  "stream": false
}
```

#### 清空对话上下文

```http
DELETE /api/chat/context/{conversationId}
Authorization: Bearer {token}
```

#### 获取对话历史

```http
GET /api/chat/history/{conversationId}
Authorization: Bearer {token}
```

### 健康检查

```http
GET /api/actuator/health
```

---

## 配置说明

### 环境配置

| 环境 | 配置文件 | 说明 |
|------|----------|------|
| `dev` | application-dev.yml | 开发环境（使用环境变量） |
| `local` | application-local.yaml | 本地环境（集中管理 API Keys） |
| `prod` | application-prod.yml | 生产环境（环境变量） |

### 配置项说明

#### 必需配置

| 配置项 | 说明 | 获取方式 |
|--------|------|----------|
| `spring.ai.dashscope.api-key` | 通义千问 API Key | [阿里云百炼平台](https://bailian.console.aliyun.com/) |
| `spring.security.jwt.secret` | JWT 密钥（至少256位） | 自行生成 |

#### 可选配置

| 配置项 | 说明 | 默认值 |
|--------|------|--------|
| `app.ai.model` | AI 模型 | qwen-plus |
| `app.ai.max-tokens` | 最大 Token 数 | 2000 |
| `app.ai.temperature` | 温度参数 | 0.7 |
| `server.port` | 服务端口 | 8080 |
| `spring.datasource.url` | 数据库路径 | jdbc:sqlite:./data/job_assistant.db |

### 完整配置模板

`application-local.yaml` 包含所有当前和未来可能用到的配置项：

- ✅ 通义千问 API
- ✅ JWT 安全配置
- 🔄 阿里云百炼/OSS/SMS（预留）
- 🔄 OpenAI/Azure/Claude（预留）
- 🔄 向量数据库（预留）
- 🔄 OAuth/GitHub/Google（预留）
- 🔄 微信/支付/邮件（预留）
- 🔄 监控日志（预留）

---

## 项目结构

```
src/main/java/com/wyh/aijobsearchassistant/
├── advisor/              # AI Advisor（拦截器/增强器）
│   ├── LogMonitorAdvisor
│   ├── DegradeAdvisor
│   ├── RateLimitAdvisor
│   └── PgVectorAdvisor
├── agent/                # AI Agent 核心逻辑
│   ├── IntentRecognitionPrompt
│   ├── JobAdvisorAgent
│   └── SkillIdentifier
├── config/               # Spring 配置类
│   ├── AiConfig
│   ├── SecurityConfig
│   └── WebConfig
├── constant/             # 常量定义
│   ├── MessageConstants
│   └── StatusCode
├── controller/           # REST 控制器
│   ├── UserController
│   ├── ChatController
│   └── HealthController
├── entity/               # JPA 实体
│   ├── SysUser
│   └── ChatHistory
├── exception/            # 异常处理
│   ├── BaseException
│   ├── BusinessException
│   └── UserException
├── model/                # DTO（Record）
│   ├── ApiResponse
│   ├── UserRegisterRequest
│   └── ChatRequest
├── repository/           # 数据访问层
│   ├── UserRepository
│   └── ChatHistoryRepository
├── security/             # 安全相关
│   ├── JwtTokenProvider
│   ├── JwtAuthenticationFilter
│   └── JwtAuthenticationEntryPoint
├── service/              # 业务逻辑层
│   ├── UserService
│   ├── ChatService
│   └── AgentService
└── validator/            # 配置校验
    └── ConfigValidator
```

---

## 数据库表结构

### sys_user（用户表）

| 字段 | 类型 | 说明 |
|------|------|------|
| id | INTEGER | 主键 |
| username | VARCHAR(50) | 用户名（唯一） |
| email | VARCHAR(100) | 邮箱 |
| password | VARCHAR(255) | BCrypt 加密密码 |
| display_name | VARCHAR(100) | 显示名称 |
| status | INTEGER | 状态（0-禁用，1-启用） |
| role | VARCHAR(20) | 角色 |
| created_at | TIMESTAMP | 创建时间 |
| updated_at | TIMESTAMP | 更新时间 |
| last_login_at | TIMESTAMP | 最后登录时间 |

### chat_history（会话记录表）

| 字段 | 类型 | 说明 |
|------|------|------|
| id | INTEGER | 主键 |
| user_id | INTEGER | 用户ID |
| conversation_id | VARCHAR(100) | 会话ID |
| role | VARCHAR(20) | 角色（USER/ASSISTANT/SYSTEM） |
| content | TEXT | 消息内容 |
| intent | VARCHAR(50) | 意图类型 |
| skill | VARCHAR(50) | 技能标识 |
| token_count | INTEGER | Token数量 |
| model | VARCHAR(50) | 模型名称 |
| created_at | TIMESTAMP | 创建时间 |

---

## Agent 工作原理

### 核心流程

```
用户输入
    ↓
意图识别 (IntentRecognitionPrompt + 通义千问)
    ↓
Skill 匹配 (SkillIdentifier)
    ↓
回答生成 (ChatClient + DashScope)
    ↓
返回结果
```

### 支持的意图

| 意图 | 说明 | 对应 Skill |
|------|------|-----------|
| `resume_optimization` | 简历优化 | `resume_optimizer` |
| `interview_preparation` | 面试准备 | `interview_coach` |
| `job_search` | 求职搜索 | `job_search_assistant` |
| `salary_negotiation` | 薪资谈判 | `salary_consultant` |
| `career_planning` | 职业规划 | `career_advisor` |
| `skill_assessment` | 技能评估 | `skill_evaluator` |
| `industry_consultation` | 行业咨询 | `industry_expert` |
| `unknown` | 未知意图 | `general_assistant` |

---

## Java 21 特性应用

### 1. Record（简化 DTO）

```java
public record ApiResponse<T>(
    int code,
    String msg,
    T data,
    long timestamp
) {
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, "Operation successful", data, System.currentTimeMillis());
    }
}
```

### 2. Sealed Classes（异常体系）

```java
public sealed abstract class BaseException extends RuntimeException
    permits BusinessException, UserException, SystemException {
    private final int code;

    protected BaseException(int code, String message) {
        super(message);
        this.code = code;
    }
}
```

### 3. Pattern Matching（增强 Switch）

```java
return switch (intent) {
    case "resume_optimization" -> "resume_optimizer";
    case "interview_preparation" -> "interview_coach";
    case "job_search" -> "job_search_assistant";
    default -> "general_assistant";
};
```

### 4. Stream API（集合操作）

```java
List<SysUser> activeUsers = userRepository.findAll().stream()
    .filter(User::isEnabled)
    .filter(u -> u.getLastLoginAt() != null)
    .toList();
```

---

## 异常码说明

| Code | 说明 |
|------|------|
| **2xx** | 成功 |
| **4xx** | 客户端错误 |
| **5xx** | 服务器错误 |
| 1001 | 用户不存在 |
| 1002 | 用户已存在 |
| 1003 | 密码错误 |
| 1004 | Token 无效 |
| 2001 | AI 模型错误 |
| 2002 | Agent 执行错误 |
| 2003 | 上下文不存在 |
| 3001 | 配置错误 |

---

## 部署指南

### Docker 部署

```dockerfile
FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY target/job-ai-assistant-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

```bash
# 构建镜像
docker build -t ai-job-assistant:1.0.0 .

# 运行容器
docker run -d -p 8080:8080 \
  -e DASHSCOPE_API_KEY=your-key \
  -e SPRING_PROFILES_ACTIVE=prod \
  ai-job-assistant:1.0.0
```

### systemd 服务（Linux）

```ini
[Unit]
Description=AI Job Assistant Service
After=network.target

[Service]
Type=simple
User=www-data
WorkingDirectory=/opt/job-assistant
ExecStart=/usr/bin/java -jar /opt/job-assistant/job-ai-assistant-1.0.0.jar
Restart=always
Environment="SPRING_PROFILES_ACTIVE=prod"
Environment="DASHSCOPE_API_KEY=your-key"

[Install]
WantedBy=multi-user.target
```

---

## 常见问题

### Q1: 如何获取通义千问 API Key？

访问 [阿里云百炼平台](https://bailian.console.aliyun.com/)，开通服务后创建 API Key。新用户有免费额度。

### Q2: 本地配置文件会被提交到 Git 吗？

不会。`application-local.yaml` 已添加到 `.gitignore`，不会被提交。

### Q3: 如何切换 AI 模型？

在配置文件中修改 `app.ai.model`：
- `qwen-turbo` - 快速响应
- `qwen-plus` - 平衡性能（推荐）
- `qwen-max` - 最强能力

### Q4: 数据库文件在哪里？

默认位置：`./data/job_assistant.db`，可在配置文件中自定义路径。

### Q5: 如何启用调试日志？

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=local -Dlogging.level.com.wyh.aijobsearchassistant=DEBUG
```

---

## 贡献指南

欢迎贡献代码、提出建议或报告问题！

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启 Pull Request

---

## 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情。

---

## 联系方式

- **Issues**: [GitHub Issues](https://github.com/your-username/AI-Job-Search-Assistant/issues)
- **Email**: your-email@example.com

---

<div align="center">

**如果这个项目对你有帮助，请给个 ⭐️ Star！**

Made with ❤️ by [Your Name]

</div>
