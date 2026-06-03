package com.wms.modules.system.vo;

import lombok.Data;

import java.util.List;

@Data
public class LoginVO {
    private String token;
    private String tokenName;
    private Long userId;
    private String username;
    private String realName;
    private String avatar;
    private List<String> roles;
    private List<String> permissions;
}
