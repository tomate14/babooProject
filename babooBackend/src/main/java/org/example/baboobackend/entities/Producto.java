package org.example.baboobackend.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Producto {

    public static final String ID = "id";
    public static final String NOMBRE = "nombre";
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String nombre;

    @Column(name = "codigo_barra")
    private String codigoBarra;

    @Column(name = "precio_compra", nullable = false)
    private Integer precioCompra;

    @Column(name = "precio_venta", nullable = false)
    private Integer precioVenta;

    @Column(name = "id_proveedor", nullable = false)
    private Integer idProveedor;

    @Column(nullable = false)
    private Integer stock;

    @Transient
    private String nombreProveedor;

    @OneToOne
    @JoinColumn(name = "id_proveedor", referencedColumnName = "dni", insertable = false, updatable = false)
    private Cliente proveedor;

}