package org.example.baboobackend.daos;

import org.example.baboobackend.dto.ProductoInformeDTO;
import org.example.baboobackend.entities.Pedido;
import org.example.baboobackend.entities.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Integer> {
    @Query("SELECT new org.example.baboobackend.dto.ProductoInformeDTO(P.nombre, CI.stock, CI.precio) " +
            "FROM ComprobanteItem CI " +
            "INNER JOIN Producto P ON P.id = CI.idProducto " +
            "INNER JOIN Pedido PE ON CI.idPedido = PE.id " +
            "WHERE PE.id = :id " +
            "ORDER BY CI.idPedido ASC")
    List<ProductoInformeDTO> getProductosInforme(int id);
    @Query("SELECT new org.example.baboobackend.dto.ProductoInformeDTO(P.nombre, CI.stock, CI.precio, P.codigoBarra)" +
            "FROM ComprobanteItem CI " +
            "INNER JOIN Producto P ON P.id = CI.idProducto " +
            "WHERE CI.idPedido = :idPedido " +
            "ORDER BY CI.idPedido ASC")
    List<ProductoInformeDTO> getProductosComprobante(int idPedido);
}

