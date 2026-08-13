package com.Santino.Pagos.listener;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.Santino.Pagos.dto.SuscripcionResponse;
import com.Santino.Pagos.service.PagoService;
import com.Santino.Pagos.dto.PagoRequest;

@Component
public class SuscripcionCreadaListener {

    private final PagoService pagoService;

    public SuscripcionCreadaListener(PagoService pagoService){
        this.pagoService = pagoService;
    }

    @RabbitListener(queues = "suscripcion.creada")
    public void recibirSuscripcion(PagoRequest request){
        
        SuscripcionResponse response = pagoService.crearPago(request);

    }

}
