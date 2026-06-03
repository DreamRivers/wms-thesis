package com.wms.modules.report.controller;

import cn.hutool.core.util.StrUtil;
import com.wms.common.result.Result;
import com.wms.modules.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/report")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    /** 仪表盘首页数据 */
    @GetMapping("/dashboard")
    public Result<?> dashboard() {
        return Result.ok(reportService.dashboard());
    }

    /** 入库趋势(按天) */
    @GetMapping("/inbound/trend")
    public Result<?> inboundTrend(@RequestParam(required = false) String startDate,
                                  @RequestParam(required = false) String endDate) {
        return Result.ok(reportService.inboundTrend(startDate, endDate));
    }

    /** 出库趋势 */
    @GetMapping("/outbound/trend")
    public Result<?> outboundTrend(@RequestParam(required = false) String startDate,
                                   @RequestParam(required = false) String endDate) {
        return Result.ok(reportService.outboundTrend(startDate, endDate));
    }

    /** 库存价值 */
    @GetMapping("/inventory/value")
    public Result<?> inventoryValue() {
        return Result.ok(reportService.inventoryValue());
    }

    /** TOP10 商品 */
    @GetMapping("/topGoods")
    public Result<?> topGoods(@RequestParam(required = false) String type) {
        return Result.ok(reportService.topGoods(type == null ? "OUT" : type));
    }
}
