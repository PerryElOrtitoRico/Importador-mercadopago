package com.finanzas.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.finanzas.model.Encabezado;
import com.finanzas.model.Movimiento;
import com.finanzas.model.Resumen;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

class T03VerificadorOcultoTest {
    @Test
    void vacio() {
        assertEquals(List.of("El resumen no tiene movimientos"),
                Verificador.verificar(new Resumen(Esperado.encabezado(), List.of())));
    }

    @Test
    void entradasDistintas() {
        Encabezado e = Esperado.encabezado();
        Encabezado mal = new Encabezado(e.titular(), e.periodo(), e.saldoInicial(), e.saldoFinal(), new BigDecimal("1.00"), e.salidas());
        assertEquals(List.of("Entradas distintas: suma 166401.38, resumen 1.00"),
                Verificador.verificar(new Resumen(mal, Esperado.movimientos())));
    }

    @Test
    void valorMalLeido() {
        List<Movimiento> ms = new ArrayList<>(Esperado.movimientos());
        ms.set(13, Esperado.m(2026, 10, 6, "Pago con QR La Esquina", "182700000013", "-0.02", "86585.45"));
        assertEquals(List.of(
                "Saldo distinto en 182700000013: calculado 86585.44, resumen 86585.45",
                "Saldo final distinto: calculado 86585.44, resumen 86585.45",
                "Salidas distintas: suma -82396.19, resumen -82396.18"),
                Verificador.verificar(new Resumen(Esperado.encabezado(), ms)));
    }
}
