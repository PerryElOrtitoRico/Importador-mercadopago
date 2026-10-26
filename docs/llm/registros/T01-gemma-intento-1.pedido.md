<!-- modelo google/gemma-4-e4b-qat, temperatura 0.2, max_tokens 6133, razonamiento no -->

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

# T01 — Importes y textos

## Objetivo
Completar `Importes.parsear`, `Importes.aCsv`, `Textos.normalizar` y los tres tests de `ImportesTest`.

## `Importes.parsear(texto)` (pasos en orden)
1. `if (texto == null || texto.isBlank()) throw new IllegalArgumentException("Importe vacío");`
2. `String s = texto.replace("$", "").replace(" ", "").replace(".", "").replace(",", ".");`
3. `return new BigDecimal(s).setScale(2, RoundingMode.HALF_UP);`

## `Importes.aCsv(valor)`
1. `return valor.setScale(2, RoundingMode.HALF_UP).toPlainString();`

## `Textos.normalizar(texto)` (pasos en orden)
1. `if (texto == null) return "";`
2. `String sinTildes = Normalizer.normalize(texto, Normalizer.Form.NFD).replaceAll("\\p{M}", "");`
3. `return sinTildes.toLowerCase().replaceAll("\\s+", " ").trim();`

## Tests (copiá estas aserciones tal cual)
- `parsear`:
  - `assertEquals(new BigDecimal("-1234.56"), Importes.parsear("$ -1.234,56"));`
  - `assertEquals(new BigDecimal("500000.00"), Importes.parsear("500.000,00"));`
  - `assertThrows(IllegalArgumentException.class, () -> Importes.parsear(" "));`
- `aCsv`:
  - `assertEquals("-1234.50", Importes.aCsv(new BigDecimal("-1234.5")));`
  - `assertEquals("1000.00", Importes.aCsv(new BigDecimal("1000")));`
- `normalizar`:
  - `assertEquals("verduleria dona", Textos.normalizar("  Verdulería   Doña "));`
  - `assertEquals("", Textos.normalizar(null));`

---

# Archivos actuales

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
        // TODO T01
        throw new UnsupportedOperationException("T01");
    }

    /** BigDecimal → texto con punto decimal y dos decimales, sin separador de miles: -1234.56, 0.50. */
    public static String aCsv(BigDecimal valor) {
        // TODO T01
        throw new UnsupportedOperationException("T01");
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
        // TODO T01
        throw new UnsupportedOperationException("T01");
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
        // TODO T01
    }

    @Test
    void aCsv() {
        // TODO T01
    }

    @Test
    void normalizar() {
        // TODO T01
    }
}
```

Completá los `// TODO` que pide la tarea T01. Devolvé estos archivos completos, cada uno en su bloque: src/main/java/com/finanzas/service/Importes.java, src/main/java/com/finanzas/service/Textos.java, src/test/java/com/finanzas/service/ImportesTest.java.
