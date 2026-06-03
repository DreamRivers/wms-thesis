package com.wms.modules.outbound.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wms.modules.outbound.entity.OutboundOrderItem;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface OutboundOrderItemMapper extends BaseMapper<OutboundOrderItem> {

    @Select("SELECT * FROM wms_outbound_order_item WHERE order_id = #{orderId} ORDER BY id ASC")
    List<OutboundOrderItem> selectByOrderId(Long orderId);

    @Delete("DELETE FROM wms_outbound_order_item WHERE order_id = #{orderId}")
    int deleteByOrderId(Long orderId);
}
