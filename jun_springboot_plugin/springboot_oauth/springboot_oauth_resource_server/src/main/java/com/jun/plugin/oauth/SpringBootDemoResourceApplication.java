package com.jun.plugin.oauth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;

/**
 * 启动器.
 *
 * @author Wujun
 * @date 2020/1/9 上午11:38
 * @version V1.0
 */
@EnableResourceServer
@SpringBootApplication
public class SpringBootDemoResourceApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringBootDemoResourceApplication.class, args);
    }

}
