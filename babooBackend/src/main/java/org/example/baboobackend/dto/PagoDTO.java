package org.example.baboobackend.dto;

import lombok.Data;
import org.example.baboobackend.entities.Pago;

import java.util.List;

@Data
public class PagoDTO {
    private List<Pago> pagos;
    private String nombreCliente;
    private String telefonoCliente;
    private String emailCliente;

    public PagoDTO(List<Pago> pagos) {
        this.pagos = pagos;
    }
}
