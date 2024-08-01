package org.example.baboobackend.dto;

import lombok.Data;
import org.example.baboobackend.entities.Pedido;

@Data
public class PedidoDTO extends Pedido {
    private String nombreCliente;
    private String telefonoCliente;

    public PedidoDTO(Pedido pedido) {
        this.setId(pedido.getId());
        this.setNumeroComprobante(pedido.getNumeroComprobante());
        this.setDescripcion(pedido.getDescripcion());
        this.setEstado(pedido.getEstado());
        this.setFechaPedido(pedido.getFechaPedido());
        this.setTipoPedido(pedido.getTipoPedido());
        this.setTotal(pedido.getTotal());
        this.setEstadoEnvio(pedido.getEstadoEnvio());
        this.setDniCliente(pedido.getDniCliente());
        this.setNombreCliente(pedido.getNombreCliente());
        this.setTelefonoCliente(pedido.getTelefonoCliente());
    }
}
