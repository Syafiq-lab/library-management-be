package com.example.commonlib.security.autoconfigure;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@AutoConfiguration
@EnableConfigurationProperties(SecuritySharedProperties.class)
public class PropertiesAutoConfiguration {}
