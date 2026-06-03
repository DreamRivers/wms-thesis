package com.wms.framework.aspect;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.servlet.ServletUtil;
import cn.hutool.json.JSONUtil;
import com.wms.framework.annotation.Log;
import com.wms.modules.system.entity.SysOperationLog;
import com.wms.modules.system.mapper.SysOperationLogMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

/**
 * 操作日志切面
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class OperationLogAspect {

    private final SysOperationLogMapper logMapper;

    @Around("@annotation(com.wms.framework.annotation.Log)")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        long t1 = System.currentTimeMillis();
        Object result = null;
        Throwable err = null;
        try {
            result = pjp.proceed();
            return result;
        } catch (Throwable e) {
            err = e;
            throw e;
        } finally {
            try {
                long cost = System.currentTimeMillis() - t1;
                saveLog(pjp, result, err, cost);
            } catch (Exception e) {
                log.warn("[操作日志记录失败] {}", e.getMessage());
            }
        }
    }

    @Async
    public void saveLog(ProceedingJoinPoint pjp, Object result, Throwable err, long cost) {
        MethodSignature sig = (MethodSignature) pjp.getSignature();
        Log lg = sig.getMethod().getAnnotation(Log.class);

        SysOperationLog log = new SysOperationLog();
        log.setModule(lg.module());
        log.setAction(lg.action());
        log.setMethod(pjp.getTarget().getClass().getName() + "." + sig.getName());

        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs != null) {
            HttpServletRequest req = attrs.getRequest();
            log.setRequestUrl(req.getRequestURI());
            log.setRequestMethod(req.getMethod());
            log.setIp(ServletUtil.getClientIP(req));
            if (lg.saveParam()) {
                log.setRequestParam(StrUtil.maxLength(JSONUtil.toJsonStr(pjp.getArgs()), 2000));
            }
        }
        if (lg.saveResult() && result != null) {
            log.setResponse(StrUtil.maxLength(JSONUtil.toJsonStr(result), 2000));
        }
        log.setCostTime(cost);
        log.setStatus(err == null ? 1 : 0);
        if (err != null) {
            log.setErrorMsg(StrUtil.maxLength(err.getMessage(), 1000));
        }
        try {
            Long uid = StpUtil.getLoginIdAsLong();
            log.setUserId(uid);
        } catch (Exception ignore) { }
        log.setCreateTime(LocalDateTime.now());
        logMapper.insert(log);
    }
}
