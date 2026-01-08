package com.example.commonlib.security.autoconfigure;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@Slf4j
@AutoConfiguration
@EnableMethodSecurity
public class MethodSecurityAutoConfiguration implements InitializingBean {

	@Override
	public void afterPropertiesSet() {
		log.debug("Method security enabled via @EnableMethodSecurity");
	}
}

