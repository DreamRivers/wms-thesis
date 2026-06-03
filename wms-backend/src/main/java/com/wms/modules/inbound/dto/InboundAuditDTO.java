package com.wms.modules.inbound.dto;

import lombok.Data;

@Data
public class InboundAuditDTO {
    private Long id;
    private Boolean pass;
    private String remark;
}
