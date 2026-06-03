package com.wms.modules.basic.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.wms.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("wms_warehouse")
public class Warehouse extends BaseEntity {
    private String warehouseCode;
    private String warehouseName;
    private String address;
    private Long managerId;
    private Integer status;
    private String remark;
}
