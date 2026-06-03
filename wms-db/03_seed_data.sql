-- =============================================
-- 电商仓储物资管理系统(WMS)
-- 03 - 种子数据
-- 密码统一:123456(BCrypt 加密后:$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy)
-- =============================================

USE `wms_db`;

-- ----------------------------
-- 1. 角色
-- ----------------------------
INSERT INTO `sys_role` (`id`,`role_name`,`role_code`,`remark`,`sort`) VALUES
(1,'系统管理员','ADMIN','拥有所有权限',1),
(2,'仓库管理员','WAREHOUSE','负责入库/出库/库存管理',2),
(3,'部门负责人','DEPT_LEADER','审批本部门出库申请',3),
(4,'普通员工','EMPLOYEE','提交出库申请',4);

-- ----------------------------
-- 2. 菜单(权限树)
-- ----------------------------
INSERT INTO `sys_menu` (`id`,`parent_id`,`menu_name`,`menu_type`,`path`,`component`,`perms`,`icon`,`sort`) VALUES
-- 目录
(1,0,'系统管理','M','/system','Layout',NULL,'Setting',1),
(2,0,'基础数据','M','/basic','Layout',NULL,'Box',2),
(3,0,'入库管理','M','/inbound','Layout',NULL,'Upload',3),
(4,0,'出库管理','M','/outbound','Layout',NULL,'Download',4),
(5,0,'库存管理','M','/stock','Layout',NULL,'Goods',5),
(6,0,'报表统计','M','/report','Layout',NULL,'DataAnalysis',6),
-- 系统管理菜单
(101,1,'用户管理','C','/system/user','system/User','system:user:list','User',1),
(102,1,'角色管理','C','/system/role','system/Role','system:role:list','UserFilled',2),
(103,1,'菜单管理','C','/system/menu','system/Menu','system:menu:list','Menu',3),
(104,1,'操作日志','C','/system/log','system/OperationLog','system:log:list','Document',4),
-- 基础数据菜单
(201,2,'商品管理','C','/basic/goods','basic/Goods','basic:goods:list','Goods',1),
(202,2,'商品分类','C','/basic/category','basic/Category','basic:category:list','Folder',2),
(203,2,'仓库管理','C','/basic/warehouse','basic/Warehouse','basic:warehouse:list','House',3),
(204,2,'库位管理','C','/basic/location','basic/Location','basic:location:list','Grid',4),
(205,2,'供应商管理','C','/basic/supplier','basic/Supplier','basic:supplier:list','Avatar',5),
-- 入库管理菜单
(301,3,'入库单列表','C','/inbound/order','inbound/Order','inbound:order:list','List',1),
(302,3,'入库单审核','C','/inbound/audit','inbound/Audit','inbound:order:audit','CircleCheck',2),
-- 出库管理菜单
(401,4,'出库单列表','C','/outbound/order','outbound/Order','outbound:order:list','List',1),
(402,4,'出库申请','C','/outbound/apply','outbound/Apply','outbound:order:apply','EditPen',2),
(403,4,'出库审批','C','/outbound/approval','outbound/Approval','outbound:order:approve','CircleCheck',3),
-- 库存管理菜单
(501,5,'实时库存','C','/stock/list','stock/List','stock:list','Box',1),
(502,5,'库存流水','C','/stock/record','stock/Record','stock:record:list','Tickets',2),
(503,5,'盘点管理','C','/stock/taking','stock/Taking','stock:take:list','Histogram',3),
(504,5,'库存预警','C','/stock/warning','stock/Warning','stock:warning:list','Warning',4),
-- 报表统计菜单
(601,6,'入库统计','C','/report/inbound','report/Inbound','report:inbound','TrendCharts',1),
(602,6,'出库统计','C','/report/outbound','report/Outbound','report:outbound','PieChart',2),
(603,6,'库存报表','C','/report/inventory','report/Inventory','report:inventory','DataLine',3);

-- ----------------------------
-- 3. 角色菜单关联
-- ----------------------------
-- 管理员:全部
INSERT INTO `sys_role_menu` (`role_id`,`menu_id`)
SELECT 1, id FROM `sys_menu`;

-- 仓库管理员:基础数据 + 入库 + 出库(执行) + 库存 + 报表
INSERT INTO `sys_role_menu` (`role_id`,`menu_id`) VALUES
(2,2),(2,201),(2,202),(2,203),(2,204),(2,205),
(2,3),(2,301),(2,302),
(2,4),(2,401),
(2,5),(2,501),(2,502),(2,503),(2,504),
(2,6),(2,601),(2,602),(2,603);

-- 部门负责人:出库审批 + 报表
INSERT INTO `sys_role_menu` (`role_id`,`menu_id`) VALUES
(3,4),(3,401),(3,403),
(3,6),(3,601),(3,602);

-- 普通员工:出库申请 + 个人中心
INSERT INTO `sys_role_menu` (`role_id`,`menu_id`) VALUES
(4,4),(4,401),(4,402);

-- ----------------------------
-- 4. 用户(4 类账号,密码 123456)
-- BCrypt: $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy
-- ----------------------------
INSERT INTO `sys_user` (`id`,`username`,`password`,`real_name`,`phone`,`dept_id`,`status`) VALUES
(1,'admin','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','系统管理员','13800000001',1,1),
(2,'wh001','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','张仓管','13800000002',2,1),
(3,'dept001','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','李部长','13800000003',3,1),
(4,'emp001','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','王员工','13800000004',3,1);

-- 用户角色
INSERT INTO `sys_user_role` (`user_id`,`role_id`) VALUES
(1,1),(2,2),(3,3),(4,4);

-- ----------------------------
-- 5. 仓库
-- ----------------------------
INSERT INTO `wms_warehouse` (`id`,`warehouse_code`,`warehouse_name`,`address`,`status`) VALUES
(1,'WH001','主仓','上海市浦东新区张江高科技园区',1),
(2,'WH002','残品仓','上海市浦东新区',1);

-- ----------------------------
-- 6. 库位(主仓 15 个,残品仓 6 个)
-- ----------------------------
INSERT INTO `wms_location` (`warehouse_id`,`location_code`,`area`,`shelf`,`layer`,`column_no`,`location_type`,`status`) VALUES
-- 主仓 A 区
(1,'A-01-01','A区','A01',1,1,1,1),(1,'A-01-02','A区','A01',1,2,1,1),(1,'A-01-03','A区','A01',1,3,1,1),
(1,'A-02-01','A区','A02',1,1,1,1),(1,'A-02-02','A区','A02',1,2,1,1),(1,'A-02-03','A区','A02',1,3,1,1),
(1,'A-03-01','A区','A03',1,1,1,1),(1,'A-03-02','A区','A03',1,2,1,1),(1,'A-03-03','A区','A03',1,3,1,1),
-- 主仓 B 区
(1,'B-01-01','B区','B01',1,1,1,1),(1,'B-01-02','B区','B01',1,2,1,1),(1,'B-01-03','B区','B01',1,3,1,1),
(1,'B-02-01','B区','B02',1,1,1,1),(1,'B-02-02','B区','B02',1,2,1,1),(1,'B-02-03','B区','B02',1,3,1,1),
-- 残品仓
(2,'C-01-01','C区','C01',1,1,3,1),(2,'C-01-02','C区','C01',1,2,3,1),
(2,'C-02-01','C区','C02',1,1,2,1),(2,'C-02-02','C区','C02',1,2,2,1),
(2,'C-03-01','C区','C03',1,1,2,1),(2,'C-03-02','C区','C03',1,2,2,1);

-- ----------------------------
-- 7. 商品分类
-- ----------------------------
INSERT INTO `wms_category` (`id`,`parent_id`,`category_name`,`sort`) VALUES
(1,0,'电子产品',1),
(2,0,'日用品',2),
(3,0,'食品饮料',3),
(11,1,'手机配件',1),
(12,1,'电脑外设',2),
(21,2,'办公用品',1),
(31,3,'零食',1);

-- ----------------------------
-- 8. 商品(10 个)
-- ----------------------------
INSERT INTO `wms_goods` (`id`,`goods_code`,`goods_name`,`category_id`,`unit`,`spec`,`barcode`,`safety_stock`,`shelf_life_days`,`warn_days`,`purchase_price`,`sale_price`) VALUES
(1,'SKU-001','华为手机',11,'台','Mate60 Pro','6900000000001',10,NULL,30,5000,6999),
(2,'SKU-002','蓝牙耳机',11,'个','FreeBuds 5','6900000000002',20,NULL,30,400,799),
(3,'SKU-003','充电宝',11,'个','20000mAh','6900000000003',30,NULL,30,80,149),
(4,'SKU-004','无线鼠标',12,'个','M550','6900000000004',15,NULL,30,60,129),
(5,'SKU-005','机械键盘',12,'个','K500','6900000000005',10,NULL,30,250,499),
(6,'SKU-006','24寸显示器',12,'台','P24','6900000000006',8,NULL,30,800,1399),
(7,'SKU-007','农夫山泉',31,'瓶','550ml','6900000000007',100,365,30,1.5,2.5),
(8,'SKU-008','方便面',31,'袋','红烧牛肉','6900000000008',200,180,30,3,5),
(9,'SKU-009','抽纸',21,'包','3层120抽','6900000000009',100,NULL,30,8,15),
(10,'SKU-010','Type-C数据线',11,'根','1m','6900000000010',50,NULL,30,5,12);

-- ----------------------------
-- 9. 供应商
-- ----------------------------
INSERT INTO `wms_supplier` (`id`,`supplier_code`,`supplier_name`,`contact`,`phone`,`address`,`credit_level`) VALUES
(1,'SUP-001','华为技术有限公司','张经理','021-12345678','深圳市龙岗区','A'),
(2,'SUP-002','比亚迪电子','李经理','0755-23456789','深圳市坪山区','A'),
(3,'SUP-003','大疆创新','王经理','0755-34567890','深圳市南山区','A'),
(4,'SUP-004','农夫山泉','赵经理','0571-45678901','杭州市西湖区','B'),
(5,'SUP-005','联合利华','陈经理','021-56789012','上海市浦东新区','A');

-- ----------------------------
-- 10. 初始库存
-- ----------------------------
INSERT INTO `wms_stock` (`goods_id`,`location_id`,`batch_no`,`quantity`,`locked_qty`,`available_qty`,`last_in_time`) VALUES
(1,1,'B20260601',50,0,50,NOW()),
(2,2,'B20260601',100,0,100,NOW()),
(3,3,'B20260601',200,0,200,NOW()),
(4,4,'B20260601',80,0,80,NOW()),
(5,5,'B20260601',40,0,40,NOW()),
(6,6,'B20260601',30,0,30,NOW()),
(7,10,'B20260601',500,0,500,NOW()),
(8,11,'B20260601',300,0,300,NOW()),
(9,12,'B20260601',150,0,150,NOW()),
(10,13,'B20260601',120,0,120,NOW());
