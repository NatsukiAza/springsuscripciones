package com.Santino.Pagos.controller;

import com.Santino.Pagos.support.IntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@IntegrationTest
@DisplayName("OpenAPI (pagos)")
class OpenApiIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("GET /v3/api-docs es público y documenta el webhook con Idempotency-Key")
    void specPublico() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.info.title").value("Pagos"))
                .andExpect(jsonPath("$.paths['/pagos/webhook']").exists())
                .andExpect(jsonPath("$.paths['/pagos/webhook'].post.parameters[*].name")
                        .value(hasItem("Idempotency-Key")));
    }

    @Test
    @DisplayName("GET /swagger-ui/index.html es público")
    void swaggerUiPublico() throws Exception {
        mockMvc.perform(get("/swagger-ui/index.html"))
                .andExpect(status().isOk());
    }
}
