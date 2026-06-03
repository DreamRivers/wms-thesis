package com.wms.modules.outbound.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wms.common.PageQuery;
import com.wms.common.constant.Constants;
import com.wms.common.enums.OutboundStatusEnum;
import com.wms.common.exception.BizException;
import com.wms.common.result.ResultCode;
import com.wms.modules.outbound.dto.ApprovalHandleDTO;
import com.wms.modules.outbound.dto.OutboundApplyDTO;
import com.wms.modules.outbound.entity.OutboundApproval;
import com.wms.modules.outbound.entity.OutboundOrder;
import com.wms.modules.outbound.entity.OutboundOrderItem;
import com.wms.modules.outbound.mapper.OutboundApprovalMapper;
import com.wms.modules.outbound.mapper.OutboundOrderItemMapper;
import com.wms.modules.outbound.mapper.OutboundOrderMapper;
import com.wms.modules.outbound.service.OutboundOrderService;
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
public class OutboundOrderServiceImpl extends ServiceImpl<OutboundOrderMapper, OutboundOrder> implements OutboundOrderService {

    private final OutboundOrderItemMapper itemMapper;
    private final OutboundApprovalMapper approvalMapper;
    private final StockService stockService;

    public OutboundOrderServiceImpl(OutboundOrderItemMapper itemMapper,
                                    OutboundApprovalMapper approvalMapper,
                                    StockService stockService) {
        this.itemMapper = itemMapper;
        this.approvalMapper = approvalMapper;
        this.stockService = stockService;
    }

    @Override
    public Map<String, Object> page(PageQuery query, String orderNo, String status) {
        Page<OutboundOrder> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<OutboundOrder> q = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(orderNo)) q.like(OutboundOrder::getOrderNo, orderNo);
        if (StrUtil.isNotBlank(status)) q.eq(OutboundOrder::getStatus, status);
        q.orderByDesc(OutboundOrder::getCreateTime);
        Page<OutboundOrder> res = this.page(page, q);
        Map<String, Object> map = new HashMap<>();
        map.put("total", res.getTotal());
        map.put("list", res.getRecords());
        return map;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long apply(OutboundApplyDTO dto) {
        if (dto.getItems() == null || dto.getItems().isEmpty()) {
            throw new BizException("申请明细不能为空");
        }

        OutboundOrder order = new OutboundOrder();
        order.setId(dto.getId());
        order.setOrderNo(generateOrderNo());
        order.setOutboundType(dto.getOutboundType() == null ? 2 : dto.getOutboundType());
        order.setApplyDeptId(dto.getApplyDeptId());
        order.setApplicantId(StpUtil.getLoginIdAsLong());
        order.setApplyReason(dto.getApplyReason());
        order.setWarehouseId(dto.getWarehouseId());
        order.setStatus(OutboundStatusEnum.APPLY.getCode());
        order.setRemark(dto.getRemark());

        if (dto.getId() == null) {
            this.save(order);
        } else {
            this.updateById(order);
            itemMapper.deleteByOrderId(order.getId());
        }

        int totalQty = 0;
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (OutboundOrderItem it : dto.getItems()) {
            it.setId(null);
            it.setOrderId(order.getId());
            if (it.getActualQty() == null) it.setActualQty(it.getPlanQty());
            if (it.getUnitPrice() != null && it.getActualQty() != null) {
                it.setAmount(it.getUnitPrice().multiply(BigDecimal.valueOf(it.getActualQty())));
                totalAmount = totalAmount.add(it.getAmount());
            }
            totalQty += it.getActualQty() == null ? 0 : it.getActualQty();
            itemMapper.insert(it);
        }
        order.setTotalQty(totalQty);
        order.setTotalAmount(totalAmount);
        this.updateById(order);
        return order.getId();
    }

    /**
     * 两级审批:
     * - 部门负责人 step=1 审批 APPLY → APPROVING (通过) / REJECTED (驳回)
     * - 仓管 step=2 审批 APPROVING → APPROVED (通过) / REJECTED (驳回)
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handleApproval(ApprovalHandleDTO dto) {
        OutboundOrder order = this.getById(dto.getOrderId());
        if (order == null) throw new BizException(ResultCode.DATA_NOT_FOUND);

        OutboundApproval record = new OutboundApproval();
        record.setOrderId(order.getId());
        record.setStep(dto.getStep());
        record.setApproverId(StpUtil.getLoginIdAsLong());
        record.setAction(Boolean.TRUE.equals(dto.getPass()) ? "PASS" : "REJECT");
        record.setRemark(dto.getRemark());
        approvalMapper.insert(record);

        if (dto.getStep() == 1) {
            if (!OutboundStatusEnum.APPLY.getCode().equals(order.getStatus())) {
                throw new BizException(ResultCode.ILLEGAL_STATUS);
            }
            if (Boolean.TRUE.equals(dto.getPass())) {
                order.setStatus(OutboundStatusEnum.APPROVING.getCode());
            } else {
                order.setStatus(OutboundStatusEnum.REJECTED.getCode());
            }
            order.setDeptAuditUserId(StpUtil.getLoginIdAsLong());
            order.setDeptAuditTime(LocalDateTime.now());
            order.setDeptAuditRemark(dto.getRemark());
        } else if (dto.getStep() == 2) {
            if (!OutboundStatusEnum.APPROVING.getCode().equals(order.getStatus())) {
                throw new BizException(ResultCode.ILLEGAL_STATUS);
            }
            if (Boolean.TRUE.equals(dto.getPass())) {
                order.setStatus(OutboundStatusEnum.APPROVED.getCode());
            } else {
                order.setStatus(OutboundStatusEnum.REJECTED.getCode());
            }
            order.setWhAuditUserId(StpUtil.getLoginIdAsLong());
            order.setWhAuditTime(LocalDateTime.now());
            order.setWhAuditRemark(dto.getRemark());
        }
        this.updateById(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void ship(Long id) {
        OutboundOrder order = this.getById(id);
        if (order == null) throw new BizException(ResultCode.DATA_NOT_FOUND);
        if (!OutboundStatusEnum.APPROVED.getCode().equals(order.getStatus())) {
            throw new BizException(ResultCode.ILLEGAL_STATUS);
        }
        List<OutboundOrderItem> items = itemMapper.selectByOrderId(id);
        // 写库存(扣减)
        List<StockService.StockChangeItem> changes = new ArrayList<>();
        for (OutboundOrderItem it : items) {
            StockService.StockChangeItem ch = new StockService.StockChangeItem();
            ch.setGoodsId(it.getGoodsId());
            ch.setLocationId(it.getLocationId());
            ch.setBatchNo(it.getBatchNo());
            ch.setQty(it.getPlanQty() == null ? 0 : it.getPlanQty());
            ch.setRemark("出库单:" + order.getOrderNo());
            changes.add(ch);
        }
        stockService.executeOutbound(order.getOrderNo(), changes, StpUtil.getLoginIdAsLong());

        order.setStatus(OutboundStatusEnum.SHIPPED.getCode());
        order.setShipTime(LocalDateTime.now());
        order.setPickerId(StpUtil.getLoginIdAsLong());
        order.setPickTime(LocalDateTime.now());
        this.updateById(order);
    }

    @Override
    public void complete(Long id) {
        OutboundOrder order = this.getById(id);
        if (order == null) throw new BizException(ResultCode.DATA_NOT_FOUND);
        order.setStatus(OutboundStatusEnum.FINISHED.getCode());
        order.setCompleteTime(LocalDateTime.now());
        this.updateById(order);
    }

    @Override
    public void cancel(Long id) {
        OutboundOrder order = this.getById(id);
        if (order == null) throw new BizException(ResultCode.DATA_NOT_FOUND);
        if (OutboundStatusEnum.SHIPPED.getCode().equals(order.getStatus())
                || OutboundStatusEnum.FINISHED.getCode().equals(order.getStatus())) {
            throw new BizException(ResultCode.ILLEGAL_STATUS);
        }
        order.setStatus(OutboundStatusEnum.CANCELED.getCode());
        this.updateById(order);
    }

    @Override
    public Map<String, Object> detail(Long id) {
        OutboundOrder order = this.getById(id);
        if (order == null) throw new BizException(ResultCode.DATA_NOT_FOUND);
        List<OutboundOrderItem> items = itemMapper.selectByOrderId(id);
        List<OutboundApproval> approvals = approvalMapper.selectList(
                new LambdaQueryWrapper<OutboundApproval>().eq(OutboundApproval::getOrderId, id)
                        .orderByAsc(OutboundApproval::getCreateTime));
        Map<String, Object> map = new HashMap<>();
        map.put("order", order);
        map.put("items", items);
        map.put("approvals", approvals);
        return map;
    }

    @Override
    public List<OutboundOrderItem> listItems(Long orderId) {
        return itemMapper.selectByOrderId(orderId);
    }

    private String generateOrderNo() {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        return Constants.ORDER_NO_PREFIX_OUTBOUND + date + IdUtil.getSnowflake(1, 1).nextIdStr().substring(10);
    }
}
