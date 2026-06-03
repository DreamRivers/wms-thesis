package com.wms.modules.inbound.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wms.modules.inbound.entity.InboundOrder;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface InboundOrderMapper extends BaseMapper<InboundOrder> {
}
