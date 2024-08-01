package org.example.baboobackend.dto;

import lombok.Data;

@Data
public class PedidoDeudaDTO {
    private String nombreCliente;
    private String telefonoCliente;
    private String emailCliente;
    private int idPedido;
    private int saldoRestante;
    private String fechaUltimoPago;
    private int montoUltimoPago;
    private String numeroComprobante;
}
