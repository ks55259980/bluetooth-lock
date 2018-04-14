package com.wemarklinks.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix="iot.wechat")
public class IOTWeChatConfig {
    
    private String token;
    private String encodingAESKey;
    private String appID;
    private String appsecret;
    
}

