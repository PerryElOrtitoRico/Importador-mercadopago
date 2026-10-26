package com.finanzas.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

class T02aEncabezadoOcultoTest {
    @Test
    void encabezadoCompleto() {
        assertEquals(Esperado.encabezado(), LectorResumen.leerEncabezado(Muestra.lineas()));
    }

    @Test
    void dosCamposEnLaMismaLinea() {
        List<String> l = List.of("Saldo inicial: $ 1,00       Saldo final: $ -2.000,50");
        assertEquals(new BigDecimal("1.00"), LectorResumen.campo(l, "Saldo inicial"));
        assertEquals(new BigDecimal("-2000.50"), LectorResumen.campo(l, "Saldo final"));
    }
}
