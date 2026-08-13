package com.Santino.Pagos.publisher;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class PagoEventPublisher {
    
    private final RabbitTemplate rabbitTemplate;

    public PagoEventPublisher(RabbitTemplate rabbitTemplate){
        this.rabbitTemplate = rabbitTemplate;
    }

}
