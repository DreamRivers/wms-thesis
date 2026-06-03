package com.wms.modules.stock.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.wms.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("wms_stocktaking_order_item")
public class StocktakingOrderItem extends BaseEntity {
    private Long takeId;
    private Long goodsId;
    private Long locationId;
    private String batchNo;
    private Integer systemQty;
    private Integer actualQty;
    private Integer diffQty;
    private String diffReason;
    private Integer adjusted;
}
