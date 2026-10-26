<!-- modelo google/gemma-4-e4b-qat, temperatura 0.2, max_tokens 5456, razonamiento no -->

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

## 2. Formato del texto del resumen
El encabezado (primera página):
```
                                                                  RESUMEN DE CUENTA EN PESOS
                                                                              Ana Laura Gomez
                                             CVU: 0000003100000000000000 CUIT/ CUIL: 27000000000
                                                               Periodo: Del 1 al 6 de octubre de 2026

                                       Entradas: $ 166.401,38
Saldo inicial: $ 2.580,25                                                      Saldo final: $ 86.585,45
                                       Salidas: $ -82.396,18
```
- Importes: `$`, espacio opcional, signo `-` opcional, miles con `.`, decimales con `,` y siempre dos decimales.
- Movimientos: cada uno es un **bloque** de líneas separado de los demás por líneas vacías. El bloque tiene exactamente una **fila** (la línea con fecha, ID, valor y saldo, ver `LectorResumen.FILA`). La descripción puede estar en la fila, en la línea de arriba, en la de abajo, o repartida en las tres:
```
01-10-2026    Rendimientos                    1750900000001     $         1,38    $     2.581,63


              Transferencia recibida
01-10-2026                                    180700000002      $   150.000,00    $   152.581,63
              PEREZ JUAN CARLOS


              Transferencia recibida
03-10-2026                                    GOMEZ ANA         181500000006      $     5.000,00    $   134.681,63
              LAURA
```
  Descripciones: `"Rendimientos"`, `"Transferencia recibida PEREZ JUAN CARLOS"`, `"Transferencia recibida GOMEZ ANA LAURA"`: las partes de arriba, de la fila y de abajo, unidas con un espacio y sin espacios repetidos.
- Los bloques sin fila (títulos "ID de la / Fecha Descripción…", número de página `1/10`, pie) se saltean.

---

# T02a — Lector: encabezado

## Objetivo
Completar **solo** `campo` y `leerEncabezado` de `LectorResumen`, y los tests de `LectorEncabezadoTest`. Los otros métodos los completa otra subtarea.

## Código que ya existe
- `Importes.parsear(String)` → `BigDecimal` ("$ -1.234,56" → -1234.56).
- `Muestra.lineas()` (en los tests): las líneas de un resumen inventado. Su titular es `"Ana Laura Gomez"`.

## `campo(lineas, nombre)` (pasos en orden)
1. `Pattern p = Pattern.compile(Pattern.quote(nombre) + ":\\s*\\$\\s*(-?[\\d.]+,\\d{2})");`
2. `for (String linea : lineas) { Matcher m = p.matcher(linea); if (m.find()) return Importes.parsear(m.group(1)); }`
3. `throw new IllegalArgumentException("Falta " + nombre + " en el resumen");`

## `leerEncabezado(lineas)` (pasos en orden)
1. `String titular = "";`
2. `for (int i = 0; i < lineas.size(); i++) { if (lineas.get(i).contains("RESUMEN DE CUENTA")) { for (int j = i + 1; j < lineas.size(); j++) { if (!lineas.get(j).isBlank()) { titular = lineas.get(j).trim(); break; } } break; } }`
3. `String periodo = "";`
4. `for (String linea : lineas) { int k = linea.indexOf("Periodo:"); if (k >= 0) { periodo = linea.substring(k + "Periodo:".length()).trim(); break; } }`
5. `return new Encabezado(titular, periodo, campo(lineas, "Saldo inicial"), campo(lineas, "Saldo final"), campo(lineas, "Entradas"), campo(lineas, "Salidas"));`

## Tests (copiá estas aserciones tal cual)
- `encabezado`: `Encabezado e = LectorResumen.leerEncabezado(Muestra.lineas());` y
  - `assertEquals("Ana Laura Gomez", e.titular());`
  - `assertEquals(new BigDecimal("2580.25"), e.saldoInicial());`
  - `assertEquals(new BigDecimal("-82396.18"), e.salidas());`
- `campoQueFalta`: `assertThrows(IllegalArgumentException.class, () -> LectorResumen.campo(java.util.List.of("hola"), "Saldo final"));`

---

# Archivos actuales

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
            "^\\s*(\\d{2}-\\d{2}-\\d{4})\\s+(.*?)\\s*(\\d{12,13})\\s+\\$\\s*(-?[\\d.]+,\\d{2})\\s+\\$\\s*(-?[\\d.]+,\\d{2})\\s*$");

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
        // TODO T02a
    }

    @Test
    void campoQueFalta() {
        // TODO T02a
    }
}
```

Completá los `// TODO` que pide la tarea T02a. Devolvé estos archivos completos, cada uno en su bloque: src/main/java/com/finanzas/service/LectorResumen.java, src/test/java/com/finanzas/service/LectorEncabezadoTest.java.
