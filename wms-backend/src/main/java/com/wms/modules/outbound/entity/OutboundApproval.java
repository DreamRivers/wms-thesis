package com.wms.modules.outbound.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("wms_outbound_approval")
public class OutboundApproval {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long orderId;
    private Integer step;
    private Long approverId;
    private String approverName;
    private String action;
    private String remark;
    @TableField(fill = com.baomidou.mybatisplus.annotation.FieldFill.INSERT)
    private LocalDateTime createTime;
}
