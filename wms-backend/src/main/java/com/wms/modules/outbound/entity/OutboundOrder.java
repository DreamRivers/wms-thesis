package com.wms.modules.outbound.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.wms.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("wms_outbound_order")
public class OutboundOrder extends BaseEntity {
    private String orderNo;
    private Integer outboundType;
    private Long applyDeptId;
    private Long applicantId;
    private String applyReason;
    private Long warehouseId;
    private LocalDate expectDate;
    private Integer totalQty;
    private BigDecimal totalAmount;
    private String status;
    private Long deptAuditUserId;
    private LocalDateTime deptAuditTime;
    private String deptAuditRemark;
    private Long whAuditUserId;
    private LocalDateTime whAuditTime;
    private String whAuditRemark;
    private Long pickerId;
    private LocalDateTime pickTime;
    private LocalDateTime shipTime;
    private LocalDateTime completeTime;
    private String remark;
}
