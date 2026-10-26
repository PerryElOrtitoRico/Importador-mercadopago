package com.finanzas.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Una fila del detalle de movimientos del resumen. {@code valor} es negativo en las salidas. */
public record Movimiento(LocalDate fecha, String descripcion, String idOperacion, BigDecimal valor, BigDecimal saldo) {
}
