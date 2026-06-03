-- =============================================
-- 电商仓储物资管理系统(WMS)
-- 02 - 建表脚本(13 张核心表)
-- =============================================

USE `wms_db`;

-- ----------------------------
-- 1. 系统模块(5 张)
-- ----------------------------

-- 用户表
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
    `id`              BIGINT       NOT NULL AUTO_INCREMENT       COMMENT '用户ID',
    `username`        VARCHAR(50)  NOT NULL                      COMMENT '登录账号',
    `password`        VARCHAR(100) NOT NULL                      COMMENT '密码(BCrypt)',
    `real_name`       VARCHAR(50)  DEFAULT NULL                  COMMENT '真实姓名',
    `avatar`          VARCHAR(255) DEFAULT NULL                  COMMENT '头像URL',
    `phone`           VARCHAR(20)  DEFAULT NULL                  COMMENT '手机号',
    `email`           VARCHAR(100) DEFAULT NULL                  COMMENT '邮箱',
    `dept_id`         BIGINT       DEFAULT NULL                  COMMENT '部门ID',
    `status`          TINYINT      NOT NULL DEFAULT 1            COMMENT '状态:1启用 0停用',
    `last_login_time` DATETIME     DEFAULT NULL                  COMMENT '最后登录时间',
    `last_login_ip`   VARCHAR(50)  DEFAULT NULL                  COMMENT '最后登录IP',
    `remark`          VARCHAR(255) DEFAULT NULL                  COMMENT '备注',
    `create_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP            COMMENT '创建时间',
    `update_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by`       BIGINT       DEFAULT NULL                  COMMENT '创建人',
    `update_by`       BIGINT       DEFAULT NULL                  COMMENT '更新人',
    `deleted`         TINYINT      NOT NULL DEFAULT 0            COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

-- 角色表
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT       COMMENT '角色ID',
    `role_name`   VARCHAR(30)  NOT NULL                      COMMENT '角色名称',
    `role_code`   VARCHAR(30)  NOT NULL                      COMMENT '角色编码',
    `remark`      VARCHAR(255) DEFAULT NULL                  COMMENT '备注',
    `status`      TINYINT      NOT NULL DEFAULT 1            COMMENT '状态',
    `sort`        INT          NOT NULL DEFAULT 0            COMMENT '排序',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `create_by`   BIGINT       DEFAULT NULL,
    `update_by`   BIGINT       DEFAULT NULL,
    `deleted`     TINYINT      NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_code` (`role_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统角色表';

-- 菜单权限表
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu` (
    `id`         BIGINT       NOT NULL AUTO_INCREMENT       COMMENT '菜单ID',
    `parent_id`  BIGINT       NOT NULL DEFAULT 0            COMMENT '父菜单ID(0为顶级)',
    `menu_name`  VARCHAR(50)  NOT NULL                      COMMENT '菜单名称',
    `menu_type`  CHAR(1)      NOT NULL                      COMMENT 'M目录 C菜单 F按钮',
    `path`       VARCHAR(200) DEFAULT NULL                  COMMENT '路由路径',
    `component`  VARCHAR(200) DEFAULT NULL                  COMMENT '组件路径',
    `perms`      VARCHAR(100) DEFAULT NULL                  COMMENT '权限标识',
    `icon`       VARCHAR(50)  DEFAULT NULL                  COMMENT '图标',
    `sort`       INT          NOT NULL DEFAULT 0            COMMENT '排序',
    `visible`    TINYINT      NOT NULL DEFAULT 1            COMMENT '是否可见',
    `status`     TINYINT      NOT NULL DEFAULT 1            COMMENT '状态',
    `create_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `deleted`    TINYINT      NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜单权限表';

-- 用户角色关联
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role` (
    `user_id` BIGINT NOT NULL,
    `role_id` BIGINT NOT NULL,
    PRIMARY KEY (`user_id`,`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- 角色菜单关联
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu` (
    `role_id` BIGINT NOT NULL,
    `menu_id` BIGINT NOT NULL,
    PRIMARY KEY (`role_id`,`menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色菜单关联表';

-- 操作日志
DROP TABLE IF EXISTS `sys_operation_log`;
CREATE TABLE `sys_operation_log` (
    `id`            BIGINT       NOT NULL AUTO_INCREMENT,
    `user_id`       BIGINT       DEFAULT NULL,
    `username`      VARCHAR(50)  DEFAULT NULL,
    `module`        VARCHAR(30)  DEFAULT NULL,
    `action`        VARCHAR(50)  DEFAULT NULL,
    `method`        VARCHAR(200) DEFAULT NULL,
    `request_url`   VARCHAR(255) DEFAULT NULL,
    `request_method` CHAR(6)     DEFAULT NULL,
    `request_param` TEXT         DEFAULT NULL,
    `response`      TEXT         DEFAULT NULL,
    `cost_time`     BIGINT       DEFAULT NULL,
    `status`        TINYINT      DEFAULT 1,
    `error_msg`     TEXT         DEFAULT NULL,
    `ip`            VARCHAR(50)  DEFAULT NULL,
    `create_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统操作日志';

-- ----------------------------
-- 2. 主数据模块(5 张)
-- ----------------------------

-- 商品分类
DROP TABLE IF EXISTS `wms_category`;
CREATE TABLE `wms_category` (
    `id`            BIGINT       NOT NULL AUTO_INCREMENT,
    `parent_id`     BIGINT       NOT NULL DEFAULT 0,
    `category_name` VARCHAR(50)  NOT NULL,
    `sort`          INT          NOT NULL DEFAULT 0,
    `status`        TINYINT      NOT NULL DEFAULT 1,
    `create_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `create_by`     BIGINT       DEFAULT NULL,
    `update_by`     BIGINT       DEFAULT NULL,
    `deleted`       TINYINT      NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品分类表';

-- 仓库
DROP TABLE IF EXISTS `wms_warehouse`;
CREATE TABLE `wms_warehouse` (
    `id`             BIGINT       NOT NULL AUTO_INCREMENT,
    `warehouse_code` VARCHAR(30)  NOT NULL,
    `warehouse_name` VARCHAR(50)  NOT NULL,
    `address`        VARCHAR(255) DEFAULT NULL,
    `manager_id`     BIGINT       DEFAULT NULL,
    `status`         TINYINT      NOT NULL DEFAULT 1,
    `remark`         VARCHAR(255) DEFAULT NULL,
    `create_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `create_by`      BIGINT       DEFAULT NULL,
    `update_by`      BIGINT       DEFAULT NULL,
    `deleted`        TINYINT      NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_warehouse_code` (`warehouse_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='仓库表';

-- 库位
DROP TABLE IF EXISTS `wms_location`;
CREATE TABLE `wms_location` (
    `id`            BIGINT       NOT NULL AUTO_INCREMENT,
    `warehouse_id`  BIGINT       NOT NULL,
    `location_code` VARCHAR(30)  NOT NULL,
    `area`          VARCHAR(20)  DEFAULT NULL COMMENT '库区',
    `shelf`         VARCHAR(20)  DEFAULT NULL COMMENT '货架',
    `layer`         INT          DEFAULT 1    COMMENT '层',
    `column_no`     INT          DEFAULT 1    COMMENT '列',
    `capacity`      DECIMAL(12,2) DEFAULT 0,
    `location_type` TINYINT      NOT NULL DEFAULT 1 COMMENT '1正常 2暂存 3残次品',
    `status`        TINYINT      NOT NULL DEFAULT 1,
    `create_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `create_by`     BIGINT       DEFAULT NULL,
    `update_by`     BIGINT       DEFAULT NULL,
    `deleted`       TINYINT      NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_location_code` (`location_code`),
    KEY `idx_warehouse` (`warehouse_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库位表';

-- 供应商
DROP TABLE IF EXISTS `wms_supplier`;
CREATE TABLE `wms_supplier` (
    `id`            BIGINT       NOT NULL AUTO_INCREMENT,
    `supplier_code` VARCHAR(30)  NOT NULL,
    `supplier_name` VARCHAR(100) NOT NULL,
    `contact`       VARCHAR(30)  DEFAULT NULL,
    `phone`         VARCHAR(20)  DEFAULT NULL,
    `address`       VARCHAR(255) DEFAULT NULL,
    `credit_level`  VARCHAR(10)  DEFAULT 'A',
    `status`        TINYINT      NOT NULL DEFAULT 1,
    `remark`        VARCHAR(255) DEFAULT NULL,
    `create_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `create_by`     BIGINT       DEFAULT NULL,
    `update_by`     BIGINT       DEFAULT NULL,
    `deleted`       TINYINT      NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_supplier_code` (`supplier_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='供应商表';

-- 商品
DROP TABLE IF EXISTS `wms_goods`;
CREATE TABLE `wms_goods` (
    `id`                BIGINT       NOT NULL AUTO_INCREMENT,
    `goods_code`        VARCHAR(30)  NOT NULL,
    `goods_name`        VARCHAR(100) NOT NULL,
    `category_id`       BIGINT       DEFAULT NULL,
    `unit`              VARCHAR(10)  DEFAULT '个',
    `spec`              VARCHAR(50)  DEFAULT NULL,
    `barcode`           VARCHAR(50)  DEFAULT NULL,
    `safety_stock`      INT          NOT NULL DEFAULT 0 COMMENT '安全库存',
    `production_date`   DATE         DEFAULT NULL,
    `shelf_life_days`   INT          DEFAULT NULL COMMENT '保质期天数',
    `warn_days`         INT          NOT NULL DEFAULT 30 COMMENT '临期预警天数',
    `purchase_price`    DECIMAL(10,2) DEFAULT 0,
    `sale_price`        DECIMAL(10,2) DEFAULT 0,
    `main_image`        VARCHAR(255) DEFAULT NULL,
    `status`            TINYINT      NOT NULL DEFAULT 1,
    `remark`            VARCHAR(255) DEFAULT NULL,
    `create_time`       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time`       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `create_by`         BIGINT       DEFAULT NULL,
    `update_by`         BIGINT       DEFAULT NULL,
    `deleted`           TINYINT      NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_goods_code` (`goods_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';

-- ----------------------------
-- 3. 入库模块(2 张)
-- ----------------------------

-- 入库单主表
DROP TABLE IF EXISTS `wms_inbound_order`;
CREATE TABLE `wms_inbound_order` (
    `id`               BIGINT       NOT NULL AUTO_INCREMENT,
    `order_no`         VARCHAR(32)  NOT NULL,
    `supplier_id`      BIGINT       DEFAULT NULL,
    `warehouse_id`     BIGINT       NOT NULL,
    `order_type`       TINYINT      NOT NULL DEFAULT 1 COMMENT '1采购 2退货 3调拨',
    `expect_date`      DATE         DEFAULT NULL,
    `total_amount`     DECIMAL(12,2) DEFAULT 0,
    `total_qty`        INT          NOT NULL DEFAULT 0,
    `status`           VARCHAR(20)  NOT NULL DEFAULT 'DRAFT' COMMENT 'DRAFT/PENDING/APPROVED/EXECUTING/FINISHED/REJECTED/CANCELED',
    `apply_user_id`    BIGINT       DEFAULT NULL,
    `apply_time`       DATETIME     DEFAULT NULL,
    `audit_user_id`    BIGINT       DEFAULT NULL,
    `audit_time`       DATETIME     DEFAULT NULL,
    `audit_remark`     VARCHAR(255) DEFAULT NULL,
    `execute_user_id`  BIGINT       DEFAULT NULL,
    `execute_time`     DATETIME     DEFAULT NULL,
    `complete_time`    DATETIME     DEFAULT NULL,
    `remark`           VARCHAR(255) DEFAULT NULL,
    `create_time`      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time`      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `create_by`        BIGINT       DEFAULT NULL,
    `update_by`        BIGINT       DEFAULT NULL,
    `deleted`          TINYINT      NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_no` (`order_no`),
    KEY `idx_status` (`status`),
    KEY `idx_supplier` (`supplier_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='入库单主表';

-- 入库单明细
DROP TABLE IF EXISTS `wms_inbound_order_item`;
CREATE TABLE `wms_inbound_order_item` (
    `id`             BIGINT       NOT NULL AUTO_INCREMENT,
    `order_id`       BIGINT       NOT NULL,
    `goods_id`       BIGINT       NOT NULL,
    `location_id`    BIGINT       DEFAULT NULL,
    `batch_no`       VARCHAR(30)  DEFAULT NULL,
    `production_date` DATE        DEFAULT NULL,
    `expire_date`    DATE         DEFAULT NULL,
    `plan_qty`       INT          NOT NULL DEFAULT 0,
    `actual_qty`     INT          NOT NULL DEFAULT 0,
    `unit_price`     DECIMAL(10,2) DEFAULT 0,
    `amount`         DECIMAL(12,2) DEFAULT 0,
    `remark`         VARCHAR(255) DEFAULT NULL,
    `create_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_order_id` (`order_id`),
    KEY `idx_goods_id` (`goods_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='入库单明细';

-- ----------------------------
-- 4. 出库模块(3 张)
-- ----------------------------

-- 出库单主表
DROP TABLE IF EXISTS `wms_outbound_order`;
CREATE TABLE `wms_outbound_order` (
    `id`                  BIGINT       NOT NULL AUTO_INCREMENT,
    `order_no`            VARCHAR(32)  NOT NULL,
    `outbound_type`       TINYINT      NOT NULL DEFAULT 1 COMMENT '1销售 2领用 3调拨 4报损',
    `apply_dept_id`       BIGINT       DEFAULT NULL,
    `applicant_id`        BIGINT       NOT NULL,
    `apply_reason`        VARCHAR(255) DEFAULT NULL,
    `warehouse_id`        BIGINT       NOT NULL,
    `expect_date`         DATE         DEFAULT NULL,
    `total_qty`           INT          NOT NULL DEFAULT 0,
    `total_amount`        DECIMAL(12,2) DEFAULT 0,
    `status`              VARCHAR(20)  NOT NULL DEFAULT 'APPLY' COMMENT 'APPLY/APPROVING/APPROVED/PICKING/SHIPPED/FINISHED/REJECTED/CANCELED',
    `dept_audit_user_id`  BIGINT       DEFAULT NULL,
    `dept_audit_time`     DATETIME     DEFAULT NULL,
    `dept_audit_remark`   VARCHAR(255) DEFAULT NULL,
    `wh_audit_user_id`    BIGINT       DEFAULT NULL,
    `wh_audit_time`       DATETIME     DEFAULT NULL,
    `wh_audit_remark`     VARCHAR(255) DEFAULT NULL,
    `picker_id`           BIGINT       DEFAULT NULL,
    `pick_time`           DATETIME     DEFAULT NULL,
    `ship_time`           DATETIME     DEFAULT NULL,
    `complete_time`       DATETIME     DEFAULT NULL,
    `remark`              VARCHAR(255) DEFAULT NULL,
    `create_time`         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time`         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `create_by`           BIGINT       DEFAULT NULL,
    `update_by`           BIGINT       DEFAULT NULL,
    `deleted`             TINYINT      NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_no` (`order_no`),
    KEY `idx_status` (`status`),
    KEY `idx_applicant` (`applicant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='出库单主表';

-- 出库单明细
DROP TABLE IF EXISTS `wms_outbound_order_item`;
CREATE TABLE `wms_outbound_order_item` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT,
    `order_id`    BIGINT       NOT NULL,
    `goods_id`    BIGINT       NOT NULL,
    `location_id` BIGINT       DEFAULT NULL,
    `batch_no`    VARCHAR(30)  DEFAULT NULL,
    `plan_qty`    INT          NOT NULL DEFAULT 0,
    `actual_qty`  INT          NOT NULL DEFAULT 0,
    `unit_price`  DECIMAL(10,2) DEFAULT 0,
    `amount`      DECIMAL(12,2) DEFAULT 0,
    `remark`      VARCHAR(255) DEFAULT NULL,
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_order_id` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='出库单明细';

-- 出库审批流
DROP TABLE IF EXISTS `wms_outbound_approval`;
CREATE TABLE `wms_outbound_approval` (
    `id`            BIGINT       NOT NULL AUTO_INCREMENT,
    `order_id`      BIGINT       NOT NULL,
    `step`          TINYINT      NOT NULL COMMENT '1部门 2仓管',
    `approver_id`   BIGINT       NOT NULL,
    `approver_name` VARCHAR(50)  DEFAULT NULL,
    `action`        VARCHAR(20)  NOT NULL COMMENT 'PASS/REJECT',
    `remark`        VARCHAR(255) DEFAULT NULL,
    `create_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_order_id` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='出库审批流留痕';

-- ----------------------------
-- 5. 库存模块(4 张)
-- ----------------------------

-- 实时库存
DROP TABLE IF EXISTS `wms_stock`;
CREATE TABLE `wms_stock` (
    `id`             BIGINT       NOT NULL AUTO_INCREMENT,
    `goods_id`       BIGINT       NOT NULL,
    `location_id`    BIGINT       NOT NULL,
    `batch_no`       VARCHAR(30)  DEFAULT '',
    `production_date` DATE        DEFAULT NULL,
    `expire_date`    DATE         DEFAULT NULL,
    `quantity`       INT          NOT NULL DEFAULT 0 COMMENT '在库数量',
    `locked_qty`     INT          NOT NULL DEFAULT 0 COMMENT '锁定(预占)',
    `available_qty`  INT          NOT NULL DEFAULT 0 COMMENT '可用 = quantity - locked_qty',
    `last_in_time`   DATETIME     DEFAULT NULL,
    `last_out_time`  DATETIME     DEFAULT NULL,
    `create_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_stock` (`goods_id`,`location_id`,`batch_no`),
    KEY `idx_goods` (`goods_id`),
    KEY `idx_location` (`location_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='实时库存表';

-- 库存流水
DROP TABLE IF EXISTS `wms_stock_record`;
CREATE TABLE `wms_stock_record` (
    `id`             BIGINT       NOT NULL AUTO_INCREMENT,
    `record_no`      VARCHAR(32)  NOT NULL,
    `business_type`  VARCHAR(20)  NOT NULL COMMENT 'INBOUND/OUTBOUND/TAKE_ADJ/TRANSFER',
    `business_no`    VARCHAR(32)  DEFAULT NULL,
    `goods_id`       BIGINT       NOT NULL,
    `location_id`    BIGINT       NOT NULL,
    `batch_no`       VARCHAR(30)  DEFAULT '',
    `change_type`    TINYINT      NOT NULL COMMENT '1加 2减',
    `change_qty`     INT          NOT NULL,
    `before_qty`     INT          NOT NULL,
    `after_qty`      INT          NOT NULL,
    `operator_id`    BIGINT       DEFAULT NULL,
    `operate_time`   DATETIME     DEFAULT CURRENT_TIMESTAMP,
    `remark`         VARCHAR(255) DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `idx_goods_time` (`goods_id`,`operate_time`),
    KEY `idx_business` (`business_type`,`business_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存流水';

-- 盘点单主表
DROP TABLE IF EXISTS `wms_stocktaking_order`;
CREATE TABLE `wms_stocktaking_order` (
    `id`                BIGINT       NOT NULL AUTO_INCREMENT,
    `take_no`           VARCHAR(32)  NOT NULL,
    `warehouse_id`      BIGINT       NOT NULL,
    `take_type`         TINYINT      NOT NULL DEFAULT 1 COMMENT '1全盘 2抽盘 3动态',
    `status`            VARCHAR(20)  NOT NULL DEFAULT 'DRAFT',
    `create_user_id`    BIGINT       DEFAULT NULL,
    `plan_start_date`   DATE         DEFAULT NULL,
    `plan_end_date`     DATE         DEFAULT NULL,
    `actual_finish_time` DATETIME    DEFAULT NULL,
    `remark`            VARCHAR(255) DEFAULT NULL,
    `create_time`       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time`       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `create_by`         BIGINT       DEFAULT NULL,
    `update_by`         BIGINT       DEFAULT NULL,
    `deleted`           TINYINT      NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_take_no` (`take_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='盘点单主表';

-- 盘点单明细
DROP TABLE IF EXISTS `wms_stocktaking_order_item`;
CREATE TABLE `wms_stocktaking_order_item` (
    `id`           BIGINT       NOT NULL AUTO_INCREMENT,
    `take_id`      BIGINT       NOT NULL,
    `goods_id`     BIGINT       NOT NULL,
    `location_id`  BIGINT       NOT NULL,
    `batch_no`     VARCHAR(30)  DEFAULT '',
    `system_qty`   INT          NOT NULL DEFAULT 0,
    `actual_qty`   INT          DEFAULT 0,
    `diff_qty`     INT          DEFAULT 0,
    `diff_reason`  VARCHAR(255) DEFAULT NULL,
    `adjusted`     TINYINT      NOT NULL DEFAULT 0,
    `create_time`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_take_id` (`take_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='盘点单明细';

-- ----------------------------
-- 6. 通知表(1 张)
-- ----------------------------

DROP TABLE IF EXISTS `wms_notification`;
CREATE TABLE `wms_notification` (
    `id`              BIGINT       NOT NULL AUTO_INCREMENT,
    `type`            VARCHAR(20)  NOT NULL COMMENT 'LOW_STOCK/EXPIRE_SOON/REVIEW',
    `title`           VARCHAR(100) NOT NULL,
    `content`         TEXT         DEFAULT NULL,
    `ref_id`          BIGINT       DEFAULT NULL,
    `ref_type`        VARCHAR(20)  DEFAULT NULL,
    `target_user_id`  BIGINT       DEFAULT NULL,
    `target_role`     VARCHAR(30)  DEFAULT NULL,
    `read_status`     TINYINT      NOT NULL DEFAULT 0,
    `read_time`       DATETIME     DEFAULT NULL,
    `create_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_target` (`target_user_id`,`read_status`),
    KEY `idx_type` (`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统通知表';

SET FOREIGN_KEY_CHECKS = 1;
