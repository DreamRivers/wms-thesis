package com.wms.modules.outbound.controller;

import com.wms.common.PageQuery;
import com.wms.common.result.Result;
import com.wms.framework.annotation.Log;
import com.wms.modules.outbound.dto.ApprovalHandleDTO;
import com.wms.modules.outbound.dto.OutboundApplyDTO;
import com.wms.modules.outbound.service.OutboundOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/outbound/order")
@RequiredArgsConstructor
public class OutboundController {

    private final OutboundOrderService outboundService;

    @GetMapping("/page")
    public Result<?> page(PageQuery query,
                          @RequestParam(required = false) String orderNo,
                          @RequestParam(required = false) String status) {
        return Result.ok(outboundService.page(query, orderNo, status));
    }

    @GetMapping("/{id}")
    public Result<?> detail(@PathVariable Long id) {
        return Result.ok(outboundService.detail(id));
    }

    @PostMapping("/apply")
    @Log(module = "出库管理", action = "提交出库申请")
    public Result<?> apply(@RequestBody OutboundApplyDTO dto) {
        return Result.ok(outboundService.apply(dto));
    }

    @PostMapping("/approval/handle")
    @Log(module = "出库管理", action = "审批出库申请")
    public Result<?> approval(@RequestBody ApprovalHandleDTO dto) {
        outboundService.handleApproval(dto);
        return Result.ok();
    }

    @PostMapping("/ship/{id}")
    @Log(module = "出库管理", action = "出库发货")
    public Result<?> ship(@PathVariable Long id) {
        outboundService.ship(id);
        return Result.ok();
    }

    @PostMapping("/complete/{id}")
    @Log(module = "出库管理", action = "完成出库")
    public Result<?> complete(@PathVariable Long id) {
        outboundService.complete(id);
        return Result.ok();
    }

    @PostMapping("/cancel/{id}")
    @Log(module = "出库管理", action = "取消出库")
    public Result<?> cancel(@PathVariable Long id) {
        outboundService.cancel(id);
        return Result.ok();
    }
}
