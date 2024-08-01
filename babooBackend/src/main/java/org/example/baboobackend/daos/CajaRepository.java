package org.example.baboobackend.daos;

import org.example.baboobackend.entities.Caja;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CajaRepository extends JpaRepository<Caja, Integer> {
    @Query("SELECT c FROM Caja c WHERE c.fecha BETWEEN :startDate AND :endDate")
    List<Caja> findByFechaBetween(@Param("startDate") String startDate, @Param("endDate") String endDate);
    Caja findFirstByOrderByFechaDesc();
    Caja findTopByOrderByFechaDesc();
    Caja findFirstByFecha(String startDate);

}
