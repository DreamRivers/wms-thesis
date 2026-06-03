package com.wms.modules.inbound.dto;

import com.wms.modules.inbound.entity.InboundOrderItem;
import lombok.Data;

import java.util.List;

@Data
public class InboundSaveDTO {
    private Long id;
    private Long supplierId;
    private Long warehouseId;
    private Integer orderType;
    private String remark;
    private List<InboundOrderItem> items;
}
