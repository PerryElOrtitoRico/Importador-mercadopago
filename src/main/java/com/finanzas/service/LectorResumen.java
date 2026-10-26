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
        return new Resumen(leerEncabezado(lineas), leerMovimientos(lineas));
    }

    /** Titular, período y los cuatro importes de la primera página. */
    public static Encabezado leerEncabezado(List<String> lineas) {
        // 1. Titular
        String titular = "";
        for (int i = 0; i < lineas.size(); i++) {
            if (lineas.get(i).contains("RESUMEN DE CUENTA")) {
                for (int j = i + 1; j < lineas.size(); j++) {
                    if (!lineas.get(j).isBlank()) {
                        titular = lineas.get(j).trim();
                        break;
                    }
                }
                break;
            }
        }

        // 2. Período
        String periodo = "";
        for (String linea : lineas) {
            int k = linea.indexOf("Periodo:");
            if (k >= 0) {
                periodo = linea.substring(k + "Periodo:".length()).trim();
                break;
            }
        }

        // 3. Importes
        BigDecimal saldoInicial = campo(lineas, "Saldo inicial");
        BigDecimal saldoFinal = campo(lineas, "Saldo final");
        BigDecimal entradas = campo(lineas, "Entradas");
        BigDecimal salidas = campo(lineas, "Salidas");

        return new Encabezado(titular, periodo, saldoInicial, saldoFinal, entradas, salidas);
    }

    /** El importe que sigue a "nombre: $" en la primera línea que lo tenga. Si ninguna lo tiene → IllegalArgumentException. */
    static BigDecimal campo(List<String> lineas, String nombre) {
        Pattern p = Pattern.compile(Pattern.quote(nombre) + ":\\s*\\$\\s*(-?[\\d.]+,\\d{2})");
        for (String linea : lineas) {
            Matcher m = p.matcher(linea);
            if (m.find()) {
                return Importes.parsear(m.group(1));
            }
        }
        throw new IllegalArgumentException("Falta " + nombre + " en el resumen");
    }

    /** Grupos de líneas no vacías separados por una o más líneas vacías (o solo con espacios). */
    static List<List<String>> bloques(List<String> lineas) {
        List<List<String>> out = new ArrayList<>();
        List<String> actual = new ArrayList<>();
        for (String linea : lineas) {
            if (linea.isBlank()) {
                if (!actual.isEmpty()) {
                    out.add(actual);
                    actual = new ArrayList<>();
                }
            } else {
                actual.add(linea);
            }
        }
        if (!actual.isEmpty()) out.add(actual);
        return out;
    }

    /** Un movimiento por cada bloque que tiene una fila; los bloques sin fila (títulos, páginas) se saltean. */
    public static List<Movimiento> leerMovimientos(List<String> lineas) {
        List<Movimiento> out = new ArrayList<>();
        for (List<String> bloque : bloques(lineas)) {
            int fila = -1;
            for (int i = 0; i < bloque.size(); i++) {
                if (FILA.matcher(bloque.get(i)).matches()) {
                    if (fila >= 0) throw new IllegalStateException("Bloque con dos movimientos: " + bloque);
                    fila = i;
                }
            }
            if (fila < 0) continue;
            Matcher m = FILA.matcher(bloque.get(fila));
            m.matches();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < fila; i++) sb.append(bloque.get(i).trim()).append(' ');
            sb.append(m.group(2).trim()).append(' ');
            for (int i = fila + 1; i < bloque.size(); i++) sb.append(bloque.get(i).trim()).append(' ');
            String descripcion = sb.toString().replaceAll("\\s+", " ").trim();
            out.add(new Movimiento(LocalDate.parse(m.group(1), FECHA), descripcion, m.group(3), Importes.parsear(m.group(4)), Importes.parsear(m.group(5))));
        }
        return out;
    }
}
