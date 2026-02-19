# 本地开发配置指南

## 快速开始

### 方式一：使用自动化脚本（推荐）

```bash
./scripts/setup-local.sh
```

脚本会自动：
- 检查是否已存在配置文件
- 复制示例配置文件
- 提示配置步骤

### 方式二：手动配置

1. 复制示例配置：
```bash
cp src/main/resources/application-local.yaml.example \
   src/main/resources/application-local.yaml
```

2. 编辑配置文件，填入 API Keys

3. 运行项目：
```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

---

## 配置项说明

### 必需配置

| 配置项 | 说明 | 获取地址 |
|--------|------|----------|
| `spring.ai.dashscope.api-key` | 通义千问 API Key | [阿里云百炼平台](https://bailian.console.aliyun.com/) |
| `spring.security.jwt.secret` | JWT 密钥（至少256位） | 自行生成 |

### 可选配置

- `app.ai.model`: AI 模型选择（qwen-turbo/qwen-plus/qwen-max）
- `app.ai.max-tokens`: 最大 Token 数
- `app.ai.temperature`: 温度参数（0.0-1.0）

---

## 完整配置模板

`application-local.yaml` 文件包含了所有当前和未来可能用到的 API Keys 配置：

- ✅ 通义千问 API
- ✅ JWT 安全配置
- 🔄 阿里云百炼（预留）
- 🔄 阿里云 OSS 存储（预留）
- 🔄 阿里云 SMS 短信（预留）
- 🔄 OpenAI / Azure OpenAI（预留）
- 🔄 Claude API（预留）
- 🔄 向量数据库（预留）
- 🔄 GitHub / Google OAuth（预留）
- 🔄 微信集成（预留）
- 🔄 支付服务（预留）
- 🔄 邮件服务（预留）
- 🔄 监控日志（预留）

---

## 运行项目

### Maven 命令行

```bash
# 激活 local profile
export SPRING_PROFILES_ACTIVE=local

# 运行项目
./mvnw spring-boot:run
```

### IntelliJ IDEA

1. 打开 Run/Debug Configurations
2. 找到 `AiJobSearchAssistantApplication`
3. 在 `Active profiles` 中填入：`local`
4. 点击运行

### VS Code

在 `launch.json` 中添加：
```json
{
  "type": "java",
  "name": "Spring Boot (local)",
  "request": "launch",
  "mainClass": "com.wyh.aijobsearchassistant.AiJobSearchAssistantApplication",
  "args": "-Dspring-boot.run.profiles=local"
}
```

---

## 环境对比

| 环境 | 配置文件 | 使用场景 |
|------|----------|----------|
| `dev` | application-dev.yml | 开发调试（默认） |
| `local` | application-local.yaml | 本地开发（含 API Keys） |
| `prod` | application-prod.yml | 生产环境（环境变量） |

---

## 安全提示

### ✅ 应该做的

- ✅ 将 `application-local.yaml` 添加到 `.gitignore`
- ✅ 为不同环境使用不同的 API Keys
- ✅ 定期轮换 API Keys
- ✅ 使用强随机密钥生成 JWT secret

### ❌ 不要做的

- ❌ 不要将 API Keys 提交到 Git
- ❌ 不要在代码中硬编码 API Keys
- ❌ 不要在生产环境使用 local 配置
- ❌ 不要与他人共享你的 API Keys

---

## 故障排除

### 启动失败：API Key 未配置

**错误信息：**
```
DashScope API Key is not configured
```

**解决方案：**
1. 检查是否激活了 `local` profile
2. 确认 `application-local.yaml` 中已填入正确的 API Key
3. 验证 API Key 格式（通常以 `sk-` 开头）

### 配置文件未生效

**检查步骤：**
```bash
# 1. 确认配置文件存在
ls -la src/main/resources/application-local.yaml

# 2. 检查激活的 profile
./mvnw spring-boot:run -Dspring-boot.run.profiles=local -X

# 3. 查看日志中的配置加载信息
grep "profile" logs/spring.log
```

---

## 更多信息

- 完整配置说明：查看 `application-local.yaml` 文件内的注释
- 项目设置指南：查看 `SETUP.md`
- API 文档：查看 `API.md`
