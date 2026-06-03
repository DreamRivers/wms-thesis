#!/usr/bin/env bash
# WMS 电商仓储物资管理系统 - 一键启动 (Git Bash / Linux / macOS)

set -e
echo "========================================"
echo "  WMS 电商仓储物资管理系统 一键启动"
echo "========================================"

# 检查 mysql
command -v mysql >/dev/null 2>&1 || {
    echo "[警告] MySQL 未安装或未配置 PATH"
    echo "请先启动 MySQL 并导入 wms-db/01~03_*.sql"
}

# 检查 redis
command -v redis-server >/dev/null 2>&1 || {
    echo "[警告] Redis 未安装"
    echo "请启动 Redis:redis-server"
}

# 启动后端
echo "[3/4] 启动后端服务..."
(cd wms-backend && mvn spring-boot:run &)

sleep 10

# 启动前端
echo "[4/4] 启动前端服务..."
(cd wms-frontend && npm run dev &)

sleep 5
echo
echo "========================================"
echo "  启动完成!"
echo "  前端:http://localhost:5173"
echo "  后端:http://localhost:8080"
echo "  接口文档:http://localhost:8080/doc.html"
echo "  默认账号:admin / 123456"
echo "========================================"

wait
