<!-- modelo google/gemma-4-e4b-qat, temperatura 0.2, max_tokens 5504, razonamiento no -->

## system

Sos un programador Java 21 cuidadoso. Te paso archivos con métodos marcados `// TODO`: completá solo los cuerpos que nombra la tarea; los métodos marcados "lo completa otra subtarea" quedan exactamente como están.
Reglas:
- No cambies package, imports que ya están (aunque todavía no se usen), nombres de clases, firmas ni métodos que ya están hechos. Podés agregar imports y métodos private.
- Todas las clases de model son records y se leen con x(): m.valor(), m.descripcion(), c.movimiento().fecha(). No existen getValor() ni getDescripcion().
- Los importes son BigDecimal: se comparan con compareTo, nunca con equals ni con ==.
- En los tests, copiá las aserciones de la tarea tal cual (mismos valores y fechas) y no escribas comentarios.
- No crees ni modifiques otros archivos.
- Devolvé cada archivo completo (nada de '...') en su propio bloque de código, con la ruta en la primera línea: un bloque por archivo, nunca dos archivos en el mismo bloque. Ejemplo:
```java
// src/main/java/com/finanzas/service/Ejemplo.java
package com.finanzas.service;

public final class Ejemplo { }
```
- Sin explicaciones fuera de los bloques.

## user

# Contrato del proyecto (extracto)

# FINANZAS — importador de resúmenes de Mercado Pago (contrato)

## 1. Reglas generales
- Paquetes: `com.finanzas.model` (datos), `com.finanzas.service` (lógica), `com.finanzas.cli` (línea de comandos).
- **Todo lo de `model` son records** y se leen con `x()`: `m.valor()`, `m.descripcion()`, `c.movimiento().fecha()`, `e.saldoInicial()`. No existen `getValor()` ni `getDescripcion()`.
- `Tipo` es un enum: `Tipo.PAGO_QR.prefijo()` es `"Pago con QR"`, `tipo.esReserva()`.
- Los importes son `BigDecimal` con escala 2. Para comparar importes se usa `compareTo(...) != 0`, nunca `equals`. Para mostrarlos, `toPlainString()`.
- Las salidas son negativas (como en el PDF): un pago de $ 2.900 tiene `valor` = -2900.00. `Encabezado.salidas()` también es negativo.
- Records del modelo:
  - `Movimiento(LocalDate fecha, String descripcion, String idOperacion, BigDecimal valor, BigDecimal saldo)`
  - `Encabezado(String titular, String periodo, BigDecimal saldoInicial, BigDecimal saldoFinal, BigDecimal entradas, BigDecimal salidas)`
  - `Resumen(Encabezado encabezado, List<Movimiento> movimientos)`
  - `Regla(String contiene, String categoria)`
  - `Clasificado(Movimiento movimiento, Tipo tipo, String contraparte, String categoria, boolean paraRevisar, String motivo)`

## 4. Clasificación
- Tipo: el primero de `Tipo` (en orden, sin `OTRO`) cuyo prefijo es el comienzo de la descripción; si ninguno, `OTRO`.
- Contraparte: la descripción sin el prefijo del tipo, con `trim()`. `"Transferencia enviada"` sola da `""`.
- **Reservas** (`RESERVA_GUARDADA`, `RESERVA_RETIRADA`): plata que se mueve dentro de la misma cuenta. No se exportan.
- **Transferencias a mi nombre** (recibidas o enviadas, todas las palabras del titular están en la descripción, en cualquier orden y sin importar mayúsculas ni tildes): sin categoría, `paraRevisar` true, motivo `"Transferencia a mi nombre"`.
- Si no: la **primera regla** cuya `contiene` (normalizada) está dentro de la descripción (normalizada) da la categoría; `paraRevisar` false, motivo `""`.
- Sin regla: categoría `""`, `paraRevisar` true, motivo `"Sin regla"`.
- Normalizar (`Textos.normalizar`): minúsculas, sin tildes ni diéresis (`ñ` → `n`), espacios repetidos a uno, sin espacios a los costados.

---

# T04a — Tipo, contraparte y transferencias propias

## Objetivo
Completar `Tipo.de`, y **solo** `contraparte` y `esPropia` de `Clasificador`, y los tests de `TipoContraparteTest`. `clasificar` y `clasificarTodos` los completa otra subtarea.

## Código que ya existe
- `Tipo.values()` en orden; `t.prefijo()`; `OTRO` tiene prefijo `""`.
- `Textos.normalizar(String)`: minúsculas, sin tildes, espacios simples ("  GOMEZ  Aná " → "gomez ana").

## `Tipo.de(descripcion)` (pasos en orden)
1. `for (Tipo t : values()) { if (t != OTRO && descripcion.startsWith(t.prefijo())) return t; }`
2. `return OTRO;`

## `Clasificador.contraparte(descripcion, tipo)` (pasos en orden)
1. `if (tipo == Tipo.OTRO) return descripcion.trim();`
2. `return descripcion.substring(tipo.prefijo().length()).trim();`

## `Clasificador.esPropia(descripcion, titular)` (pasos en orden)
1. `Set<String> palabras = new HashSet<>(Arrays.asList(Textos.normalizar(descripcion).split(" ")));`
2. `for (String p : Textos.normalizar(titular).split(" ")) { if (!palabras.contains(p)) return false; }`
3. `return true;`

## Tests (copiá estas aserciones tal cual)
- `tipos`:
  - `assertEquals(Tipo.RENDIMIENTOS, Tipo.de("Rendimientos"));`
  - `assertEquals(Tipo.RESERVA_RETIRADA, Tipo.de("Dinero retirado HOGAR"));`
  - `assertEquals(Tipo.OTRO, Tipo.de("Compra rara"));`
- `contraparte`:
  - `assertEquals("Carrefour", Clasificador.contraparte("Pago con QR Carrefour", Tipo.PAGO_QR));`
  - `assertEquals("", Clasificador.contraparte("Transferencia enviada", Tipo.TRANSFERENCIA_ENVIADA));`
- `propia`:
  - `assertTrue(Clasificador.esPropia("Transferencia recibida GOMEZ ANA LAURA", "Ana Laura Gomez"));`
  - `assertFalse(Clasificador.esPropia("Transferencia enviada Ana Laura Perez", "Ana Laura Gomez"));`

---

# Archivos actuales

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
        // TODO T04a
        throw new UnsupportedOperationException("T04a");
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
        // TODO T04a
        throw new UnsupportedOperationException("T04a");
    }

    /** true si todas las palabras del titular aparecen como palabras de la descripción (normalizadas, en cualquier orden). */
    static boolean esPropia(String descripcion, String titular) {
        // TODO T04a
        throw new UnsupportedOperationException("T04a");
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
        // TODO T04a
    }

    @Test
    void contraparte() {
        // TODO T04a
    }

    @Test
    void propia() {
        // TODO T04a
    }
}
```

Completá los `// TODO` que pide la tarea T04a. Devolvé estos archivos completos, cada uno en su bloque: src/main/java/com/finanzas/model/Tipo.java, src/main/java/com/finanzas/service/Clasificador.java, src/test/java/com/finanzas/service/TipoContraparteTest.java.
