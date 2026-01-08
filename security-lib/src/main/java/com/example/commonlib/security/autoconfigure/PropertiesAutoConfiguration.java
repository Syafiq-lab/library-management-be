package com.example.commonlib.security.autoconfigure;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@Slf4j
@AutoConfiguration
@EnableConfigurationProperties(SecuritySharedProperties.class)
public class PropertiesAutoConfiguration implements InitializingBean {

    @Override
    public void afterPropertiesSet() {
        log.debug("SecuritySharedProperties enabled via @EnableConfigurationProperties");
    }
}

