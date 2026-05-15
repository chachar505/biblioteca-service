package com.gameup.library_service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("GameUp Library Service API")
                        .version("1.0")
                        .description("Microservicio encargado de gestionar la biblioteca de juegos de los usuarios"));
    }
}