package com.wms.modules.stock.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.wms.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("wms_stocktaking_order")
public class StocktakingOrder extends BaseEntity {
    private String takeNo;
    private Long warehouseId;
    private Integer takeType;
    private String status;
    private Long createUserId;
    private LocalDate planStartDate;
    private LocalDate planEndDate;
    private LocalDateTime actualFinishTime;
    private String remark;
}
