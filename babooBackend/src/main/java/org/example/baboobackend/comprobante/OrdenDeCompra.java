package org.example.baboobackend.comprobante;

import org.example.baboobackend.comprobante.calculador.Calculador;
import org.example.baboobackend.comprobante.calculador.Resta;
import org.example.baboobackend.comprobante.calculador.Suma;
import org.example.baboobackend.entities.Producto;

import java.util.Optional;

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

    @Override
    public int getPrecio(Producto producto) {
        return producto.getPrecioCompra();
    }

    @Override
    public void buildForSave(Optional<Producto> optExistente, Producto newProd) {
        final Producto prodExistente = optExistente.get();
        int nuevoStock = this.calcularStock(prodExistente.getStock(), newProd.getStock());
        newProd.setStock(nuevoStock);

        final int precioCompraMayor = Math.max(prodExistente.getPrecioCompra(), newProd.getPrecioCompra());;
        newProd.setPrecioCompra(precioCompraMayor);

        newProd.setPrecioVenta(newProd.calcularPrecioConAumento(prodExistente.getProveedor()));
    }
}
