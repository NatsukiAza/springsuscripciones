package com.Santino.Pagos.publisher;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.Santino.Pagos.config.RabbitConfig;
import com.Santino.Pagos.dto.SuscripcionResponse;

@Component
public class PagoEventPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final RabbitConfig rabbitConfig;

    public PagoEventPublisher(RabbitTemplate rabbitTemplate, RabbitConfig rabbitConfig) {
        this.rabbitTemplate = rabbitTemplate;
        this.rabbitConfig = rabbitConfig;
    }

    public void PagoSuccessPublisher(Long suscripcionId, Long pagoId) {
        SuscripcionResponse pagoExitosoEvent = new SuscripcionResponse(suscripcionId, pagoId);

        rabbitTemplate.convertAndSend(rabbitConfig.pagosExchangeName, rabbitConfig.pagoExitosoRoutingKey,
                pagoExitosoEvent);

        System.out.println("Evento publicado: pago.exitoso para suscripcion " + suscripcionId);
    }

    public void PagoFailPublisher(Long suscripcionId, Long pagoId) {
        SuscripcionResponse pagoFallidoEvent = new SuscripcionResponse(suscripcionId, pagoId);

        rabbitTemplate.convertAndSend(rabbitConfig.pagosExchangeName, rabbitConfig.pagoFallidoRoutingKey,
                pagoFallidoEvent);

        System.out.println("Evento publicado: pago.fallido para suscripcion " + suscripcionId);
    }

}
