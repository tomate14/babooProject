package org.example.baboobackend.service.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import org.example.baboobackend.comprobante.Comprobante;
import org.example.baboobackend.daos.ClienteRepository;
import org.example.baboobackend.daos.NumeracionRepository;
import org.example.baboobackend.daos.ProductoRepository;
import org.example.baboobackend.entities.Cliente;
import org.example.baboobackend.entities.Pedido;
import org.example.baboobackend.entities.Producto;
import org.example.baboobackend.enumerados.TipoUsuario;
import org.example.baboobackend.service.PedidoService;
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
        if (queryParams.containsKey(Producto.ID_PROVEEDOR)) {
            filtros.put(Producto.ID_PROVEEDOR,queryParams.get(Producto.ID_PROVEEDOR));
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
    public List<Producto> createProductos(Comprobante comprobante, List<Producto> productos) {
        productos.forEach(newProd -> {
            if (newProd.getId() != null) {
                Optional<Producto> optExistente = productoRepository.findById(newProd.getId());
                if (optExistente.isPresent()) {
                    final Producto prodExistente = optExistente.get();
                    int nuevoStock = comprobante.calcularStock(prodExistente.getStock(), newProd.getStock());
                    newProd.setStock(nuevoStock);

                    final int precioCompraMayor = Math.max(prodExistente.getPrecioCompra(), newProd.getPrecioCompra());;
                    newProd.setPrecioCompra(precioCompraMayor);

                    newProd.setPrecioVenta(newProd.calcularPrecioConAumento(prodExistente.getProveedor()));

                }
            } else {
                Optional<Cliente> proveedor = clienteRepository.findByDni(newProd.getIdProveedor());
                proveedor.ifPresent(cliente -> newProd.setPrecioVenta(newProd.calcularPrecioConAumento(cliente)));
            }
        });
        return productoRepository.saveAll(productos);
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
            } else {
                predicates.add(cb.equal(producto.get(entry.getKey()), entry.getValue()));
            }
        }

        cq.where(predicates.toArray(new Predicate[0]));

        return entityManager.createQuery(cq).getResultList();
    }
}
