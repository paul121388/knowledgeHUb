package com.paul.knowledgeHub;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 主类（项目启动入口）
 */
@SpringBootApplication
@MapperScan("com.paul.knowledgeHub.mapper")
@EnableScheduling
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
public class MainApplication {

    public static void main(String[] args) {
//        SpringApplicationBuilder builder = new SpringApplicationBuilder(MainApplication.class);
//        builder.application().setAdditionalProfiles("dev");
//        builder.run(args);

        SpringApplication.run(MainApplication.class, args);
    }

}
