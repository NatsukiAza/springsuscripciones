package com.Santino.Suscripciones.exception;

public class PlanAlreadyExists extends RuntimeException {
    public PlanAlreadyExists(String message){
        super(message);
    }
}
