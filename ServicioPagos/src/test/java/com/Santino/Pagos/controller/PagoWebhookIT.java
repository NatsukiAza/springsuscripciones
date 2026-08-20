package com.Santino.Pagos.controller;

import com.Santino.Pagos.entity.Pago;
import com.Santino.Pagos.repository.PagoRepository;
import com.Santino.Pagos.service.IdempotencyService;
import com.Santino.Pagos.support.IntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@IntegrationTest
@DisplayName("Webhook de pagos (integración)")
class PagoWebhookIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PagoRepository pagoRepository;

    @Autowired
    private IdempotencyService idempotencyService;

    @Autowired
    private StringRedisTemplate redis;

    @BeforeEach
    void limpiar() {
        pagoRepository.deleteAll();
        Set<String> keys = redis.keys("idempotency:*");
        if (keys != null && !keys.isEmpty()) {
            redis.delete(keys);
        }
    }

    @Test
    @DisplayName("POST /pagos/webhook persiste un cobro SUCCESS o FAILURE")
    void webhookPersisteElPago() throws Exception {
        String body = """
                {"userID":7,"email":"ana@mail.com","suscripcionID":50,"monto":1500,"plan":"premium"}
                """;
        String idempotencyKey = "evt-" + UUID.randomUUID();

        mockMvc.perform(post("/pagos/webhook")
                        .header("Idempotency-Key", idempotencyKey)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());

        assertThat(pagoRepository.findAll()).hasSize(1);
        Pago pago = pagoRepository.findAll().getFirst();
        assertThat(pago.getUserID()).isEqualTo(7L);
        assertThat(pago.getSuscripcionID()).isEqualTo(50L);
        assertThat(pago.getMonto()).isEqualTo(1500L);
        assertThat(pago.getEstado()).isIn("SUCCESS", "FAILURE");
    }

    @Test
    @DisplayName("la misma Idempotency-Key no cobra dos veces")
    void webhookEsIdempotente() throws Exception {
        String body = """
                {"userID":7,"email":"ana@mail.com","suscripcionID":51,"monto":1500,"plan":"premium"}
                """;
        String idempotencyKey = "evt-" + UUID.randomUUID();

        mockMvc.perform(post("/pagos/webhook")
                        .header("Idempotency-Key", idempotencyKey)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());
        mockMvc.perform(post("/pagos/webhook")
                        .header("Idempotency-Key", idempotencyKey)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());

        assertThat(pagoRepository.findAll()).hasSize(1);
    }

    @Test
    @DisplayName("IdempotencyService contra Redis real: la segunda vez es duplicado")
    void idempotencyServiceUsaRedis() {
        String key = "it-" + UUID.randomUUID();
        assertThat(idempotencyService.esPrimeraVez(key)).isTrue();
        assertThat(idempotencyService.esPrimeraVez(key)).isFalse();
        idempotencyService.liberar(key);
        assertThat(idempotencyService.esPrimeraVez(key)).isTrue();
    }
}
