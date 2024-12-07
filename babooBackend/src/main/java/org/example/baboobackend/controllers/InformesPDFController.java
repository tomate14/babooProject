package org.example.baboobackend.controllers;

import org.example.baboobackend.daos.PedidoRepository;
import org.example.baboobackend.daos.ProductoRepository;
import org.example.baboobackend.dto.ProductoInformeDTO;
import org.example.baboobackend.entities.Pedido;
import org.example.baboobackend.entities.Producto;
import org.example.baboobackend.enumerados.TipoPedido;
import org.example.baboobackend.service.InformesPDFService;
import org.example.baboobackend.service.impl.GeneradorCodigoBarra;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/informes")
public class InformesPDFController {
    @Autowired
    private InformesPDFService informeService;

    @Autowired
    private ProductoRepository productoRepository;
    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private GeneradorCodigoBarra generadorCodigoBarra;

    @GetMapping
    public ResponseEntity<InputStreamResource> generarInforme(@RequestParam("idPedido") int idPedido) {
        Optional<Pedido> optPedido = pedidoRepository.findById(idPedido);
        if (optPedido.isPresent()) {
            Pedido pedido = optPedido.get();
            List<ProductoInformeDTO> productos = productoRepository.getProductosInforme(pedido.getId());
            ByteArrayInputStream bis = informeService.generarORVPDF(productos, TipoPedido.fromCodigo(pedido.getTipoPedido()), pedido);

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "inline; filename=Informe_" + informeService.getNombreArchivo(pedido, TipoPedido.fromCodigo(pedido.getTipoPedido())) + ".pdf");

            return ResponseEntity
                    .ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(new InputStreamResource(bis));
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{idProducto}")
    public ResponseEntity<InputStreamResource> generarEtiqueta(@PathVariable("idProducto") Integer idProducto) {
        Optional<Producto> optProducto = productoRepository.findById(idProducto);
        if (optProducto.isPresent()) {
            Producto producto = optProducto.get();
            String codigoBarra = producto.getCodigoBarra(); // Asegúrate de que el producto tiene un código de barras

            // Generar el PDF con el código de barras
            ByteArrayOutputStream outputStream = generadorCodigoBarra.generarEtiquetaConCodigoBarra(codigoBarra);

            // Configurar los headers de la respuesta
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "inline; filename=Etiqueta_" + producto.getCodigoBarra() + ".pdf");

            // Devolver el archivo PDF como un InputStreamResource
            return ResponseEntity
                    .ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(new InputStreamResource(new ByteArrayInputStream(outputStream.toByteArray())));
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}