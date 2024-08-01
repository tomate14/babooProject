package org.example.baboobackend.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import lombok.Data;

@Data
@Entity
public class Pedido {
    public static final String NUMERO_COMPROBANTE = "numeroComprobante";
    public static final String ORDEN_FECHA = "ordenFecha";
    public static final String FECHA_PEDIDO = "fechaPedido";
    public static final String TIPO_PEDIDO = "tipoPedido";
    public static final String ESTADO_ENVIO = "estadoEnvio";
    public static final String ESTADO = "estado";
    public static final String DNI_CLIENTE = "dniCliente";
    public static final String FECHA_DESDE = "fechaDesde";
    public static final String FECHA_HASTA = "fechaHasta";
    public static final String GTE = "$gte";
    public static final String LT = "$lt";
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private int dniCliente;
    private String fechaPedido;
    private Integer total;
    private String estado;
    private String descripcion;
    private int tipoPedido;
    private int estadoEnvio;
    private String numeroComprobante;
    private boolean conSena;
    @Transient
    private String nombreCliente;
    @Transient
    private String telefonoCliente;
}