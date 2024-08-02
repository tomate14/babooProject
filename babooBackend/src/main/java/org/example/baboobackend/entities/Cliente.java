package org.example.baboobackend.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import lombok.Data;

@Entity
@Data
public class Cliente {

    private String nombre;
    @Id
    private int dni;
    private String fechaNacimiento;
    private String direccion;
    private String telefono;
    private String email;
    private String cuit;
    private String fechaAlta;
    private int tipoUsuario;
    private Integer porcentajeRemarcar;

    @Transient
    private boolean esDeudor = false;

}
