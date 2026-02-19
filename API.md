# API 接口文档

## 基础信息

- **Base URL**: `http://localhost:8080/api`
- **认证方式**: JWT Bearer Token
- **Content-Type**: `application/json`

## 统一响应格式

所有接口返回统一的 JSON 格式：

```json
{
  "code": 200,
  "msg": "Operation successful",
  "data": {},
  "timestamp": 1234567890123
}
```

---

## 认证接口

### 1. 用户注册

**请求**:
```http
POST /api/auth/register
Content-Type: application/json
```

**请求体**:
```json
{
  "username": "testuser",
  "password": "password123",
  "email": "test@example.com",
  "displayName": "Test User"
}
```

| 参数 | 类型 | 必需 | 说明 |
|------|------|------|------|
| username | String | 是 | 用户名（3-50字符） |
| password | String | 是 | 密码（6-100字符） |
| email | String | 否 | 邮箱地址 |
| displayName | String | 否 | 显示名称 |

**响应**:
```json
{
  "code": 200,
  "msg": "Registration successful",
  "data": {
    "id": 1,
    "username": "testuser",
    "email": "test@example.com",
    "displayName": "Test User",
    "role": "USER",
    "status": 1,
    "createdAt": "2024-01-01T00:00:00"
  },
  "timestamp": 1234567890123
}
```

---

### 2. 用户登录

**请求**:
```http
POST /api/auth/login
Content-Type: application/json
```

**请求体**:
```json
{
  "username": "testuser",
  "password": "password123"
}
```

**响应**:
```json
{
  "code": 200,
  "msg": "Login successful",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "tokenType": "Bearer",
    "userId": 1,
    "username": "testuser",
    "displayName": "Test User",
    "role": "USER"
  },
  "timestamp": 1234567890123
}
```

**Token 使用**:
在后续请求的 Header 中添加：
```http
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

---

### 3. 用户登出

**请求**:
```http
POST /api/auth/logout
Authorization: Bearer YOUR_TOKEN
```

**响应**:
```json
{
  "code": 200,
  "msg": "Logout successful",
  "data": null,
  "timestamp": 1234567890123
}
```

---

### 4. 获取当前用户信息

**请求**:
```http
GET /api/auth/me
Authorization: Bearer YOUR_TOKEN
```

**响应**:
```json
{
  "code": 200,
  "msg": "Operation successful",
  "data": {
    "id": 1,
    "username": "testuser",
    "email": "test@example.com",
    "displayName": "Test User",
    "role": "USER"
  },
  "timestamp": 1234567890123
}
```

---

## 聊天接口

### 1. LLM 聊天

**请求**:
```http
POST /api/chat/send
Authorization: Bearer YOUR_TOKEN
Content-Type: application/json
```

**请求体**:
```json
{
  "message": "请帮我优化简历",
  "conversationId": "",
  "stream": false
}
```

| 参数 | 类型 | 必需 | 说明 |
|------|------|------|------|
| message | String | 是 | 消息内容（最多2000字符） |
| conversationId | String | 否 | 会话ID，首次为空 |
| stream | Boolean | 否 | 是否流式响应 |

**响应**:
```json
{
  "code": 200,
  "msg": "Operation successful",
  "data": {
    "content": "以下是简历优化的建议...",
    "conversationId": "123e4567-e89b-12d3-a456-426614174000",
    "intent": null,
    "skill": null,
    "model": "qwen-plus",
    "totalTokens": null
  },
  "timestamp": 1234567890123
}
```

---

### 2. Agent 交互

**请求**:
```http
POST /api/chat/agent
Authorization: Bearer YOUR_TOKEN
Content-Type: application/json
```

**请求体**:
```json
{
  "message": "我想准备前端开发岗位的面试",
  "conversationId": "",
  "intent": "",
  "skill": "",
  "stream": false
}
```

| 参数 | 类型 | 必需 | 说明 |
|------|------|------|------|
| message | String | 是 | 消息内容 |
| conversationId | String | 否 | 会话ID |
| intent | String | 否 | 指定意图（不指定则自动识别） |
| skill | String | 否 | 指定技能（不指定则自动匹配） |
| stream | Boolean | 否 | 是否流式响应 |

**响应**:
```json
{
  "code": 200,
  "msg": "Operation successful",
  "data": {
    "content": "作为前端开发面试准备，建议从以下几个方面入手...",
    "conversationId": "123e4567-e89b-12d3-a456-426614174000",
    "intent": "interview_preparation",
    "skill": "interview_coach",
    "toolResults": null,
    "model": "qwen-plus",
    "executionTime": 1234,
    "totalTokens": null
  },
  "timestamp": 1234567890123
}
```

**支持的意图类型**:
- `resume_optimization` - 简历优化
- `interview_preparation` - 面试准备
- `job_search` - 求职搜索
- `salary_negotiation` - 薪资谈判
- `career_planning` - 职业规划
- `skill_assessment` - 技能评估
- `industry_consultation` - 行业咨询

---

### 3. 清空对话上下文

**请求**:
```http
DELETE /api/chat/context/{conversationId}
Authorization: Bearer YOUR_TOKEN
```

**路径参数**:
| 参数 | 类型 | 说明 |
|------|------|------|
| conversationId | String | 会话ID |

**响应**:
```json
{
  "code": 200,
  "msg": "Context cleared successfully",
  "data": null,
  "timestamp": 1234567890123
}
```

---

### 4. 获取对话历史

**请求**:
```http
GET /api/chat/history/{conversationId}
Authorization: Bearer YOUR_TOKEN
```

**路径参数**:
| 参数 | 类型 | 说明 |
|------|------|------|
| conversationId | String | 会话ID |

**响应**:
```json
{
  "code": 200,
  "msg": "Operation successful",
  "data": [
    {
      "id": 1,
      "userId": 1,
      "conversationId": "123e4567-e89b-12d3-a456-426614174000",
      "role": "USER",
      "content": "请帮我优化简历",
      "intent": "resume_optimization",
      "skill": "resume_optimizer",
      "tokenCount": null,
      "model": "qwen-plus",
      "createdAt": "2024-01-01T00:00:00"
    },
    {
      "id": 2,
      "userId": 1,
      "conversationId": "123e4567-e89b-12d3-a456-426614174000",
      "role": "ASSISTANT",
      "content": "以下是简历优化的建议...",
      "intent": "resume_optimization",
      "skill": "resume_optimizer",
      "tokenCount": null,
      "model": "qwen-plus",
      "createdAt": "2024-01-01T00:00:01"
    }
  ],
  "timestamp": 1234567890123
}
```

---

### 5. 获取所有会话

**请求**:
```http
GET /api/chat/conversations
Authorization: Bearer YOUR_TOKEN
```

**响应**:
```json
{
  "code": 200,
  "msg": "Operation successful",
  "data": [
    {
      "id": 2,
      "userId": 1,
      "conversationId": "123e4567-e89b-12d3-a456-426614174000",
      "role": "ASSISTANT",
      "content": "以下是简历优化的建议...",
      "intent": "resume_optimization",
      "skill": "resume_optimizer",
      "tokenCount": null,
      "model": "qwen-plus",
      "createdAt": "2024-01-01T00:00:01"
    }
  ],
  "timestamp": 1234567890123
}
```

---

## 健康检查

### 1. 系统健康状态

**请求**:
```http
GET /api/health/status
```

**响应**:
```json
{
  "status": "UP",
  "version": "1.0.0",
  "components": {
    "dashscope": {
      "status": "UP",
      "details": {
        "configured": true,
        "timestamp": "2024-01-01T00:00:00"
      }
    },
    "database": {
      "status": "UP",
      "details": {
        "url": "jdbc:sqlite:./data/job_assistant.db",
        "type": "SQLite",
        "timestamp": "2024-01-01T00:00:00"
      }
    },
    "agent": {
      "status": "UP",
      "details": {
        "status": "Ready",
        "timestamp": "2024-01-01T00:00:00"
      }
    }
  }
}
```

---

### 2. Actuator 健康检查

**请求**:
```http
GET /api/actuator/health
```

**响应**:
```json
{
  "status": "UP"
}
```

---

## 预留接口（增强版）

以下接口在增强版中实现，当前返回 `UnsupportedOperationException`：

### Skill 管理

```http
POST /api/reserve/skill
```

### Tool 调用

```http
POST /api/reserve/tool
```

### MCP 编排

```http
POST /api/reserve/mcp
```

### PGVector 向量存储

```http
POST /api/reserve/vector
```

### 高级分析

```http
GET /api/reserve/analytics
```

---

## 错误码说明

| 错误码 | 说明 | 示例 |
|--------|------|------|
| 200 | 成功 | 请求成功 |
| 400 | 请求参数错误 | 用户名长度不符合要求 |
| 401 | 未认证 | Token 无效或过期 |
| 403 | 无权限 | 没有访问权限 |
| 404 | 资源不存在 | 用户不存在 |
| 500 | 服务器内部错误 | 系统异常 |
| 1001 | 用户不存在 | 登录时用户不存在 |
| 1002 | 用户已存在 | 注册时用户名重复 |
| 1003 | 密码错误 | 登录密码错误 |
| 1004 | Token 无效 | Token 解析失败 |
| 2001 | AI 模型错误 | LLM 调用失败 |
| 2002 | Agent 执行错误 | Agent 执行异常 |
| 2003 | 上下文不存在 | 会话不存在 |
| 3001 | 配置错误 | API Key 未配置 |

---

## 使用示例

### 完整的对话流程

```bash
# 1. 注册用户
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "demo",
    "password": "demo123456",
    "displayName": "Demo User"
  }'

# 2. 登录获取 Token
TOKEN=$(curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "demo",
    "password": "demo123456"
  }' | jq -r '.data.token')

# 3. 发起 Agent 对话
CONV_ID=$(curl -X POST http://localhost:8080/api/chat/agent \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "message": "请帮我准备Java后端开发的面试"
  }' | jq -r '.data.conversationId')

# 4. 继续对话（多轮）
curl -X POST http://localhost:8080/api/chat/agent \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d "{
    \"message\": \"针对Spring框架有什么建议\",
    \"conversationId\": \"$CONV_ID\"
  }"

# 5. 查看对话历史
curl http://localhost:8080/api/chat/history/$CONV_ID \
  -H "Authorization: Bearer $TOKEN"

# 6. 清空上下文
curl -X DELETE http://localhost:8080/api/chat/context/$CONV_ID \
  -H "Authorization: Bearer $TOKEN"
```
