package org.example.baboobackend.comprobante.factory;

import org.example.baboobackend.comprobante.Comprobante;
import org.example.baboobackend.comprobante.OrdenDeCompra;
import org.example.baboobackend.comprobante.OrdenDeVenta;
import org.example.baboobackend.enumerados.TipoPedido;

public class ComprobanteFactory {
    public static Comprobante createComprobante(TipoPedido tipoPedido) {

        return switch (tipoPedido) {
            case ORC -> new OrdenDeCompra();
            case ORV -> new OrdenDeVenta();
            default -> new OrdenDeCompra();
        };
    }
}
