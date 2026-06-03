package com.wms.modules.stock.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wms.modules.stock.entity.Stock;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface StockMapper extends BaseMapper<Stock> {

    @Select("SELECT * FROM wms_stock WHERE goods_id = #{goodsId} AND location_id = #{locationId} AND batch_no = #{batchNo} LIMIT 1 FOR UPDATE")
    Stock selectForUpdate(@Param("goodsId") Long goodsId,
                          @Param("locationId") Long locationId,
                          @Param("batchNo") String batchNo);

    @Update("UPDATE wms_stock SET quantity = quantity + #{delta}, available_qty = available_qty + #{delta}, " +
            "last_in_time = NOW() WHERE goods_id = #{goodsId} AND location_id = #{locationId} AND batch_no = #{batchNo}")
    int incrQuantity(@Param("goodsId") Long goodsId,
                     @Param("locationId") Long locationId,
                     @Param("batchNo") String batchNo,
                     @Param("delta") int delta);
}
