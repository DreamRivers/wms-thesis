package com.wms.modules.basic.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.wms.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("wms_location")
public class Location extends BaseEntity {
    private Long warehouseId;
    private String locationCode;
    private String area;
    private String shelf;
    private Integer layer;
    private Integer columnNo;
    private BigDecimal capacity;
    private Integer locationType;
    private Integer status;
}
