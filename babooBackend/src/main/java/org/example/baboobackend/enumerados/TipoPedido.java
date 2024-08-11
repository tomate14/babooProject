package org.example.baboobackend.enumerados;

public enum TipoPedido {
    PEDIDO(1, "PEDIDO", "PED"),
    CC(2, "CUENTA CORRIENTE", "CC"),
    ORC(3, "ORDEN DE COMPRA", "ORC"),
    ORV(4, "ORDEN DE VENTA", "ORV"),
    NDC(5, "NOTA DE CREDITO", "NDC");;

    private final int codigo;
    private final String descripcion;
    private final String sigla;

    TipoPedido(int codigo, String descripcion, String sigla) {
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.sigla = sigla;
    }

    public int getCodigo() {
        return codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getSigla() {
        return sigla;
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