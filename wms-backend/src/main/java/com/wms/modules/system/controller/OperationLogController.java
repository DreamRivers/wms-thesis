package com.wms.modules.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wms.common.PageQuery;
import com.wms.common.result.Result;
import com.wms.modules.system.entity.SysOperationLog;
import com.wms.modules.system.mapper.SysOperationLogMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/system/log")
@RequiredArgsConstructor
public class OperationLogController {

    private final SysOperationLogMapper logMapper;

    @GetMapping("/page")
    public Result<?> page(PageQuery query,
                          @RequestParam(required = false) String module,
                          @RequestParam(required = false) String username) {
        Page<SysOperationLog> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<SysOperationLog> q = new LambdaQueryWrapper<>();
        if (module != null && !module.isEmpty()) q.like(SysOperationLog::getModule, module);
        if (username != null && !username.isEmpty()) q.like(SysOperationLog::getUsername, username);
        q.orderByDesc(SysOperationLog::getCreateTime);
        Page<SysOperationLog> res = logMapper.selectPage(page, q);
        Map<String, Object> map = new HashMap<>();
        map.put("total", res.getTotal());
        map.put("list", res.getRecords());
        return Result.ok(map);
    }
}
