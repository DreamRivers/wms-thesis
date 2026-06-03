package com.wms.modules.system.dto;

import lombok.Data;

@Data
public class LoginDTO {
    private String username;
    private String password;
    private String captchaKey;
    private String captcha;
}
