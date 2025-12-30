package com.example.commonlib.security.autoconfigure;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@AutoConfiguration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
@Slf4j
public class ReactiveJwtDecoderAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean(ReactiveJwtDecoder.class)
	public ReactiveJwtDecoder reactiveJwtDecoder(@Value("${security.jwt.secret}") String jwtSecret) {
		log.info("Auto-configuring ReactiveJwtDecoder (HS256)");

		byte[] keyBytes;
		try {
			keyBytes = Base64.getDecoder().decode(jwtSecret);
			log.debug("JWT secret decoded as Base64 (len={})", keyBytes.length);
		} catch (IllegalArgumentException ex) {
			keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
			log.debug("JWT secret treated as plain text (len={})", keyBytes.length);
		}

		SecretKey secretKey = new SecretKeySpec(keyBytes, "HmacSHA256");

		return NimbusReactiveJwtDecoder.withSecretKey(secretKey)
				.macAlgorithm(MacAlgorithm.HS256)
				.build();
	}
}
