package com.Santino.Suscripciones.controller;

import com.Santino.Suscripciones.entity.Plan;
import com.Santino.Suscripciones.repository.PlanRepository;
import com.Santino.Suscripciones.service.PlanService;
import com.Santino.Suscripciones.support.IntegrationTest;
import com.Santino.Suscripciones.support.JwtTokens;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@IntegrationTest
@DisplayName("Plan API (integración)")
class PlanIT {

    private static final String AUTH = JwtTokens.bearer(7L, "ana@mail.com", "ana");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PlanRepository planRepository;

    @Autowired
    private PlanService planService;

    @BeforeEach
    void limpiar() {
        planRepository.deleteAll();
    }

    @Test
    @DisplayName("POST /plan/crear persiste el plan y GET /plan lo encuentra")
    void crearYConsultarPlan() throws Exception {
        mockMvc.perform(post("/plan/crear")
                        .header("Authorization", AUTH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombre":"premium","descripcion":"Full HD","costo":1500}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("premium"))
                .andExpect(jsonPath("$.costo").value(1500));

        mockMvc.perform(get("/plan")
                        .header("Authorization", AUTH)
                        .param("nombre", "premium"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));

        Optional<Plan> desdeCache = planService.mostrarPlan("premium");
        Optional<Plan> segundaLectura = planService.mostrarPlan("premium");
        assertThat(desdeCache).isPresent();
        assertThat(segundaLectura).isPresent();
        assertThat(segundaLectura.get().getNombre()).isEqualTo("premium");
        assertThat(segundaLectura.get().getCosto()).isEqualTo(1500L);
        assertThat(desdeCache.get().getDescripcion()).isEqualTo("Full HD");
    }

    @Test
    @DisplayName("POST /plan/crear duplicado responde 409")
    void crearPlanDuplicado() throws Exception {
        String body = """
                {"nombre":"premium","descripcion":"Full HD","costo":1500}
                """;
        mockMvc.perform(post("/plan/crear")
                        .header("Authorization", AUTH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());

        mockMvc.perform(post("/plan/crear")
                        .header("Authorization", AUTH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Plan already exists"));
    }

    @Test
    @DisplayName("POST /plan/crear sin token es rechazado")
    void crearPlanSinToken() throws Exception {
        mockMvc.perform(post("/plan/crear")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombre":"premium","descripcion":"Full HD","costo":1500}
                                """))
                .andExpect(status().isForbidden());
    }
}
