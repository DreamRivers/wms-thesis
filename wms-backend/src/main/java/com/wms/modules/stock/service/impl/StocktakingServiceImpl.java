package com.wms.modules.stock.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wms.common.PageQuery;
import com.wms.common.constant.Constants;
import com.wms.modules.stock.entity.Stock;
import com.wms.modules.stock.entity.StockRecord;
import com.wms.modules.stock.entity.StocktakingOrder;
import com.wms.modules.stock.entity.StocktakingOrderItem;
import com.wms.modules.stock.mapper.StockMapper;
import com.wms.modules.stock.mapper.StockRecordMapper;
import com.wms.modules.stock.mapper.StocktakingOrderItemMapper;
import com.wms.modules.stock.mapper.StocktakingOrderMapper;
import com.wms.modules.stock.service.StocktakingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StocktakingServiceImpl extends ServiceImpl<StocktakingOrderMapper, StocktakingOrder>
        implements StocktakingService {

    private final StocktakingOrderItemMapper itemMapper;
    private final StockMapper stockMapper;
    private final StockRecordMapper recordMapper;

    public StocktakingServiceImpl(StocktakingOrderItemMapper itemMapper,
                                  StockMapper stockMapper,
                                  StockRecordMapper recordMapper) {
        this.itemMapper = itemMapper;
        this.stockMapper = stockMapper;
        this.recordMapper = recordMapper;
    }

    @Override
    public Map<String, Object> page(PageQuery query, String takeNo, String status) {
        Page<StocktakingOrder> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<StocktakingOrder> q = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(takeNo)) q.like(StocktakingOrder::getTakeNo, takeNo);
        if (StrUtil.isNotBlank(status)) q.eq(StocktakingOrder::getStatus, status);
        q.orderByDesc(StocktakingOrder::getCreateTime);
        Page<StocktakingOrder> res = this.page(page, q);
        Map<String, Object> map = new HashMap<>();
        map.put("total", res.getTotal());
        map.put("list", res.getRecords());
        return map;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(StocktakingOrder order, List<StocktakingOrderItem> items) {
        order.setTakeNo(generateTakeNo());
        order.setStatus("DRAFT");
        order.setCreateUserId(StpUtil.getLoginIdAsLong());
        this.save(order);

        // 自动抓取该仓库下所有库存作为盘点项
        if (items == null || items.isEmpty()) {
            items = new ArrayList<>();
            List<Stock> stocks = stockMapper.selectList(
                    new LambdaQueryWrapper<Stock>().eq(Stock::getLocationId, order.getWarehouseId()));
            for (Stock s : stocks) {
                StocktakingOrderItem it = new StocktakingOrderItem();
                it.setTakeId(order.getId());
                it.setGoodsId(s.getGoodsId());
                it.setLocationId(s.getLocationId());
                it.setBatchNo(s.getBatchNo());
                it.setSystemQty(s.getQuantity());
                it.setActualQty(0);
                it.setDiffQty(-s.getQuantity());
                it.setAdjusted(0);
                items.add(it);
            }
        }
        for (StocktakingOrderItem it : items) {
            it.setId(null);
            it.setTakeId(order.getId());
            if (it.getSystemQty() == null) it.setSystemQty(0);
            if (it.getActualQty() == null) it.setActualQty(0);
            it.setDiffQty(it.getActualQty() - it.getSystemQty());
            itemMapper.insert(it);
        }
        return order.getId();
    }

    @Override
    public Map<String, Object> detail(Long id) {
        StocktakingOrder order = this.getById(id);
        List<StocktakingOrderItem> items = itemMapper.selectByTakeId(id);
        Map<String, Object> map = new HashMap<>();
        map.put("order", order);
        map.put("items", items);
        return map;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void record(Long takeId, List<StocktakingOrderItem> items) {
        for (StocktakingOrderItem it : items) {
            StocktakingOrderItem exist = itemMapper.selectById(it.getId());
            if (exist == null) continue;
            exist.setActualQty(it.getActualQty());
            exist.setDiffQty(it.getActualQty() - exist.getSystemQty());
            exist.setDiffReason(it.getDiffReason());
            itemMapper.updateById(exist);
        }
        StocktakingOrder order = this.getById(takeId);
        order.setStatus("EXECUTING");
        this.updateById(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void adjust(Long takeId) {
        List<StocktakingOrderItem> items = itemMapper.selectByTakeId(takeId);
        for (StocktakingOrderItem it : items) {
            if (it.getDiffQty() == null || it.getDiffQty() == 0) continue;
            Stock s = stockMapper.selectForUpdate(it.getGoodsId(), it.getLocationId(),
                    StrUtil.blankToDefault(it.getBatchNo(), ""));
            if (s == null) continue;
            int before = s.getQuantity();
            int after = before + it.getDiffQty();
            s.setQuantity(after);
            s.setAvailableQty(after - s.getLockedQty());
            stockMapper.updateById(s);

            // 写流水
            StockRecord r = new StockRecord();
            r.setRecordNo("R" + IdUtil.fastSimpleUUID());
            r.setBusinessType("TAKE_ADJ");
            r.setBusinessNo(takeId.toString());
            r.setGoodsId(it.getGoodsId());
            r.setLocationId(it.getLocationId());
            r.setBatchNo(StrUtil.blankToDefault(it.getBatchNo(), ""));
            r.setChangeType(it.getDiffQty() > 0 ? 1 : 2);
            r.setChangeQty(Math.abs(it.getDiffQty()));
            r.setBeforeQty(before);
            r.setAfterQty(after);
            r.setOperatorId(StpUtil.getLoginIdAsLong());
            r.setOperateTime(LocalDateTime.now());
            r.setRemark("盘点调整:" + (it.getDiffQty() > 0 ? "盘盈" : "盘亏"));
            recordMapper.insert(r);

            it.setAdjusted(1);
            itemMapper.updateById(it);
        }
        StocktakingOrder order = this.getById(takeId);
        order.setStatus("FINISHED");
        order.setActualFinishTime(LocalDateTime.now());
        this.updateById(order);
    }

    private String generateTakeNo() {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        return Constants.ORDER_NO_PREFIX_TAKING + date + IdUtil.getSnowflake(1, 1).nextIdStr().substring(10);
    }
}
