package com.mylicense.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "license")
public class LicenseInfoConfig {

    private String subject;
    private String publicAlias;
    private String storePass;
    private String licensePath;
    private String publicKeysStorePath;

    /**
     * 证书有效结束日期
     */
    private String licenseValidDate;

    /**
     * 证书剩余时间
     */
    private String remainTime;
}
