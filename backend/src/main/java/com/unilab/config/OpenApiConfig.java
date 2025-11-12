package com.unilab.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

	@Bean
	public OpenAPI customOpenAPI() {
		final String bearerSchemeName = "bearerAuth";
		return new OpenAPI()
				.info(new Info().title("Lab Booking API").version("v1"))
				.components(new Components()
						.addSecuritySchemes(
								bearerSchemeName,
								new SecurityScheme()
										.type(SecurityScheme.Type.HTTP)
										.scheme("bearer")
										.bearerFormat("JWT")
						)
						.addParameters("idParam", new Parameter()
								.in("path")
								.name("id")
								.description("ID parameter")
								.required(true)
								.schema(new IntegerSchema().format("int64"))
						)
						.addParameters("roleNameParam", new Parameter()
								.in("path")
								.name("roleName")
								.description("Role name")
								.required(true)
								.schema(new StringSchema())
						)
				)
				.addSecurityItem(new SecurityRequirement().addList(bearerSchemeName));
	}
}


