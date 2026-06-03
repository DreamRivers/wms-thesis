package com.wms.modules.outbound.dto;

import com.wms.modules.outbound.entity.OutboundOrderItem;
import lombok.Data;

import java.util.List;

@Data
public class OutboundApplyDTO {
    private Long id;
    private Integer outboundType;
    private Long applyDeptId;
    private String applyReason;
    private Long warehouseId;
    private String remark;
    private List<OutboundOrderItem> items;
}
