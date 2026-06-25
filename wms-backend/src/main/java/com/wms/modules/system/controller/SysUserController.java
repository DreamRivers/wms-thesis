package com.wms.modules.system.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wms.common.PageQuery;
import com.wms.common.exception.BizException;
import com.wms.common.result.Result;
import com.wms.common.result.ResultCode;
import com.wms.framework.annotation.Log;
import com.wms.modules.system.entity.SysUser;
import com.wms.modules.system.service.SysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;

@RestController
@RequestMapping("/system/user")
@RequiredArgsConstructor
public class SysUserController {

    private final SysUserService userService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @GetMapping("/page")
    public Result<?> page(PageQuery query,
                          @RequestParam(required = false) String realName,
                          @RequestParam(required = false) Integer status) {
        return Result.ok(userService.page(query, realName, status));
    }

    @GetMapping("/{id}")
    public Result<?> get(@PathVariable Long id) {
        SysUser u = userService.getById(id);
        if (u != null) u.setPassword(null);
        return Result.ok(u);
    }

    @PostMapping
    @Log(module = "用户管理", action = "新增用户")
    public Result<?> add(@RequestBody SysUser user) {
        if (StrUtil.isBlank(user.getPassword())) user.setPassword("123456");
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userService.save(user);
        return Result.ok();
    }

    @PutMapping
    @Log(module = "用户管理", action = "修改用户")
    public Result<?> update(@RequestBody SysUser user) {
        if (StrUtil.isNotBlank(user.getPassword())) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        } else {
            user.setPassword(null);
        }
        userService.updateById(user);
        return Result.ok();
    }

    @DeleteMapping("/{ids}")
    @Log(module = "用户管理", action = "删除用户")
    public Result<?> remove(@PathVariable Long[] ids) {
        userService.removeBatchByIds(Arrays.asList(ids));
        return Result.ok();
    }

    @PostMapping("/resetPwd/{id}")
    @Log(module = "用户管理", action = "重置密码")
    public Result<?> resetPwd(@PathVariable Long id) {
        SysUser u = userService.getById(id);
        if (u == null) throw new BizException(ResultCode.DATA_NOT_FOUND);
        u.setPassword(passwordEncoder.encode("123456"));
        userService.updateById(u);
        return Result.ok();
    }

    @PostMapping("/changeStatus/{id}")
    @Log(module = "用户管理", action = "启停用户")
    public Result<?> changeStatus(@PathVariable Long id, @RequestParam Integer status) {
        SysUser u = new SysUser();
        u.setId(id);
        u.setStatus(status);
        userService.updateById(u);
        return Result.ok();
    }

    @PostMapping("/changeMyPassword")
    @Log(module = "个人中心", action = "修改自己的密码")
    public Result<?> changeMyPassword(@RequestBody java.util.Map<String, String> body) {
        String oldPwd = body.get("oldPassword");
        String newPwd = body.get("newPassword");
        if (StrUtil.isBlank(oldPwd) || StrUtil.isBlank(newPwd)) {
            throw new BizException(ResultCode.PARAM_ERROR);
        }
        if (newPwd.length() < 6) throw new BizException(ResultCode.PARAM_ERROR);
        // 当前登录用户
        Long uid = cn.dev33.satoken.stp.StpUtil.getLoginIdAsLong();
        SysUser u = userService.getById(uid);
        if (u == null) throw new BizException(ResultCode.DATA_NOT_FOUND);
        BCryptPasswordEncoder enc = new BCryptPasswordEncoder();
        if (!enc.matches(oldPwd, u.getPassword())) {
            throw new BizException(ResultCode.PARAM_ERROR.getCode(), "原密码错误");
        }
        u.setPassword(enc.encode(newPwd));
        userService.updateById(u);
        return Result.ok();
    }
}
