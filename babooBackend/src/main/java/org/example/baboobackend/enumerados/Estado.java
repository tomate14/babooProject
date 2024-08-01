package org.example.baboobackend.enumerados;

public enum Estado {
    PENDIENTE(1, "PENDIENTE"),
    COMPLETO(2, "COMPLETO");

    private final int codigo;
    private final String descripcion;

    Estado(int codigo, String descripcion) {
        this.codigo = codigo;
        this.descripcion = descripcion;
    }

    public int getCodigo() {
        return codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public static EstadoPedido fromCodigo(int codigo) {
        for (EstadoPedido estado : EstadoPedido.values()) {
            if (estado.getCodigo() == codigo) {
                return estado;
            }
        }
        throw new IllegalArgumentException("Código de estado no válido: " + codigo);
    }

}
