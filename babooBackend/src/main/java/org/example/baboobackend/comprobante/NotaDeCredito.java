package org.example.baboobackend.comprobante;

import org.example.baboobackend.comprobante.calculador.Resta;
import org.example.baboobackend.comprobante.calculador.Suma;
import org.example.baboobackend.entities.Pedido;
import org.example.baboobackend.entities.Producto;
import org.example.baboobackend.enumerados.Estado;

import java.util.Optional;

public class NotaDeCredito extends Comprobante {

    public NotaDeCredito() {
        //stock se suma al total
        super.calcStock = new Suma();
        //Total es positivo porque es un pago
        super.calcTotal = new Resta();
        super.isUsuarioExistente = false;
    }

    @Override
    public int calcularStock(int existente, int nuevo) {
        return calcStock.calcular(existente, nuevo);
    }

    @Override
    public int calcularTotal(int total) {
        return calcTotal.calcular(0, total);
    }

    @Override
    public int getPrecio(Producto producto) {
        return producto.getPrecioVenta();
    }

    @Override
    public void buildForSave(Optional<Producto> optExistente, Producto newProd) {
        final Producto prodExistente = optExistente.get();
        int nuevoStock = this.calcularStock(prodExistente.getStock(), newProd.getStock());
        newProd.setStock(nuevoStock);
        newProd.setPrecioVenta(prodExistente.getPrecioVenta());
        newProd.setPrecioCompra(prodExistente.getPrecioCompra());
        newProd.setCodigoBarra(prodExistente.getCodigoBarra());
        newProd.setIdProveedor(prodExistente.getIdProveedor());
    }

    @Override
    public int getIdUsuario(Producto producto) {
        return 0;
    }

    @Override
    public void setEstado(Pedido pedido) {
        pedido.setEstado(Estado.PENDIENTE.getDescripcion());
    }
}
