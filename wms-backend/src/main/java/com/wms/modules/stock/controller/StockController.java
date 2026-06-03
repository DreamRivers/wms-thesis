package com.wms.modules.stock.controller;

import com.wms.common.PageQuery;
import com.wms.common.result.Result;
import com.wms.modules.stock.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/stock")
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;

    @GetMapping("/list/page")
    public Result<?> pageStock(PageQuery query,
                               @RequestParam(required = false) Long goodsId,
                               @RequestParam(required = false) Long locationId) {
        return Result.ok(stockService.pageStock(query, goodsId, locationId));
    }

    @GetMapping("/record/page")
    public Result<?> pageRecord(PageQuery query,
                                @RequestParam(required = false) Long goodsId,
                                @RequestParam(required = false) String businessType) {
        return Result.ok(stockService.pageRecord(query, goodsId, businessType));
    }
}
