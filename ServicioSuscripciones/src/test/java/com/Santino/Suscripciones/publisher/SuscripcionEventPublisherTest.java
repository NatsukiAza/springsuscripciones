package com.Santino.Suscripciones.publisher;

import com.Santino.Suscripciones.config.RabbitConfig;
import com.Santino.Suscripciones.dto.PagoRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("SuscripcionEventPublisher")
class SuscripcionEventPublisherTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    private SuscripcionEventPublisher publisher;

    @BeforeEach
    void setUp() {
        RabbitConfig rabbitConfig = new RabbitConfig();
        rabbitConfig.suscripcionExchangeName = "suscripcion-exchange";
        rabbitConfig.suscripcionRoutingKey = "suscripcion-creada";
        publisher = new SuscripcionEventPublisher(rabbitTemplate, rabbitConfig);
    }

    @Test
    @DisplayName("publica PagoRequest en el exchange de suscripción con la routing key correcta")
    void publicaEnElExchangeYRoutingKeyConfigurados() {
        PagoRequest evento = new PagoRequest(7L, "ana@mail.com", 50L, 1500L, "premium");

        publisher.publicarSuscripcion(evento);

        verify(rabbitTemplate).convertAndSend("suscripcion-exchange", "suscripcion-creada", evento);
    }
}
