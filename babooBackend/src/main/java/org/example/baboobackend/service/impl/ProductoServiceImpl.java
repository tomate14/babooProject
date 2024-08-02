package org.example.baboobackend.service.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import org.example.baboobackend.daos.ClienteRepository;
import org.example.baboobackend.daos.ProductoRepository;
import org.example.baboobackend.entities.Cliente;
import org.example.baboobackend.entities.Pedido;
import org.example.baboobackend.entities.Producto;
import org.example.baboobackend.enumerados.TipoUsuario;
import org.example.baboobackend.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ProductoServiceImpl implements ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private ClienteRepository clienteRepository;

    public List<Producto> getAllProductos(Map<String, String> queryParams) {
        Map<String, Object> filtros = new HashMap<>();

        if (queryParams.containsKey(Producto.ID)) {
            try {
                filtros.put(Producto.ID, Integer.valueOf(queryParams.get(Producto.ID)));
            } catch (Exception e) {
                throw new IllegalArgumentException("ID no válido");
            }
        }

        if (queryParams.containsKey(Producto.NOMBRE)) {
            filtros.put(Producto.NOMBRE,queryParams.get(Producto.NOMBRE));
        }

        List<Producto> productos = new ArrayList<>();

        if (!filtros.isEmpty()) {
            productos = findByCriteria(filtros);
        } else {
            productos = productoRepository.findAll();
        }

        for (Producto producto : productos) {
            if (Objects.nonNull(producto.getProveedor())) {
                producto.setNombreProveedor(producto.getProveedor().getNombre());
            }
        }
        return productos;
    }

    public Optional<Producto> getProductoById(Integer id) {
        return productoRepository.findById(id);
    }

    @Transactional
    public Producto createProducto(Producto producto) {
        return productoRepository.save(producto);
    }

    @Transactional
    public Optional<Producto> updateProducto(Producto producto) {
        return productoRepository.findById(producto.getId()).map(existingProducto -> {
            existingProducto.setNombre(producto.getNombre());
            existingProducto.setCodigoBarra(producto.getCodigoBarra());
            existingProducto.setPrecioCompra(producto.getPrecioCompra());
            existingProducto.setPrecioVenta(producto.getPrecioVenta());
            existingProducto.setIdProveedor(producto.getIdProveedor());
            existingProducto.setStock(producto.getStock());
            return productoRepository.save(existingProducto);
        });
    }

    @Transactional
    public void deleteProducto(Integer id) {
        productoRepository.deleteById(id);
    }

    private List<Producto> findByCriteria(Map<String, Object> filtros) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Producto> cq = cb.createQuery(Producto.class);
        Root<Producto> producto = cq.from(Producto.class);

        List<Predicate> predicates = new ArrayList<>();

        for (Map.Entry<String, Object> entry : filtros.entrySet()) {
            if (Producto.NOMBRE.equals(entry.getKey())) {
                predicates.add(cb.like(cb.lower(producto.get(Producto.NOMBRE)), "%" + entry.getValue().toString().toLowerCase() + "%"));
            } else if (Producto.ID.equals(entry.getKey())) {
                predicates.add(cb.equal(producto.get(entry.getKey()), entry.getValue()));
            }
        }

        cq.where(predicates.toArray(new Predicate[0]));

        return entityManager.createQuery(cq).getResultList();
    }
}
