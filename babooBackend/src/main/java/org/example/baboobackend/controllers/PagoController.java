package org.example.baboobackend.controllers;

import org.example.baboobackend.dto.PagoDTO;
import org.example.baboobackend.entities.Pago;
import org.example.baboobackend.service.PagoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
//ESTA OK
@RestController
@RequestMapping("/pago")
public class PagoController {

    @Autowired
    private PagoService pagoService;

    @GetMapping("/{idPedido}") //OK
    public PagoDTO getPagosByIdPedido(@PathVariable int idPedido) {
        return pagoService.getPagosByIdPedido(idPedido);
    }

    @GetMapping("/caja/{startDate}/{endDate}")
    public List<Pago> getPagosByFecha(
            @PathVariable String startDate,
            @PathVariable String endDate) {
        return pagoService.getPagosByFecha(startDate, endDate);
    }

    @DeleteMapping("/{idPago}")
    public void deletePagoById(@PathVariable int idPago) {
        pagoService.deletePagoById(idPago);
    }

    @PostMapping
    public Pago createPago(@RequestBody Pago nuevoPago) {
        return pagoService.createPago(nuevoPago);
    }

    @PutMapping("/{idPago}")
    public Pago updatePago(@PathVariable int idPago, @RequestBody Pago pagoActualizado) {
        return pagoService.updatePago(idPago, pagoActualizado);
    }
}
