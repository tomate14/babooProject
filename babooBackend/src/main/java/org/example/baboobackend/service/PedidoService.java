package org.example.baboobackend.service;


import org.example.baboobackend.dto.PedidoDeudaDTO;
import org.example.baboobackend.entities.Pedido;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface PedidoService {
    Pedido crearPedido(Pedido nuevoPedido);
    long deletePedidosByIdPedidoAndEstado(int idPedido, String estado);
    Optional<Pedido> actualizarPedido(int idPedido, Pedido pedidoActualizado);
    List<Pedido> getPedidosVencidos(String fechaDesde, int tipoPedido);
    PedidoDeudaDTO getDeudaPedido(int idPedido);
    List<Pedido> getPedidosFiltrados(Map<String, String> queryParams);
}

