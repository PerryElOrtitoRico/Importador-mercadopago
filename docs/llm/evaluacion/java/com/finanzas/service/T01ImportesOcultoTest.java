package com.finanzas.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

class T01ImportesOcultoTest {
    @Test
    void parsearCasos() {
        assertEquals(new BigDecimal("0.01"), Importes.parsear("$ 0,01"));
        assertEquals(new BigDecimal("-82396.18"), Importes.parsear("$ -82.396,18"));
        assertEquals(new BigDecimal("1.38"), Importes.parsear("$         1,38"));
        assertThrows(IllegalArgumentException.class, () -> Importes.parsear(null));
    }

    @Test
    void aCsvCasos() {
        assertEquals("0.50", Importes.aCsv(new BigDecimal("0.5")));
        assertEquals("-0.01", Importes.aCsv(new BigDecimal("-0.01")));
    }

    @Test
    void normalizarCasos() {
        assertEquals("aeiou nandu u", Textos.normalizar("ÁÉÍÓÚ Ñandú  ü"));
        assertEquals("gomez ana", Textos.normalizar("\tGÓMEZ\n Ana "));
    }
}
