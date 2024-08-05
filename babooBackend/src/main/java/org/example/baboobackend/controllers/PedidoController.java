package org.example.baboobackend.controllers;

import jakarta.websocket.server.PathParam;
import org.example.baboobackend.dto.CrearPedidoDTO;
import org.example.baboobackend.dto.PedidoDeudaDTO;
import org.example.baboobackend.entities.Pedido;
import org.example.baboobackend.entities.Producto;
import org.example.baboobackend.service.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

@RestController
@RequestMapping("/pedido")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @PostMapping
    public ResponseEntity<?> crearPedido(@RequestBody Pedido nuevoPedido) { //OK
        Pedido pedidoCreado = pedidoService.crearPedido(nuevoPedido);
        return ResponseEntity.ok(pedidoCreado);
    }

    @PostMapping("/crear")
    public ResponseEntity<?> crearPedido(@RequestBody CrearPedidoDTO crearDto) { //OK
        Pedido pedidoCreado = pedidoService.generarPedido(crearDto);
        return ResponseEntity.ok(pedidoCreado);
    }

    @DeleteMapping("/{idPedido}")
    public ResponseEntity<String> deletePedidoById(@PathVariable int idPedido) { //OK
        long deletedCount = pedidoService.deletePedidosByIdPedidoAndEstado(idPedido, "COMPLETO");
        return ResponseEntity.ok("Se eliminaron " + deletedCount + " envíos con idPedido " + idPedido);
    }

    @PutMapping("/{idPedido}")
    public ResponseEntity<?> actualizarPedido(@PathVariable int idPedido, @RequestBody Pedido pedidoActualizado) {// OK
        Optional<Pedido> pedido = pedidoService.actualizarPedido(idPedido, pedidoActualizado);

        if (pedido.isPresent()) {
            return ResponseEntity.status(HttpStatus.OK).body(pedido.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encontró el envío con idPedido " + idPedido);
        }
    }
    @GetMapping("/pedidos-vencidos/{fechaDesde}/{tipoPedido}")
    public ResponseEntity<List<Pedido>> getPedidosVencidos(
            @PathVariable String fechaDesde,
            @PathVariable int tipoPedido) { //OK

        return ResponseEntity.ok(pedidoService.getPedidosVencidos(fechaDesde, tipoPedido));
    }

    @GetMapping("/informe-deuda")
    public ResponseEntity<PedidoDeudaDTO> getDeudaPedido(@RequestParam int id) { //OK
        PedidoDeudaDTO deudaDTO = pedidoService.getDeudaPedido(id);
        return ResponseEntity.ok(deudaDTO);
    }

    @GetMapping
    public ResponseEntity<?> getPedidosFiltrados(@RequestParam Map<String, String> queryParams) { //OK
        try {
            List<Pedido> pedidos = pedidoService.getPedidosFiltrados(queryParams);
            return new ResponseEntity<>(pedidos, HttpStatus.OK);
        } catch (IllegalArgumentException | NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        }
    }
}