package org.example.baboobackend.comprobante;

import org.example.baboobackend.comprobante.calculador.Calculador;
import org.example.baboobackend.comprobante.calculador.Resta;
import org.example.baboobackend.comprobante.calculador.Suma;
import org.example.baboobackend.entities.Cliente;
import org.example.baboobackend.entities.Producto;

import java.util.Optional;

public class OrdenDeVenta extends Comprobante {

    public OrdenDeVenta() {
        //stock se resta del total
        super.calcStock = new Resta();
        //Total es positivo porque es una venta
        super.calcTotal = new Suma();

        super.isUsuarioExistente = false;
    }

    public int calcularStock(int existente, int nuevo) {
        return calcStock.calcular(existente, nuevo);
    }

    public int calcularTotal(int total) {
        return calcTotal.calcular(total, 0);
    }

    @Override
    public int getPrecio(Producto producto) {
        return producto.getPrecioVenta();
    }

    @Override
    public void buildForSave(Optional<Producto> optExistente, Producto newProd) {
        if (optExistente.isPresent()) {
            final Producto prodExistente = optExistente.get();
            int nuevoStock = this.calcularStock(prodExistente.getStock(), newProd.getStock());
            newProd.setIdProveedor(prodExistente.getIdProveedor());
            newProd.setStock(nuevoStock);
            newProd.setPrecioCompra(prodExistente.getPrecioCompra());
            newProd.setPrecioVenta(prodExistente.getPrecioVenta());
        }
    }

    @Override
    public int getIdUsuario(Producto producto) {
        return 0;
    }

}
