package org.example.baboobackend.dto;

import lombok.Data;
import org.example.baboobackend.entities.Producto;

import java.util.List;

@Data
public class CrearPedidoDTO {
    private List<Producto> productos;
    private String tipoComprobante;
    private int formaPago;
    private int total;
    private int dni;
}
