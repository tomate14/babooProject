package org.example.baboobackend.comprobante;

import org.example.baboobackend.comprobante.calculador.Calculador;
import org.example.baboobackend.comprobante.calculador.Resta;
import org.example.baboobackend.comprobante.calculador.Suma;
import org.example.baboobackend.entities.Producto;

import java.util.List;
import java.util.Optional;

public abstract class Comprobante {

    protected Calculador calcStock;
    protected Calculador calcTotal;
    protected boolean isUsuarioExistente;

    public boolean checkUsuarioExistente() {
        return this.isUsuarioExistente;
    };

    public abstract int calcularStock(int existente, int nuevo);
    public abstract int calcularTotal(int total);
    public abstract int getPrecio(Producto producto);
    public abstract void buildForSave(Optional<Producto> prodExistente, Producto newProd);
    public abstract int getIdUsuario(Producto producto);
}
