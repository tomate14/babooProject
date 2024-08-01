package org.example.baboobackend.enumerados;

public enum EstadoPedido {
    NO_ENVIADO(1, "No enviado"),
    EN_PRODUCCION(2, "En produccion"),
    ENVIADO(3, "Enviado"),
    TERMINADO(4, "Terminado");

    private final int codigo;
    private final String descripcion;

    EstadoPedido(int codigo, String descripcion) {
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

    @Override
    public String toString() {
        return this.descripcion;
    }
}
