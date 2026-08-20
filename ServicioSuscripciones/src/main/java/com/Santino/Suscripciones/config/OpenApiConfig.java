package com.Santino.Suscripciones.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI suscripcionesOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Suscripciones")
                        .version("1.0")
                        .description("""
                                Planes y suscripciones. Crear una suscripción persiste en estado Pendiente \
                                y publica `suscripcion.creada`; el cobro llega después por RabbitMQ, no por este HTTP.
                                """))
                .servers(List.of(new Server().url("http://localhost:8080").description("Local (Compose)")))
                .addSecurityItem(new SecurityRequirement().addList("bearer-jwt"))
                .components(new Components()
                        .addSecuritySchemes("bearer-jwt", new SecurityScheme()
                                .name("bearer-jwt")
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("JWT emitido por Usuarios (`POST :8081/auth/login`). Authorize en la UI.")));
    }
}
