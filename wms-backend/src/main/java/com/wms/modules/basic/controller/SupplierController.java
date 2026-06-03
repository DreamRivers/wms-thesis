package com.wms.modules.basic.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wms.common.PageQuery;
import com.wms.common.result.Result;
import com.wms.framework.annotation.Log;
import com.wms.modules.basic.entity.Supplier;
import com.wms.modules.basic.mapper.SupplierMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/basic/supplier")
@RequiredArgsConstructor
public class SupplierController {

    private final SupplierMapper supplierMapper;

    @GetMapping("/page")
    public Result<?> page(PageQuery query, @RequestParam(required = false) String supplierName) {
        Page<Supplier> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<Supplier> q = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(supplierName)) q.like(Supplier::getSupplierName, supplierName);
        q.orderByDesc(Supplier::getCreateTime);
        Page<Supplier> res = supplierMapper.selectPage(page, q);
        Map<String, Object> map = new HashMap<>();
        map.put("total", res.getTotal());
        map.put("list", res.getRecords());
        return Result.ok(map);
    }

    @GetMapping("/listAll")
    public Result<?> listAll() {
        return Result.ok(supplierMapper.selectList(
                new LambdaQueryWrapper<Supplier>().eq(Supplier::getStatus, 1)));
    }

    @PostMapping
    @Log(module = "供应商管理", action = "新增")
    public Result<?> add(@RequestBody Supplier s) {
        supplierMapper.insert(s);
        return Result.ok();
    }

    @PutMapping
    @Log(module = "供应商管理", action = "修改")
    public Result<?> update(@RequestBody Supplier s) {
        supplierMapper.updateById(s);
        return Result.ok();
    }

    @DeleteMapping("/{ids}")
    @Log(module = "供应商管理", action = "删除")
    public Result<?> remove(@PathVariable Long[] ids) {
        supplierMapper.deleteBatchIds(Arrays.asList(ids));
        return Result.ok();
    }
}
