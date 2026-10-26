<!-- modelo google/gemma-4-e4b-qat, temperatura 0.2, max_tokens 6129, razonamiento no -->

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

## 6. CSV para Actual Budget
- Cabecera `fecha,beneficiario,categoria,importe,notas`; líneas separadas por `\n`, la última también termina en `\n`.
- `fecha`: ISO (`2026-10-02`). `beneficiario`: la contraparte; si está vacía, el prefijo del tipo; si también está vacío (OTRO), la descripción. `categoria`: la del clasificado (puede ser vacía). `importe`: `Importes.aCsv(valor)` (`-1400.00`). `notas`: `"MP " + idOperacion`, y si es para revisar, además `" - REVISAR: " + motivo`.
- Un campo con coma, comillas o salto de línea va entre comillas dobles, y las comillas de adentro se duplican.

---

# T06 — CSV para Actual Budget

## Objetivo
Completar `csv`, `linea` y `campo` de `ExportadorCsv`, y los tests de `ExportadorCsvTest`.

## Código que ya existe
- `CABECERA` = `"fecha,beneficiario,categoria,importe,notas"`.
- `Clasificado(movimiento, tipo, contraparte, categoria, paraRevisar, motivo)`; `Movimiento(fecha, descripcion, idOperacion, valor, saldo)`; `tipo.prefijo()`.
- `Importes.aCsv(BigDecimal)` → `"-1400.00"`.
- En los tests: `Esperado.m(año, mes, día, descripción, id, valor, saldo)` arma un `Movimiento`.

## `campo(texto)` (pasos en orden)
1. `if (texto.contains(",") || texto.contains("\"") || texto.contains("\n")) return "\"" + texto.replace("\"", "\"\"") + "\"";`
2. `return texto;`

## `linea(c)` (pasos en orden)
1. `Movimiento m = c.movimiento();`
2. `String beneficiario = c.contraparte();`
3. `if (beneficiario.isEmpty()) beneficiario = c.tipo().prefijo();`
4. `if (beneficiario.isEmpty()) beneficiario = m.descripcion();`
5. `String notas = "MP " + m.idOperacion();`
6. `if (c.paraRevisar()) notas = notas + " - REVISAR: " + c.motivo();`
7. `return String.join(",", m.fecha().toString(), campo(beneficiario), campo(c.categoria()), Importes.aCsv(m.valor()), campo(notas));`

## `csv(clasificados)` (pasos en orden)
1. `StringBuilder sb = new StringBuilder(CABECERA).append('\n');`
2. `for (Clasificado c : clasificados) sb.append(linea(c)).append('\n');`
3. `return sb.toString();`

## Tests (copiá estas aserciones tal cual)
- `campo`:
  - `assertEquals("hola", ExportadorCsv.campo("hola"));`
  - `assertEquals("\"a,b\"", ExportadorCsv.campo("a,b"));`
- `linea`:
  - `Clasificado c = new Clasificado(Esperado.m(2026, 10, 2, "Pago con QR Carrefour", "182000000005", "-1400.00", "128281.63"), Tipo.PAGO_QR, "Carrefour", "Supermercado", false, "");`
  - `assertEquals("2026-10-02,Carrefour,Supermercado,-1400.00,MP 182000000005", ExportadorCsv.linea(c));`
  - `assertEquals(ExportadorCsv.CABECERA + "\n" + ExportadorCsv.linea(c) + "\n", ExportadorCsv.csv(List.of(c)));`

---

# Archivos actuales

```java
// src/main/java/com/finanzas/service/ExportadorCsv.java
package com.finanzas.service;

import com.finanzas.model.Clasificado;
import com.finanzas.model.Movimiento;

import java.util.List;

/** CSV para importar en Actual Budget: fecha,beneficiario,categoria,importe,notas. */
public final class ExportadorCsv {

    public static final String CABECERA = "fecha,beneficiario,categoria,importe,notas";

    private ExportadorCsv() {
    }

    /** Cabecera y una línea por movimiento, separadas por "\n" y terminadas en "\n". */
    public static String csv(List<Clasificado> clasificados) {
        // TODO T06
        throw new UnsupportedOperationException("T06");
    }

    /** Una línea sin el "\n" final. */
    static String linea(Clasificado c) {
        // TODO T06
        throw new UnsupportedOperationException("T06");
    }

    /** Entre comillas (y las comillas internas dobladas) si tiene coma, comillas o salto de línea; si no, tal cual. */
    static String campo(String texto) {
        // TODO T06
        throw new UnsupportedOperationException("T06");
    }
}
```

```java
// src/test/java/com/finanzas/service/ExportadorCsvTest.java
package com.finanzas.service;

import static org.junit.jupiter.api.Assertions.*;

import com.finanzas.model.Clasificado;
import com.finanzas.model.Tipo;
import java.util.List;
import org.junit.jupiter.api.Test;

class ExportadorCsvTest {

    @Test
    void campo() {
        // TODO T06
    }

    @Test
    void linea() {
        // TODO T06
    }
}
```

Completá los `// TODO` que pide la tarea T06. Devolvé estos archivos completos, cada uno en su bloque: src/main/java/com/finanzas/service/ExportadorCsv.java, src/test/java/com/finanzas/service/ExportadorCsvTest.java.
