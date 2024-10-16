package com.example.library.config;

import io.swagger.v3.oas.models.*;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for OpenAPI (Swagger) documentation.
 */
@Configuration
public class OpenAPIConfig {

	/**
	 * Configures the OpenAPI specification.
	 *
	 * @return OpenAPI instance with custom information
	 */
	@Bean
	public OpenAPI libraryOpenAPI() {
		return new OpenAPI()
				.info(new Info()
						.title("Library Management API")
						.version("1.0")
						.description("API documentation for the Library Management System"));
	}
}
