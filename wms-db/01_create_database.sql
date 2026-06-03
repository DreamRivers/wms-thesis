-- =============================================
-- 电商仓储物资管理系统(WMS)
-- 01 - 建库脚本
-- 数据库:MySQL 8.0+
-- 字符集:utf8mb4
-- =============================================

DROP DATABASE IF EXISTS `wms_db`;
CREATE DATABASE `wms_db`
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE `wms_db`;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;
