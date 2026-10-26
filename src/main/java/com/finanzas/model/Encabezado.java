package com.finanzas.model;

import java.math.BigDecimal;

/** Datos de la primera página del resumen. {@code salidas} es negativo, como lo muestra el PDF. */
public record Encabezado(String titular, String periodo, BigDecimal saldoInicial, BigDecimal saldoFinal,
                         BigDecimal entradas, BigDecimal salidas) {
}
