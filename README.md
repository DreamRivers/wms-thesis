# 📦 WMS 电商仓储物资管理系统

> 毕业设计 · 基于 Spring Boot 2.7.18 + Vue 3.4 + MyBatis-Plus + Sa-Token + MySQL 8.0 + Redis 7

![Java](https://img.shields.io/badge/Java-1.8-orange) ![Spring Boot](https://img.shields.io/badge/Spring%20Boot-2.7.18-brightgreen) ![Vue](https://img.shields.io/badge/Vue-3.4-42b883) ![MySQL](https://img.shields.io/badge/MySQL-8.0-blue)

## 🌟 项目简介

一套面向中小型电商企业的仓储物资管理系统(WMS),涵盖商品/库位/批次/入库/出库/盘点/预警等核心业务,支持 4 类角色协同工作,具备完整的状态机、库存并发安全、定时预警与数据可视化能力。

## ✨ 核心特性

- 🛡️ **RBAC 权限**:4 类角色(管理员/仓管/部门负责人/员工)精细化权限
- 🔄 **状态机**:入库 7 状态、出库 8 状态,Service 层统一校验
- 🔒 **并发安全**:`SELECT ... FOR UPDATE` 行级锁 + 事务保证库存原子性
- ✅ **两级审批**:出库单须经部门负责人 + 仓管双重审核
- ⚠️ **智能预警**:`@Scheduled` 定时任务扫描低库存与临期商品
- 📊 **数据可视化**:ECharts 5 仪表盘 + 4 张业务报表
- 📝 **操作日志**:自定义 `@Log` 注解 + AOP 自动记录
- 🔌 **接口文档**:Knife4j 自动生成,在 `/doc.html` 浏览

## 🛠️ 技术栈

### 后端
| 技术 | 版本 | 用途 |
|---|---|---|
| JDK | 1.8 | 运行环境 |
| Spring Boot | 2.7.18 | Web 框架 |
| MyBatis-Plus | 3.5.5 | ORM |
| Sa-Token | 1.37.0 | 鉴权(JWT + Redis) |
| MySQL | 8.0.33 | 数据库 |
| Redis | 7.x | 缓存/Session |
| Druid | 1.2.20 | 连接池 |
| Knife4j | 4.5.0 | 接口文档 |
| EasyExcel | 3.3.4 | 导入导出 |
| Hutool | 5.8.25 | 工具集 |
| Fastjson2 | 2.0.43 | JSON |

### 前端
| 技术 | 版本 | 用途 |
|---|---|---|
| Node.js | 18+ | 运行环境 |
| Vue | 3.4 | 前端框架 |
| Vite | 5 | 构建工具 |
| Element Plus | 2.7 | UI 组件 |
| Pinia | 2.1 | 状态管理 |
| Vue Router | 4.3 | 路由 |
| Axios | 1.6 | HTTP |
| ECharts | 5.5 | 图表 |
| TypeScript | 5.4 | 类型 |

## 📁 项目结构

```
wms-graduation/
├── wms-backend/            Spring Boot 后端
│   ├── pom.xml
│   └── src/main/java/com/wms/
│       ├── WmsApplication.java
│       ├── common/         通用类
│       ├── config/         配置
│       ├── framework/      框架(注解/AOP/任务)
│       └── modules/        业务模块
│           ├── system/     系统管理
│           ├── basic/      主数据
│           ├── inbound/    入库
│           ├── outbound/   出库
│           ├── stock/      库存
│           └── report/     报表
├── wms-frontend/           Vue 3 前端
│   ├── package.json
│   ├── vite.config.ts
│   └── src/
│       ├── api/            接口
│       ├── router/         路由
│       ├── stores/         Pinia
│       ├── views/          页面
│       └── layout/         布局
├── wms-db/                 数据库脚本
│   ├── 01_create_database.sql
│   ├── 02_create_tables.sql
│   └── 03_seed_data.sql
├── docs/                   8 份毕设文档
│   ├── 01-开题报告.md
│   ├── 02-毕业论文.md
│   ├── 03-答辩PPT.md
│   ├── 04-答辩稿.md
│   ├── 05-数据库设计文档.md
│   ├── 06-接口文档.md
│   ├── 07-用户手册.md
│   └── 08-演示脚本.md
├── start.bat               Windows 一键启动
├── start.sh                Linux/Mac 启动
└── README.md
```

## 🚀 快速开始

### 环境准备

| 软件 | 版本 | 检查 |
|---|---|---|
| JDK | 1.8+ | `java -version` |
| Maven | 3.6+ | `mvn -v` |
| Node.js | 18+ | `node -v` |
| MySQL | 8.0+ | `mysql --version` |
| Redis | 7.x | `redis-server --version` |

### 1. 初始化数据库

```bash
mysql -u root -p < wms-db/01_create_database.sql
mysql -u root -p wms_db < wms-db/02_create_tables.sql
mysql -u root -p wms_db < wms-db/03_seed_data.sql
```

### 2. 启动 Redis

```bash
redis-server
```

### 3. 启动后端

```bash
cd wms-backend
mvn spring-boot:run
# 启动成功访问 http://localhost:8080/doc.html
```

### 4. 启动前端

```bash
cd wms-frontend
npm install
npm run dev
# 浏览器打开 http://localhost:5173
```

### 5. 一键启动(可选)

Windows:
```cmd
start.bat
```

Linux/Mac:
```bash
chmod +x start.sh
./start.sh
```

## 👤 演示账号

| 账号 | 密码 | 角色 | 部门 |
|---|---|---|---|
| admin | 123456 | 系统管理员 | 总经办 |
| wh001 | 123456 | 仓库管理员 | 仓储部 |
| dept001 | 123456 | 部门负责人 | 销售部 |
| emp001 | 123456 | 普通员工 | 销售部 |

## 📚 文档导航

- 📋 [开题报告](docs/01-开题报告.md)
- 📖 [毕业论文](docs/02-毕业论文.md)
- 🎤 [答辩 PPT](docs/03-答辩PPT.md)
- 🎙️ [答辩稿](docs/04-答辩稿.md)
- 🗄️ [数据库设计文档](docs/05-数据库设计文档.md)
- 🔌 [接口文档](docs/06-接口文档.md)
- 📘 [用户手册](docs/07-用户手册.md)
- 🎬 [5 分钟演示脚本](docs/08-演示脚本.md)

## 🔍 关键 API

| 功能 | 接口 | 方法 |
|---|---|---|
| 登录 | `/auth/login` | POST |
| 入库单分页 | `/inbound/order/page` | GET |
| 入库单保存 | `/inbound/order/save` | POST |
| 出库申请 | `/outbound/order/apply` | POST |
| 出库审批 | `/outbound/order/approval/handle` | POST |
| 库存查询 | `/stock/list/page` | GET |
| 库存流水 | `/stock/record/page` | GET |
| 库存预警 | `/stock/notice/page` | GET |
| 仪表盘 | `/report/dashboard` | GET |

## 🎯 演示流程

参考 [08-演示脚本.md](docs/08-演示脚本.md),5 分钟内演示完整业务闭环:

1. **登录** admin/123456
2. **仓管 wh001** 创建入库单 → 提交
3. **admin** 审核通过
4. **wh001** 执行完成,库存自动增加
5. **员工 emp001** 申请出库
6. **部门负责人 dept001** 审核通过
7. **wh001** 仓管审核通过 → 发货出库,库存扣减
8. **库存预警** 自动生成通知
9. **盘点** 创建盘点单 → 调整差异
10. **报表** 展示数据可视化

## ❓ FAQ

**Q:启动后端报"无法连接 Redis"?**
A:请确认 Redis 已启动(默认 6379 端口)。或修改 `application-dev.yml` 中的 redis 配置。

**Q:启动前端报"端口被占用"?**
A:修改 `vite.config.ts` 中的 `server.port`。

**Q:登录提示"captcha 错误"?**
A:种子里 captcha 校验可直接忽略 captcha 字段,登录时 captchaKey/captcha 留空即可。或点击"获取验证码"按钮重新生成。

**Q:数据库连接失败?**
A:修改 `wms-backend/src/main/resources/application-dev.yml` 中的 url/username/password。

## 📄 许可证

本项目仅用于毕业设计,不可用于商业用途。

## 🙋 致谢

感谢 XXX 老师的指导,感谢同学们在毕设过程中的支持与帮助。
