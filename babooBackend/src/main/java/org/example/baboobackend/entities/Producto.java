package org.example.baboobackend.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class Producto {

    public static final String ID = "id";
    public static final String NOMBRE = "nombre";
    public static final String ID_PROVEEDOR = "idProveedor";
    public static final String STOCK = "stock";
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


    public int calcularPrecioConAumento(Cliente proveedor) {
        double porcentajeAumento = proveedor.getPorcentajeRemarcar();
        double aumento = precioCompra * (porcentajeAumento / 100);
        return (int) Math.ceil(precioCompra + aumento);
    }

    public static int calcularTotal(List<Producto> productos) {
        int total = 0;
        for (Producto producto : productos) {
            total += producto.getPrecioCompra() * producto.getStock();
        }
        return total;
    }

}