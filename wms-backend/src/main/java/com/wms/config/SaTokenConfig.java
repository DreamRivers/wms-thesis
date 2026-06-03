package com.wms.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.router.SaRouter;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Sa-Token 配置
 */
@Configuration
public class SaTokenConfig implements WebMvcConfigurer {

    /** 注册 Sa-Token 拦截器 */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SaInterceptor(handle -> {
            // 路由拦截匹配规则
            SaRouter.match("/**")
                    .notMatch("/auth/login", "/auth/captcha", "/auth/logout", "/doc.html",
                            "/webjars/**", "/swagger-resources/**", "/v2/api-docs/**",
                            "/v3/api-docs/**", "/druid/**", "/favicon.ico", "/error",
                            "/ws/**")
                    .check(r -> StpUtil.checkLogin());
        })).addPathPatterns("/**");
    }

    /** 跨域配置 */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
