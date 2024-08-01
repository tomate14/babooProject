package org.example.baboobackend.enumerados;

public enum TipoPedido {
    PEDIDO(1, "PEDIDO"),
    CC(2, "CUENTA CORRIENTE");

    private final int codigo;
    private final String descripcion;

    TipoPedido(int codigo, String descripcion) {
        this.codigo = codigo;
        this.descripcion = descripcion;
    }

    public int getCodigo() {
        return codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public static TipoPedido fromCodigo(int codigo) {
        for (TipoPedido estado : TipoPedido.values()) {
            if (estado.getCodigo() == codigo) {
                return estado;
            }
        }
        throw new IllegalArgumentException("Código de estado no válido: " + codigo);
    }

}