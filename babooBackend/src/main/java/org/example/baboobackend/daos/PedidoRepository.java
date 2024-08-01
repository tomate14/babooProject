package org.example.baboobackend.daos;

import org.example.baboobackend.entities.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Integer> {
    long deleteByIdAndEstado(int idPedido, String estado);
    @Query("SELECT p FROM Pedido p WHERE p.fechaPedido < :fechaDesde AND p.estado = 'PENDIENTE' AND p.tipoPedido = :tipoPedido")
    List<Pedido> findVencidosByFechaPedidoAndTipoPedido(@Param("fechaDesde") String fechaDesde, @Param("tipoPedido") int tipoPedido);
    List<Pedido> findPedidoByEstadoIs(String estado);
}
