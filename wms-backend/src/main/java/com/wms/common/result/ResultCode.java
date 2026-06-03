package com.wms.common.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 业务状态码枚举
 */
@Getter
@AllArgsConstructor
public enum ResultCode {

    SUCCESS(200, "操作成功"),
    FAIL(500, "操作失败"),

    UNAUTHORIZED(401, "未登录或登录已过期"),
    FORBIDDEN(403, "无权限访问"),
    NOT_FOUND(404, "资源不存在"),
    PARAM_ERROR(400, "参数错误"),

    STOCK_NOT_ENOUGH(1001, "库存不足"),
    ILLEGAL_STATUS(1002, "状态非法,不允许此操作"),
    DATA_NOT_FOUND(1003, "数据不存在"),
    REPEAT_SUBMIT(1004, "请勿重复提交"),
    DATA_EXISTED(1005, "数据已存在"),
    PASSWORD_ERROR(1006, "用户名或密码错误"),
    USER_DISABLED(1007, "账号已被停用"),
    CAPTCHA_ERROR(1008, "验证码错误"),
    CAPTCHA_EXPIRED(1009, "验证码已过期");

    private final Integer code;
    private final String message;
}
