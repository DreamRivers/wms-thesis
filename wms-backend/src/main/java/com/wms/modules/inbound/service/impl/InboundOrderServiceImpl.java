package com.wms.modules.inbound.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wms.common.PageQuery;
import com.wms.common.constant.Constants;
import com.wms.common.enums.InboundStatusEnum;
import com.wms.common.exception.BizException;
import com.wms.common.result.ResultCode;
import com.wms.modules.inbound.dto.InboundAuditDTO;
import com.wms.modules.inbound.dto.InboundSaveDTO;
import com.wms.modules.inbound.entity.InboundOrder;
import com.wms.modules.inbound.entity.InboundOrderItem;
import com.wms.modules.inbound.mapper.InboundOrderItemMapper;
import com.wms.modules.inbound.mapper.InboundOrderMapper;
import com.wms.modules.inbound.service.InboundOrderService;
import com.wms.modules.stock.service.StockService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class InboundOrderServiceImpl extends ServiceImpl<InboundOrderMapper, InboundOrder> implements InboundOrderService {

    private final InboundOrderItemMapper itemMapper;
    private final StockService stockService;

    public InboundOrderServiceImpl(InboundOrderItemMapper itemMapper, StockService stockService) {
        this.itemMapper = itemMapper;
        this.stockService = stockService;
    }

    @Override
    public Map<String, Object> page(PageQuery query, String orderNo, String status) {
        Page<InboundOrder> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<InboundOrder> q = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(orderNo)) q.like(InboundOrder::getOrderNo, orderNo);
        if (StrUtil.isNotBlank(status)) q.eq(InboundOrder::getStatus, status);
        q.orderByDesc(InboundOrder::getCreateTime);
        Page<InboundOrder> res = this.page(page, q);
        Map<String, Object> map = new HashMap<>();
        map.put("total", res.getTotal());
        map.put("list", res.getRecords());
        return map;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long saveDraft(InboundSaveDTO dto) {
        validateItems(dto.getItems());

        InboundOrder order = new InboundOrder();
        order.setId(dto.getId());
        order.setOrderNo(generateOrderNo());
        order.setSupplierId(dto.getSupplierId());
        order.setWarehouseId(dto.getWarehouseId());
        order.setOrderType(dto.getOrderType() == null ? 1 : dto.getOrderType());
        order.setStatus(InboundStatusEnum.DRAFT.getCode());
        order.setRemark(dto.getRemark());
        if (dto.getId() == null) {
            // 新增
            order.setApplyUserId(StpUtil.getLoginIdAsLong());
            order.setApplyTime(LocalDateTime.now());
            this.save(order);
        } else {
            this.updateById(order);
            itemMapper.deleteByOrderId(order.getId());
        }
        // 重新写入 items
        BigDecimal totalAmount = BigDecimal.ZERO;
        int totalQty = 0;
        for (InboundOrderItem it : dto.getItems()) {
            it.setId(null);
            it.setOrderId(order.getId());
            if (it.getActualQty() == null) it.setActualQty(it.getPlanQty());
            if (it.getUnitPrice() != null && it.getActualQty() != null) {
                it.setAmount(it.getUnitPrice().multiply(BigDecimal.valueOf(it.getActualQty())));
                totalAmount = totalAmount.add(it.getAmount());
            }
            if (it.getActualQty() != null) totalQty += it.getActualQty();
            itemMapper.insert(it);
        }
        order.setTotalAmount(totalAmount);
        order.setTotalQty(totalQty);
        this.updateById(order);
        return order.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submit(Long id) {
        InboundOrder order = this.getById(id);
        if (order == null) throw new BizException(ResultCode.DATA_NOT_FOUND);
        if (!InboundStatusEnum.DRAFT.getCode().equals(order.getStatus())) {
            throw new BizException(ResultCode.ILLEGAL_STATUS);
        }
        order.setStatus(InboundStatusEnum.PENDING.getCode());
        order.setApplyTime(LocalDateTime.now());
        this.updateById(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void audit(InboundAuditDTO dto) {
        InboundOrder order = this.getById(dto.getId());
        if (order == null) throw new BizException(ResultCode.DATA_NOT_FOUND);
        if (!InboundStatusEnum.PENDING.getCode().equals(order.getStatus())) {
            throw new BizException(ResultCode.ILLEGAL_STATUS);
        }
        order.setAuditUserId(StpUtil.getLoginIdAsLong());
        order.setAuditTime(LocalDateTime.now());
        order.setAuditRemark(dto.getRemark());
        if (Boolean.TRUE.equals(dto.getPass())) {
            order.setStatus(InboundStatusEnum.APPROVED.getCode());
        } else {
            order.setStatus(InboundStatusEnum.REJECTED.getCode());
        }
        this.updateById(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void execute(Long id, List<InboundOrderItem> items) {
        InboundOrder order = this.getById(id);
        if (order == null) throw new BizException(ResultCode.DATA_NOT_FOUND);
        if (!InboundStatusEnum.APPROVED.getCode().equals(order.getStatus())) {
            throw new BizException(ResultCode.ILLEGAL_STATUS);
        }
        order.setStatus(InboundStatusEnum.EXECUTING.getCode());
        order.setExecuteUserId(StpUtil.getLoginIdAsLong());
        order.setExecuteTime(LocalDateTime.now());
        this.updateById(order);

        // 更新明细 actual_qty
        for (InboundOrderItem it : items) {
            InboundOrderItem exist = itemMapper.selectById(it.getId());
            exist.setActualQty(it.getActualQty());
            exist.setLocationId(it.getLocationId());
            exist.setBatchNo(it.getBatchNo());
            exist.setProductionDate(it.getProductionDate());
            exist.setExpireDate(it.getExpireDate());
            itemMapper.updateById(exist);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void complete(Long id) {
        InboundOrder order = this.getById(id);
        if (order == null) throw new BizException(ResultCode.DATA_NOT_FOUND);
        if (!InboundStatusEnum.EXECUTING.getCode().equals(order.getStatus())) {
            throw new BizException(ResultCode.ILLEGAL_STATUS);
        }
        // 写库存
        List<InboundOrderItem> items = itemMapper.selectByOrderId(id);
        // 老数据兜底校验:所有明细必须有库位
        for (InboundOrderItem it : items) {
            if (it.getLocationId() == null) {
                throw new BizException("明细库位为空,请重新编辑单据补全库位");
            }
        }
        List<StockService.StockChangeItem> changes = new ArrayList<>();
        for (InboundOrderItem it : items) {
            StockService.StockChangeItem ch = new StockService.StockChangeItem();
            ch.setGoodsId(it.getGoodsId());
            ch.setLocationId(it.getLocationId());
            ch.setBatchNo(it.getBatchNo());
            ch.setQty(it.getActualQty() == null ? 0 : it.getActualQty());
            ch.setRemark("入库单:" + order.getOrderNo());
            changes.add(ch);
        }
        stockService.executeInbound(order.getOrderNo(), changes, StpUtil.getLoginIdAsLong());

        order.setStatus(InboundStatusEnum.FINISHED.getCode());
        order.setCompleteTime(LocalDateTime.now());
        this.updateById(order);
    }

    @Override
    public void cancel(Long id) {
        InboundOrder order = this.getById(id);
        if (order == null) throw new BizException(ResultCode.DATA_NOT_FOUND);
        if (InboundStatusEnum.FINISHED.getCode().equals(order.getStatus())) {
            throw new BizException(ResultCode.ILLEGAL_STATUS);
        }
        order.setStatus(InboundStatusEnum.CANCELED.getCode());
        this.updateById(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        InboundOrder order = this.getById(id);
        if (order == null) throw new BizException(ResultCode.DATA_NOT_FOUND);
        if (!InboundStatusEnum.DRAFT.getCode().equals(order.getStatus())) {
            throw new BizException("仅草稿状态可删除");
        }
        // 先删明细,再删主单(逻辑删除)
        itemMapper.deleteByOrderId(id);
        this.removeById(id);
    }

    @Override
    public Map<String, Object> detail(Long id) {
        InboundOrder order = this.getById(id);
        if (order == null) throw new BizException(ResultCode.DATA_NOT_FOUND);
        List<InboundOrderItem> items = itemMapper.selectByOrderId(id);
        Map<String, Object> map = new HashMap<>();
        map.put("order", order);
        map.put("items", items);
        return map;
    }

    private void validateItems(List<InboundOrderItem> items) {
        if (items == null || items.isEmpty()) {
            throw new BizException("入库单明细不能为空");
        }
        for (InboundOrderItem it : items) {
            if (it.getGoodsId() == null) throw new BizException("商品不能为空");
            if (it.getLocationId() == null) throw new BizException("库位不能为空");
            if (it.getPlanQty() == null || it.getPlanQty() <= 0) throw new BizException("计划数量必须大于0");
        }
    }

    private String generateOrderNo() {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        return Constants.ORDER_NO_PREFIX_INBOUND + date + IdUtil.getSnowflake(1, 1).nextIdStr().substring(10);
    }
}
