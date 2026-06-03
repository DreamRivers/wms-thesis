package com.wms.modules.basic.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wms.modules.basic.entity.Category;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
}
