package com.wms.modules.basic.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.wms.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("wms_goods")
public class Goods extends BaseEntity {
    private String goodsCode;
    private String goodsName;
    private Long categoryId;
    private String unit;
    private String spec;
    private String barcode;
    private Integer safetyStock;
    private LocalDate productionDate;
    private Integer shelfLifeDays;
    private Integer warnDays;
    private BigDecimal purchasePrice;
    private BigDecimal salePrice;
    private String mainImage;
    private Integer status;
    private String remark;
}
