# AI Job Search Assistant

<div align="center">

![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.6-green)
![Spring AI](https://img.shields.io/badge/Spring%20AI%20Alibaba-1.0.0--M2-blue)
![License](https://img.shields.io/badge/License-MIT-yellow)

基于 Spring AI Alibaba 框架的智能求职助手

[功能特性](#功能特性) • [快速开始](#快速开始) • [项目文档](#项目文档) • [技术支持](#技术支持)

</div>

---

## 📖 项目简介

AI Job Search Assistant 是一款基于 Spring AI Alibaba 和通义千问大模型的智能求职助手后端服务。通过 AI Agent 架构实现意图识别、技能匹配和智能对话，为求职者提供简历优化、面试准备、职业规划等全方位支持。

### 🎯 核心能力

- **🤖 AI Agent 智能编排** - 意图识别 → Skill匹配 → 回答生成
- **💬 多轮对话记忆** - 基于上下文的连续对话体验
- **🔐 JWT 安全认证** - BCrypt 密码加密 + Token 认证
- **📊 SQLite 本地存储** - 零配置数据库，开箱即用
- **🚀 Java 21 特性** - Record、Sealed Class、Pattern Matching
- **⚙️ 多环境配置** - dev / local / prod 环境隔离
- **📝 完整的前端交互** - 会话管理、历史对话、用户信息管理

---

## ✨ 功能特性

### 已实现功能

#### 🔑 用户系统
- 用户注册/登录/登出
- BCrypt 密码加密
- JWT Token 认证
- 用户信息管理（修改昵称、密码）

#### 🤖 AI 核心能力
- **意图识别** - 自动识别用户需求（简历优化/面试准备/求职搜索等）
- **Agent 编排** - 智能路由到对应 Skill
- **多轮对话** - 保持对话上下文记忆
- **流式响应** - 支持实时流式输出（可选）

#### 💬 前端交互功能
- **会话管理**
  - 创建/查询/重命名/删除会话
  - 分页查询会话列表
  - 按名称关键词搜索
  - 清空会话（保留会话主体）

- **历史对话**
  - 分页查询历史消息（时间倒序）
  - 删除单条消息
  - 编辑会话上下文
  - 获取会话统计信息

- **用户信息**
  - 查询用户资料
  - 修改密码（含复杂度验证）
  - 修改昵称和头像（预留）

#### 🛠️ 工程化能力
- 全局异常处理（Java 21 Sealed Classes）
- 启动配置校验
- CORS 跨域支持
- 健康检查端点
- 多环境配置管理
- Swagger API 文档

### 预留扩展

- 扩展 Advisor（日志监控、降级、限流）
- Tool Calling（函数回调、联网搜索）
- Skill 管理（CRUD 接口）
- PGVector 向量存储
- MCP 多工具联动

---

## 🛠️ 技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| **Java** | 21 | 使用 Record、Sealed Class、Pattern Matching 等新特性 |
| **Spring Boot** | 3.3.6 | 核心框架 |
| **Spring AI Alibaba** | 1.0.0-M2 | 阿里云通义千问集成 |
| **Spring Security** | 6.x | 安全认证框架 |
| **Spring Data JPA** | 3.x | ORM 框架 |
| **SQLite** | 3.x | 嵌入式数据库 |
| **Maven** | 3.8+ | 依赖管理工具 |
| **SpringDoc OpenAPI** | 2.3.0 | Swagger API 文档 |

---

## 🚀 快速开始

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

#### 方式一：使用自动化脚本（推荐）

```bash
./scripts/setup-local.sh
```

脚本会自动：
- 检查是否已存在配置文件
- 复制示例配置文件
- 提示配置步骤

#### 方式二：手动配置

1. 复制示例配置文件：
```bash
cp src/main/resources/application-local.yaml.example src/main/resources/application-local.yaml
```

2. 编辑配置文件，填入实际的 API Keys：
```yaml
spring:
  ai:
    dashscope:
      api-key: sk-your-actual-dashscope-api-key-here
  security:
    jwt:
      secret: your-very-long-and-secure-secret-key-at-least-256-bits
```

3. 运行时激活 `local` profile：
```bash
# 方式一：使用环境变量
export SPRING_PROFILES_ACTIVE=local
./mvnw spring-boot:run

# 方式二：使用运行参数
./mvnw spring-boot:run -Dspring-boot.run.profiles=local

# 方式三：IDE 配置（IntelliJ IDEA）
# Run -> Edit Configurations -> Active profiles: local
```

### 4. 验证运行

```bash
# 健康检查
curl http://localhost:8080/api/actuator/health
```

**预期响应**：
```json
{
  "status": "UP",
  "components": {
    "db": {"status": "UP"},
    "diskSpace": {"status": "UP"},
    "ping": {"status": "UP"}
  }
}
```

### 5. 快速测试

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

## 📚 项目文档

完整的项目文档位于 [`docs/`](docs/) 目录：

### 核心文档

| 文档 | 说明 |
|------|------|
| [**FRONTEND_GUIDE.md**](docs/FRONTEND_GUIDE.md) | 前端对接指南 - 完整的 API 使用说明，包含所有接口的请求/响应示例、分页规范、错误码说明 |
| [**TEST_REPORT.md**](docs/TEST_REPORT.md) | 完整测试报告 - 包含所有接口的测试结果、性能数据、测试统计 |
| [**INTEGRATION_GUIDE.md**](docs/INTEGRATION_GUIDE.md) | 集成说明 - 代码集成步骤、数据库变更、测试示例 |
| [**FRONTEND_FEATURE_SUMMARY.md**](docs/FRONTEND_FEATURE_SUMMARY.md) | 功能实现总结 - 新增功能的详细说明、代码规范、验收标准 |

### 配置文档

| 文档 | 说明 |
|------|------|
| [**LOCAL_CONFIG.md**](docs/LOCAL_CONFIG.md) | 本地开发配置指南 - 快速配置 API Keys |
| [**SETUP.md**](docs/SETUP.md) | 安装与配置指南 - 环境要求、依赖安装、运行项目 |

### API 文档

| 文档 | 说明 |
|------|------|
| [**API.md**](API.md) | API 接口文档 - RESTful API 详细说明 |

### Swagger 在线文档

启动项目后访问：`http://localhost:8080/swagger-ui/index.html`

---

## 🎯 功能演示

### 会话管理流程

```bash
# 1. 创建会话
curl -X POST "http://localhost:8080/api/conversations?name=Java面试准备" \
  -H "Authorization: Bearer $TOKEN"

# 2. 查询会话列表（分页）
curl -X GET "http://localhost:8080/api/conversations?pageNum=1&pageSize=10" \
  -H "Authorization: Bearer $TOKEN"

# 3. 重命名会话
curl -X PUT http://localhost:8080/api/conversations/rename \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"conversationId": "uuid", "newName": "新名称"}'

# 4. 清空会话（保留会话主体）
curl -X DELETE "http://localhost:8080/api/conversations/uuid/clear" \
  -H "Authorization: Bearer $TOKEN"
```

### 历史对话查询

```bash
# 查询历史消息（分页+倒序）
curl -X GET "http://localhost:8080/api/messages/conversation/uuid?pageNum=1&pageSize=20" \
  -H "Authorization: Bearer $TOKEN"

# 获取会话上下文摘要
curl -X GET "http://localhost:8080/api/messages/context/uuid/summary" \
  -H "Authorization: Bearer $TOKEN"
```

---

## 🏗️ 项目结构

```
AI-Job-Search-Assistant/
├── docs/                          # 📚 项目文档目录
│   ├── FRONTEND_GUIDE.md          # 前端对接指南
│   ├── TEST_REPORT.md             # 完整测试报告
│   ├── INTEGRATION_GUIDE.md       # 集成说明
│   ├── FRONTEND_FEATURE_SUMMARY.md # 功能实现总结
│   ├── LOCAL_CONFIG.md            # 本地配置指南
│   └── SETUP.md                   # 安装配置指南
├── src/main/java/com/wyh/aijobsearchassistant/
│   ├── advisor/                     # AI Advisor（拦截器/增强器）
│   ├── agent/                       # AI Agent 核心逻辑
│   ├── config/                      # Spring 配置类
│   ├── constant/                    # 常量定义
│   ├── controller/                   # REST 控制器
│   ├── dto/                         # DTO（Record）
│   ├── entity/                      # JPA 实体
│   ├── enums/                       # 枚举类
│   ├── exception/                   # 异常类（Sealed Classes）
│   ├── model/                       # 数据模型
│   ├── repository/                  # 数据访问层
│   ├── security/                    # 安全相关
│   ├── service/                     # 业务逻辑层
│   └── validator/                   # 参数校验
├── src/main/resources/
│   ├── application.yaml             # 主配置
│   ├── application-dev.yml          # 开发环境配置
│   ├── application-prod.yml          # 生产环境配置
│   └── application-local.yaml.example # 本地配置模板
├── scripts/                           # 脚本工具
│   └── setup-local.sh                # 本地配置自动化脚本
├── pom.xml                            # Maven 配置
├── README.md                          # 项目说明（本文件）
├── LICENSE                            # MIT 许可证
├── API.md                             # API 接口文档
└── .gitignore                        # Git 忽略规则
```

---

## 🔧 配置说明

### 环境配置

| 环境 | 配置文件 | 使用场景 |
|------|----------|----------|
| `dev` | application-dev.yml | 开发调试（使用环境变量） |
| `local` | application-local.yaml | 本地开发（集中管理 API Keys） |
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

---

## 🎯 Agent 工作原理

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

## 🧪 测试状态

### 测试覆盖率

| 模块 | 接口数 | 测试通过 | 通过率 |
|------|--------|---------|--------|
| 用户认证 | 2 | 2 | 100% |
| 会话管理 | 6 | 6 | 100% |
| 历史对话 | 4 | 4 | 100% |
| 用户信息 | 3 | 3 | 100% |
| **总计** | **15** | **15** | **100%** |

### 测试报告

详细的测试报告请查看：[docs/TEST_REPORT.md](docs/TEST_REPORT.md)

---

## 🚀 部署指南

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

创建 `/etc/systemd/system/job-assistant.service`：

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

## 📈 性能指标

| 指标 | 数值 | 说明 |
|------|------|------|
| 应用启动时间 | ~5.7秒 | 冷启动时间 |
| API 响应时间 | < 50ms | 大部分接口（不含AI处理） |
| AI 处理时间 | ~16秒 | 包含意图识别和回复生成 |
| 内存占用 | < 512MB | 空闲状态 |
| 并发支持 | 100+ | 单实例 |

---

## 🤝 贡献指南

欢迎贡献代码、提出建议或报告问题！

### 开发流程

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启 Pull Request

### 代码规范

- 遵循 Google Java Style Guide
- 使用 Java 21 新特性（Record、Sealed Class 等）
- 所有公共方法添加 Javadoc 注释
- 提交前确保测试通过

---

## 📝 更新日志

### v1.0.0 (2025-02-19)

**新增功能**:
- ✅ 完整的前端交互功能（会话管理、历史对话、用户信息）
- ✅ 统一分页查询功能
- ✅ Swagger API 文档支持
- ✅ 完善的权限验证和参数校验
- ✅ 完整的项目文档和测试报告

**技术改进**:
- 新增 `Conversation` 实体和 `conversation` 表
- 新增 19 个 Java 文件（总数：68 个）
- 新增 8 个 DTO、2 个枚举类
- 完善异常处理和日志记录

**文档完善**:
- 创建 `docs/` 目录，整理所有项目文档
- 编写完整的前端对接指南和测试报告
- 更新 README.md，增加文档目录引用

---

## 📞 技术支持

### 常见问题

#### Q1: 如何获取通义千问 API Key？

访问 [阿里云百炼平台](https://bailian.console.aliyun.com/)，开通服务后创建 API Key。新用户有免费额度。

#### Q2: 本地配置文件会被提交到 Git 吗？

不会。`application-local.yaml` 已添加到 `.gitignore`，不会被提交。

#### Q3: 如何切换 AI 模型？

在配置文件中修改 `app.ai.model`：
- `qwen-turbo` - 快速响应
- `qwen-plus` - 平衡性能（推荐）
- `qwen-max` - 最强能力

#### Q4: 数据库文件在哪里？

默认位置：`./data/job_assistant.db`，可在配置文件中自定义路径。

#### Q5: 如何启用调试日志？

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=local -Dlogging.level.com.wyh.aijobsearchassistant=DEBUG
```

---

## 📜 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情。

---

<div align="center">

**如果这个项目对你有帮助，请给个 ⭐️ Star！**

Made with ❤️ by [AI Job Search Assistant Team]

**项目状态**: ✅ 生产就绪 | **测试状态**: ✅ 100% 通过

</div>
