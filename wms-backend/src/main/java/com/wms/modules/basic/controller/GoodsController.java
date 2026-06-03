package com.wms.modules.basic.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wms.common.PageQuery;
import com.wms.common.result.Result;
import com.wms.framework.annotation.Log;
import com.wms.modules.basic.entity.Goods;
import com.wms.modules.basic.mapper.GoodsMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/basic/goods")
@RequiredArgsConstructor
public class GoodsController {

    private final GoodsMapper goodsMapper;

    @GetMapping("/page")
    public Result<?> page(PageQuery query,
                          @RequestParam(required = false) String goodsName,
                          @RequestParam(required = false) Long categoryId,
                          @RequestParam(required = false) Integer status) {
        Page<Goods> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<Goods> q = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(goodsName)) q.like(Goods::getGoodsName, goodsName);
        if (categoryId != null) q.eq(Goods::getCategoryId, categoryId);
        if (status != null) q.eq(Goods::getStatus, status);
        q.orderByDesc(Goods::getCreateTime);
        Page<Goods> res = goodsMapper.selectPage(page, q);
        Map<String, Object> map = new HashMap<>();
        map.put("total", res.getTotal());
        map.put("list", res.getRecords());
        return Result.ok(map);
    }

    @GetMapping("/listAll")
    public Result<?> listAll() {
        return Result.ok(goodsMapper.selectList(
                new LambdaQueryWrapper<Goods>().eq(Goods::getStatus, 1)));
    }

    @GetMapping("/{id}")
    public Result<?> get(@PathVariable Long id) {
        return Result.ok(goodsMapper.selectById(id));
    }

    @PostMapping
    @Log(module = "商品管理", action = "新增商品")
    public Result<?> add(@RequestBody Goods goods) {
        goodsMapper.insert(goods);
        return Result.ok();
    }

    @PutMapping
    @Log(module = "商品管理", action = "修改商品")
    public Result<?> update(@RequestBody Goods goods) {
        goodsMapper.updateById(goods);
        return Result.ok();
    }

    @DeleteMapping("/{ids}")
    @Log(module = "商品管理", action = "删除商品")
    public Result<?> remove(@PathVariable Long[] ids) {
        goodsMapper.deleteBatchIds(Arrays.asList(ids));
        return Result.ok();
    }
}
