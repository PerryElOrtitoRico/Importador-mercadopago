package com.finanzas.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.util.List;

class T02bMovimientosOcultoTest {
    @Test
    void todosLosMovimientos() {
        assertEquals(Esperado.movimientos(), LectorResumen.leerMovimientos(Muestra.lineas()));
    }

    @Test
    void resumenCompleto() {
        assertEquals(Esperado.resumen(), LectorResumen.leer(Muestra.lineas()));
    }

    @Test
    void dosFilasEnUnBloque() {
        List<String> l = List.of(
                "01-10-2026    Rendimientos    1750900000001     $ 1,38    $ 2.581,63",
                "02-10-2026    Rendimientos    1750900000002     $ 1,00    $ 2.582,63");
        assertThrows(IllegalStateException.class, () -> LectorResumen.leerMovimientos(l));
    }

    @Test
    void sinMovimientos() {
        assertEquals(List.of(), LectorResumen.leerMovimientos(List.of("hola", "", "1/10")));
    }
}
