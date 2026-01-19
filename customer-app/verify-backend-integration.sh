#!/bin/bash

# Customer App 后端集成验证脚本
# 此脚本用于验证 customer-app 是否完全依赖后端 API

echo "🔍 Customer App 后端集成验证"
echo "================================"
echo ""

# 颜色定义
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 检查项计数
TOTAL_CHECKS=0
PASSED_CHECKS=0
FAILED_CHECKS=0

# 检查函数
check_item() {
    TOTAL_CHECKS=$((TOTAL_CHECKS + 1))
    if [ $1 -eq 0 ]; then
        echo -e "${GREEN}✓${NC} $2"
        PASSED_CHECKS=$((PASSED_CHECKS + 1))
    else
        echo -e "${RED}✗${NC} $2"
        FAILED_CHECKS=$((FAILED_CHECKS + 1))
    fi
}

# 1. 检查是否存在 mock 数据
echo "1️⃣  检查 mock 数据..."
echo "----------------------------"

# 检查 src 目录下是否有 mock 相关文件(排除测试文件)
MOCK_FILES=$(find src -type f \( -name "*.ts" -o -name "*.vue" \) ! -path "*/node_modules/*" ! -path "*/__tests__/*" ! -path "*/test/*" -exec grep -l "mock" {} \; 2>/dev/null | wc -l)
check_item $([ $MOCK_FILES -eq 0 ] && echo 0 || echo 1) "无 mock 数据文件 (排除测试)"

# 检查是否有 dummy 数据
DUMMY_FILES=$(find src -type f \( -name "*.ts" -o -name "*.vue" \) ! -path "*/node_modules/*" ! -path "*/__tests__/*" -exec grep -l "dummy" {} \; 2>/dev/null | wc -l)
check_item $([ $DUMMY_FILES -eq 0 ] && echo 0 || echo 1) "无 dummy 数据"

echo ""

# 2. 检查 Store 是否调用 API
echo "2️⃣  检查 Store API 调用..."
echo "----------------------------"

# 检查 authStore
grep -q "authAPI\." src/store/authStore.ts
check_item $? "authStore 调用 authAPI"

# 检查 productStore
grep -q "productAPI\." src/store/productStore.ts
check_item $? "productStore 调用 productAPI"

# 检查 storeStore
grep -q "storeAPI\." src/store/storeStore.ts
check_item $? "storeStore 调用 storeAPI"

# 检查 orderStore
grep -q "orderAPI\." src/store/orderStore.ts
check_item $? "orderStore 调用 orderAPI"

# 检查 recommendationStore
grep -q "recommendAPI\." src/store/recommendationStore.ts
check_item $? "recommendationStore 调用 recommendAPI"

echo ""

# 3. 检查 API 服务定义
echo "3️⃣  检查 API 服务定义..."
echo "----------------------------"

# 检查是否定义了所有必要的 API
grep -q "export const authAPI" src/services/api.ts
check_item $? "authAPI 已定义"

grep -q "export const productAPI" src/services/api.ts
check_item $? "productAPI 已定义"

grep -q "export const storeAPI" src/services/api.ts
check_item $? "storeAPI 已定义"

grep -q "export const orderAPI" src/services/api.ts
check_item $? "orderAPI 已定义"

grep -q "export const recommendAPI" src/services/api.ts
check_item $? "recommendAPI 已定义"

echo ""

# 4. 检查 API Base URL 配置
echo "4️⃣  检查 API 配置..."
echo "----------------------------"

# 检查是否使用环境变量配置 API Base URL
grep -q "VITE_API_BASE_URL" src/services/api.ts
check_item $? "使用环境变量配置 API Base URL"

# 检查 .env 文件
if [ -f .env ]; then
    grep -q "VITE_API_BASE_URL" .env
    check_item $? ".env 文件包含 API Base URL"
else
    check_item 1 ".env 文件存在"
fi

echo ""

# 5. 检查是否有硬编码的数据
echo "5️⃣  检查硬编码数据..."
echo "----------------------------"

# 检查 Store 中是否有硬编码的数组数据(排除空数组初始化)
HARDCODED_ARRAYS=$(grep -r "= \[{" src/store/*.ts | grep -v "items: \[\]" | grep -v "state.*\[\]" | wc -l)
check_item $([ $HARDCODED_ARRAYS -eq 0 ] && echo 0 || echo 1) "Store 中无硬编码数组数据"

echo ""

# 6. 检查错误处理
echo "6️⃣  检查错误处理..."
echo "----------------------------"

# 检查是否有 try-catch
grep -q "try {" src/store/authStore.ts
check_item $? "authStore 有错误处理"

grep -q "try {" src/store/productStore.ts
check_item $? "productStore 有错误处理"

grep -q "try {" src/store/storeStore.ts
check_item $? "storeStore 有错误处理"

echo ""

# 7. 总结
echo "================================"
echo "📊 验证总结"
echo "================================"
echo -e "总检查项: ${TOTAL_CHECKS}"
echo -e "${GREEN}通过: ${PASSED_CHECKS}${NC}"
echo -e "${RED}失败: ${FAILED_CHECKS}${NC}"
echo ""

if [ $FAILED_CHECKS -eq 0 ]; then
    echo -e "${GREEN}🎉 恭喜!Customer App 已完全集成后端 API!${NC}"
    exit 0
else
    echo -e "${YELLOW}⚠️  发现 ${FAILED_CHECKS} 个问题,请检查上述失败项${NC}"
    exit 1
fi
