<!-- modelo qwen/qwen3.5-9b, temperatura 0.2, max_tokens 4809, razonamiento no -->

## system

Sos un programador Java 21 cuidadoso. Te paso archivos con métodos marcados `// TODO`: completá solo los cuerpos que nombra la tarea; los métodos marcados "lo completa otra subtarea" quedan exactamente como están.
Reglas:
- No cambies package, imports que ya están (aunque todavía no se usen), nombres de clases, firmas ni métodos que ya están hechos. Podés agregar imports y métodos private.
- Todas las clases de model son records y se leen con x(): m.valor(), m.descripcion(), c.movimiento().fecha(). No existen getValor() ni getDescripcion().
- Los importes son BigDecimal: se comparan con compareTo, nunca con equals ni con ==.
- En los tests, copiá las aserciones de la tarea tal cual (mismos valores y fechas) y no escribas comentarios.
- No crees ni modifiques otros archivos.
- Devolvé solo los métodos que completaste, cada uno entero (firma y cuerpo), sin el resto de la clase: un bloque por archivo, con la ruta en la primera línea. Ejemplo:
```java
// src/main/java/com/finanzas/service/Ejemplo.java
    public static int doble(int x) {
        return 2 * x;
    }
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

# T02b — Lector: movimientos

## Objetivo
Completar **solo** `bloques`, `leerMovimientos` y `leer` de `LectorResumen`, y los tests de `LectorMovimientosTest`. `campo` y `leerEncabezado` ya están hechos: no los cambies.

## Código que ya existe
- `FILA` (Pattern): grupos 1 fecha `dd-MM-yyyy`, 2 descripción de la fila (puede ser ""), 3 ID, 4 valor, 5 saldo. `FECHA` es el `DateTimeFormatter` de `dd-MM-yyyy`.
- `Importes.parsear(String)` → `BigDecimal`.
- `Muestra.lineas()` (en los tests): un resumen inventado con 14 movimientos.

## `bloques(lineas)` (pasos en orden)
1. `List<List<String>> out = new ArrayList<>(); List<String> actual = new ArrayList<>();`
2. `for (String linea : lineas) { if (linea.isBlank()) { if (!actual.isEmpty()) { out.add(actual); actual = new ArrayList<>(); } } else { actual.add(linea); } }`
3. `if (!actual.isEmpty()) out.add(actual);`
4. `return out;`

## `leerMovimientos(lineas)` (pasos en orden)
1. `List<Movimiento> out = new ArrayList<>();`
2. `for (List<String> bloque : bloques(lineas)) {`
3. `    int fila = -1;`
4. `    for (int i = 0; i < bloque.size(); i++) { if (FILA.matcher(bloque.get(i)).matches()) { if (fila >= 0) throw new IllegalStateException("Bloque con dos movimientos: " + bloque); fila = i; } }`
5. `    if (fila < 0) continue;`
6. `    Matcher m = FILA.matcher(bloque.get(fila)); m.matches();`
7. `    StringBuilder sb = new StringBuilder();`
8. `    for (int i = 0; i < fila; i++) sb.append(bloque.get(i).trim()).append(' ');`
9. `    sb.append(m.group(2).trim()).append(' ');`
10. `    for (int i = fila + 1; i < bloque.size(); i++) sb.append(bloque.get(i).trim()).append(' ');`
11. `    String descripcion = sb.toString().replaceAll("\\s+", " ").trim();`
12. `    out.add(new Movimiento(LocalDate.parse(m.group(1), FECHA), descripcion, m.group(3), Importes.parsear(m.group(4)), Importes.parsear(m.group(5))));`
13. `}`
14. `return out;`

## `leer(lineas)`
1. `return new Resumen(leerEncabezado(lineas), leerMovimientos(lineas));`

## Tests (copiá estas aserciones tal cual)
- `bloques`: `List<List<String>> b = LectorResumen.bloques(List.of("a", "", "  ", "b", "c", ""));` y `assertEquals(List.of(List.of("a"), List.of("b", "c")), b);`
- `movimientos`: `List<Movimiento> ms = LectorResumen.leerMovimientos(Muestra.lineas());` y
  - `assertEquals(14, ms.size());`
  - `assertEquals(LocalDate.of(2026, 10, 1), ms.get(0).fecha());`
  - `assertEquals("Transferencia recibida PEREZ JUAN CARLOS", ms.get(1).descripcion());`
  - `assertEquals("180700000002", ms.get(1).idOperacion());`
  - `assertEquals(new BigDecimal("-0.01"), ms.get(13).valor());`

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
        // TODO T02b
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
        // TODO T02b
        throw new UnsupportedOperationException("T02b");
    }

    /** Un movimiento por cada bloque que tiene una fila; los bloques sin fila (títulos, páginas) se saltean. */
    public static List<Movimiento> leerMovimientos(List<String> lineas) {
        // TODO T02b
        throw new UnsupportedOperationException("T02b");
    }
}
```

```java
// src/test/java/com/finanzas/service/LectorMovimientosTest.java
package com.finanzas.service;

import static org.junit.jupiter.api.Assertions.*;

import com.finanzas.model.Movimiento;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

class LectorMovimientosTest {

    @Test
    void bloques() {
        // TODO T02b
    }

    @Test
    void movimientos() {
        // TODO T02b
    }
}
```

Completá los `// TODO` que pide la tarea T02b. Devolvé **solo los métodos que completaste**, cada uno entero (firma y cuerpo), sin el resto de la clase: un bloque por archivo, con la ruta en la primera línea y después los métodos (y los imports nuevos, si agregaste). Archivos: src/main/java/com/finanzas/service/LectorResumen.java, src/test/java/com/finanzas/service/LectorMovimientosTest.java.
