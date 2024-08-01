package org.example.baboobackend.service.impl;

import ch.qos.logback.core.util.StringUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.baboobackend.daos.ClienteRepository;
import org.example.baboobackend.daos.NumeracionRepository;
import org.example.baboobackend.daos.PagoRepository;
import org.example.baboobackend.daos.PedidoRepository;
import org.example.baboobackend.dto.PedidoDeudaDTO;
import org.example.baboobackend.entities.Cliente;
import org.example.baboobackend.entities.Numeracion;
import org.example.baboobackend.entities.Pago;
import org.example.baboobackend.entities.Pedido;
import org.example.baboobackend.enumerados.Estado;
import org.example.baboobackend.enumerados.EstadoPedido;
import org.example.baboobackend.enumerados.TipoPedido;
import org.example.baboobackend.service.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Pattern;

@Service
public class PedidoServiceImpl implements PedidoService {
    private static final Logger logger = LogManager.getLogger(PedidoServiceImpl.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private PagoRepository pagoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private NumeracionRepository numeracionRepository;

    @Transactional
    public Pedido crearPedido(Pedido nuevoPedido) {
        if (nuevoPedido == null || nuevoPedido.getDniCliente() == 0) {
            throw new IllegalArgumentException("Datos incompletos al crear pedido");
        }

        //obtener numeracion por defecto
        int tipo = checkTipo(nuevoPedido);

        Numeracion numerador = numeracionRepository.findByTipoComprobante(tipo);
        if (Objects.isNull(numerador)) {
            numerador = new Numeracion();
            numerador.setNumeroComprobante(1);
            numerador.setTipoComprobante(nuevoPedido.getTipoPedido());
        }
        int index = numerador.getNumeroComprobante();
        nuevoPedido.setNumeroComprobante(formatearNumero(index));
        Optional<Cliente> cliente = clienteRepository.findByDni(nuevoPedido.getDniCliente());
        if (cliente.isEmpty()) {
            throw new IllegalArgumentException("No se encontró el cliente asociado. Darlo de alta");
        }

        Pedido resultado = pedidoRepository.save(nuevoPedido);

        if (Objects.isNull(resultado)) {
            throw new NullPointerException("No se pudo crear el pedido");
        }

        nuevoPedido.setId(resultado.getId());
        nuevoPedido.setNombreCliente(cliente.get().getNombre());
        nuevoPedido.setTelefonoCliente(cliente.get().getTelefono());

        numerador.setNumeroComprobante(index + 1);
        numeracionRepository.save(numerador);

        return nuevoPedido;
    }

    private int checkTipo(Pedido nuevoPedido) {
        int tipo = nuevoPedido.getTipoPedido();
        if (tipo == 1 || tipo == 2) {
            tipo = 1;
        }
        return tipo;
    }

    @Transactional
    public long deletePedidosByIdPedidoAndEstado(int idPedido, String estado) {
        long cantidad = pedidoRepository.deleteByIdAndEstado(idPedido, estado);
        if (cantidad == 0) {
            throw new IllegalArgumentException("No se pudo eliminar el pedido");
        }
        return cantidad;
    }

    public Optional<Pedido> actualizarPedido(int idPedido, Pedido pedidoActualizado) {
        return pedidoRepository.findById(idPedido).map(pedidoExistente -> {

            if (pedidoActualizado.getTotal() != null) {
                pedidoExistente.setTotal(pedidoActualizado.getTotal());
            }
            pedidoActualizado.setConSena(pedidoExistente.isConSena());
            try {
                Estado.valueOf(pedidoActualizado.getEstado());
                if (pedidoActualizado.getEstado() != null) {
                    pedidoExistente.setEstado(pedidoActualizado.getEstado());
                }
            } catch(Exception e) {
                logger.warn("Estado no es valido "+ pedidoActualizado.getEstado() +" para el id "+ idPedido);
            }



            if (pedidoActualizado.getDescripcion() != null) {
                pedidoExistente.setDescripcion(pedidoActualizado.getDescripcion());
            }

            try {
                TipoPedido.fromCodigo(pedidoActualizado.getTipoPedido());
                pedidoExistente.setTipoPedido(pedidoActualizado.getTipoPedido());
            } catch(Exception e) {
                logger.warn("Tipo de pedido no es valido "+ pedidoActualizado.getTipoPedido() +" para el id "+ idPedido);
            }
            try {
                EstadoPedido.fromCodigo(pedidoActualizado.getEstadoEnvio());
                pedidoExistente.setEstadoEnvio(pedidoActualizado.getEstadoEnvio());
            } catch(Exception e) {
                logger.warn("Estado de pedido no es valido "+ pedidoActualizado.getEstadoEnvio() +" para el id "+ idPedido);
            }



            return pedidoRepository.save(pedidoExistente);
        });
    }

    public List<Pedido> getPedidosVencidos(String fechaDesde, int tipoPedido) {
        List<Pedido> pedidos = pedidoRepository.findVencidosByFechaPedidoAndTipoPedido(fechaDesde, tipoPedido);
        List<Cliente> clientes = clienteRepository.findAll();
        for (Pedido pedido : pedidos) {
            Optional<Cliente> clienteOpt = clientes.stream()
                    .filter(cliente -> cliente.getDni() == pedido.getDniCliente())
                    .findFirst();
            clienteOpt.ifPresent(cliente -> {
                pedido.setNombreCliente(cliente.getNombre());
                pedido.setTelefonoCliente(cliente.getTelefono());
            });
        }

        return pedidos;
    }

    public PedidoDeudaDTO getDeudaPedido(int idPedido) {
        Optional<Pedido> pedidoOpt = pedidoRepository.findById(idPedido);
        if (pedidoOpt.isEmpty()) {
            throw new IllegalArgumentException("ID no válido");
        }

        Pedido pedido = pedidoOpt.get();
        Optional<Cliente> clienteOpt = clienteRepository.findByDni(pedido.getDniCliente());
        if (clienteOpt.isEmpty()) {
            throw new IllegalArgumentException("Cliente con id "+ pedido.getDniCliente() + " no encontrado");
        }

        Cliente cliente = clienteOpt.get();
        List<Pago> pagos = pagoRepository.findByIdPedidoOrderByFechaPagoDesc(idPedido);

        int sumaPagos = pagos.stream().mapToInt(Pago::getValor).sum();

        PedidoDeudaDTO deudaDTO = new PedidoDeudaDTO();
        deudaDTO.setNombreCliente(cliente.getNombre());
        deudaDTO.setTelefonoCliente(cliente.getTelefono());
        deudaDTO.setEmailCliente(cliente.getEmail());
        deudaDTO.setIdPedido(idPedido);
        deudaDTO.setSaldoRestante(pedido.getTotal() - sumaPagos);
        deudaDTO.setNumeroComprobante(pedido.getNumeroComprobante());

        if (!pagos.isEmpty()) {
            Pago ultimoPago = pagos.get(0);
            deudaDTO.setFechaUltimoPago(ultimoPago.getFechaPago());
            deudaDTO.setMontoUltimoPago(ultimoPago.getValor());
        }

        return deudaDTO;
    }

    public List<Pedido> getPedidosFiltrados(Map<String, String> queryParams) {
        Map<String, Object> filtros = new HashMap<>();

        if (queryParams.containsKey("id")) {
            try {
                filtros.put("id", queryParams.get("id"));
            } catch (Exception e) {
                throw new IllegalArgumentException("ID no válido");
            }
        }

        if (queryParams.containsKey(Pedido.ESTADO_ENVIO)) {
            filtros.put(Pedido.ESTADO_ENVIO, Integer.parseInt(queryParams.get(Pedido.ESTADO_ENVIO)));
        }

        if (queryParams.containsKey(Pedido.TIPO_PEDIDO)) {
            filtros.put(Pedido.TIPO_PEDIDO, Integer.parseInt(queryParams.get(Pedido.TIPO_PEDIDO)));
        }

        if (queryParams.containsKey(Pedido.ESTADO)) {
            filtros.put(Pedido.ESTADO, queryParams.get(Pedido.ESTADO));
        }

        if (queryParams.containsKey(Pedido.DNI_CLIENTE)) {
            filtros.put(Pedido.DNI_CLIENTE, Integer.parseInt(queryParams.get(Pedido.DNI_CLIENTE)));
        }

        if (queryParams.containsKey(Pedido.ORDEN_FECHA)) {
            int sortOrden = Integer.parseInt(queryParams.get(Pedido.ORDEN_FECHA));
            filtros.put(Pedido.ORDEN_FECHA, sortOrden);
        }

        if (queryParams.containsKey(Pedido.NUMERO_COMPROBANTE)) {
            String regex = ".*" + queryParams.get(Pedido.NUMERO_COMPROBANTE) + ".*";
            filtros.put(Pedido.NUMERO_COMPROBANTE, regex);
        }

        if (queryParams.containsKey(Pedido.FECHA_DESDE) || queryParams.containsKey(Pedido.FECHA_HASTA)) {
            Map<String, Object> fechaFiltro = new HashMap<>();

            String fechaDesde = queryParams.get(Pedido.FECHA_DESDE);
            if (StringUtil.notNullNorEmpty(fechaDesde)) {
                fechaFiltro.put(Pedido.GTE, fechaDesde);
            }

            String fechaHasta = queryParams.get(Pedido.FECHA_HASTA);
            if (StringUtil.notNullNorEmpty(fechaHasta)) {
                fechaFiltro.put(Pedido.LT, fechaHasta);
            }

            if (!fechaFiltro.isEmpty()) {
                filtros.put(Pedido.FECHA_PEDIDO, fechaFiltro);
            }
        }

        List<Pedido> pedidos = new ArrayList<>();

        if (!filtros.isEmpty()) {
            pedidos = findByCriteria(filtros);
        }

        if (queryParams.containsKey("nombreCliente")) {
            String regex = Pattern.compile(queryParams.get("nombreCliente"), Pattern.CASE_INSENSITIVE).toString();
            List<Cliente> clientes = clienteRepository.findByNombreContaining(regex);
            if (clientes.isEmpty()) {
                throw new NoSuchElementException("No se encontraron clientes con el nombre proporcionado");
            }
            Map<Integer, Cliente> clienteMap = new HashMap<>();
            for (Cliente cliente : clientes) {
                clienteMap.put(cliente.getDni(), cliente);
            }
            pedidos.removeIf(pedido -> !clienteMap.containsKey(pedido.getDniCliente()));
            for (Pedido pedido : pedidos) {
                Cliente cliente = clienteMap.get(pedido.getDniCliente());
                pedido.setNombreCliente(cliente.getNombre());
                pedido.setTelefonoCliente(cliente.getTelefono());
            }
        }
        List<Cliente> clientes = clienteRepository.findAll();
        pedidos.forEach(pedido -> {
            Optional<Cliente> c = clientes.stream().filter(cliente -> pedido.getDniCliente() == cliente.getDni()).findFirst();
            if (c.isPresent()) {
                pedido.setNombreCliente(c.get().getNombre());
                pedido.setTelefonoCliente(c.get().getTelefono());
            }
        });

        return pedidos;
    }

    private String formatearNumero(int numero) {
        return String.format("%08d", numero);
    }

    private List<Pedido> findByCriteria(Map<String, Object> filtros) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Pedido> cq = cb.createQuery(Pedido.class);
        Root<Pedido> pedido = cq.from(Pedido.class);

        List<Predicate> predicates = new ArrayList<>();

        for (Map.Entry<String, Object> entry : filtros.entrySet()) {
            switch (entry.getKey()) {
                case Pedido.FECHA_PEDIDO:
                    Map<String, Object> fechaFiltro = (Map<String, Object>) entry.getValue();
                    if (fechaFiltro.containsKey(Pedido.GTE)) {
                        predicates.add(cb.greaterThanOrEqualTo(pedido.get(Pedido.FECHA_PEDIDO), (Comparable) fechaFiltro.get(Pedido.GTE)));
                    }
                    if (fechaFiltro.containsKey(Pedido.LT)) {
                        predicates.add(cb.lessThan(pedido.get(Pedido.FECHA_PEDIDO), (Comparable) fechaFiltro.get(Pedido.LT)));
                    }
                    break;
                case "_id":
                    predicates.add(cb.equal(pedido.get(entry.getKey()), entry.getValue()));
                    break;
                case Pedido.ORDEN_FECHA:
                    int sort = (int) entry.getValue();
                    if (sort == 1) {
                        cq.orderBy(cb.asc(pedido.get(Pedido.FECHA_PEDIDO)));
                    } else {
                        cq.orderBy(cb.desc(pedido.get(Pedido.FECHA_PEDIDO)));
                    }
                    break;
                case Pedido.NUMERO_COMPROBANTE:
                    predicates.add(cb.like(cb.lower(pedido.get(Pedido.NUMERO_COMPROBANTE)), "%" + entry.getValue().toString().toLowerCase() + "%"));
                    break;
                default:
                    predicates.add(cb.equal(pedido.get(entry.getKey()), entry.getValue()));
                    break;
            }
        }

        if (!filtros.containsKey(Pedido.ORDEN_FECHA)) {
            cq.orderBy(cb.desc(pedido.get(Pedido.FECHA_PEDIDO)));
        }

        cq.where(predicates.toArray(new Predicate[0]));

        return entityManager.createQuery(cq).getResultList();
    }

}
