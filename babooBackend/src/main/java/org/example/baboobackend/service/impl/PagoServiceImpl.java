package org.example.baboobackend.service.impl;

import org.example.baboobackend.daos.CajaRepository;
import org.example.baboobackend.daos.ClienteRepository;
import org.example.baboobackend.daos.PagoRepository;
import org.example.baboobackend.daos.PedidoRepository;
import org.example.baboobackend.dto.PagoDTO;
import org.example.baboobackend.entities.Caja;
import org.example.baboobackend.entities.Cliente;
import org.example.baboobackend.entities.Pago;
import org.example.baboobackend.entities.Pedido;
import org.example.baboobackend.service.PagoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class PagoServiceImpl implements PagoService {

    @Autowired
    private PagoRepository pagoRepository;

    @Autowired
    private CajaRepository cajaRepository;

    @Autowired
    private PedidoRepository pedidoRepository;
    @Autowired
    private ClienteRepository clienteRepository;

    @Override
    public PagoDTO getPagosByIdPedido(int idPedido) {
        Optional<Pedido> pedido = pedidoRepository.findById(idPedido);
        if (pedido.isPresent()) {
            Optional<Cliente> cliente = clienteRepository.findByDni(pedido.get().getDniCliente());
            List<Pago> pagos = pagoRepository.findAllByIdPedido(idPedido);
            PagoDTO pagoDto = new PagoDTO(pagos);
            if (cliente.isPresent()) {
                pagoDto.setNombreCliente(cliente.get().getNombre());
                pagoDto.setTelefonoCliente(cliente.get().getTelefono());
                pagoDto.setEmailCliente(cliente.get().getEmail());
            }
            return pagoDto;
        } else {
            throw new IllegalArgumentException("El pedido "+idPedido+" no existe");
        }
    }

    @Override
    public List<Pago> getPagosByFecha(String startDate, String endDate) {
        return pagoRepository.findAllByFechaPagoBetween(startDate, endDate);
    }

    @Override
    public void deletePagoById(int idPago) {
        pagoRepository.deleteById(idPago);
    }

    @Override
    public void deletePagosByIdPedido(int idPedido) {
        pagoRepository.deleteAllByIdPedido(idPedido);
    }

    @Override
    public Pago createPago(Pago nuevoPago) {
        String fechaPago = nuevoPago.getFechaPago();
        String nuevaFecha = fechaPago;//reemplazarT(fechaPago);
        nuevoPago.setFechaPago(nuevaFecha);

        if (esCajaCerrada(nuevaFecha)) {
            throw new IllegalStateException("No se pueden agregar datos porque la caja del día ya fue cerrada");
        }

        return pagoRepository.save(nuevoPago);
    }

    @Override
    public Pago updatePago(int idPago, Pago pagoActualizado) {
        Optional<Pago> pagoExistente = pagoRepository.findById(idPago);
        if (pagoExistente.isPresent()) {
            Pago pago = pagoExistente.get();
            // Actualizar campos
            pago.setIdPedido(pagoActualizado.getIdPedido());
            pago.setFechaPago(pagoActualizado.getFechaPago());
            pago.setValor(pagoActualizado.getValor());
            pago.setFormaPago(pagoActualizado.getFormaPago());
            pago.setDescripcion(pagoActualizado.getDescripcion());

            return pagoRepository.save(pago);
        } else {
            throw new IllegalStateException("No se pueden actualizar el pago porque no existe");
        }
    }

    private boolean esCajaCerrada(String fecha) {
        Caja lastDate = cajaRepository.findTopByOrderByFechaDesc();
        if (Objects.nonNull(lastDate)) {
            String lastDateCaja = lastDate.getFecha();
            return lastDateCaja != null && lastDateCaja.equalsIgnoreCase(fecha);
        }
        return false;

    }
}
