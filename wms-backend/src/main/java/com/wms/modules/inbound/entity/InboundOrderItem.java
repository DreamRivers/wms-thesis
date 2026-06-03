package com.wms.modules.inbound.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.wms.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("wms_inbound_order_item")
public class InboundOrderItem extends BaseEntity {
    private Long orderId;
    private Long goodsId;
    private Long locationId;
    private String batchNo;
    private LocalDate productionDate;
    private LocalDate expireDate;
    private Integer planQty;
    private Integer actualQty;
    private BigDecimal unitPrice;
    private BigDecimal amount;
    private String remark;
}
