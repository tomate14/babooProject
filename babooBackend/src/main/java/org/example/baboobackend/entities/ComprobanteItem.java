package org.example.baboobackend.entities;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class ComprobanteItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private int idPedido;
    private int idProducto;
    private int stock;
    private int precioCompra;
}
