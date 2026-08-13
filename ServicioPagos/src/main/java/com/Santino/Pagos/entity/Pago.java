package com.Santino.Pagos.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "pago")
public class Pago {
    
    public Pago(){

    }

    public Pago(Long userID, Long suscripcionID, Long monto){
        this.userID = userID;
        this.suscripcionID = suscripcionID;
        this.monto = monto;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ID;

    @Column(nullable = false)
    private Long suscripcionID;

    @Column(nullable = false)
    private Long userID;

    private Long monto;

    private String estado;

    private Date fechaHora;

    public Long getID(){
        return ID;
    }

    public Long getUserID(){
        return userID;
    }

    public void setUserID(Long userID){
        this.userID = userID;
    }

    public Long getSuscripcionID(){
        return suscripcionID;
    }

    public void setSuscripcionID(Long suscripcionID){
        this.suscripcionID = suscripcionID;
    }

    public Long getMonto(){
        return monto;
    }

    public void setMonto(Long monto){
        this.monto = monto;
    }

    public String getEstado(){
        return estado;
    }

    public void setEstado(String estado){
        this.estado = estado;
    }

    public Date getFechaHora(){
        return fechaHora;
    }

}
