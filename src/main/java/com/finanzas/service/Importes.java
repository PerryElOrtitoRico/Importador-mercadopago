package com.finanzas.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** Importes con el formato del resumen ("$ -1.234,56") y del CSV ("-1234.56"). */
public final class Importes {

    private Importes() {
    }

    /** "$ -1.234,56", "-1.234,56" o "$ 0,01" → BigDecimal con escala 2. Texto vacío o null → IllegalArgumentException. */
    public static BigDecimal parsear(String texto) {
        if (texto == null || texto.isBlank()) {
            throw new IllegalArgumentException("Importe vacío");
        }
        String s = texto.replace("$", "").replace(" ", "").replace(".", "").replace(",", ".");
        return new BigDecimal(s).setScale(2, RoundingMode.HALF_UP);
    }

    /** BigDecimal → texto con punto decimal y dos decimales, sin separador de miles: -1234.56, 0.50. */
    public static String aCsv(BigDecimal valor) {
        return valor.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }
}
