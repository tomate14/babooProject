package org.example.baboobackend.comprobante;

import org.example.baboobackend.comprobante.calculador.Calculador;
import org.example.baboobackend.comprobante.calculador.Resta;
import org.example.baboobackend.comprobante.calculador.Suma;

public class OrdenDeVenta extends Comprobante {

    public OrdenDeVenta() {
        //stock se resta del total
        super.calcStock = new Resta();
        //Total es positivo porque es una venta
        super.calcTotal = new Suma();
    }

    public int calcularStock(int existente, int nuevo) {
        return calcStock.calcular(existente, nuevo);
    }

    public int calcularTotal(int total) {
        return calcTotal.calcular(total, 0);
    }
}
