package com.wms.modules.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wms.common.PageQuery;
import com.wms.common.result.Result;
import com.wms.framework.annotation.Log;
import com.wms.modules.system.entity.SysRole;
import com.wms.modules.system.entity.SysRoleMenu;
import com.wms.modules.system.mapper.SysRoleMapper;
import com.wms.modules.system.mapper.SysRoleMenuMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/system/role")
@RequiredArgsConstructor
public class SysRoleController {

    private final SysRoleMapper roleMapper;
    private final SysRoleMenuMapper roleMenuMapper;

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

    @GetMapping("/menus/{roleId}")
    public Result<?> getMenusByRoleId(@PathVariable Long roleId) {
        return Result.ok(roleMenuMapper.selectMenuIdsByRoleId(roleId));
    }

    @PostMapping("/assignMenus")
    @Log(module = "角色管理", action = "分配权限")
    @Transactional
    public Result<?> assignMenus(@RequestBody Map<String, Object> body) {
        Long roleId = Long.valueOf(body.get("roleId").toString());
        @SuppressWarnings("unchecked")
        List<Number> menuIds = (List<Number>) body.get("menuIds");
        // 删旧
        roleMenuMapper.deleteByRoleId(roleId);
        // 插新
        for (Number mid : menuIds) {
            SysRoleMenu rm = new SysRoleMenu();
            rm.setRoleId(roleId);
            rm.setMenuId(mid.longValue());
            rm.setDeleted(0);
            roleMenuMapper.insert(rm);
        }
        return Result.ok();
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
