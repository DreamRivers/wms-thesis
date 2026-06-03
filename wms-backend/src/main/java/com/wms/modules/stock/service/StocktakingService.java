package com.wms.modules.stock.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wms.common.PageQuery;
import com.wms.modules.stock.entity.StocktakingOrder;
import com.wms.modules.stock.entity.StocktakingOrderItem;

import java.util.List;
import java.util.Map;

public interface StocktakingService extends IService<StocktakingOrder> {

    Map<String, Object> page(PageQuery query, String takeNo, String status);

    Long create(StocktakingOrder order, List<StocktakingOrderItem> items);

    Map<String, Object> detail(Long id);

    void record(Long takeId, List<StocktakingOrderItem> items);

    void adjust(Long takeId);
}
