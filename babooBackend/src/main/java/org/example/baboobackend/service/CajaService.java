package org.example.baboobackend.service;

import org.example.baboobackend.entities.Caja;

import java.util.List;

public interface CajaService {
    List<Caja> getCajasByDateRange(String startDate, String endDate);
    Caja cierreCaja(String startDate, String endDate);
    Caja getLastCaja();
}
