package com.Santino.Pagos.listener;

import com.Santino.Pagos.config.RabbitConfig;
import com.Santino.Pagos.dto.PagoRequest;
import com.Santino.Pagos.entity.Pago;
import com.Santino.Pagos.repository.PagoRepository;
import com.Santino.Pagos.support.IntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Duration;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@IntegrationTest
@DisplayName("SuscripcionCreadaListener (integración)")
class SuscripcionCreadaListenerIT {

    @Autowired
    private PagoRepository pagoRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RabbitConfig rabbitConfig;

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
    @DisplayName("un evento suscripcion-creada persiste el cobro")
    void consumeElEventoYPersisteElPago() {
        PagoRequest request = new PagoRequest(7L, "ana@mail.com", 80L, 1500L, "premium");

        rabbitTemplate.convertAndSend(
                rabbitConfig.suscripcionExchangeName,
                rabbitConfig.suscripcionRoutingKey,
                request);

        await().atMost(Duration.ofSeconds(10)).untilAsserted(() -> {
            assertThat(pagoRepository.findAll()).hasSize(1);
            Pago pago = pagoRepository.findAll().getFirst();
            assertThat(pago.getUserID()).isEqualTo(7L);
            assertThat(pago.getSuscripcionID()).isEqualTo(80L);
            assertThat(pago.getMonto()).isEqualTo(1500L);
            assertThat(pago.getEstado()).isIn("SUCCESS", "FAILURE");
        });
    }
}
