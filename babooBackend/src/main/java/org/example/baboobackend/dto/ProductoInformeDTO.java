package org.example.baboobackend.dto;

import lombok.Data;
import org.example.baboobackend.entities.Producto;

@Data
public class ProductoInformeDTO extends Producto {
    private String nombre;
    private double precio;

    public ProductoInformeDTO(String nombre, int stock, double precio) {
        this.nombre = nombre;
        this.stock = stock;
        this.precio = precio;
    }

}
