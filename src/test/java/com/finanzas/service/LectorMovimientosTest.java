package com.finanzas.service;

import static org.junit.jupiter.api.Assertions.*;

import com.finanzas.model.Movimiento;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

class LectorMovimientosTest {

    @Test
    void bloques() {
        List<List<String>> b = LectorResumen.bloques(List.of("a", "", "  ", "b", "c", ""));
        assertEquals(List.of(List.of("a"), List.of("b", "c")), b);
    }

    @Test
    void movimientos() {
        List<Movimiento> ms = LectorResumen.leerMovimientos(Muestra.lineas());
        assertEquals(14, ms.size());
        assertEquals(LocalDate.of(2026, 10, 1), ms.get(0).fecha());
        assertEquals("Transferencia recibida PEREZ JUAN CARLOS", ms.get(1).descripcion());
        assertEquals("180700000002", ms.get(1).idOperacion());
        assertEquals(new BigDecimal("-0.01"), ms.get(13).valor());
    }
}
