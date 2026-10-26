```java
// src/main/java/com/finanzas/model/Tipo.java
package com.finanzas.model;

/** Tipo de movimiento según cómo empieza la descripción. */
public enum Tipo {
    RENDIMIENTOS("Rendimientos"),
    TRANSFERENCIA_RECIBIDA("Transferencia recibida"),
    TRANSFERENCIA_ENVIADA("Transferencia enviada"),
    PAGO_QR("Pago con QR"),
    DEVOLUCION("Devolución de dinero"),
    RETIRO("Retiro de dinero"),
    RESERVA_GUARDADA("Dinero reservado"),
    RESERVA_RETIRADA("Dinero retirado"),
    OTRO("");

    private final String prefijo;

    Tipo(String prefijo) {
        this.prefijo = prefijo;
    }

    public String prefijo() {
        return prefijo;
    }

    /** Las reservas de Mercado Pago se mueven dentro de la misma cuenta: no son gastos ni ingresos. */
    public boolean esReserva() {
        return this == RESERVA_GUARDADA || this == RESERVA_RETIRADA;
    }

    /** El primer tipo (en el orden de arriba, sin OTRO) cuyo prefijo es el comienzo de la descripción; si ninguno, OTRO. */
    public static Tipo de(String descripcion) {
        for (Tipo t : values()) {
            if (t != OTRO && descripcion.startsWith(t.prefijo())) {
                return t;
            }
        }
        return OTRO;
    }
}
```

```java
// src/main/java/com/finanzas/service/Clasificador.java
package com.finanzas.service;

import com.finanzas.model.Clasificado;
import com.finanzas.model.Movimiento;
import com.finanzas.model.Regla;
import com.finanzas.model.Tipo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/** Pone tipo, contraparte y categoría a cada movimiento. */
public final class Clasificador {

    private final List<Regla> reglas;
    private final String titular;

    /** {@code titular}: el nombre del encabezado, para reconocer las transferencias a mi nombre. */
    public Clasificador(List<Regla> reglas, String titular) {
        this.reglas = List.copyOf(reglas);
        this.titular = titular;
    }

    /** La descripción sin el prefijo del tipo, sin espacios a los costados. Para OTRO, la descripción entera (trim). */
    static String contraparte(String descripcion, Tipo tipo) {
        if (tipo == Tipo.OTRO) {
            return descripcion.trim();
        }
        return descripcion.substring(tipo.prefijo().length()).trim();
    }

    /** true si todas las palabras del titular aparecen como palabras de la descripción (normalizadas, en cualquier orden). */
    static boolean esPropia(String descripcion, String titular) {
        Set<String> palabras = new HashSet<>(Arrays.asList(Textos.normalizar(descripcion).split(" ")));
        for (String p : Textos.normalizar(titular).split(" ")) {
            if (!palabras.contains(p)) {
                return false;
            }
        }
        return true;
    }

    /** Vacío para las reservas; si no, el movimiento clasificado. */
    public Optional<Clasificado> clasificar(Movimiento m) {
        // lo completa otra subtarea (T04b): no lo cambies
        throw new UnsupportedOperationException("T04b");
    }

    /** Los clasificados, en el mismo orden, sin las reservas. */
    public List<Clasificado> clasificarTodos(List<Movimiento> movimientos) {
        // lo completa otra subtarea (T04b): no lo cambies
        throw new UnsupportedOperationException("T04b");
    }
}
```

```java
// src/test/java/com/finanzas/service/TipoContraparteTest.java
package com.finanzas.service;

import static org.junit.jupiter.api.Assertions.*;

import com.finanzas.model.Tipo;
import org.junit.jupiter.api.Test;

class TipoContraparteTest {

    @Test
    void tipos() {
        assertEquals(Tipo.RENDIMIENTOS, Tipo.de("Rendimientos"));
        assertEquals(Tipo.RESERVA_RETIRADA, Tipo.de("Dinero retirado HOGAR"));
        assertEquals(Tipo.OTRO, Tipo.de("Compra rara"));
    }

    @Test
    void contraparte() {
        assertEquals("Carrefour", Clasificador.contraparte("Pago con QR Carrefour", Tipo.PAGO_QR));
        assertEquals("", Clasificador.contraparte("Transferencia enviada", Tipo.TRANSFERENCIA_ENVIADA));
    }

    @Test
    void propia() {
        assertTrue(Clasificador.esPropia("Transferencia recibida GOMEZ ANA LAURA", "Ana Laura Gomez"));
        assertFalse(Clasificador.esPropia("Transferencia enviada Ana Laura Perez", "Ana Laura Gomez"));
    }
}
```

<!-- 75 s · entrada 2628 · salida 1223 · finish_reason stop -->
