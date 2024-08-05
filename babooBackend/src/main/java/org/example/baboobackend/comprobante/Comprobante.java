package org.example.baboobackend.comprobante;

import org.example.baboobackend.comprobante.calculador.Calculador;
import org.example.baboobackend.comprobante.calculador.Resta;
import org.example.baboobackend.comprobante.calculador.Suma;

public abstract class Comprobante {

    protected Calculador calcStock;
    protected Calculador calcTotal;

    public abstract int calcularStock(int existente, int nuevo);
    public abstract int calcularTotal(int total);
}
