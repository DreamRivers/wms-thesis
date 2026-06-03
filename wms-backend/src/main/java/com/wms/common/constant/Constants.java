package com.wms.common.constant;

/**
 * 系统常量
 */
public interface Constants {

    /** 角色编码 */
    String ROLE_ADMIN = "ADMIN";
    String ROLE_WAREHOUSE = "WAREHOUSE";
    String ROLE_DEPT_LEADER = "DEPT_LEADER";
    String ROLE_EMPLOYEE = "EMPLOYEE";

    /** 单据前缀 */
    String ORDER_NO_PREFIX_INBOUND = "RK";
    String ORDER_NO_PREFIX_OUTBOUND = "CK";
    String ORDER_NO_PREFIX_TAKING = "PD";

    /** 库存流水前缀 */
    String RECORD_NO_PREFIX = "R";

    /** 通用状态:启用 */
    Integer STATUS_ENABLE = 1;
    Integer STATUS_DISABLE = 0;

    /** 通知类型 */
    String NOTICE_LOW_STOCK = "LOW_STOCK";
    String NOTICE_EXPIRE_SOON = "EXPIRE_SOON";
    String NOTICE_REVIEW = "REVIEW";
}
