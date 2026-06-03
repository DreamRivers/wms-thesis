package com.wms.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 出库单状态
 */
@Getter
@AllArgsConstructor
public enum OutboundStatusEnum {

    APPLY("APPLY", "已申请", "info"),
    APPROVING("APPROVING", "审批中", "warning"),
    APPROVED("APPROVED", "已审核", "primary"),
    PICKING("PICKING", "拣货中", "warning"),
    SHIPPED("SHIPPED", "已发货", "primary"),
    FINISHED("FINISHED", "已完成", "success"),
    REJECTED("REJECTED", "已驳回", "danger"),
    CANCELED("CANCELED", "已取消", "info");

    private final String code;
    private final String desc;
    private final String tag;

    public static OutboundStatusEnum of(String code) {
        return Arrays.stream(values()).filter(e -> e.code.equals(code)).findFirst().orElse(null);
    }

    public static List<String> codes() {
        return Arrays.stream(values()).map(OutboundStatusEnum::getCode).collect(Collectors.toList());
    }
}
