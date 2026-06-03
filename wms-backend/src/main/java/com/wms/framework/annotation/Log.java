package com.wms.framework.annotation;

import java.lang.annotation.*;

/**
 * 操作日志注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Log {

    /** 模块名 */
    String module() default "";

    /** 操作描述 */
    String action() default "";

    /** 是否记录请求参数 */
    boolean saveParam() default true;

    /** 是否记录返回结果 */
    boolean saveResult() default false;
}
