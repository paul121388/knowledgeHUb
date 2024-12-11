package com.paul.knowledgeHub.config;

import com.jd.platform.hotkey.client.ClientStarter;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 全局跨域配置
 */
@Configuration
@Data
@ConfigurationProperties(prefix = "hotkey")
public class HotKeyConfig{
    private String etcdServer = "http://127.0.0.1:2379";

    private String appName = "knowledgeHub";

    private int caffeineSize = 10000;

    private long pushPeriod = 1000L;

    @Bean
    public void initHotkey(){
        ClientStarter.Builder builder = new ClientStarter.Builder();
        ClientStarter starter = builder.setAppName(appName)
                .setEtcdServer(etcdServer)
                .setCaffeineSize(caffeineSize)
                .setPushPeriod(pushPeriod).build();
        starter.startPipeline();
    }
}
