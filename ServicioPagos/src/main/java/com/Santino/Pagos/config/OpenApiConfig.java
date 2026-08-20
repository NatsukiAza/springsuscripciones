package com.Santino.Pagos.config;

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
    public OpenAPI pagosOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Pagos")
                        .version("1.0")
                        .description("""
                                El cobro real entra por Rabbit (`suscripcion.creada`). Este HTTP es el webhook \
                                del PSP simulado: mismo contrato (header `Idempotency-Key`) e idempotencia con Redis SET NX.
                                """))
                .servers(List.of(new Server().url("http://localhost:8084").description("Local (Compose)")))
                .components(new Components()
                        .addSecuritySchemes("bearer-jwt", new SecurityScheme()
                                .name("bearer-jwt")
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("El webhook es público (lo llama el PSP). El resto del servicio exige JWT.")));
    }
}
