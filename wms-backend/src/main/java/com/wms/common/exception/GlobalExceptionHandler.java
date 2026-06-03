package com.wms.common.exception;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.hutool.core.util.StrUtil;
import com.wms.common.result.Result;
import com.wms.common.result.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.util.stream.Collectors;

/**
 * 全局异常处理
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /** 业务异常 */
    @ExceptionHandler(BizException.class)
    public Result<?> handleBiz(BizException e, HttpServletRequest req) {
        log.warn("[业务异常] {} - {}", req.getRequestURI(), e.getMessage());
        return Result.fail(e.getCode(), e.getMessage());
    }

    /** Sa-Token 未登录 */
    @ExceptionHandler(NotLoginException.class)
    public ResponseEntity<Result<?>> handleNotLogin(NotLoginException e) {
        log.warn("[未登录] {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Result.fail(ResultCode.UNAUTHORIZED));
    }

    /** Sa-Token 无权限 */
    @ExceptionHandler(NotPermissionException.class)
    public ResponseEntity<Result<?>> handleNoPerm(NotPermissionException e) {
        log.warn("[无权限] {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Result.fail(ResultCode.FORBIDDEN));
    }

    /** @Valid 校验失败 */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<?> handleValid(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(";"));
        log.warn("[参数校验失败] {}", msg);
        return Result.fail(ResultCode.PARAM_ERROR.getCode(), StrUtil.blankToDefault(msg, "参数错误"));
    }

    /** 表单绑定异常 */
    @ExceptionHandler(BindException.class)
    public Result<?> handleBind(BindException e) {
        String msg = e.getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(";"));
        return Result.fail(ResultCode.PARAM_ERROR.getCode(), StrUtil.blankToDefault(msg, "参数错误"));
    }

    /** 兜底异常 */
    @ExceptionHandler(Exception.class)
    public Result<?> handleAll(Exception e, HttpServletRequest req) {
        log.error("[系统异常] {} - ", req.getRequestURI(), e);
        return Result.fail(ResultCode.FAIL.getCode(), "系统繁忙,请稍后再试");
    }
}
