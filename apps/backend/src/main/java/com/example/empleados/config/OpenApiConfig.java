package com.example.empleados.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    OpenAPI openAPI() {
        return new OpenAPI()
            .info(new Info().title("Empleados API").version("1.1.0").description("CRUD de empleados y departamentos con autenticación básica"))
            .addSecurityItem(new SecurityRequirement().addList("basicAuth"))
            .schemaRequirement("basicAuth", new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("basic"));
    }
}
