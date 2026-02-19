# 安装与配置指南

## 环境要求

### 必需软件

| 软件 | 版本要求 | 说明 |
|------|----------|------|
| JDK | 21+ | 必须使用 JDK 21 |
| Maven | 3.8+ | 依赖管理工具 |
| SQLite | 3.x | 自动包含在依赖中 |

### 可选软件

| 软件 | 说明 |
|------|------|
| Git | 版本控制 |
| curl | API 测试 |
| jq | JSON 解析（用于测试脚本） |

---

## 详细安装步骤

### 1. 安装 JDK 21

#### macOS (使用 Homebrew)

```bash
# 安装 Homebrew（如果未安装）
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"

# 安装 JDK 21
brew install openjdk@21

# 设置环境变量
echo 'export PATH="/usr/local/opt/openjdk@21/bin:$PATH"' >> ~/.zshrc
source ~/.zshrc

# 验证安装
java -version
```

#### Linux (Ubuntu/Debian)

```bash
# 安装 JDK 21
sudo apt update
sudo apt install openjdk-21-jdk

# 验证安装
java -version
```

#### Windows

1. 下载 JDK 21: https://adoptium.net/
2. 运行安装程序
3. 配置环境变量 `JAVA_HOME`
4. 添加 `%JAVA_HOME%\bin` 到 PATH

---

### 2. 安装 Maven

#### macOS

```bash
brew install maven
mvn -version
```

#### Linux

```bash
sudo apt install maven
mvn -version
```

#### Windows

1. 下载 Maven: https://maven.apache.org/download.cgi
2. 解压到目录（如 `C:\Program Files\Maven`）
3. 配置环境变量 `MAVEN_HOME`
4. 添加 `%MAVEN_HOME%\bin` 到 PATH

---

### 3. 获取通义千问 API Key

#### 步骤：

1. 访问 [阿里云百炼平台](https://bailian.console.aliyun.com/)
2. 登录/注册阿里云账号
3. 开通「通义千问」服务
4. 创建 API Key
5. 保存 API Key（后续配置使用）

#### 费用说明

- 新用户有免费额度
- 按实际调用量计费
- 建议设置消费限额

---

### 4. 克隆项目（可选）

```bash
git clone <repository-url>
cd AI-Job-Search-Assistant
```

---

### 5. 配置环境变量

#### 方式一：使用 `application-local.yaml`（推荐用于本地开发）

这是最简单的方式，所有 API Keys 集中管理在一个配置文件中。

**步骤：**

1. 复制示例配置文件：
```bash
cd src/main/resources
cp application-local.yaml.example application-local.yaml
```

2. 编辑 `application-local.yaml`，填入实际的 API Keys：
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

**完整配置说明请查看：** `application-local.yaml` 文件中的注释

**安全提示：**
- `application-local.yaml` 已添加到 `.gitignore`，不会被提交到 Git
- 包含所有当前和未来可能用到的 API Keys 配置项
- 适合团队协作，每个人维护自己的 local 配置

#### 方式二：使用 `.env` 文件（传统方式）

创建项目根目录下创建 `.env` 文件：

```bash
# 通义千问配置
DASHSCOPE_API_KEY=your-actual-api-key-here

# 数据库配置
DB_PATH=./data/job_assistant.db

# JWT 配置
JWT_SECRET=your-very-long-and-secure-secret-key-at-least-256-bits-for-hs256
JWT_EXPIRATION=86400000

# 服务器配置
SERVER_PORT=8080
SPRING_PROFILES_ACTIVE=dev

# AI 模型配置
AI_MODEL=qwen-plus
MAX_TOKENS=2000
TEMPERATURE=0.7

# CORS 配置
CORS_ORIGINS=http://localhost:3000,http://localhost:5173
```

**安全提示**：不要将 `.env` 文件提交到版本控制系统！

#### 方式二：使用系统环境变量

##### macOS / Linux

在 `~/.bashrc` 或 `~/.zshrc` 中添加：

```bash
export DASHSCOPE_API_KEY="your-api-key"
export DB_PATH="./data/job_assistant.db"
export JWT_SECRET="your-jwt-secret"
export SERVER_PORT=8080
export SPRING_PROFILES_ACTIVE=dev
```

然后执行：
```bash
source ~/.bashrc  # 或 source ~/.zshrc
```

##### Windows

在系统环境变量中添加：

```
DASHSCOPE_API_KEY=your-api-key
DB_PATH=./data/job_assistant.db
JWT_SECRET=your-jwt-secret
SERVER_PORT=8080
SPRING_PROFILES_ACTIVE=dev
```

---

## 编译项目

### 开发环境

```bash
# 清理并编译
./mvnw clean package

# 跳过测试编译
./mvnw clean package -DskipTests
```

### 生产环境

```bash
# 使用 prod 配置编译
./mvnw clean package -Pprod -DskipTests
```

---

## 运行项目

### 方式一：使用 Maven 插件（推荐用于开发）

```bash
# 开发环境
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# 生产环境
./mvnw spring-boot:run -Dspring-boot.run.profiles=prod
```

### 方式二：使用 JAR 文件（推荐用于生产）

```bash
# 运行 JAR
java -Dspring.profiles.active=dev -jar target/job-ai-assistant-1.0.0.jar

# 指定端口运行
java -Dserver.port=9090 -Dspring.profiles.active=prod -jar target/job-ai-assistant-1.0.0.jar

# 指定 JVM 参数
java -Xmx1g -Xms512m -Dspring.profiles.active=prod -jar target/job-ai-assistant-1.0.0.jar
```

### 方式三：使用 IDE

1. 导入项目为 Maven 项目
2. 等待依赖下载完成
3. 运行 `AiJobSearchAssistantApplication` 主类

---

## 验证安装

### 1. 检查服务启动

```bash
# 查看日志
tail -f logs/spring.log

# 或检查进程
ps aux | grep java
```

### 2. 健康检查

```bash
# 基础健康检查
curl http://localhost:8080/api/health/status

# Actuator 健康检查
curl http://localhost:8080/api/actuator/health
```

**预期响应**：
```json
{
  "status": "UP",
  "version": "1.0.0",
  "components": {
    "dashscope": {
      "status": "UP",
      "details": {
        "configured": true
      }
    },
    "database": {
      "status": "UP",
      "details": {
        "url": "jdbc:sqlite:./data/job_assistant.db",
        "type": "SQLite"
      }
    },
    "agent": {
      "status": "UP",
      "details": {
        "status": "Ready"
      }
    }
  }
}
```

### 3. 测试用户注册

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "test123456",
    "displayName": "Test User"
  }'
```

---

## 常见问题

### Q1: 启动时提示 "DashScope API Key is not configured"

**原因**：未配置通义千问 API Key

**解决**：
```bash
export DASHSCOPE_API_KEY="your-actual-api-key"
```

---

### Q2: 数据库文件创建失败

**原因**：数据目录权限不足

**解决**：
```bash
# 创建数据目录
mkdir -p ./data
chmod 755 ./data
```

---

### Q3: 端口 8080 被占用

**原因**：端口冲突

**解决**：
```bash
# 方式一：修改端口
export SERVER_PORT=9090

# 方式二：结束占用进程
lsof -ti:8080 | xargs kill -9  # macOS/Linux
netstat -ano | findstr :8080   # Windows
```

---

### Q4: Maven 依赖下载缓慢

**原因**：网络问题

**解决**：
```bash
# 配置阿里云镜像
# 在 ~/.m2/settings.xml 中添加
<mirror>
  <id>aliyun</id>
  <mirrorOf>central</mirrorOf>
  <name>Aliyun Maven</name>
  <url>https://maven.aliyun.com/repository/public</url>
</mirror>
```

---

### Q5: JWT Token 过期

**原因**：Token 默认有效期 24 小时

**解决**：
```bash
# 重新登录获取新 Token
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "your-username", "password": "your-password"}'
```

---

## 配置文件说明

### application.yaml

主配置文件，包含所有环境的公共配置：

- 数据库配置
- JWT 配置
- AI 模型配置
- CORS 配置
- 日志配置

### application-dev.yml

开发环境配置：

- 端口：8080
- 数据库：`./data/job_assistant_dev.db`
- 日志级别：DEBUG
- SQL 日志：开启

### application-prod.yml

生产环境配置：

- 端口：通过环境变量配置
- 数据库：通过环境变量配置
- 日志级别：INFO
- SQL 日志：关闭

---

## 生产环境部署

### 1. 使用 systemd 服务（Linux）

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
RestartSec=10
Environment="SPRING_PROFILES_ACTIVE=prod"
Environment="DASHSCOPE_API_KEY=your-api-key"
Environment="DB_PATH=/var/data/job_assistant.db"
Environment="JWT_SECRET=your-jwt-secret"

[Install]
WantedBy=multi-user.target
```

启动服务：
```bash
sudo systemctl daemon-reload
sudo systemctl start job-assistant
sudo systemctl enable job-assistant
```

### 2. 使用 Docker（可选）

创建 `Dockerfile`：

```dockerfile
FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY target/job-ai-assistant-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

构建和运行：
```bash
docker build -t job-assistant:1.0.0 .
docker run -d -p 8080:8080 \
  -e DASHSCOPE_API_KEY=your-key \
  -e SPRING_PROFILES_ACTIVE=prod \
  job-assistant:1.0.0
```

---

## 性能优化建议

### 1. JVM 参数优化

```bash
java -Xmx1g -Xms512m \
  -XX:+UseG1GC \
  -XX:MaxGCPauseMillis=200 \
  -XX:+HeapDumpOnOutOfMemoryError \
  -Dspring.profiles.active=prod \
  -jar job-ai-assistant-1.0.0.jar
```

### 2. 数据库优化

- 定期清理过期会话记录
- 考虑迁移到 PostgreSQL（高并发场景）

### 3. 连接池配置

在 `application.yaml` 中配置 HikariCP：

```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 10
      minimum-idle: 5
      connection-timeout: 30000
```

---

## 监控与日志

### 日志文件位置

- 开发环境：控制台输出
- 生产环境：`/var/log/job-assistant.log`

### 日志级别调整

在 `application-prod.yml` 中：

```yaml
logging:
  level:
    root: WARN
    com.ai.job: INFO
```

### Actuator 端点

- 健康检查：`/api/actuator/health`
- 信息：`/api/actuator/info`
- 指标：`/api/actuator/metrics`

---

## 备份与恢复

### 数据库备份

```bash
# 备份
cp ./data/job_assistant.db ./backup/job_assistant_$(date +%Y%m%d).db

# 恢复
cp ./backup/job_assistant_20240101.db ./data/job_assistant.db
```

### 配置备份

```bash
# 备份配置
tar -czf config_backup_$(date +%Y%m%d).tar.gz \
  application.yaml \
  application-dev.yml \
  application-prod.yml \
  .env
```

---

## 卸载

### 1. 停止服务

```bash
# 如果使用 systemd
sudo systemctl stop job-assistant

# 如果直接运行
kill -9 $(ps aux | grep job-ai-assistant | grep -v grep | awk '{print $2}')
```

### 2. 清理文件

```bash
# 删除应用目录
rm -rf /opt/job-assistant

# 删除服务配置
sudo rm /etc/systemd/system/job-assistant.service

# 删除数据（可选）
rm -rf ./data
```

---

## 技术支持

如有问题，请：

1. 查看日志文件
2. 检查配置文件
3. 参考 API 文档
4. 提交 Issue
