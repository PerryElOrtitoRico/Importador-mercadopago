package com.finanzas.service;

import com.finanzas.model.Encabezado;
import com.finanzas.model.Movimiento;
import com.finanzas.model.Resumen;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/** Lo que tiene que dar leer {@link Muestra#TEXTO}, escrito a mano. */
public final class Esperado {

    private Esperado() {
    }

    public static Movimiento m(int a, int mes, int d, String desc, String id, String valor, String saldo) {
        return new Movimiento(LocalDate.of(a, mes, d), desc, id, new BigDecimal(valor), new BigDecimal(saldo));
    }

    public static Encabezado encabezado() {
        return new Encabezado("Ana Laura Gomez", "Del 1 al 6 de octubre de 2026", new BigDecimal("2580.25"),
                new BigDecimal("86585.45"), new BigDecimal("166401.38"), new BigDecimal("-82396.18"));
    }

    public static List<Movimiento> movimientos() {
        return List.of(
                m(2026, 10, 1, "Rendimientos", "1750900000001", "1.38", "2581.63"),
                m(2026, 10, 1, "Transferencia recibida PEREZ JUAN CARLOS", "180700000002", "150000.00", "152581.63"),
                m(2026, 10, 1, "Transferencia enviada Ana Laura Gomez", "180800000003", "-20000.00", "132581.63"),
                m(2026, 10, 2, "Pago con QR Autoservicio7030", "181900000004", "-2900.00", "129681.63"),
                m(2026, 10, 2, "Pago con QR Carrefour", "182000000005", "-1400.00", "128281.63"),
                m(2026, 10, 2, "Devolución de dinero Carrefour", "182000000005", "1400.00", "129681.63"),
                m(2026, 10, 3, "Transferencia recibida GOMEZ ANA LAURA", "181500000006", "5000.00", "134681.63"),
                m(2026, 10, 3, "Dinero reservado HOGAR", "1752000000007", "-30000.00", "104681.63"),
                m(2026, 10, 4, "Dinero retirado HOGAR", "1752100000008", "10000.00", "114681.63"),
                m(2026, 10, 4, "Transferencia enviada", "184900000009", "-3500.00", "111181.63"),
                m(2026, 10, 5, "Pago con QR ECOGAS", "182600000010", "-12345.67", "98835.96"),
                m(2026, 10, 5, "Retiro de dinero Carrefour", "182700000011", "-8000.00", "90835.96"),
                m(2026, 10, 5, "Transferencia enviada Verdulería Doña Rosa", "182000000012", "-4250.50", "86585.46"),
                m(2026, 10, 6, "Pago con QR La Esquina", "182700000013", "-0.01", "86585.45"));
    }

    public static Resumen resumen() {
        return new Resumen(encabezado(), movimientos());
    }
}
