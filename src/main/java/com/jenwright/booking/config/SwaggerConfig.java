package com.jenwright.booking.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger/OpenAPI configuration for API documentation.
 *
 * Configures the OpenAPI specification for the Booking API with JWT Bearer authentication
 * scheme. This enables interactive API documentation through Swagger UI with the ability
 * to authenticate using JWT tokens.
 *
 * @author jen
 * @version 1.0
 */
@Configuration
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
public class SwaggerConfig {

    /**
     * Configures the OpenAPI specification metadata.
     *
     * Defines the API title, version, and description that will be displayed
     * in the Swagger UI documentation interface.
     *
     * @return OpenAPI configuration with API metadata
     */
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Booking API")
                        .version("1.0")
                        .description("REST API for appointment/room booking with JWT authentication"));
    }
}
