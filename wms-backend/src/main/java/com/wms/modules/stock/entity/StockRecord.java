package com.wms.modules.stock.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("wms_stock_record")
public class StockRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String recordNo;
    private String businessType;
    private String businessNo;
    private Long goodsId;
    private Long locationId;
    private String batchNo;
    private Integer changeType;
    private Integer changeQty;
    private Integer beforeQty;
    private Integer afterQty;
    private Long operatorId;
    private LocalDateTime operateTime;
    private String remark;
}
