package org.example.baboobackend.daos;

import org.example.baboobackend.entities.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface PagoRepository extends JpaRepository<Pago, Integer> {
    List<Pago> findAllByIdPedido(int idPedido);
    List<Pago> findAllByFechaPagoBetween(String startDate, String endDate);
    void deleteAllByIdPedido(int idPedido);
    List<Pago> findByIdPedidoOrderByFechaPagoDesc(int idPedido);

}
