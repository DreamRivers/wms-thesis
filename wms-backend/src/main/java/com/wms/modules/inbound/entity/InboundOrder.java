package com.wms.modules.inbound.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.wms.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("wms_inbound_order")
public class InboundOrder extends BaseEntity {
    private String orderNo;
    private Long supplierId;
    private Long warehouseId;
    private Integer orderType;
    private LocalDate expectDate;
    private BigDecimal totalAmount;
    private Integer totalQty;
    private String status;
    private Long applyUserId;
    private LocalDateTime applyTime;
    private Long auditUserId;
    private LocalDateTime auditTime;
    private String auditRemark;
    private Long executeUserId;
    private LocalDateTime executeTime;
    private LocalDateTime completeTime;
    private String remark;
}
