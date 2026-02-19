# 前端交互功能集成说明

本文档说明如何将新增的前端交互功能集成到现有项目中。

---

## 📦 一、新增依赖

### 1. Maven 依赖（已添加到 pom.xml）

```xml
<!-- SpringDoc OpenAPI (Swagger UI) -->
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.3.0</version>
</dependency>
```

### 2. 重新编译项目

```bash
# 清理并重新编译
./mvnw clean compile

# 如果遇到依赖下载问题，使用阿里云镜像
# 在 ~/.m2/settings.xml 中添加阿里云镜像
```

---

## 📁 二、新增文件清单

### 1. 配置类

| 文件路径 | 说明 |
|----------|------|
| `config/OpenApiConfig.java` | Swagger API 文档配置 |

### 2. 实体类

| 文件路径 | 说明 |
|----------|------|
| `entity/Conversation.java` | 会话实体（新增） |

### 3. 枚举类

| 文件路径 | 说明 |
|----------|------|
| `enums/MessageRole.java` | 消息角色枚举 |
| `enums/ConversationStatus.java` | 会话状态枚举 |

### 4. DTO 类

| 文件路径 | 说明 |
|----------|------|
| `dto/PageRequestDTO.java` | 分页请求 DTO |
| `dto/PageResponseDTO.java` | 分页响应 DTO |
| `dto/ConversationDTO.java` | 会话 DTO |
| `dto/ConversationRenameRequest.java` | 会话重命名请求 |
| `dto/MessageDTO.java` | 消息 DTO |
| `dto/UserProfileDTO.java` | 用户信息 DTO |
| `dto/ChangePasswordRequest.java` | 修改密码请求 |
| `dto/EditContextRequest.java` | 编辑上下文请求 |

### 5. Service 类

| 文件路径 | 说明 |
|----------|------|
| `service/ConversationService.java` | 会话管理服务 |
| `service/MessageService.java` | 历史对话管理服务 |
| `service/UserInfoService.java` | 用户信息管理服务 |

### 6. Controller 类

| 文件路径 | 说明 |
|----------|------|
| `controller/ConversationController.java` | 会话管理接口 |
| `controller/MessageController.java` | 历史对话接口 |
| `controller/UserInfoController.java` | 用户信息接口 |

### 7. Repository 类

| 文件路径 | 说明 |
|----------|------|
| `repository/ConversationRepository.java` | 会话数据访问层（新增） |

### 8. 文档

| 文件路径 | 说明 |
|----------|------|
| `FRONTEND_GUIDE.md` | 前端对接指南 |
| `INTEGRATION_GUIDE.md` | 本文档 |

---

## 🔧 三、数据库变更

### 1. 新增表：conversation

```sql
CREATE TABLE conversation (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    conversation_id VARCHAR(100) NOT NULL UNIQUE,
    user_id INTEGER NOT NULL,
    name VARCHAR(200),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    message_count INTEGER DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE INDEX idx_user_id ON conversation(user_id);
CREATE INDEX idx_status ON conversation(status);
CREATE INDEX idx_created_at ON conversation(created_at);
CREATE INDEX idx_updated_at ON conversation(updated_at);
```

### 2. 现有表无需修改

- `sys_user` 表无需修改
- `chat_history` 表无需修改（已有所有必需字段）

### 3. 数据库自动创建

启动项目时，Hibernate 会自动创建 `conversation` 表，无需手动执行 SQL。

---

## 🚀 四、启动项目

### 1. 配置 API Key

```bash
# 运行配置脚本
./scripts/setup-local.sh

# 编辑生成的配置文件
vim src/main/resources/application-local.yaml

# 填入 API Key 和 JWT Secret
spring:
  ai:
    dashscope:
      api-key: sk-your-api-key-here
  security:
    jwt:
      secret: your-jwt-secret-at-least-256-bits
```

### 2. 启动项目

```bash
# 使用 local 配置
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

### 3. 访问 Swagger 文档

```
http://localhost:8080/swagger-ui/index.html
```

---

## 🧪 五、测试接口

### 1. 注册并登录

```bash
# 注册
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "Test123456",
    "displayName": "测试用户"
  }'

# 登录获取 Token
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "Test123456"
  }' | jq -r '.data.token')

echo "Token: $TOKEN"
```

### 2. 测试会话管理

```bash
# 创建会话
curl -X POST "http://localhost:8080/api/conversations?name=Java面试准备" \
  -H "Authorization: Bearer $TOKEN"

# 查询会话列表
curl -X GET "http://localhost:8080/api/conversations?pageNum=1&pageSize=10" \
  -H "Authorization: Bearer $TOKEN"

# 重命名会话（替换 CONVERSATION_ID）
curl -X PUT http://localhost:8080/api/conversations/rename \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "conversationId": "CONVERSATION_ID",
    "newName": "新会话名称"
  }'

# 清空会话（替换 CONVERSATION_ID）
curl -X DELETE "http://localhost:8080/api/conversations/CONVERSATION_ID/clear" \
  -H "Authorization: Bearer $TOKEN"

# 删除会话（替换 CONVERSATION_ID）
curl -X DELETE "http://localhost:8080/api/conversations/CONVERSATION_ID" \
  -H "Authorization: Bearer $TOKEN"
```

### 3. 测试历史对话

```bash
# 查询历史消息（替换 CONVERSATION_ID）
curl -X GET "http://localhost:8080/api/messages/conversation/CONVERSATION_ID?pageNum=1&pageSize=20" \
  -H "Authorization: Bearer $TOKEN"

# 删除单条消息（替换 MESSAGE_ID）
curl -X DELETE "http://localhost:8080/api/messages/MESSAGE_ID" \
  -H "Authorization: Bearer $TOKEN"

# 编辑上下文（替换 CONVERSATION_ID）
curl -X PUT http://localhost:8080/api/messages/context \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "conversationId": "CONVERSATION_ID",
    "newContext": "你是一位专业的Java面试官"
  }'
```

### 4. 测试用户信息

```bash
# 获取用户信息
curl -X GET http://localhost:8080/api/user/profile \
  -H "Authorization: Bearer $TOKEN"

# 修改密码
curl -X POST http://localhost:8080/api/user/change-password \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "oldPassword": "Test123456",
    "newPassword": "New123456",
    "confirmPassword": "New123456"
  }'

# 修改昵称
curl -X PUT "http://localhost:8080/api/user/nickname?nickname=新昵称" \
  -H "Authorization: Bearer $TOKEN"
```

---

## ⚠️ 六、注意事项

### 1. 权限验证

所有接口都实现了用户权限验证：
- 会话操作只能操作属于当前用户的会话
- 消息删除只能删除属于当前用户的消息
- 跨用户操作会返回 403 错误

### 2. 分页参数

- `pageNum` 从 1 开始（不是 0）
- `pageSize` 默认 10，最大 100
- 超过限制会返回 400 错误

### 3. 密码复杂度

修改密码时的验证规则：
- 长度：6-50 字符
- 必须包含：大写字母、小写字母、数字
- 新密码不能与原密码相同

### 4. 会话名称

- 会话名称长度：1-200 字符
- 创建会话时如不指定名称，默认为 "新对话"
- 可以后续通过重命名接口修改

### 5. 上下文编辑

- 编辑上下文后，后续对话会基于新的上下文生成
- 建议谨慎使用，可能影响 AI 的回复质量

---

## 🔍 七、故障排查

### 1. Swagger 页面无法访问

**检查**:
- 确认项目已启动
- 确认 SecurityConfig 中已添加 Swagger 路径白名单
- 访问：`http://localhost:8080/swagger-ui/index.html`

### 2. 会话列表返回空

**检查**:
- 确认用户已登录并携带有效 Token
- 确认数据库中有 conversation 表数据
- 检查用户 ID 是否正确

### 3. 分页查询报错

**检查**:
- 确认 `pageNum` ≥ 1
- 确认 `pageSize` 在 1-100 之间
- 检查请求参数格式是否正确

### 4. 权限验证失败

**检查**:
- 确认 Token 是否有效（未过期）
- 确认 Token 格式正确（`Bearer {token}`）
- 确认资源属于当前用户

---

## 📊 八、性能优化建议

### 1. 分页查询

- 前端默认使用 `pageSize=20`
- 避免一次加载过多数据
- 使用"加载更多"而非一次性加载所有数据

### 2. 会话列表

- 会话列表按 `updatedAt` 倒序排列
- 建议前端缓存会话列表，减少请求频率
- 可使用轮询（每 5-10 秒）或 WebSocket（待实现）

### 3. 消息列表

- 消息列表按时间倒序返回（最新的在前）
- 前端加载更多时应追加到列表底部
- 建议使用虚拟滚动优化长列表性能

---

## 🎯 九、功能扩展建议

### 1. WebSocket 实时通信

当前使用轮询更新，建议后续实现 WebSocket：

```java
// TODO: 实现 WebSocket 支持
@ServerEndpoint("/ws/chat/{userId}")
public class ChatWebSocket {
    // WebSocket 处理逻辑
}
```

### 2. 消息搜索

在 MessageService 中添加搜索方法：

```java
public PageResponseDTO<MessageDTO> searchMessages(
    Long userId,
    String conversationId,
    String keyword,
    PageRequestDTO pageRequestDTO) {
    // 实现全文搜索
}
```

### 3. 会话归档

实现会话归档功能：

```java
@Transactional
public void archiveConversation(Long userId, String conversationId) {
    // 将会话状态改为 ARCHIVED
}
```

### 4. 批量操作

添加批量删除消息、批量归档会话等接口。

---

## 📝 十、更新日志

### v1.0.0 (2025-01-15)

**新增功能**:
- ✅ 会话管理（列表、创建、重命名、删除、清空）
- ✅ 历史对话管理（分页查询、删除、上下文编辑）
- ✅ 用户信息管理（查询、修改密码、修改昵称）
- ✅ Swagger API 文档
- ✅ 统一分页响应格式
- ✅ 枚举值规范化

**技术改进**:
- 新增 `Conversation` 实体和 `conversation` 表
- 新增 3 个 Service、3 个 Controller
- 新增 8 个 DTO 类
- 新增 2 个枚举类
- 完善权限验证和异常处理

---

## 📞 十一、技术支持

如有问题，请：
1. 查看 `FRONTEND_GUIDE.md` 前端对接指南
2. 查看 Swagger API 文档：`http://localhost:8080/swagger-ui/index.html`
3. 检查日志文件：`logs/spring.log`
4. 提交 Issue 到 GitHub

---

**文档版本**: v1.0.0
**最后更新**: 2025-01-15
**维护者**: AI Job Search Assistant Team
