package org.example.baboobackend.controllers;

import org.example.baboobackend.entities.Cliente;
import org.example.baboobackend.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
//ESTA OK
@RestController
@RequestMapping("/cliente")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @GetMapping("/{dni}")
    public ResponseEntity<Cliente> getClient(@PathVariable Integer dni) { //OK
        Optional<Cliente> client = clienteService.getClient(dni);
        return client.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    @GetMapping
    public ResponseEntity<List<Cliente>> getAllClientes() { //OK
        List<Cliente> clientes = clienteService.getAllClientes();
        return ResponseEntity.ok(clientes);
    }

    @PostMapping
    public ResponseEntity<Cliente> createCliente(@RequestBody Cliente cliente) { //OK
        Cliente createdCliente = clienteService.createCliente(cliente);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCliente);
    }

    @PutMapping
    public ResponseEntity<Cliente> updateClients(@RequestBody Cliente cliente) { //OK
        Optional<Cliente> updatedClient = clienteService.updateCliente(cliente);
        return updatedClient.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }
}