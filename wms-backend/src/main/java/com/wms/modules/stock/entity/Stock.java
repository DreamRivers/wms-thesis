package com.wms.modules.stock.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("wms_stock")
public class Stock {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long goodsId;
    private Long locationId;
    private String batchNo;
    private LocalDate productionDate;
    private LocalDate expireDate;
    private Integer quantity;
    private Integer lockedQty;
    private Integer availableQty;
    private LocalDateTime lastInTime;
    private LocalDateTime lastOutTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
