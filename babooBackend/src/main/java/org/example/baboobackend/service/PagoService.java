package org.example.baboobackend.service;


import org.example.baboobackend.dto.PagoDTO;
import org.example.baboobackend.entities.Pago;

import java.util.List;

public interface PagoService {
    PagoDTO getPagosByIdPedido(int idPedido);
    List<Pago> getPagosByFecha(String fechaInicio, String fechaFin);
    void deletePagoById(int idPago);
    void deletePagosByIdPedido(int idPedido);
    Pago createPago(Pago nuevoPago);
    Pago updatePago(int idPago, Pago pagoActualizado);
}
