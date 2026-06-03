package com.wms.modules.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wms.common.PageQuery;
import com.wms.modules.system.dto.LoginDTO;
import com.wms.modules.system.entity.SysUser;
import com.wms.modules.system.vo.LoginVO;
import com.wms.modules.system.vo.RouteVO;

import java.util.List;
import java.util.Map;

public interface SysUserService extends IService<SysUser> {

    /** 登录 */
    LoginVO login(LoginDTO dto);

    /** 注销 */
    void logout();

    /** 获取当前用户信息 */
    Map<String, Object> getCurrentUserInfo();

    /** 获取动态路由 */
    List<RouteVO> getMenuRoutes();

    /** 获取用户角色编码 */
    List<String> getRoleCodesByUserId(Long userId);

    /** 分页查询 */
    Map<String, Object> page(PageQuery query, String realName, Integer status);
}
