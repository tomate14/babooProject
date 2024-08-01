package org.example.baboobackend.enumerados;

public enum TipoUsuario {
    CLIENTE(1, "CLIENTE"),
    PROVEEDOR(2, "PROVEEDOR");

    private final int codigo;
    private final String descripcion;

    TipoUsuario(int codigo, String descripcion) {
        this.codigo = codigo;
        this.descripcion = descripcion;
    }

    public int getCodigo() {
        return codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public static TipoUsuario fromCodigo(int codigo) {
        for (TipoUsuario estado : TipoUsuario.values()) {
            if (estado.getCodigo() == codigo) {
                return estado;
            }
        }
        throw new IllegalArgumentException("Código de usuario no válido: " + codigo);
    }
}

