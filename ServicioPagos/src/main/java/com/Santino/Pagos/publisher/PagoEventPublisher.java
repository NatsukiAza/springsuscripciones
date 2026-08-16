package com.Santino.Pagos.publisher;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import com.Santino.Pagos.config.RabbitConfig;
import com.Santino.Pagos.dto.PagoRequest;
import com.Santino.Pagos.dto.SuscripcionResponse;

@Component
public class PagoEventPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final RabbitConfig rabbitConfig;

    public PagoEventPublisher(RabbitTemplate rabbitTemplate, RabbitConfig rabbitConfig) {
        this.rabbitTemplate = rabbitTemplate;
        this.rabbitConfig = rabbitConfig;
    }

    public void PagoSuccessPublisher(PagoRequest request, Long pagoId) {
        SuscripcionResponse pagoExitosoEvent = new SuscripcionResponse(request.suscripcionID(), pagoId, "Exitoso",
                request.email(),
                request.plan());

        rabbitTemplate.convertAndSend(rabbitConfig.pagosExchangeName, rabbitConfig.pagoExitosoRoutingKey,
                pagoExitosoEvent);

        System.out.println("Evento publicado: pago.exitoso para suscripcion " + request.suscripcionID());
    }

    public void PagoFailPublisher(PagoRequest request, Long pagoId) {
        SuscripcionResponse pagoFallidoEvent = new SuscripcionResponse(request.suscripcionID(), pagoId, "Fallido",
                request.email(),
                request.plan());

        rabbitTemplate.convertAndSend(rabbitConfig.pagosExchangeName, rabbitConfig.pagoFallidoRoutingKey,
                pagoFallidoEvent);

        System.out.println("Evento publicado: pago.fallido para suscripcion " + request.suscripcionID());
    }

}
