package com.wms.modules.outbound.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wms.common.PageQuery;
import com.wms.modules.outbound.dto.ApprovalHandleDTO;
import com.wms.modules.outbound.dto.OutboundApplyDTO;
import com.wms.modules.outbound.entity.OutboundOrder;
import com.wms.modules.outbound.entity.OutboundOrderItem;

import java.util.List;
import java.util.Map;

public interface OutboundOrderService extends IService<OutboundOrder> {

    Map<String, Object> page(PageQuery query, String orderNo, String status);

    Long apply(OutboundApplyDTO dto);

    void handleApproval(ApprovalHandleDTO dto);

    void ship(Long id);

    void complete(Long id);

    void cancel(Long id);

    Map<String, Object> detail(Long id);

    List<OutboundOrderItem> listItems(Long orderId);
}
