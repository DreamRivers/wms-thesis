package com.wms.modules.system.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 前端动态路由 VO
 */
@Data
public class RouteVO {
    private String path;
    private String component;
    private String name;
    private String redirect;
    private MetaVO meta;
    private List<RouteVO> children;

    @Data
    public static class MetaVO {
        private String title;
        private String icon;
        private Boolean hidden;
        private List<String> roles;
    }
}
