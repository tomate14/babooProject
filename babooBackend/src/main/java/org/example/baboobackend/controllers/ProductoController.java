package org.example.baboobackend.controllers;

import org.example.baboobackend.entities.Pedido;
import org.example.baboobackend.entities.Producto;
import org.example.baboobackend.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

@RestController
@RequestMapping("/producto")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @GetMapping
    public ResponseEntity<?> getProductos(@RequestParam Map<String, String> queryParams) {
        try {
            List<Producto> productos = productoService.getAllProductos(queryParams);
            return new ResponseEntity<>(productos, HttpStatus.OK);
        } catch (IllegalArgumentException | NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Producto> getProductoById(@PathVariable Integer id) {
        Optional<Producto> producto = productoService.getProductoById(id);
        return producto.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    /*@PostMapping
    public ResponseEntity<List<Producto>> createProductos(@RequestBody List<Producto> productos) {
        //Mover a generar Pedido
        List<Producto> createdProducto = productoService.createProductos(productos);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProducto);
    }*/

    @PutMapping
    public ResponseEntity<Producto> updateProducto(@RequestBody Producto producto) {
        Optional<Producto> updatedProducto = productoService.updateProducto(producto);
        return updatedProducto.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProducto(@PathVariable Integer id) {
        productoService.deleteProducto(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
