package com.Santino.Pagos.service;

import org.springframework.stereotype.Service;

import java.util.Random;

import com.Santino.Pagos.entity.Pago;
import com.Santino.Pagos.publisher.PagoEventPublisher;
import com.Santino.Pagos.dto.PagoRequest;
import com.Santino.Pagos.repository.PagoRepository;

@Service
public class PagoService {

    private final PagoRepository pagoRepository;
    private final PagoEventPublisher pagoEventPubliser;

    public PagoService(PagoRepository pagoRepository, PagoEventPublisher pagoEventPublisher) {
        this.pagoRepository = pagoRepository;
        this.pagoEventPubliser = pagoEventPublisher;
    }

    public void crearPago(PagoRequest request) {

        String estado;
        Pago pago = new Pago(request.userID(), request.suscripcionID(), request.monto());
        Random rand = new Random();

        if (rand.nextInt(10) < 7) {
            estado = "SUCCESS";
        } else {
            estado = "FAILURE";
        }

        pago.setEstado(estado);

        try {
            Pago pagoGuardado = pagoRepository.save(pago);
            switch (estado) {
                case "SUCCESS":
                    pagoEventPubliser.PagoSuccessPublisher(request, pagoGuardado.getID());
                    break;
                case "FAILURE":
                    pagoEventPubliser.PagoFailPublisher(request, pagoGuardado.getID());
                    break;
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage());
        }

    }

}
