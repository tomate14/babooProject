package org.example.baboobackend.dto;

import lombok.Data;
import org.example.baboobackend.entities.Caja;

@Data
public class CierreCajaDTO extends Caja {
    public CierreCajaDTO(Caja cierreCaja) {
        this.setContado(cierreCaja.getContado());
        this.setGastos(cierreCaja.getGastos());
        this.setGanancia(cierreCaja.getGanancia());
        this.setTarjeta(cierreCaja.getTarjeta());
        this.setCuentaDni(cierreCaja.getCuentaDni());
        this.setTransferencia(cierreCaja.getTransferencia());
        this.setFecha(cierreCaja.getFecha());
    }

    private double diferencia;
}
