package org.example.baboobackend.controllers;

import org.example.baboobackend.entities.Caja;
import org.example.baboobackend.service.CajaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/caja")
public class CajaController {

    @Autowired
    private CajaService cajaService;

    @GetMapping("/{startDate}/{endDate}")
    public ResponseEntity<List<Caja>> getCajaByDate(
            @PathVariable String startDate,
            @PathVariable String endDate) {
        List<Caja> cajas = cajaService.getCajasByDateRange(startDate, endDate);
        return ResponseEntity.ok(cajas);
    }

    @GetMapping("/caja-cierre/{fechaInicio}/{fechaFin}")
    public ResponseEntity<?> cerrarCaja(
            @PathVariable String fechaInicio,
            @PathVariable String fechaFin) {
        try {
            Caja caja = cajaService.cierreCaja(fechaInicio, fechaFin);
            return ResponseEntity.ok(caja);
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.ok(exception.getMessage());
        }

    }

    @GetMapping("/ultima-cerrada")
    public ResponseEntity<Caja> getLastCaja() {
        Caja caja = cajaService.getLastCaja();
        if (Objects.nonNull(caja)) {
            return ResponseEntity.ok(caja);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }
}
