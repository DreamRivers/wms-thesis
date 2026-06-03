package com.wms.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 库存变动类型
 */
@Getter
@AllArgsConstructor
public enum StockChangeTypeEnum {

    INBOUND("INBOUND", "入库", 1),
    OUTBOUND("OUTBOUND", "出库", 2),
    TAKE_ADJUST("TAKE_ADJ", "盘点调整", 1),
    TRANSFER_IN("TRANSFER_IN", "调拨入", 1),
    TRANSFER_OUT("TRANSFER_OUT", "调拨出", 2);

    private final String code;
    private final String desc;
    /** 1 加 2 减 */
    private final Integer sign;
}
