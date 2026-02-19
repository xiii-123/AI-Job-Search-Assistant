#!/bin/bash

# ============================================================================
# 本地开发配置快速设置脚本
# ============================================================================
# 使用方法：./scripts/setup-local.sh
# ============================================================================

set -e

PROJECT_ROOT="$(cd "$(dirname "$0")/.." && pwd)"
RESOURCES_DIR="$PROJECT_ROOT/src/main/resources"
LOCAL_CONFIG="$RESOURCES_DIR/application-local.yaml"
EXAMPLE_CONFIG="$RESOURCES_DIR/application-local.yaml.example"

echo "============================================"
echo "  本地开发配置快速设置"
echo "============================================"
echo ""

# 检查 local 配置是否已存在
if [ -f "$LOCAL_CONFIG" ]; then
    echo "⚠️  检测到配置文件已存在"
    echo "📁 文件位置: $LOCAL_CONFIG"
    echo ""
    read -p "是否要覆盖现有配置？: " -n 1 -r
    echo
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        echo "❌ 取消配置"
        exit 0
    fi
    BACKUP="$LOCAL_CONFIG.backup.$(date +%Y%m%d_%H%M%S)"
    cp "$LOCAL_CONFIG" "$BACKUP"
    echo "✅ 已备份现有配置到: $BACKUP"
fi

# 复制示例配置
if [ -f "$EXAMPLE_CONFIG" ]; then
    cp "$EXAMPLE_CONFIG" "$LOCAL_CONFIG"
    echo "✅ 已创建配置文件"
else
    echo "❌ 错误：找不到示例配置文件"
    exit 1
fi

echo ""
echo "============================================"
echo "  下一步操作"
echo "============================================"
echo ""
echo "1. 编辑配置文件，填入 API Keys："
echo "   vim $LOCAL_CONFIG"
echo ""
echo "2. 主要配置项："
echo "   - spring.ai.dashscope.api-key: 通义千问 API Key"
echo "   - spring.security.jwt.secret: JWT 密钥"
echo ""
echo "3. 运行项目："
echo "   ./mvnw spring-boot:run -Dspring-boot.run.profiles=local"
echo ""
echo "============================================"
