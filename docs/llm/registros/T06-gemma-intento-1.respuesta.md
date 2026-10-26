```java
// src/main/java/com/finanzas/service/ExportadorCsv.java
package com.finanzas.service;

import com.finanzas.model.Clasificado;
import com.finanzas.model.Movimiento;
import com.finanzas.model.Tipo;

import java.util.List;

/** CSV para importar en Actual Budget: fecha,beneficiario,categoria,importe,notas. */
public final class ExportadorCsv {

    public static final String CABECERA = "fecha,beneficiario,categoria,importe,notas";

    private ExportadorCsv() {
    }

    /** Cabecera y una línea por movimiento, separadas por "\n" y terminadas en "\n". */
    public static String csv(List<Clasificado> clasificados) {
        StringBuilder sb = new StringBuilder(CABECERA).append('\n');
        for (Clasificado c : clasificados) sb.append(linea(c)).append('\n');
        return sb.toString();
    }

    /** Una línea sin el "\n" final. */
    static String linea(Clasificado c) {
        Movimiento m = c.movimiento();
        String beneficiario = c.contraparte();
        if (beneficiario.isEmpty()) beneficiario = c.tipo().prefijo();
        if (beneficiario.isEmpty()) beneficiario = m.descripcion();
        String notas = "MP " + m.idOperacion();
        if (c.paraRevisar()) notas = notas + " - REVISAR: " + c.motivo();
        return String.join(",", m.fecha().toString(), campo(beneficiario), campo(c.categoria()), Importes.aCsv(m.valor()), campo(notas));
    }

    /** Entre comillas (y las comillas internas dobladas) si tiene coma, comillas o salto de línea; si no, tal cual. */
    static String campo(String texto) {
        if (texto.contains(",") || texto.contains("\"") || texto.contains("\n")) return "\"" + texto.replace("\"", "\"\"") + "\"";
        return texto;
    }
}
```

```java
// src/test/java/com/finanzas/service/ExportadorCsvTest.java
package com.finanzas.service;

import static org.junit.jupiter.api.Assertions.*;

import com.finanzas.model.Clasificado;
import com.finanzas.model.Tipo;
import com.finanzas.model.Movimiento;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

class ExportadorCsvTest {

    @Test
    void campo() {
        assertEquals("hola", ExportadorCsv.campo("hola"));
        assertEquals("\"a,b\"", ExportadorCsv.campo("a,b"));
    }

    @Test
    void linea() {
        Movimiento m = new Movimiento(LocalDate.of(2026, 10, 2), "Pago con QR Carrefour", "182000000005", new BigDecimal("-1400.00"), new BigDecimal("128281.63"));
        Clasificado c = new Clasificado(m, Tipo.PAGO_QR, "Carrefour", "Supermercado", false, "");
        assertEquals("2026-10-02,Carrefour,Supermercado,-1400.00,MP 182000000005", ExportadorCsv.linea(c));
        assertEquals(ExportadorCsv.CABECERA + "\n" + ExportadorCsv.linea(c) + "\n", ExportadorCsv.csv(List.of(c)));
    }
}
```

<!-- 54 s · entrada 2139 · salida 878 · finish_reason stop -->
