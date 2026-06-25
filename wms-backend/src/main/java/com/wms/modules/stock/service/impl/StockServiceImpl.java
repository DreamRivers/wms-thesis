package com.wms.modules.stock.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wms.common.PageQuery;
import com.wms.common.exception.BizException;
import com.wms.common.result.ResultCode;
import com.wms.modules.stock.entity.Stock;
import com.wms.modules.stock.entity.StockRecord;
import com.wms.modules.stock.mapper.StockMapper;
import com.wms.modules.stock.mapper.StockRecordMapper;
import com.wms.modules.stock.service.StockService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StockServiceImpl extends ServiceImpl<StockMapper, Stock> implements StockService {

    private final StockRecordMapper recordMapper;

    public StockServiceImpl(StockRecordMapper recordMapper) {
        this.recordMapper = recordMapper;
    }

    /**
     * 入库核心事务
     * - 行级锁 FOR UPDATE 防并发
     * - 写 wms_stock + 写 wms_stock_record 流水
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void executeInbound(String orderNo, List<StockChangeItem> changes, Long operatorId) {
        for (StockChangeItem ch : changes) {
            Stock s = baseMapper.selectForUpdate(ch.getGoodsId(), ch.getLocationId(),
                    StrUtil.blankToDefault(ch.getBatchNo(), ""));
            int before = 0, after = 0;
            if (s == null) {
                s = new Stock();
                s.setGoodsId(ch.getGoodsId());
                s.setLocationId(ch.getLocationId());
                s.setBatchNo(StrUtil.blankToDefault(ch.getBatchNo(), ""));
                s.setQuantity(0);
                s.setLockedQty(0);
                s.setAvailableQty(0);
                baseMapper.insert(s);
                s = baseMapper.selectForUpdate(ch.getGoodsId(), ch.getLocationId(),
                        StrUtil.blankToDefault(ch.getBatchNo(), ""));
            }
            before = s.getQuantity();
            after = before + ch.getQty();
            s.setQuantity(after);
            s.setAvailableQty(after - s.getLockedQty());
            s.setLastInTime(LocalDateTime.now());
            baseMapper.updateById(s);

            // 写流水
            StockRecord r = new StockRecord();
            r.setRecordNo("R" + IdUtil.fastSimpleUUID());
            r.setBusinessType("INBOUND");
            r.setBusinessNo(orderNo);
            r.setGoodsId(ch.getGoodsId());
            r.setLocationId(ch.getLocationId());
            r.setBatchNo(StrUtil.blankToDefault(ch.getBatchNo(), ""));
            r.setChangeType(1);
            r.setChangeQty(ch.getQty());
            r.setBeforeQty(before);
            r.setAfterQty(after);
            r.setOperatorId(operatorId);
            r.setOperateTime(LocalDateTime.now());
            r.setRemark(ch.getRemark());
            recordMapper.insert(r);
        }
    }

    /**
     * 出库核心事务(校验库存充足)
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void executeOutbound(String orderNo, List<StockChangeItem> changes, Long operatorId) {
        for (StockChangeItem ch : changes) {
            Stock s = baseMapper.selectForUpdate(ch.getGoodsId(), ch.getLocationId(),
                    StrUtil.blankToDefault(ch.getBatchNo(), ""));
            if (s == null) {
                throw new BizException(ResultCode.STOCK_NOT_ENOUGH);
            }
            int before = s.getQuantity();
            if (before < ch.getQty()) {
                throw new BizException(ResultCode.STOCK_NOT_ENOUGH);
            }
            int after = before - ch.getQty();
            s.setQuantity(after);
            s.setAvailableQty(after - s.getLockedQty());
            s.setLastOutTime(LocalDateTime.now());
            baseMapper.updateById(s);

            // 写流水
            StockRecord r = new StockRecord();
            r.setRecordNo("R" + IdUtil.fastSimpleUUID());
            r.setBusinessType("OUTBOUND");
            r.setBusinessNo(orderNo);
            r.setGoodsId(ch.getGoodsId());
            r.setLocationId(ch.getLocationId());
            r.setBatchNo(StrUtil.blankToDefault(ch.getBatchNo(), ""));
            r.setChangeType(2);
            r.setChangeQty(ch.getQty());
            r.setBeforeQty(before);
            r.setAfterQty(after);
            r.setOperatorId(operatorId);
            r.setOperateTime(LocalDateTime.now());
            r.setRemark(ch.getRemark());
            recordMapper.insert(r);
        }
    }

    @Override
    public Map<String, Object> pageStock(PageQuery query, Long goodsId, Long locationId) {
        Page<Stock> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<Stock> q = new LambdaQueryWrapper<>();
        if (goodsId != null) q.eq(Stock::getGoodsId, goodsId);
        if (locationId != null) q.eq(Stock::getLocationId, locationId);
        q.orderByDesc(Stock::getUpdateTime);
        Page<Stock> res = this.page(page, q);
        Map<String, Object> map = new HashMap<>();
        map.put("total", res.getTotal());
        map.put("list", res.getRecords());
        return map;
    }

    @Override
    public Map<String, Object> pageRecord(PageQuery query, Long goodsId, String businessType) {
        Page<StockRecord> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<StockRecord> q = new LambdaQueryWrapper<>();
        if (goodsId != null) q.eq(StockRecord::getGoodsId, goodsId);
        if (StrUtil.isNotBlank(businessType)) q.eq(StockRecord::getBusinessType, businessType);
        q.orderByDesc(StockRecord::getOperateTime);
        Page<StockRecord> res = recordMapper.selectPage(page, q);
        Map<String, Object> map = new HashMap<>();
        map.put("total", res.getTotal());
        map.put("list", res.getRecords());
        return map;
    }

    @Override
    public Integer sumLocationQty(Long locationId) {
        if (locationId == null) return 0;
        LambdaQueryWrapper<Stock> q = new LambdaQueryWrapper<>();
        q.eq(Stock::getLocationId, locationId);
        List<Stock> list = this.list(q);
        return list.stream().mapToInt(s -> s.getQuantity() == null ? 0 : s.getQuantity()).sum();
    }
}
