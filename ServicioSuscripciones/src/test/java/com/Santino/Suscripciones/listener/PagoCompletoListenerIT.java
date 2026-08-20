package com.Santino.Suscripciones.listener;

import com.Santino.Suscripciones.config.RabbitConfig;
import com.Santino.Suscripciones.dto.PagoResponse;
import com.Santino.Suscripciones.entity.Suscripcion;
import com.Santino.Suscripciones.repository.SuscripcionRepository;
import com.Santino.Suscripciones.support.IntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@IntegrationTest
@DisplayName("PagoCompletoListener (integración)")
class PagoCompletoListenerIT {

    @Autowired
    private SuscripcionRepository suscripcionRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RabbitConfig rabbitConfig;

    @BeforeEach
    void limpiar() {
        suscripcionRepository.deleteAll();
    }

    @Test
    @DisplayName("pago exitoso pasa la suscripción a Activo")
    void pagoExitosoActivaLaSuscripcion() {
        Suscripcion suscripcion = suscripcionRepository.save(new Suscripcion(10L, 7L));

        rabbitTemplate.convertAndSend(
                rabbitConfig.pagosExchangeName,
                rabbitConfig.pagoExitosoRoutingKey,
                new PagoResponse(suscripcion.getID(), 99L, "Exitoso", "ana@mail.com", 10L));

        await().atMost(Duration.ofSeconds(10)).untilAsserted(() -> {
            Suscripcion actualizada = suscripcionRepository.findById(suscripcion.getID()).orElseThrow();
            assertThat(actualizada.getEstado()).isEqualTo("Activo");
        });
    }

    @Test
    @DisplayName("pago fallido pasa la suscripción a Pago rechazado")
    void pagoFallidoRechazaLaSuscripcion() {
        Suscripcion suscripcion = suscripcionRepository.save(new Suscripcion(10L, 7L));

        rabbitTemplate.convertAndSend(
                rabbitConfig.pagosExchangeName,
                rabbitConfig.pagoFallidoRoutingKey,
                new PagoResponse(suscripcion.getID(), 99L, "Fallido", "ana@mail.com", 10L));

        await().atMost(Duration.ofSeconds(10)).untilAsserted(() -> {
            Suscripcion actualizada = suscripcionRepository.findById(suscripcion.getID()).orElseThrow();
            assertThat(actualizada.getEstado()).isEqualTo("Pago rechazado");
        });
    }
}
