package com.wms;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 电商仓储物资管理系统 - 启动类
 *
 * @author WMS Team
 * @since 2026-06-01
 */
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class, SecurityFilterAutoConfiguration.class})
@EnableScheduling
@EnableAsync
@MapperScan("com.wms.modules.**.mapper")
public class WmsApplication {

    public static void main(String[] args) {
        SpringApplication.run(WmsApplication.class, args);
        System.out.println("\n" +
                "  __        __ ___  ___   _____   \n" +
                "  \\ \\      / / __|/ __| / /\\ \\  \n" +
                "   \\ \\ /\\ / /| _| \\__ \\/ /  \\ \\ \n" +
                "    \\ V  V / | |__|__/ / /    > >\n" +
                "     \\_/\\_/  |___||___/_/    /_/ \n" +
                "  WMS 启动成功  Knife4j: http://localhost:8080/doc.html\n");
    }
}
