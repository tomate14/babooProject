package org.example.baboobackend.service.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.baboobackend.daos.ClienteRepository;
import org.example.baboobackend.daos.PedidoRepository;
import org.example.baboobackend.entities.Cliente;
import org.example.baboobackend.entities.Pedido;
import org.example.baboobackend.enumerados.Estado;
import org.example.baboobackend.enumerados.TipoUsuario;
import org.example.baboobackend.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.regex.Pattern;

@Service
public class ClienteServiceImpl implements ClienteService {

    private static final Logger logger = LogManager.getLogger(ClienteServiceImpl.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private ClienteRepository clientRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Override
    public Optional<Cliente> getClient(Integer dni) {
        return clientRepository.findByDni(dni);
    }

    @Override
    public List<Cliente> getAllClientes(Map<String, String> queryParams) {
        Map<String, Object> filtros = new HashMap<>();

        if (queryParams.containsKey("dni")) {
            try {
                filtros.put("dni", queryParams.get("dni"));
            } catch (Exception e) {
                throw new IllegalArgumentException("ID no válido");
            }
        }

        if (queryParams.containsKey("tipoUsuario")) {
            filtros.put("tipoUsuario", Integer.parseInt(queryParams.get("tipoUsuario")));
        }
        if (queryParams.containsKey("nombre")) {
            filtros.put("nombre",queryParams.get("nombre"));
        }

        List<Cliente> clients = findByCriteria(filtros);

        List<Pedido> pedidos = pedidoRepository.findPedidoByEstadoIs(Estado.PENDIENTE.getDescripcion());
        if (!CollectionUtils.isEmpty(pedidos)) {
            List<Integer> dniDeudores = pedidos.stream().map(Pedido::getDniCliente).distinct().toList();
            clients.forEach(client -> client.setEsDeudor(dniDeudores.contains(client.getDni())));
        }
        return clients;
    }

    @Override
    public Cliente createCliente(Cliente cliente) {
        Optional<Cliente> existingClient = clientRepository.findByDni(cliente.getDni());
        if (existingClient.isPresent()) {
            throw new IllegalArgumentException("El cliente ya existe");
        }

        return existingClient.orElseGet(() -> clientRepository.save(cliente));
    }

    @Override
    public Optional<Cliente> updateCliente(Cliente cliente) {
        Optional<Cliente> existingClientOpt = clientRepository.findByDni(cliente.getDni());
        if (existingClientOpt.isPresent()) {
            Cliente existingClient = existingClientOpt.get();
            if (cliente.getNombre() != null && !cliente.getNombre().isEmpty()) {
                existingClient.setNombre(cliente.getNombre());
            } else {
                throw new IllegalArgumentException("El nombre no puede estar vacío");
            }

            if (cliente.getFechaNacimiento() != null && !cliente.getFechaNacimiento().isEmpty()) {
                existingClient.setFechaNacimiento(cliente.getFechaNacimiento());
            } else {
                throw new IllegalArgumentException("La fecha de nacimiento no puede estar vacía");
            }

            try {
                TipoUsuario.fromCodigo(cliente.getTipoUsuario());
                existingClient.setTipoUsuario(cliente.getTipoUsuario());
            } catch(Exception e) {
                logger.warn("Tipo de pedido no es valido "+ existingClient.getTipoUsuario() +" para el id "+ existingClient.getDni());
            }

            if (cliente.getDireccion() != null && !cliente.getDireccion().isEmpty()) {
                existingClient.setDireccion(cliente.getDireccion());
            } else {
                throw new IllegalArgumentException("La dirección no puede estar vacía");
            }

            if (cliente.getTelefono() != null && !cliente.getTelefono().isEmpty()) {
                existingClient.setTelefono(cliente.getTelefono());
            } else {
                throw new IllegalArgumentException("El teléfono no puede estar vacío");
            }

            if (cliente.getEmail() != null && !cliente.getEmail().isEmpty() && cliente.getEmail().contains("@")) {
                existingClient.setEmail(cliente.getEmail());
            } else {
                throw new IllegalArgumentException("El email debe ser válido y no puede estar vacío");
            }

            if (cliente.getCuit() != null && !cliente.getCuit().isEmpty()) {
                existingClient.setCuit(cliente.getCuit());
            } else {
                throw new IllegalArgumentException("El CUIT no puede estar vacío");
            }

            // Guardar el cliente actualizado
            return Optional.of(clientRepository.save(existingClient));
        } else {
            throw new IllegalArgumentException("El cliente no existe");
        }
    }

    private List<Cliente> findByCriteria(Map<String, Object> filtros) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Cliente> cq = cb.createQuery(Cliente.class);
        Root<Cliente> cliente = cq.from(Cliente.class);

        List<Predicate> predicates = new ArrayList<>();

        for (Map.Entry<String, Object> entry : filtros.entrySet()) {
            if ("nombre".equals(entry.getKey())) {
                predicates.add(cb.like(cb.lower(cliente.get("nombre")), "%" + entry.getValue().toString().toLowerCase() + "%"));
            } else if ("tipoUsuario".equals(entry.getKey())) {
                predicates.add(cb.equal(cliente.get(entry.getKey()), entry.getValue()));
            } else if ("dni".equals(entry.getKey())) {
                predicates.add(cb.equal(cliente.get(entry.getKey()), entry.getValue()));
            }
        }

        cq.where(predicates.toArray(new Predicate[0]));

        return entityManager.createQuery(cq).getResultList();
    }

}