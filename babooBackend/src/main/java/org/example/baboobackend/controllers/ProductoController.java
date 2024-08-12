package org.example.baboobackend.controllers;

import org.example.baboobackend.entities.Pedido;
import org.example.baboobackend.entities.Producto;
import org.example.baboobackend.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
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
    @GetMapping(path ="codigo-barra",produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getBarcode(@RequestParam("idProducto") int idProducto) {
        try {
            byte[] barcodeImage = productoService.generateBarcode(idProducto);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            return ResponseEntity.ok().headers(headers).body(barcodeImage);
        } catch (IOException e) {
            return ResponseEntity.status(500).build();
        }
    }
    @GetMapping("/{id}")
    public ResponseEntity<Producto> getProductoById(@PathVariable Integer id) {
        Optional<Producto> producto = productoService.getProductoById(id);
        return producto.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

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
