package org.example.baboobackend.service;

import org.example.baboobackend.dto.ProductoInformeDTO;
import org.example.baboobackend.entities.Pedido;
import org.example.baboobackend.entities.Producto;
import org.example.baboobackend.enumerados.TipoPedido;

import java.io.ByteArrayInputStream;
import java.util.List;

public interface InformesPDFService {
    ByteArrayInputStream generarORVPDF(List<ProductoInformeDTO> productos, TipoPedido tipoPedido, Pedido pedido);
    String getNombreArchivo(Pedido pedido, TipoPedido tipoPedido);
}
