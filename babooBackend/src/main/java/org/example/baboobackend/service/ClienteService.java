package org.example.baboobackend.service;


import org.example.baboobackend.entities.Cliente;

import java.util.List;
import java.util.Optional;

public interface ClienteService {
    Optional<Cliente> getClient(Integer dni);
    List<Cliente> getAllClientes();
    Cliente createCliente(Cliente cliente);
    Optional<Cliente> updateCliente(Cliente cliente);
}