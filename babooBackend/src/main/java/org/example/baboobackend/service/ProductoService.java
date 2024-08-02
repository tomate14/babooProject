package org.example.baboobackend.service;

import org.example.baboobackend.entities.Producto;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ProductoService {
    List<Producto> getAllProductos(Map<String, String> queryParams);
    Optional<Producto> getProductoById(Integer id);
    Producto createProducto(Producto producto);
    Optional<Producto> updateProducto(Producto producto);
    void deleteProducto(Integer id);
}
