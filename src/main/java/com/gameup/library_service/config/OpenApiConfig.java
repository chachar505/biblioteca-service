package com.gameup.library_service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Biblioteca Service API")
                        .version("1.0")
                        .description("Microservicio de biblioteca de juegos de usuarios - GameUp"));
    }

    @Bean
    public GroupedOpenApi bibliotecaApi() {
        return GroupedOpenApi.builder()
                .group("biblioteca")
                .pathsToMatch("/api/biblioteca/**")
                .build();
    }
}