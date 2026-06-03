package com.wms.modules.basic.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.wms.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("wms_category")
public class Category extends BaseEntity {
    private Long parentId;
    private String categoryName;
    private Integer sort;
    private Integer status;
}
