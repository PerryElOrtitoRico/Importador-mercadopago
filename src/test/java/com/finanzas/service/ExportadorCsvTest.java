package com.finanzas.service;

import static org.junit.jupiter.api.Assertions.*;

import com.finanzas.model.Clasificado;
import com.finanzas.model.Tipo;
import com.finanzas.model.Movimiento;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

class ExportadorCsvTest {

    @Test
    void campo() {
        assertEquals("hola", ExportadorCsv.campo("hola"));
        assertEquals("\"a,b\"", ExportadorCsv.campo("a,b"));
    }

    @Test
    void linea() {
        Movimiento m = new Movimiento(LocalDate.of(2026, 10, 2), "Pago con QR Carrefour", "182000000005", new BigDecimal("-1400.00"), new BigDecimal("128281.63"));
        Clasificado c = new Clasificado(m, Tipo.PAGO_QR, "Carrefour", "Supermercado", false, "");
        assertEquals("2026-10-02,Carrefour,Supermercado,-1400.00,MP 182000000005", ExportadorCsv.linea(c));
        assertEquals(ExportadorCsv.CABECERA + "\n" + ExportadorCsv.linea(c) + "\n", ExportadorCsv.csv(List.of(c)));
    }
}
