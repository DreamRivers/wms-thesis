package com.wms.modules.system.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;
import com.wms.common.exception.BizException;
import com.wms.common.result.Result;
import com.wms.common.result.ResultCode;
import com.wms.modules.system.dto.LoginDTO;
import com.wms.modules.system.service.SysUserService;
import com.wms.modules.system.vo.LoginVO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 登录/注销
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class LoginController {

    private final SysUserService userService;
    private final StringRedisTemplate redisTemplate;

    /** 简易图形验证码(免图片库,返回 base64 文字码) */
    @GetMapping("/captcha")
    @SaIgnore
    public Result<Map<String, String>> captcha() {
        String code = String.valueOf(1000 + (int) (Math.random() * 8999));
        String key = "captcha:" + UUID.randomUUID().toString();
        redisTemplate.opsForValue().set(key, code, 5, TimeUnit.MINUTES);
        Map<String, String> map = new HashMap<>();
        map.put("captchaKey", key);
        map.put("captchaImg", code);  // 演示用,生产应返回图片 base64
        return Result.ok(map);
    }

    @PostMapping("/login")
    @SaIgnore
    public Result<LoginVO> login(@RequestBody LoginDTO dto) {
        // 验证码校验(可关闭)
        if (StrUtil.isNotBlank(dto.getCaptchaKey()) && StrUtil.isNotBlank(dto.getCaptcha())) {
            String real = redisTemplate.opsForValue().get(dto.getCaptchaKey());
            if (real == null) throw new BizException(ResultCode.CAPTCHA_EXPIRED);
            if (!real.equalsIgnoreCase(dto.getCaptcha())) throw new BizException(ResultCode.CAPTCHA_ERROR);
        }
        return Result.ok(userService.login(dto));
    }

    @PostMapping("/logout")
    public Result<?> logout() {
        userService.logout();
        return Result.ok();
    }

    @GetMapping("/info")
    public Result<?> info() {
        return Result.ok(userService.getCurrentUserInfo());
    }

    @GetMapping("/routes")
    public Result<?> routes() {
        return Result.ok(userService.getMenuRoutes());
    }

    /** 校验登录态 */
    @GetMapping("/check")
    public Result<?> check() {
        return Result.ok(StpUtil.isLogin());
    }
}
