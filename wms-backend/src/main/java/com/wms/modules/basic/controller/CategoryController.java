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
        List<Category> list = categoryMapper.selectList(null);
        return Result.ok(buildTree(list, 0L));
    }

    private List<Category> buildTree(List<Category> all, Long parentId) {
        return all.stream()
                .filter(c -> c.getParentId().equals(parentId))
                .peek(c -> c.setCategoryName(null))  // 简化:此处可扩展
                .toList();
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
