package com.wms.modules.stock.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("wms_notification")
public class Notification {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String type;
    private String title;
    private String content;
    private Long refId;
    private String refType;
    private Long targetUserId;
    private String targetRole;
    private Integer readStatus;
    private LocalDateTime readTime;
    @TableField(fill = com.baomidou.mybatisplus.annotation.FieldFill.INSERT)
    private LocalDateTime createTime;
}
