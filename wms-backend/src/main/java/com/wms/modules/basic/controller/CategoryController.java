package com.wms.modules.basic.controller;

import com.wms.common.result.Result;
import com.wms.framework.annotation.Log;
import com.wms.modules.basic.entity.Category;
import com.wms.modules.basic.mapper.CategoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/basic/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryMapper categoryMapper;

    @GetMapping("/list")
    public Result<?> list() {
        // 返回所有启用的分类(含二级),供前端字典 join 使用
        List<Category> list = categoryMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Category>()
                .eq(Category::getStatus, 1)
                .orderByAsc(Category::getSort));
        return Result.ok(list);
    }

    @PostMapping
    @Log(module = "商品分类", action = "新增")
    public Result<?> add(@RequestBody Category category) {
        categoryMapper.insert(category);
        return Result.ok();
    }

    @PutMapping
    @Log(module = "商品分类", action = "修改")
    public Result<?> update(@RequestBody Category category) {
        categoryMapper.updateById(category);
        return Result.ok();
    }

    @DeleteMapping("/{ids}")
    @Log(module = "商品分类", action = "删除")
    public Result<?> remove(@PathVariable Long[] ids) {
        categoryMapper.deleteBatchIds(Arrays.asList(ids));
        return Result.ok();
    }
}
