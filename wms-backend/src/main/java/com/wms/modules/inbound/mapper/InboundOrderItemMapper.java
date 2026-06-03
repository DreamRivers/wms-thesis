package com.wms.modules.inbound.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wms.modules.inbound.entity.InboundOrderItem;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface InboundOrderItemMapper extends BaseMapper<InboundOrderItem> {

    @Select("SELECT * FROM wms_inbound_order_item WHERE order_id = #{orderId} ORDER BY id ASC")
    List<InboundOrderItem> selectByOrderId(Long orderId);

    @Delete("DELETE FROM wms_inbound_order_item WHERE order_id = #{orderId}")
    int deleteByOrderId(Long orderId);
}
