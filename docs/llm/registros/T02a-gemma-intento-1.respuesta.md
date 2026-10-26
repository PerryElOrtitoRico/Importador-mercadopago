```java
// src/main/java/com/finanzas/service/LectorResumen.java
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
            "^\\s*(\\d{2}-\d{2}-\d{4})\\s+(.*?)\\s*(\\d{12,13})\\s+\\$\\s*(-?[\\d.]+,\\d{2})\\s+\\$\\s*(-?[\\d.]+,\\d{2})\\s*$");

    static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    private LectorResumen() {
    }

    /** Encabezado y movimientos. */
    public static Resumen leer(List<String> lineas) {
        // lo completa otra subtarea (T02b): no lo cambies
        throw new UnsupportedOperationException("T02b");
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
        // lo completa otra subtarea (T02b): no lo cambies
        throw new UnsupportedOperationException("T02b");
    }

    /** Un movimiento por cada bloque que tiene una fila; los bloques sin fila (títulos, páginas) se saltean. */
    public static List<Movimiento> leerMovimientos(List<String> lineas) {
        // lo completa otra subtarea (T02b): no lo cambies
        throw new UnsupportedOperationException("T02b");
    }
}
```

```java
// src/test/java/com/finanzas/service/LectorEncabezadoTest.java
package com.finanzas.service;

import static org.junit.jupiter.api.Assertions.*;

import com.finanzas.model.Encabezado;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class LectorEncabezadoTest {

    @Test
    void encabezado() {
        Encabezado e = LectorResumen.leerEncabezado(Muestra.lineas());
        assertEquals("Ana Laura Gomez", e.titular());
        assertEquals(new BigDecimal("2580.25"), e.saldoInicial());
        assertEquals(new BigDecimal("-82396.18"), e.salidas());
    }

    @Test
    void campoQueFalta() {
        assertThrows(IllegalArgumentException.class, () -> LectorResumen.campo(java.util.List.of("hola"), "Saldo final"));
    }
}
```

<!-- 74 s · entrada 2787 · salida 1268 · finish_reason stop -->
