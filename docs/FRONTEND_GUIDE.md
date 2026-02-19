# 前端对接指南 - AI Job Search Assistant

> 本文档提供前端开发人员对接后端 API 的完整指南

## 📋 目录

- [环境准备](#环境准备)
- [API 基础信息](#api-基础信息)
- [认证机制](#认证机制)
- [接口文档](#接口文档)
- [数据格式规范](#数据格式规范)
- [错误码说明](#错误码说明)
- [分页规范](#分页规范)
- [测试示例](#测试示例)

---

## 环境准备

### 1. 启动后端服务

```bash
# 方式一：使用 local 配置（推荐）
./mvnw spring-boot:run -Dspring-boot.run.profiles=local

# 方式二：使用 dev 配置
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

### 2. 访问 API 文档

启动成功后，访问 Swagger UI：

```
http://localhost:8080/swagger-ui/index.html
```

---

## API 基础信息

### Base URL

```
开发环境: http://localhost:8080/api
生产环境: https://your-domain.com/api
```

### 请求头

所有需要认证的接口必须携带以下请求头：

```http
Authorization: Bearer {jwt_token}
Content-Type: application/json
```

### 响应格式

所有接口统一返回以下格式：

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {},
  "timestamp": 1234567890123
}
```

分页接口返回格式：

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "pageNum": 1,
    "pageSize": 10,
    "total": 100,
    "totalPages": 10,
    "records": [],
    "hasNext": true,
    "hasPrevious": false
  },
  "timestamp": 1234567890123
}
```

---

## 认证机制

### 获取 Token

**接口**: `POST /api/auth/login`

**请求示例**:

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "test123456"
  }'
```

**响应示例**:

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
  }
}
```

### 使用 Token

在后续所有需要认证的请求中，将 token 添加到请求头：

```javascript
// 示例：使用 fetch
fetch('http://localhost:8080/api/conversations', {
  headers: {
    'Authorization': `Bearer ${token}`,
    'Content-Type': 'application/json'
  }
})

// 示例：使用 axios
axios.get('/api/conversations', {
  headers: {
    'Authorization': `Bearer ${token}`
  }
})
```

---

## 接口文档

### 一、会话管理

#### 1. 查询会话列表

**接口**: `GET /api/conversations`

**说明**: 分页查询用户的所有会话，支持按名称关键词过滤

**请求参数**:

| 参数 | 类型 | 必填 | 说明 | 示例 |
|------|------|------|------|------|
| keyword | String | 否 | 会话名称关键词（模糊搜索） | Java |
| pageNum | Integer | 是 | 页码（从1开始） | 1 |
| pageSize | Integer | 是 | 每页大小（1-100） | 10 |

**请求示例**:

```bash
curl -X GET "http://localhost:8080/api/conversations?pageNum=1&pageSize=10&keyword=Java" \
  -H "Authorization: Bearer {token}"
```

**响应示例**:

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "pageNum": 1,
    "pageSize": 10,
    "total": 5,
    "totalPages": 1,
    "records": [
      {
        "conversationId": "uuid-123",
        "name": "Java面试准备",
        "messageCount": 12,
        "createdAt": "2025-01-15T10:30:00",
        "updatedAt": "2025-01-15T11:45:00",
        "status": "ACTIVE"
      }
    ],
    "hasNext": false,
    "hasPrevious": false
  }
}
```

#### 2. 创建新会话

**接口**: `POST /api/conversations`

**请求参数**:

| 参数 | 类型 | 必填 | 说明 | 示例 |
|------|------|------|------|------|
| name | String | 否 | 会话名称（默认"新对话"） | 简历优化 |

**请求示例**:

```bash
curl -X POST "http://localhost:8080/api/conversations?name=简历优化" \
  -H "Authorization: Bearer {token}"
```

#### 3. 重命名会话

**接口**: `PUT /api/conversations/rename`

**请求体**:

```json
{
  "conversationId": "uuid-123",
  "newName": "Java后端面试准备"
}
```

**请求示例**:

```bash
curl -X PUT http://localhost:8080/api/conversations/rename \
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/json" \
  -d '{
    "conversationId": "uuid-123",
    "newName": "Java后端面试准备"
  }'
```

#### 4. 删除会话

**接口**: `DELETE /api/conversations/{conversationId}`

**说明**: 删除指定会话及其所有聊天记录（级联删除）

**路径参数**:

| 参数 | 类型 | 说明 | 示例 |
|------|------|------|------|
| conversationId | String | 会话ID | uuid-123 |

**请求示例**:

```bash
curl -X DELETE "http://localhost:8080/api/conversations/uuid-123" \
  -H "Authorization: Bearer {token}"
```

#### 5. 清空会话

**接口**: `DELETE /api/conversations/{conversationId}/clear`

**说明**: 清空会话的所有聊天记录，但保留会话主体（"重置会话"功能）

**请求示例**:

```bash
curl -X DELETE "http://localhost:8080/api/conversations/uuid-123/clear" \
  -H "Authorization: Bearer {token}"
```

---

### 二、历史对话管理

#### 1. 查询会话历史消息

**接口**: `GET /api/messages/conversation/{conversationId}`

**说明**: 分页查询指定会话的所有聊天记录，按时间倒序（最新的在前）

**路径参数**:

| 参数 | 类型 | 说明 | 示例 |
|------|------|------|------|
| conversationId | String | 会话ID | uuid-123 |

**请求参数**:

| 参数 | 类型 | 必填 | 说明 | 示例 |
|------|------|------|------|------|
| pageNum | Integer | 是 | 页码（从1开始） | 1 |
| pageSize | Integer | 是 | 每页大小（1-100） | 20 |

**请求示例**:

```bash
curl -X GET "http://localhost:8080/api/messages/conversation/uuid-123?pageNum=1&pageSize=20" \
  -H "Authorization: Bearer {token}"
```

**响应示例**:

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "pageNum": 1,
    "pageSize": 20,
    "total": 12,
    "totalPages": 1,
    "records": [
      {
        "id": 1,
        "conversationId": "uuid-123",
        "role": "ASSISTANT",
        "content": "Java后端面试主要考察以下内容...",
        "intent": "interview_preparation",
        "skill": "interview_coach",
        "tokenCount": 150,
        "sendTime": "2025-01-15T11:45:00",
        "model": "qwen-plus"
      },
      {
        "id": 2,
        "conversationId": "uuid-123",
        "role": "USER",
        "content": "请帮我准备Java后端开发的面试",
        "intent": null,
        "skill": null,
        "tokenCount": null,
        "sendTime": "2025-01-15T11:44:30",
        "model": null
      }
    ],
    "hasNext": false,
    "hasPrevious": false
  }
}
```

#### 2. 删除单条消息

**接口**: `DELETE /api/messages/{messageId}`

**路径参数**:

| 参数 | 类型 | 说明 | 示例 |
|------|------|------|------|
| messageId | Long | 消息ID | 1 |

**请求示例**:

```bash
curl -X DELETE "http://localhost:8080/api/messages/1" \
  -H "Authorization: Bearer {token}"
```

#### 3. 编辑会话上下文

**接口**: `PUT /api/messages/context`

**说明**: 手动修改会话的上下文内容，修改后影响后续对话

**请求体**:

```json
{
  "conversationId": "uuid-123",
  "newContext": "你是一位专业的Java面试官，专注于后端开发相关问题"
}
```

**请求示例**:

```bash
curl -X PUT http://localhost:8080/api/messages/context \
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/json" \
  -d '{
    "conversationId": "uuid-123",
    "newContext": "你是一位专业的Java面试官"
  }'
```

#### 4. 获取会话上下文摘要

**接口**: `GET /api/messages/context/{conversationId}/summary`

**响应示例**:

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "conversationId": "uuid-123",
    "totalMessages": 12,
    "hasContext": true,
    "firstMessageTime": "2025-01-15T10:30:00",
    "lastMessageTime": "2025-01-15T11:45:00"
  }
}
```

---

### 三、用户信息管理

#### 1. 获取用户信息

**接口**: `GET /api/user/profile`

**响应示例**:

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "userId": 1,
    "username": "testuser",
    "email": "test@example.com",
    "displayName": "Test User",
    "role": "USER",
    "status": 1,
    "totalConversations": 5,
    "createdAt": "2025-01-01T10:00:00",
    "lastLoginAt": "2025-01-15T09:30:00"
  }
}
```

#### 2. 修改密码

**接口**: `POST /api/user/change-password`

**请求体**:

```json
{
  "oldPassword": "old123456",
  "newPassword": "new123456",
  "confirmPassword": "new123456"
}
```

**密码要求**:
- 长度：6-50 字符
- 必须包含：大写字母、小写字母、数字

**请求示例**:

```bash
curl -X POST http://localhost:8080/api/user/change-password \
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/json" \
  -d '{
    "oldPassword": "old123456",
    "newPassword": "New123456",
    "confirmPassword": "New123456"
  }'
```

#### 3. 修改昵称

**接口**: `PUT /api/user/nickname`

**请求参数**:

| 参数 | 类型 | 必填 | 说明 | 示例 |
|------|------|------|------|------|
| nickname | String | 是 | 新昵称 | 新昵称 |

**请求示例**:

```bash
curl -X PUT "http://localhost:8080/api/user/nickname?nickname=我的昵称" \
  -H "Authorization: Bearer {token}"
```

---

## 数据格式规范

### 日期时间格式

所有日期时间字段使用 **ISO 8601** 格式：

```json
{
  "createdAt": "2025-01-15T10:30:00",
  "updatedAt": "2025-01-15T11:45:00"
}
```

### 枚举值

#### 消息角色 (MessageRole)

| 值 | 说明 |
|------|------|
| ROLE_USER | 用户消息 |
| ROLE_ASSISTANT | AI助手消息 |
| ROLE_SYSTEM | 系统消息 |

#### 会话状态 (ConversationStatus)

| 值 | 说明 |
|------|------|
| ACTIVE | 活跃状态 |
| DELETED | 已删除 |
| ARCHIVED | 已归档 |

---

## 错误码说明

| HTTP Code | Business Code | 说明 | 处理建议 |
|-----------|---------------|------|----------|
| 200 | 200 | 成功 | - |
| 400 | 400 | 请求参数错误 | 检查请求参数格式和内容 |
| 401 | 401 | 未认证 | 重新登录获取新 Token |
| 403 | 403 | 无权限 | 检查是否有权限访问该资源 |
| 404 | 404 | 资源不存在 | 检查资源ID是否正确 |
| 500 | 500 | 服务器内部错误 | 联系后端开发人员 |
| 1001 | 1001 | 用户不存在 | - |
| 1002 | 1002 | 用户已存在 | - |
| 1003 | 1003 | 密码错误 | - |
| 1004 | 1004 | Token无效 | 重新登录 |

### 错误响应示例

```json
{
  "code": 400,
  "msg": "新密码与确认密码不一致",
  "data": null,
  "timestamp": 1234567890123
}
```

---

## 分页规范

### 分页参数

| 参数 | 类型 | 默认值 | 限制 | 说明 |
|------|------|--------|------|------|
| pageNum | Integer | 1 | ≥ 1 | 页码（从1开始） |
| pageSize | Integer | 10 | 1-100 | 每页大小 |
| sortField | String | createdAt | - | 排序字段 |
| sortDirection | String | DESC | ASC/DESC | 排序方向 |

### 分页响应

```json
{
  "pageNum": 1,
  "pageSize": 10,
  "total": 100,
  "totalPages": 10,
  "records": [],
  "hasNext": true,
  "hasPrevious": false
}
```

### 前端分页实现示例

```javascript
// 加载第一页数据
async function loadConversations(page = 1) {
  const response = await fetch(
    `/api/conversations?pageNum=${page}&pageSize=10`,
    {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    }
  );

  const result = await response.json();

  if (result.code === 200) {
    const { pageNum, total, totalPages, records } = result.data;

    // 更新列表
    this.conversationList = records;

    // 更新分页信息
    this.pagination = {
      current: pageNum,
      total: total,
      totalPages: totalPages
    };
  }
}

// 加载更多（滚动加载）
async function loadMore() {
  if (this.pagination.current >= this.pagination.totalPages) {
    return; // 已是最后一页
  }

  const nextPage = this.pagination.current + 1;
  await loadConversations(nextPage);
}
```

---

## 测试示例

### 完整的测试流程

```bash
# 1. 注册用户
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "Test123456",
    "displayName": "测试用户"
  }'

# 2. 登录获取 Token
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "Test123456"
  }' | jq -r '.data.token')

echo "Token: $TOKEN"

# 3. 创建会话
curl -X POST "http://localhost:8080/api/conversations?name=Java面试" \
  -H "Authorization: Bearer $TOKEN"

# 4. 查询会话列表
curl -X GET "http://localhost:8080/api/conversations?pageNum=1&pageSize=10" \
  -H "Authorization: Bearer $TOKEN"

# 5. 发送消息
curl -X POST http://localhost:8080/api/chat/agent \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "message": "请帮我准备Java后端开发的面试",
    "conversationId": "test-conv-001"
  }'

# 6. 查询历史消息
curl -X GET "http://localhost:8080/api/messages/conversation/test-conv-001?pageNum=1&pageSize=20" \
  -H "Authorization: Bearer $TOKEN"

# 7. 获取用户信息
curl -X GET http://localhost:8080/api/user/profile \
  -H "Authorization: Bearer $TOKEN"

# 8. 修改密码
curl -X POST http://localhost:8080/api/user/change-password \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "oldPassword": "Test123456",
    "newPassword": "New123456",
    "confirmPassword": "New123456"
  }'

# 9. 重命名会话
curl -X PUT http://localhost:8080/api/conversations/rename \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "conversationId": "test-conv-001",
    "newName": "Java面试准备"
  }'

# 10. 删除会话
curl -X DELETE "http://localhost:8080/api/conversations/test-conv-001" \
  -H "Authorization: Bearer $TOKEN"
```

---

## 常见问题

### Q1: Token 过期怎么办？

**A**: Token 有效期为 24 小时，过期后需要重新登录获取新 Token。

**前端实现建议**:

```javascript
// 拦截器处理 401 错误
axios.interceptors.response.use(
  response => response,
  error => {
    if (error.response?.status === 401) {
      // 清除本地 Token
      localStorage.removeItem('token');

      // 跳转到登录页
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);
```

### Q2: 如何实现"加载更多"？

**A**: 使用分页接口，每次加载一页数据，前端追加到列表中。

```javascript
async function loadMoreMessages() {
  const nextPage = this.currentPage + 1;

  const response = await fetch(
    `/api/messages/conversation/${conversationId}?pageNum=${nextPage}&pageSize=20`,
    { headers: { 'Authorization': `Bearer ${token}` }}
  );

  const result = await response.json();

  // 追加到现有列表
  this.messages = [...this.messages, ...result.data.records];
  this.currentPage = nextPage;
}
```

### Q3: 如何实时更新会话列表？

**A**: 建议使用轮询或 WebSocket（待后端实现）。

```javascript
// 轮询实现（每 5 秒更新一次）
setInterval(async () => {
  const result = await fetchConversations();
  this.conversationList = result.data.records;
}, 5000);
```

---

## 附录

### A. 完整的 API 列表

| 分类 | 接口 | 方法 | 说明 |
|------|------|------|------|
| **会话管理** | `/conversations` | GET | 查询会话列表 |
| | `/conversations` | POST | 创建新会话 |
| | `/conversations/{conversationId}` | GET | 获取会话详情 |
| | `/conversations/rename` | PUT | 重命名会话 |
| | `/conversations/{conversationId}` | DELETE | 删除会话 |
| | `/conversations/{conversationId}/clear` | DELETE | 清空会话 |
| **历史对话** | `/messages/conversation/{conversationId}` | GET | 查询历史消息 |
| | `/messages/{messageId}` | DELETE | 删除单条消息 |
| | `/messages/context` | PUT | 编辑上下文 |
| | `/messages/context/{conversationId}/summary` | GET | 获取上下文摘要 |
| **用户信息** | `/user/profile` | GET | 获取用户信息 |
| | `/user/change-password` | POST | 修改密码 |
| | `/user/nickname` | PUT | 修改昵称 |
| | `/user/avatar` | PUT | 修改头像 |

### B. 前端状态管理建议

```typescript
interface AppState {
  // 用户信息
  user: {
    id: number;
    username: string;
    displayName: string;
    token: string;
  };

  // 会话列表
  conversations: {
    list: Conversation[];
    pagination: {
      current: number;
      total: number;
      totalPages: number;
    };
  };

  // 当前会话
  currentConversation: {
    info: Conversation | null;
    messages: Message[];
    pagination: {
      current: number;
      total: number;
      totalPages: number;
    };
  };
}
```

---

**文档版本**: v1.0.0
**最后更新**: 2025-01-15
**维护者**: AI Job Search Assistant Team
