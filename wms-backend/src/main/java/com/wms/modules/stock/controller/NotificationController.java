package com.wms.modules.stock.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wms.common.PageQuery;
import com.wms.common.result.Result;
import com.wms.modules.stock.entity.Notification;
import com.wms.modules.stock.mapper.NotificationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/stock/notice")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationMapper noticeMapper;

    @GetMapping("/page")
    public Result<?> page(PageQuery query,
                          @RequestParam(required = false) String type,
                          @RequestParam(required = false) Integer readStatus) {
        Page<Notification> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<Notification> q = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(type)) q.eq(Notification::getType, type);
        if (readStatus != null) q.eq(Notification::getReadStatus, readStatus);
        q.orderByDesc(Notification::getCreateTime);
        Page<Notification> res = noticeMapper.selectPage(page, q);
        Map<String, Object> map = new HashMap<>();
        map.put("total", res.getTotal());
        map.put("list", res.getRecords());
        return Result.ok(map);
    }

    @GetMapping("/unreadCount")
    public Result<?> unreadCount() {
        Long count = noticeMapper.selectCount(
                new LambdaQueryWrapper<Notification>().eq(Notification::getReadStatus, 0));
        return Result.ok(count);
    }

    @PostMapping("/read/{id}")
    public Result<?> read(@PathVariable Long id) {
        Notification n = new Notification();
        n.setId(id);
        n.setReadStatus(1);
        n.setReadTime(LocalDateTime.now());
        noticeMapper.updateById(n);
        return Result.ok();
    }

    @PostMapping("/readAll")
    public Result<?> readAll() {
        Notification n = new Notification();
        n.setReadStatus(1);
        n.setReadTime(LocalDateTime.now());
        // 批量更新
        noticeMapper.update(n, new LambdaQueryWrapper<Notification>().eq(Notification::getReadStatus, 0));
        return Result.ok();
    }
}
