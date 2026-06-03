package com.wms.modules.stock.controller;

import com.wms.common.PageQuery;
import com.wms.common.result.Result;
import com.wms.framework.annotation.Log;
import com.wms.modules.stock.entity.StocktakingOrder;
import com.wms.modules.stock.entity.StocktakingOrderItem;
import com.wms.modules.stock.service.StocktakingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/stock/taking")
@RequiredArgsConstructor
public class StocktakingController {

    private final StocktakingService stocktakingService;

    @GetMapping("/page")
    public Result<?> page(PageQuery query,
                          @RequestParam(required = false) String takeNo,
                          @RequestParam(required = false) String status) {
        return Result.ok(stocktakingService.page(query, takeNo, status));
    }

    @GetMapping("/{id}")
    public Result<?> detail(@PathVariable Long id) {
        return Result.ok(stocktakingService.detail(id));
    }

    @PostMapping("/create")
    @Log(module = "盘点管理", action = "创建盘点单")
    public Result<?> create(@RequestBody StocktakingOrder order,
                            @RequestParam(required = false) List<StocktakingOrderItem> items) {
        return Result.ok(stocktakingService.create(order, items));
    }

    @PostMapping("/record/{takeId}")
    @Log(module = "盘点管理", action = "录入实盘数")
    public Result<?> record(@PathVariable Long takeId, @RequestBody List<StocktakingOrderItem> items) {
        stocktakingService.record(takeId, items);
        return Result.ok();
    }

    @PostMapping("/adjust/{takeId}")
    @Log(module = "盘点管理", action = "盘点调整")
    public Result<?> adjust(@PathVariable Long takeId) {
        stocktakingService.adjust(takeId);
        return Result.ok();
    }
}
