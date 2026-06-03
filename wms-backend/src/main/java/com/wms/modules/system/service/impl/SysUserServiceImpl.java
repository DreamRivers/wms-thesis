package com.wms.modules.system.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wms.common.PageQuery;
import com.wms.common.exception.BizException;
import com.wms.common.result.ResultCode;
import com.wms.modules.system.dto.LoginDTO;
import com.wms.modules.system.entity.SysMenu;
import com.wms.modules.system.entity.SysUser;
import com.wms.modules.system.mapper.SysMenuMapper;
import com.wms.modules.system.mapper.SysUserMapper;
import com.wms.modules.system.service.SysUserService;
import com.wms.modules.system.vo.LoginVO;
import com.wms.modules.system.vo.RouteVO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final SysMenuMapper menuMapper;

    @Override
    public LoginVO login(LoginDTO dto) {
        if (StrUtil.hasBlank(dto.getUsername(), dto.getPassword())) {
            throw new BizException(ResultCode.PARAM_ERROR);
        }
        SysUser user = this.lambdaQuery().eq(SysUser::getUsername, dto.getUsername()).one();
        if (user == null) {
            throw new BizException(ResultCode.PASSWORD_ERROR);
        }
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new BizException(ResultCode.PASSWORD_ERROR);
        }
        if (user.getStatus() == null || user.getStatus() == 0) {
            throw new BizException(ResultCode.USER_DISABLED);
        }
        // 登录
        StpUtil.login(user.getId());
        // 更新最后登录
        user.setLastLoginTime(LocalDateTime.now());
        this.updateById(user);

        LoginVO vo = new LoginVO();
        vo.setToken(StpUtil.getTokenValue());
        vo.setTokenName(StpUtil.getTokenName());
        vo.setUserId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setRealName(user.getRealName());
        vo.setAvatar(user.getAvatar());
        vo.setRoles(getRoleCodesByUserId(user.getId()));
        return vo;
    }

    @Override
    public void logout() {
        StpUtil.logout();
    }

    @Override
    public Map<String, Object> getCurrentUserInfo() {
        Long uid = StpUtil.getLoginIdAsLong();
        SysUser user = this.getById(uid);
        if (user == null) throw new BizException(ResultCode.UNAUTHORIZED);
        Map<String, Object> map = new HashMap<>();
        map.put("userId", user.getId());
        map.put("username", user.getUsername());
        map.put("realName", user.getRealName());
        map.put("avatar", user.getAvatar());
        map.put("roles", getRoleCodesByUserId(uid));
        return map;
    }

    @Override
    public List<RouteVO> getMenuRoutes() {
        Long uid = StpUtil.getLoginIdAsLong();
        List<SysMenu> menus = menuMapper.selectMenusByUserId(uid);
        return buildRoutes(menus, 0L);
    }

    private List<RouteVO> buildRoutes(List<SysMenu> all, Long parentId) {
        return all.stream()
                .filter(m -> Objects.equals(m.getParentId(), parentId))
                .map(m -> {
                    RouteVO r = new RouteVO();
                    r.setPath(m.getPath());
                    r.setComponent(m.getComponent());
                    r.setName(m.getMenuName());
                    RouteVO.MetaVO meta = new RouteVO.MetaVO();
                    meta.setTitle(m.getMenuName());
                    meta.setIcon(m.getIcon());
                    meta.setHidden(m.getVisible() == null || m.getVisible() == 0);
                    r.setMeta(meta);
                    r.setChildren(buildRoutes(all, m.getId()));
                    return r;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getRoleCodesByUserId(Long userId) {
        return baseMapper.selectRoleCodesByUserId(userId);
    }

    @Override
    public Map<String, Object> page(PageQuery query, String realName, Integer status) {
        Page<SysUser> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<SysUser> q = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(realName)) q.like(SysUser::getRealName, realName);
        if (status != null) q.eq(SysUser::getStatus, status);
        q.orderByDesc(SysUser::getCreateTime);
        IPage<SysUser> res = this.page(page, q);
        // 密码置空
        res.getRecords().forEach(u -> u.setPassword(null));
        Map<String, Object> map = new HashMap<>();
        map.put("total", res.getTotal());
        map.put("list", res.getRecords());
        return map;
    }
}
