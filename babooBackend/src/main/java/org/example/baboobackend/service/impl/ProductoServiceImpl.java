package org.example.baboobackend.service.impl;

import ch.qos.logback.core.util.StringUtil;
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
import org.krysalis.barcode4j.impl.upcean.EAN13Bean;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

@Service
public class ProductoServiceImpl implements ProductoService {

    private static final String COUNTRY_CODE = "779";
    private static final String COMPANY_CODE = "430496";
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
                comprobante.buildForSave(optExistente, newProd);
            } else {
                //New Product. Controlar antes de enviar en las ordenes de venta ids != null
                Optional<Cliente> proveedor = clienteRepository.findByDni(newProd.getIdProveedor());
                proveedor.ifPresent(cliente -> newProd.setPrecioVenta(newProd.calcularPrecioConAumento(cliente)));
            }
        });
        List<Producto> productosGuardados = productoRepository.saveAll(productos);
        productosGuardados.forEach((producto) -> {
            if (StringUtil.isNullOrEmpty(producto.getCodigoBarra())) {
                producto.setCodigoBarra(this.getCodigoDeBarra(producto.getId()));
            }
        });
        productoRepository.saveAll(productosGuardados);
        return productosGuardados;
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
    public String getCodigoDeBarra(int idProducto) {
        String productCode = String.format("%03d", idProducto);  // Formatea el ID del producto con ceros a la izquierda si es necesario
        String rawBarcode = COUNTRY_CODE + COMPANY_CODE + productCode;
        return rawBarcode + calculateCheckDigit(rawBarcode);
    }
    public byte[] generateBarcode(int idProducto) throws IOException {

        Optional<Producto> prodExistente = productoRepository.findById(idProducto);
        if (prodExistente.isPresent()) {
            String ean13Barcode = prodExistente.get().getCodigoBarra();
            EAN13Bean barcodeGenerator = new EAN13Bean();
            final int dpi = 160;

            barcodeGenerator.setModuleWidth(0.2);
            barcodeGenerator.doQuietZone(true);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            BitmapCanvasProvider canvas = new BitmapCanvasProvider(baos, "image/x-png", dpi, BufferedImage.TYPE_BYTE_BINARY, false, 0);

            barcodeGenerator.generateBarcode(canvas, ean13Barcode);
            canvas.finish();

            return baos.toByteArray();
        }
        throw new RuntimeException("No se pudo generar el codigo de barra "+idProducto);
    }

    private int calculateCheckDigit(String barcode) {
        int sum = 0;

        for (int i = 0; i < barcode.length(); i++) {
            int digit = Character.getNumericValue(barcode.charAt(i));
            sum += (i % 2 == 0) ? digit : digit * 3;
        }

        int mod = sum % 10;
        return mod == 0 ? 0 : 10 - mod;
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
