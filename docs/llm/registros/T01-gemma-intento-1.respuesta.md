```java
// src/main/java/com/finanzas/service/Importes.java
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
```

```java
// src/main/java/com/finanzas/service/Textos.java
package com.finanzas.service;

import java.text.Normalizer;

/** Texto normalizado para comparar descripciones sin importar mayúsculas, tildes ni espacios. */
public final class Textos {

    private Textos() {
    }

    /** "  Verdulería   Doña " → "verduleria dona". null → "". */
    public static String normalizar(String texto) {
        if (texto == null) {
            return "";
        }
        String sinTildes = Normalizer.normalize(texto, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        return sinTildes.toLowerCase().replaceAll("\\s+", " ").trim();
    }
}
```

```java
// src/test/java/com/finanzas/service/ImportesTest.java
package com.finanzas.service;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class ImportesTest {

    @Test
    void parsear() {
        assertEquals(new BigDecimal("-1234.56"), Importes.parsear("$ -1.234,56"));
        assertEquals(new BigDecimal("500000.00"), Importes.parsear("500.000,00"));
        assertThrows(IllegalArgumentException.class, () -> Importes.parsear(" "));
    }

    @Test
    void aCsv() {
        assertEquals("-1234.50", Importes.aCsv(new BigDecimal("-1234.5")));
        assertEquals("1000.00", Importes.aCsv(new BigDecimal("1000")));
    }

    @Test
    void normalizar() {
        assertEquals("verduleria dona", Textos.normalizar("  Verdulería   Doña "));
        assertEquals("", Textos.normalizar(null));
    }
}
```

<!-- 44 s · entrada 2086 · salida 785 · finish_reason stop -->
