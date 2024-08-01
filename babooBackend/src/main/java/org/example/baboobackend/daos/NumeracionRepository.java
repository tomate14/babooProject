package org.example.baboobackend.daos;


import org.example.baboobackend.entities.Numeracion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NumeracionRepository extends JpaRepository<Numeracion, Integer> {
    Numeracion findByTipoComprobante(int tipoComprobante);
}

