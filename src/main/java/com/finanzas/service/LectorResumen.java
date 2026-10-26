package com.finanzas.service;

import com.finanzas.model.Encabezado;
import com.finanzas.model.Movimiento;
import com.finanzas.model.Resumen;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Lee el texto de un resumen de Mercado Pago tal como lo deja {@code pdftotext -layout}. */
public final class LectorResumen {

    /** Fila de movimiento: fecha, descripción (puede faltar), ID de 12 o 13 dígitos, valor y saldo. */
    static final Pattern FILA = Pattern.compile(
            "^\\s*(\\d{2}-\\d{2}-\\d{4})\\s+(.*?)\\s*(\\d{12,13})\\s+\\$\\s*(-?[\\d.]+,\\d{2})\\s+\\$\\s*(-?[\\d.]+,\\d{2})\\s*$");

    static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    private LectorResumen() {
    }

    /** Encabezado y movimientos. */
    public static Resumen leer(List<String> lineas) {
        // TODO T02b
        throw new UnsupportedOperationException("T02b");
    }

    /** Titular, período y los cuatro importes de la primera página. */
    public static Encabezado leerEncabezado(List<String> lineas) {
        // TODO T02a
        throw new UnsupportedOperationException("T02a");
    }

    /** El importe que sigue a "nombre: $" en la primera línea que lo tenga. Si ninguna lo tiene → IllegalArgumentException. */
    static BigDecimal campo(List<String> lineas, String nombre) {
        // TODO T02a
        throw new UnsupportedOperationException("T02a");
    }

    /** Grupos de líneas no vacías separados por una o más líneas vacías (o solo con espacios). */
    static List<List<String>> bloques(List<String> lineas) {
        // TODO T02b
        throw new UnsupportedOperationException("T02b");
    }

    /** Un movimiento por cada bloque que tiene una fila; los bloques sin fila (títulos, páginas) se saltean. */
    public static List<Movimiento> leerMovimientos(List<String> lineas) {
        // TODO T02b
        throw new UnsupportedOperationException("T02b");
    }
}
