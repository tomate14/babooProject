package org.example.baboobackend.service.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.baboobackend.daos.CajaRepository;
import org.example.baboobackend.daos.PagoRepository;
import org.example.baboobackend.entities.Caja;
import org.example.baboobackend.entities.Pago;
import org.example.baboobackend.service.CajaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class CajaServiceImpl implements CajaService {
    private static final Logger logger = LogManager.getLogger(CajaServiceImpl.class);

    @Autowired
    private CajaRepository cajaRepository;

    @Autowired
    private PagoRepository pagoRepository;

    public List<Caja> getCajasByDateRange(String startDate, String endDate) {
        return cajaRepository.findByFechaBetween(startDate, endDate);
    }

    public Caja cierreCaja(String startDate, String endDate) {
        Caja ultimoCierre = cajaRepository.findFirstByFecha(startDate);
        if (Objects.nonNull(ultimoCierre)) {
            logger.info("Ya existe un cierre de caja para la fecha proporcionada {} ", startDate);
            throw new IllegalArgumentException("Ya existe un cierre de caja para la fecha proporcionada: " + startDate);
        }

        List<Pago> pagos = pagoRepository.findAllByFechaPagoBetween(startDate, endDate);

        double contado = 0;
        double tarjeta = 0;
        double transferencia = 0;
        double cuentaDni = 0;
        double ganancia = 0;
        double gastos = 0;
        double diferenciaCaja = 0;

        if (pagos.isEmpty()) {
            logger.info("Dia sin pagos para procesar {}", startDate);
            throw new IllegalArgumentException("No hay entradas para cerrar la caja del día: " + startDate);
        }

        for (Pago pago : pagos) {
            double valor = pago.getValor();
            int formaPago = pago.getFormaPago();
            int idPedido = pago.getIdPedido();
            if (idPedido != -2 && idPedido != -3) {
                switch (formaPago) {
                    case 1:
                        contado += valor;
                        break;
                    case 2:
                        tarjeta += valor;
                        break;
                    case 3:
                        cuentaDni += valor;
                        break;
                    case 4:
                        transferencia += valor;
                        break;
                }

                if (valor < 0) {
                    gastos += -valor;
                } else {
                    ganancia += valor;
                }
            } else {
                diferenciaCaja += valor;
                logger.info("No se contempla el movimiento de caja {}", pago.getId());
            }
        }

        Caja cierreCaja = new Caja();
        cierreCaja.setFecha(startDate);
        cierreCaja.setContado(contado);
        cierreCaja.setTarjeta(tarjeta);
        cierreCaja.setCuentaDni(cuentaDni);
        cierreCaja.setTransferencia(transferencia);
        cierreCaja.setGastos(gastos);
        cierreCaja.setGanancia(ganancia - gastos);

        cajaRepository.save(cierreCaja);
        cierreCaja.setDiferencia(diferenciaCaja);
        return cierreCaja;
    }

    public Caja getLastCaja() {
        return cajaRepository.findFirstByOrderByFechaDesc();
    }
}