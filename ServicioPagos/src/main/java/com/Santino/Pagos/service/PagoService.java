package com.Santino.Pagos.service;

import org.springframework.stereotype.Service;

import java.util.Random;

import com.Santino.Pagos.entity.Pago;
import com.Santino.Pagos.dto.PagoRequest;
import com.Santino.Pagos.dto.SuscripcionResponse;
import com.Santino.Pagos.repository.PagoRepository;

@Service
public class PagoService {

    private final PagoRepository pagoRepository;

    public PagoService(PagoRepository pagoRepository){
        this.pagoRepository = pagoRepository;
    }

    public SuscripcionResponse crearPago(PagoRequest request){
        
        String estado;
        Pago pago = new Pago(request.userID(), request.suscripcionID(), request.monto());
        Random rand = new Random();

        if(rand.nextInt(10) > 8){
            estado = "FAILED";
        }else{
            estado = "SUCCESS";
        }

        return new SuscripcionResponse(estado, pago.getID());
        
    }

}
