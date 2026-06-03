package com.wms.modules.inbound.controller;

import com.wms.common.PageQuery;
import com.wms.common.result.Result;
import com.wms.framework.annotation.Log;
import com.wms.modules.inbound.dto.InboundAuditDTO;
import com.wms.modules.inbound.dto.InboundSaveDTO;
import com.wms.modules.inbound.entity.InboundOrderItem;
import com.wms.modules.inbound.service.InboundOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/inbound/order")
@RequiredArgsConstructor
public class InboundController {

    private final InboundOrderService inboundService;

    @GetMapping("/page")
    public Result<?> page(PageQuery query,
                          @RequestParam(required = false) String orderNo,
                          @RequestParam(required = false) String status) {
        return Result.ok(inboundService.page(query, orderNo, status));
    }

    @GetMapping("/{id}")
    public Result<?> detail(@PathVariable Long id) {
        return Result.ok(inboundService.detail(id));
    }

    @PostMapping("/save")
    @Log(module = "入库管理", action = "保存草稿")
    public Result<?> save(@RequestBody InboundSaveDTO dto) {
        return Result.ok(inboundService.saveDraft(dto));
    }

    @PostMapping("/submit/{id}")
    @Log(module = "入库管理", action = "提交审核")
    public Result<?> submit(@PathVariable Long id) {
        inboundService.submit(id);
        return Result.ok();
    }

    @PostMapping("/audit")
    @Log(module = "入库管理", action = "审核")
    public Result<?> audit(@RequestBody InboundAuditDTO dto) {
        inboundService.audit(dto);
        return Result.ok();
    }

    @PostMapping("/execute/{id}")
    @Log(module = "入库管理", action = "执行入库")
    public Result<?> execute(@PathVariable Long id, @RequestBody List<InboundOrderItem> items) {
        inboundService.execute(id, items);
        return Result.ok();
    }

    @PostMapping("/complete/{id}")
    @Log(module = "入库管理", action = "完成入库")
    public Result<?> complete(@PathVariable Long id) {
        inboundService.complete(id);
        return Result.ok();
    }

    @PostMapping("/cancel/{id}")
    @Log(module = "入库管理", action = "作废入库单")
    public Result<?> cancel(@PathVariable Long id) {
        inboundService.cancel(id);
        return Result.ok();
    }
}
