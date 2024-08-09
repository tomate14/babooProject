package org.example.baboobackend.service;


import org.example.baboobackend.entities.Cliente;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ClienteService {
    Optional<Cliente> getClient(Integer dni);
    List<Cliente> getAllClientes(Map<String, String> queryParams);
    Cliente createCliente(Cliente cliente);
    Optional<Cliente> updateCliente(Cliente cliente);

    Optional<Cliente> findByDni(int dniCliente);

    List<Cliente> findAll();

    List<Cliente> findByNombreContaining(String regex);
}