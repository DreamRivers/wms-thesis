package com.wms.common;

import lombok.Data;

/**
 * 分页查询参数
 */
@Data
public class PageQuery {

    /** 当前页 */
    private Long pageNum = 1L;
    /** 每页大小 */
    private Long pageSize = 10L;
    /** 关键字 */
    private String keyword;
    /** 排序字段 */
    private String orderBy;
    /** 排序方式 asc/desc */
    private String orderDir = "desc";
}
