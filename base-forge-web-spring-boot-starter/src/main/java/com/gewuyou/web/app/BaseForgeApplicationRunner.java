package com.gewuyou.web.app;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

/**
 * Base Forge 应用程序运行程序
 *
 * @author gewuyou
 * @since 2024-09-30 16:31:18
 */
@Slf4j
public class BaseForgeApplicationRunner implements ApplicationRunner {

    @Value("${spring.application.name}")
    private String applicationName;

    @Override
    public void run(ApplicationArguments args) {
        System.out.println("""
                
                ██████╗  █████╗ ███████╗███████╗    ███████╗ ██████╗ ██████╗  ██████╗ ███████╗
                ██╔══██╗██╔══██╗██╔════╝██╔════╝    ██╔════╝██╔═══██╗██╔══██╗██╔════╝ ██╔════╝
                ██████╔╝███████║███████╗█████╗█████╗█████╗  ██║   ██║██████╔╝██║  ███╗█████╗ \s
                ██╔══██╗██╔══██║╚════██║██╔══╝╚════╝██╔══╝  ██║   ██║██╔══██╗██║   ██║██╔══╝ \s
                ██████╔╝██║  ██║███████║███████╗    ██║     ╚██████╔╝██║  ██║╚██████╔╝███████╗
                ╚═════╝ ╚═╝  ╚═╝╚══════╝╚══════╝    ╚═╝      ╚═════╝ ╚═╝  ╚═╝ ╚═════╝ ╚══════╝
                """);
        log.info("==>>{} 启动成功<<==", applicationName);
    }
}
