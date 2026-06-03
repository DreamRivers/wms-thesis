package com.wms.modules.stock.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wms.common.PageQuery;
import com.wms.modules.stock.entity.Stock;
import com.wms.modules.stock.entity.StockRecord;

import java.util.List;
import java.util.Map;

public interface StockService extends IService<Stock> {

    /** 入库操作(写入库存 + 写流水) */
    void executeInbound(String orderNo, List<StockChangeItem> changes, Long operatorId);

    /** 出库操作(扣减库存 + 写流水) */
    void executeOutbound(String orderNo, List<StockChangeItem> changes, Long operatorId);

    /** 分页查询实时库存 */
    Map<String, Object> pageStock(PageQuery query, Long goodsId, Long locationId);

    /** 分页查询库存流水 */
    Map<String, Object> pageRecord(PageQuery query, Long goodsId, String businessType);

    /** 库存变动项 */
    @lombok.Data
    @lombok.AllArgsConstructor
    @lombok.NoArgsConstructor
    class StockChangeItem {
        private Long goodsId;
        private Long locationId;
        private String batchNo;
        private Integer qty;
        private String remark;
    }
}
