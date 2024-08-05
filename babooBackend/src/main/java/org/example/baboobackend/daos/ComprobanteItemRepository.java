package org.example.baboobackend.daos;

import org.example.baboobackend.entities.Cliente;
import org.example.baboobackend.entities.ComprobanteItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ComprobanteItemRepository extends JpaRepository<ComprobanteItem, Integer> {
}
