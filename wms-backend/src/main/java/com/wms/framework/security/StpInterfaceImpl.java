package com.wms.framework.security;

import cn.dev33.satoken.stp.StpInterface;
import com.wms.modules.system.service.SysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Sa-Token 自定义权限/角色加载
 */
@Component
@RequiredArgsConstructor
public class StpInterfaceImpl implements StpInterface {

    private final SysUserService userService;

    /** 返回该账号拥有的角色码 */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        return userService.getRoleCodesByUserId(Long.parseLong(loginId.toString()));
    }

    /** 返回该账号拥有的权限码 */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        List<String> perms = new ArrayList<>();
        userService.getRoleCodesByUserId(Long.parseLong(loginId.toString()))
                .forEach(code -> perms.add(code.toLowerCase()));
        return perms;
    }
}
