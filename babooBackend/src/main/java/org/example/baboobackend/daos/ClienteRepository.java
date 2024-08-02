package org.example.baboobackend.daos;

import org.example.baboobackend.entities.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Integer> {
    Optional<Cliente> findByDni(Integer dni);
    @Query("SELECT c FROM Cliente c WHERE c.nombre LIKE %:regex%")
    List<Cliente> findByNombreContaining(@Param("regex") String regex);
    List<Cliente> findByTipoUsuario(int i);
}
