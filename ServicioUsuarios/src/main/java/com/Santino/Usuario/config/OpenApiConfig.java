package com.Santino.Usuario.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI usuarioOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Usuarios")
                        .version("1.0")
                        .description("""
                                Registro y login. Emite el JWT que viaja en `Authorization` \
                                de Suscripciones (`:8080`) y Pagos (`:8084`). Este servicio no publica eventos.
                                """))
                .servers(List.of(new Server().url("http://localhost:8081").description("Local (Compose)")))
                .components(new Components()
                        .addSecuritySchemes("bearer-jwt", new SecurityScheme()
                                .name("bearer-jwt")
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Token de POST /auth/login. Los endpoints de este servicio son públicos.")));
    }
}
