package com.wms.modules.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wms.common.PageQuery;
import com.wms.common.result.Result;
import com.wms.framework.annotation.Log;
import com.wms.modules.system.entity.SysRole;
import com.wms.modules.system.mapper.SysRoleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/system/role")
@RequiredArgsConstructor
public class SysRoleController {

    private final SysRoleMapper roleMapper;

    @GetMapping("/page")
    public Result<?> page(PageQuery query, @RequestParam(required = false) String roleName) {
        Page<SysRole> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<SysRole> q = new LambdaQueryWrapper<>();
        if (roleName != null && !roleName.isEmpty()) q.like(SysRole::getRoleName, roleName);
        q.orderByAsc(SysRole::getSort);
        Page<SysRole> res = roleMapper.selectPage(page, q);
        Map<String, Object> map = new HashMap<>();
        map.put("total", res.getTotal());
        map.put("list", res.getRecords());
        return Result.ok(map);
    }

    @GetMapping("/listAll")
    public Result<?> listAll() {
        return Result.ok(roleMapper.selectList(
                new LambdaQueryWrapper<SysRole>().eq(SysRole::getStatus, 1).orderByAsc(SysRole::getSort)));
    }

    @PostMapping
    @Log(module = "角色管理", action = "新增角色")
    public Result<?> add(@RequestBody SysRole role) {
        roleMapper.insert(role);
        return Result.ok();
    }

    @PutMapping
    @Log(module = "角色管理", action = "修改角色")
    public Result<?> update(@RequestBody SysRole role) {
        roleMapper.updateById(role);
        return Result.ok();
    }

    @DeleteMapping("/{ids}")
    @Log(module = "角色管理", action = "删除角色")
    public Result<?> remove(@PathVariable Long[] ids) {
        roleMapper.deleteBatchIds(Arrays.asList(ids));
        return Result.ok();
    }
}
