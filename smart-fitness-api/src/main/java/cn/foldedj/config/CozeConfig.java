package cn.foldedj.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

/**
 * Coze配置类，用于从application.yml中读取Coze相关配置
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "coze.assistant")
public class CozeConfig {
    
    /**
     * Coze API Token
     */
    private String token;
    
    /**
     * Coze API 基础URL
     */
    private String baseUrl;
}