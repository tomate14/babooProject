package org.example.baboobackend.service;

import org.example.baboobackend.comprobante.Comprobante;
import org.example.baboobackend.entities.Producto;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ProductoService {
    List<Producto> getAllProductos(Map<String, String> queryParams);
    Optional<Producto> getProductoById(Integer id);
    List<Producto> createProductos(Comprobante comprobante, List<Producto> productos);
    Optional<Producto> updateProducto(Producto producto);
    void deleteProducto(Integer id);
    String getCodigoDeBarra(int idProducto);
    byte[] generateBarcode(int idProducto) throws IOException;
}
