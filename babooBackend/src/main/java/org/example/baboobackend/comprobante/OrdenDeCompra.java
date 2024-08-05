package org.example.baboobackend.comprobante;

import org.example.baboobackend.comprobante.calculador.Calculador;
import org.example.baboobackend.comprobante.calculador.Resta;
import org.example.baboobackend.comprobante.calculador.Suma;

public class OrdenDeCompra extends Comprobante {

    public OrdenDeCompra() {
        //stock se suma al total
        super.calcStock = new Suma();
        //Total es positivo porque es un pago
        super.calcTotal = new Resta();
    }

    public int calcularStock(int existente, int nuevo) {
        return calcStock.calcular(existente, nuevo);
    }

    public int calcularTotal(int total) {
        return calcTotal.calcular(0, total);
    }
}
