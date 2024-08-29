package org.example.baboobackend.daos;

import org.example.baboobackend.entities.ComprobanteItem;
import org.example.baboobackend.entities.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComprobanteItemRepository extends JpaRepository<ComprobanteItem, Integer> {
}
