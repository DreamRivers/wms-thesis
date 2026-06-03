package com.wms.modules.system.controller;

import com.wms.common.result.Result;
import com.wms.framework.annotation.Log;
import com.wms.modules.system.entity.SysMenu;
import com.wms.modules.system.mapper.SysMenuMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/system/menu")
@RequiredArgsConstructor
public class SysMenuController {

    private final SysMenuMapper menuMapper;

    @GetMapping("/list")
    public Result<?> list() {
        return Result.ok(menuMapper.selectList(null));
    }

    @PostMapping
    @Log(module = "菜单管理", action = "新增菜单")
    public Result<?> add(@RequestBody SysMenu menu) {
        menuMapper.insert(menu);
        return Result.ok();
    }

    @PutMapping
    @Log(module = "菜单管理", action = "修改菜单")
    public Result<?> update(@RequestBody SysMenu menu) {
        menuMapper.updateById(menu);
        return Result.ok();
    }

    @DeleteMapping("/{ids}")
    @Log(module = "菜单管理", action = "删除菜单")
    public Result<?> remove(@PathVariable Long[] ids) {
        menuMapper.deleteBatchIds(Arrays.asList(ids));
        return Result.ok();
    }
}
