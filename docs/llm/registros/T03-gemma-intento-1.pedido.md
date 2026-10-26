<!-- modelo google/gemma-4-e4b-qat, temperatura 0.2, max_tokens 6096, razonamiento no -->

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

## 3. Verificación
Si algo no cuadra, el texto se leyó mal: no se importa nada. Los mensajes, exactamente así (importes con `toPlainString()`):
- `El resumen no tiene movimientos` (si no hay movimientos, es el único error).
- `Saldo distinto en <idOperacion>: calculado <x>, resumen <y>`: el saldo calculado (saldo inicial + valores hasta ese movimiento incluido) no es igual al saldo de la fila. El cálculo sigue con el saldo calculado, no con el de la fila.
- `Saldo final distinto: calculado <x>, resumen <y>`
- `Entradas distintas: suma <x>, resumen <y>`: suma de los valores positivos.
- `Salidas distintas: suma <x>, resumen <y>`: suma de los valores negativos (da negativa).

---

# T03 — Verificador

## Objetivo
Completar `Verificador.verificar` y los tests de `VerificadorTest`.

## Código que ya existe
- Records `Resumen(encabezado, movimientos)`, `Encabezado(titular, periodo, saldoInicial, saldoFinal, entradas, salidas)`, `Movimiento(fecha, descripcion, idOperacion, valor, saldo)`.
- En los tests: `Muestra.resumen()` (un resumen que cuadra; el movimiento 3 tiene ID `"181900000004"` y saldo 129681.63) y `Esperado.m(año, mes, día, descripción, id, valor, saldo)` que arma un `Movimiento`.

## `verificar(resumen)` (pasos en orden)
1. `List<String> errores = new ArrayList<>();`
2. `if (resumen.movimientos().isEmpty()) { errores.add("El resumen no tiene movimientos"); return errores; }`
3. `BigDecimal saldo = resumen.encabezado().saldoInicial(); BigDecimal entradas = BigDecimal.ZERO; BigDecimal salidas = BigDecimal.ZERO;`
4. `for (Movimiento m : resumen.movimientos()) {`
5. `    saldo = saldo.add(m.valor());`
6. `    if (saldo.compareTo(m.saldo()) != 0) errores.add("Saldo distinto en " + m.idOperacion() + ": calculado " + saldo.toPlainString() + ", resumen " + m.saldo().toPlainString());`
7. `    if (m.valor().signum() > 0) entradas = entradas.add(m.valor()); else salidas = salidas.add(m.valor());`
8. `}`
9. `if (saldo.compareTo(resumen.encabezado().saldoFinal()) != 0) errores.add("Saldo final distinto: calculado " + saldo.toPlainString() + ", resumen " + resumen.encabezado().saldoFinal().toPlainString());`
10. `if (entradas.compareTo(resumen.encabezado().entradas()) != 0) errores.add("Entradas distintas: suma " + entradas.toPlainString() + ", resumen " + resumen.encabezado().entradas().toPlainString());`
11. `if (salidas.compareTo(resumen.encabezado().salidas()) != 0) errores.add("Salidas distintas: suma " + salidas.toPlainString() + ", resumen " + resumen.encabezado().salidas().toPlainString());`
12. `return errores;`

## Tests (copiá estas aserciones tal cual)
- `cuadra`: `assertEquals(List.of(), Verificador.verificar(Muestra.resumen()));`
- `saldoDistinto`:
  - `List<Movimiento> ms = new ArrayList<>(Muestra.resumen().movimientos());`
  - `ms.set(3, Esperado.m(2026, 10, 2, "Pago con QR Autoservicio7030", "181900000004", "-2900.00", "129681.00"));`
  - `List<String> errores = Verificador.verificar(new Resumen(Muestra.resumen().encabezado(), ms));`
  - `assertEquals(List.of("Saldo distinto en 181900000004: calculado 129681.63, resumen 129681.00"), errores);`

---

# Archivos actuales

```java
// src/main/java/com/finanzas/service/Verificador.java
package com.finanzas.service;

import com.finanzas.model.Movimiento;
import com.finanzas.model.Resumen;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/** Comprueba que el resumen leído cuadre: si algo no cuadra, se leyó mal y no se importa nada. */
public final class Verificador {

    private Verificador() {
    }

    /** Lista de errores en castellano; vacía si todo cuadra. */
    public static List<String> verificar(Resumen resumen) {
        // TODO T03
        throw new UnsupportedOperationException("T03");
    }
}
```

```java
// src/test/java/com/finanzas/service/VerificadorTest.java
package com.finanzas.service;

import static org.junit.jupiter.api.Assertions.*;

import com.finanzas.model.Movimiento;
import com.finanzas.model.Resumen;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class VerificadorTest {

    @Test
    void cuadra() {
        // TODO T03
    }

    @Test
    void saldoDistinto() {
        // TODO T03
    }
}
```

Completá los `// TODO` que pide la tarea T03. Devolvé estos archivos completos, cada uno en su bloque: src/main/java/com/finanzas/service/Verificador.java, src/test/java/com/finanzas/service/VerificadorTest.java.
