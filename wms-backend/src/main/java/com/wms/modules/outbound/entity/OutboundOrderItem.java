package com.wms.modules.outbound.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.wms.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("wms_outbound_order_item")
public class OutboundOrderItem extends BaseEntity {
    private Long orderId;
    private Long goodsId;
    private Long locationId;
    private String batchNo;
    private Integer planQty;
    private Integer actualQty;
    private BigDecimal unitPrice;
    private BigDecimal amount;
    private String remark;
}
