package com.wms.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 入库单状态
 */
@Getter
@AllArgsConstructor
public enum InboundStatusEnum {

    DRAFT("DRAFT", "草稿", "info"),
    PENDING("PENDING", "待审核", "warning"),
    APPROVED("APPROVED", "已审核", "primary"),
    EXECUTING("EXECUTING", "执行中", "warning"),
    FINISHED("FINISHED", "已完成", "success"),
    REJECTED("REJECTED", "已驳回", "danger"),
    CANCELED("CANCELED", "已作废", "info");

    private final String code;
    private final String desc;
    private final String tag;

    public static InboundStatusEnum of(String code) {
        return Arrays.stream(values()).filter(e -> e.code.equals(code)).findFirst().orElse(null);
    }

    public static List<String> codes() {
        return Arrays.stream(values()).map(InboundStatusEnum::getCode).collect(Collectors.toList());
    }
}
