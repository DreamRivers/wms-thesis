package com.wms.modules.outbound.dto;

import lombok.Data;

@Data
public class ApprovalHandleDTO {
    private Long orderId;
    private Integer step;       // 1 部门 / 2 仓管
    private Boolean pass;
    private String remark;
}
