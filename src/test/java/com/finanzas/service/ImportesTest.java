package com.finanzas.service;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class ImportesTest {

    @Test
    void parsear() {
        assertEquals(new BigDecimal("-1234.56"), Importes.parsear("$ -1.234,56"));
        assertEquals(new BigDecimal("500000.00"), Importes.parsear("500.000,00"));
        assertThrows(IllegalArgumentException.class, () -> Importes.parsear(" "));
    }

    @Test
    void aCsv() {
        assertEquals("-1234.50", Importes.aCsv(new BigDecimal("-1234.5")));
        assertEquals("1000.00", Importes.aCsv(new BigDecimal("1000")));
    }

    @Test
    void normalizar() {
        assertEquals("verduleria dona", Textos.normalizar("  Verdulería   Doña "));
        assertEquals("", Textos.normalizar(null));
    }
}
