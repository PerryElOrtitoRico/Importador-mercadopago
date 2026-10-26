package com.finanzas.service;

import static org.junit.jupiter.api.Assertions.*;

import com.finanzas.model.Encabezado;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class LectorEncabezadoTest {

    @Test
    void encabezado() {
        Encabezado e = LectorResumen.leerEncabezado(Muestra.lineas());
        assertEquals("Ana Laura Gomez", e.titular());
        assertEquals(new BigDecimal("2580.25"), e.saldoInicial());
        assertEquals(new BigDecimal("-82396.18"), e.salidas());
    }

    @Test
    void campoQueFalta() {
        assertThrows(IllegalArgumentException.class, () -> LectorResumen.campo(java.util.List.of("hola"), "Saldo final"));
    }
}
