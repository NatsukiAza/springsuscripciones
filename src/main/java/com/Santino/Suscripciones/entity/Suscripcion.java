package com.Santino.Suscripciones.entity;

import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import java.util.Date;
import java.util.UUID;


@Entity
@Table(name = "suscripcion")
public class Suscripcion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID ID;
    private Long planID;
    private Long userID;
    private Date fechaIni;
    private Date fechaFin;

    public Suscripcion(){}

    public Suscripcion(Long planID){
        this.planID = planID;
    }

    public UUID getID(){
        return ID;
    }

    public Long getUserID(){
        return userID;
    }

    public void setUserID(Long userID){
        this.userID = userID;
    }

    public Long getPlanID(){
        return planID;
    }

    public void setPlanID(Long planID){
        this.planID = planID;
    }

    public Date getFechaIni(){
        return fechaIni;
    }

    public void setFechaIni(Date fechaIni){
        this.fechaIni = fechaIni;
    }

    public Date getFechaFin(){
        return fechaFin;
    }

    public void setFechaFin(Date fechaFin){
        this.fechaFin = fechaFin;
    }

}
