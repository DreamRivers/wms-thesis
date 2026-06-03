package com.wms.modules.stock.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wms.modules.stock.entity.StockRecord;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface StockRecordMapper extends BaseMapper<StockRecord> {
}
