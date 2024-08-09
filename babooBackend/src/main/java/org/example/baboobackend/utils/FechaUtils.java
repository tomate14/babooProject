package org.example.baboobackend.utils;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class FechaUtils {
    // Método para obtener la fecha actual en UTC con el timezone de Argentina
    public static String obtenerFechaEnUTC() {
        // Zona horaria de Argentina
        ZoneId zoneIdArgentina = ZoneId.of("America/Argentina/Buenos_Aires");

        // Fecha y hora actual en la zona horaria de Argentina
        ZonedDateTime fechaArgentina = ZonedDateTime.now(zoneIdArgentina);

        // Formatear la fecha en la zona horaria de Argentina
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        return fechaArgentina.format(formatter);
    }
}
