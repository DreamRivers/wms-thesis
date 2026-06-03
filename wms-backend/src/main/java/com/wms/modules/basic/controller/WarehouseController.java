package com.wms.modules.basic.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wms.common.PageQuery;
import com.wms.common.result.Result;
import com.wms.framework.annotation.Log;
import com.wms.modules.basic.entity.Warehouse;
import com.wms.modules.basic.mapper.WarehouseMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/basic/warehouse")
@RequiredArgsConstructor
public class WarehouseController {

    private final WarehouseMapper warehouseMapper;

    @GetMapping("/page")
    public Result<?> page(PageQuery query, @RequestParam(required = false) String warehouseName) {
        Page<Warehouse> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<Warehouse> q = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(warehouseName)) q.like(Warehouse::getWarehouseName, warehouseName);
        q.orderByDesc(Warehouse::getCreateTime);
        Page<Warehouse> res = warehouseMapper.selectPage(page, q);
        Map<String, Object> map = new HashMap<>();
        map.put("total", res.getTotal());
        map.put("list", res.getRecords());
        return Result.ok(map);
    }

    @GetMapping("/listAll")
    public Result<?> listAll() {
        return Result.ok(warehouseMapper.selectList(
                new LambdaQueryWrapper<Warehouse>().eq(Warehouse::getStatus, 1)));
    }

    @PostMapping
    @Log(module = "仓库管理", action = "新增")
    public Result<?> add(@RequestBody Warehouse w) {
        warehouseMapper.insert(w);
        return Result.ok();
    }

    @PutMapping
    @Log(module = "仓库管理", action = "修改")
    public Result<?> update(@RequestBody Warehouse w) {
        warehouseMapper.updateById(w);
        return Result.ok();
    }

    @DeleteMapping("/{ids}")
    @Log(module = "仓库管理", action = "删除")
    public Result<?> remove(@PathVariable Long[] ids) {
        warehouseMapper.deleteBatchIds(Arrays.asList(ids));
        return Result.ok();
    }
}
