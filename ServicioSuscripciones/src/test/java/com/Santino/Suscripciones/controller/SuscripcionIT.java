package com.Santino.Suscripciones.controller;

import com.Santino.Suscripciones.config.RabbitConfig;
import com.Santino.Suscripciones.entity.Suscripcion;
import com.Santino.Suscripciones.repository.PlanRepository;
import com.Santino.Suscripciones.repository.SuscripcionRepository;
import com.Santino.Suscripciones.support.IntegrationTest;
import com.Santino.Suscripciones.support.JwtTokens;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@IntegrationTest
@DisplayName("Suscripcion API (integración)")
class SuscripcionIT {

    private static final long USER_ID = 7L;
    private static final String AUTH = JwtTokens.bearer(USER_ID, "ana@mail.com", "ana");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PlanRepository planRepository;

    @Autowired
    private SuscripcionRepository suscripcionRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RabbitConfig rabbitConfig;

    @BeforeEach
    void limpiar() {
        suscripcionRepository.deleteAll();
        planRepository.deleteAll();
        rabbitTemplate.receive(rabbitConfig.suscripcionCreadaQueueName, 50);
    }

    @Test
    @DisplayName("POST /suscribirse persiste Pendiente, publica el cobro y GET /suscripcion lo muestra")
    void suscribirsePublicaYConsulta() throws Exception {
        mockMvc.perform(post("/plan/crear")
                        .header("Authorization", AUTH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombre":"premium","descripcion":"Full HD","costo":1500}
                                """))
                .andExpect(status().isOk());

        mockMvc.perform(post("/suscribirse")
                        .header("Authorization", AUTH)
                        .param("plan", "premium"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userID").value((int) USER_ID))
                .andExpect(jsonPath("$.estado").value("Pendiente"));

        Suscripcion persistida = suscripcionRepository.findByUserID(USER_ID).orElseThrow();
        assertThat(persistida.getEstado()).isEqualTo("Pendiente");

        Message mensaje = rabbitTemplate.receive(rabbitConfig.suscripcionCreadaQueueName, 5000);
        assertThat(mensaje).isNotNull();
        String publicado = new String(mensaje.getBody(), StandardCharsets.UTF_8);
        assertThat(publicado).contains("\"userID\":7");
        assertThat(publicado).contains("\"email\":\"ana@mail.com\"");
        assertThat(publicado).contains("\"suscripcionID\":" + persistida.getID());
        assertThat(publicado).contains("\"monto\":1500");
        assertThat(publicado).contains("\"plan\":\"premium\"");

        mockMvc.perform(get("/suscripcion").header("Authorization", AUTH))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(persistida.getID().intValue()))
                .andExpect(jsonPath("$.idUser").value((int) USER_ID))
                .andExpect(jsonPath("$.estado").value("Pendiente"));
    }

    @Test
    @DisplayName("POST /suscribirse a un plan inexistente responde 404")
    void suscribirsePlanInexistente() throws Exception {
        mockMvc.perform(post("/suscribirse")
                        .header("Authorization", AUTH)
                        .param("plan", "inexistente"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"));
    }
}
