package com.finanzas.service;

import static org.junit.jupiter.api.Assertions.*;

import com.finanzas.model.Movimiento;
import com.finanzas.model.Resumen;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class VerificadorTest {

    @Test
    void cuadra() {
        assertEquals(List.of(), Verificador.verificar(Muestra.resumen()));
    }

    @Test
    void saldoDistinto() {
        List<Movimiento> ms = new ArrayList<>(Muestra.resumen().movimientos());
        ms.set(3, Esperado.m(2026, 10, 2, "Pago con QR Autoservicio7030", "181900000004", "-2900.00", "129681.00"));
        List<String> errores = Verificador.verificar(new Resumen(Muestra.resumen().encabezado(), ms));
        assertEquals(List.of("Saldo distinto en 181900000004: calculado 129681.63, resumen 129681.00"), errores);
    }
}
