package com.wms.modules.stock.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wms.modules.stock.entity.StocktakingOrderItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface StocktakingOrderItemMapper extends BaseMapper<StocktakingOrderItem> {

    @Select("SELECT * FROM wms_stocktaking_order_item WHERE take_id = #{takeId} ORDER BY id ASC")
    List<StocktakingOrderItem> selectByTakeId(Long takeId);
}
