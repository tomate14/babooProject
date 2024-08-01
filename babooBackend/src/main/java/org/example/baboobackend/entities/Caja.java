package org.example.baboobackend.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import lombok.Data;

@Data
@Entity
public class Caja {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String fecha;

    private double contado;
    private double tarjeta;
    private double cuentaDni;
    private double transferencia;
    private double gastos;
    private double ganancia;
    @Transient
    private double diferencia;

}
