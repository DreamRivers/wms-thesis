package com.wms.modules.inbound.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wms.common.PageQuery;
import com.wms.modules.inbound.dto.InboundAuditDTO;
import com.wms.modules.inbound.dto.InboundSaveDTO;
import com.wms.modules.inbound.entity.InboundOrder;
import com.wms.modules.inbound.entity.InboundOrderItem;

import java.util.List;
import java.util.Map;

public interface InboundOrderService extends IService<InboundOrder> {

    /** 分页查询 */
    Map<String, Object> page(PageQuery query, String orderNo, String status);

    /** 新增/修改草稿 */
    Long saveDraft(InboundSaveDTO dto);

    /** 提交审核 */
    void submit(Long id);

    /** 审核 */
    void audit(InboundAuditDTO dto);

    /** 执行入库(写入库存) */
    void execute(Long id, List<InboundOrderItem> items);

    /** 完成 */
    void complete(Long id);

    /** 作废 */
    void cancel(Long id);

    /** 详情(含 items) */
    Map<String, Object> detail(Long id);
}
