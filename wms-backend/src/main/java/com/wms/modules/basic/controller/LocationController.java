package com.wms.modules.basic.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wms.common.PageQuery;
import com.wms.common.result.Result;
import com.wms.framework.annotation.Log;
import com.wms.modules.basic.entity.Location;
import com.wms.modules.basic.mapper.LocationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/basic/location")
@RequiredArgsConstructor
public class LocationController {

    private final LocationMapper locationMapper;

    @GetMapping("/page")
    public Result<?> page(PageQuery query,
                          @RequestParam(required = false) Long warehouseId,
                          @RequestParam(required = false) String locationCode) {
        Page<Location> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<Location> q = new LambdaQueryWrapper<>();
        if (warehouseId != null) q.eq(Location::getWarehouseId, warehouseId);
        if (StrUtil.isNotBlank(locationCode)) q.like(Location::getLocationCode, locationCode);
        q.orderByAsc(Location::getLocationCode);
        Page<Location> res = locationMapper.selectPage(page, q);
        Map<String, Object> map = new HashMap<>();
        map.put("total", res.getTotal());
        map.put("list", res.getRecords());
        return Result.ok(map);
    }

    @GetMapping("/listByWarehouse/{warehouseId}")
    public Result<?> listByWarehouse(@PathVariable Long warehouseId) {
        return Result.ok(locationMapper.selectList(
                new LambdaQueryWrapper<Location>().eq(Location::getWarehouseId, warehouseId)
                        .eq(Location::getStatus, 1).orderByAsc(Location::getLocationCode)));
    }

    @PostMapping
    @Log(module = "库位管理", action = "新增")
    public Result<?> add(@RequestBody Location l) {
        locationMapper.insert(l);
        return Result.ok();
    }

    @PutMapping
    @Log(module = "库位管理", action = "修改")
    public Result<?> update(@RequestBody Location l) {
        locationMapper.updateById(l);
        return Result.ok();
    }

    @DeleteMapping("/{ids}")
    @Log(module = "库位管理", action = "删除")
    public Result<?> remove(@PathVariable Long[] ids) {
        locationMapper.deleteBatchIds(Arrays.asList(ids));
        return Result.ok();
    }
}
