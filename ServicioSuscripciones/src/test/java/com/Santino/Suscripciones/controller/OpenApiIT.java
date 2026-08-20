package com.Santino.Suscripciones.controller;

import com.Santino.Suscripciones.support.IntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@IntegrationTest
@DisplayName("OpenAPI (suscripciones)")
class OpenApiIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("GET /v3/api-docs es público, el resto de la API no")
    void specPublicoConJwt() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.info.title").value("Suscripciones"))
                .andExpect(jsonPath("$.paths['/suscribirse']").exists())
                .andExpect(jsonPath("$.paths['/plan/crear']").exists())
                .andExpect(jsonPath("$.components.securitySchemes['bearer-jwt']").exists())
                .andExpect(jsonPath("$.security[0]['bearer-jwt']").exists());
    }

    @Test
    @DisplayName("GET /swagger-ui/index.html es público")
    void swaggerUiPublico() throws Exception {
        mockMvc.perform(get("/swagger-ui/index.html"))
                .andExpect(status().isOk());
    }
}
