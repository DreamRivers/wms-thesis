package com.wms.modules.basic.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.wms.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("wms_supplier")
public class Supplier extends BaseEntity {
    private String supplierCode;
    private String supplierName;
    private String contact;
    private String phone;
    private String address;
    private String creditLevel;
    private Integer status;
    private String remark;
}
