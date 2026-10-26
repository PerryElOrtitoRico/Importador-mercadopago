<!-- modelo qwen/qwen3.5-9b, temperatura 0.2, max_tokens 5687, razonamiento no -->

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

## 4. Clasificación
- Tipo: el primero de `Tipo` (en orden, sin `OTRO`) cuyo prefijo es el comienzo de la descripción; si ninguno, `OTRO`.
- Contraparte: la descripción sin el prefijo del tipo, con `trim()`. `"Transferencia enviada"` sola da `""`.
- **Reservas** (`RESERVA_GUARDADA`, `RESERVA_RETIRADA`): plata que se mueve dentro de la misma cuenta. No se exportan.
- **Transferencias a mi nombre** (recibidas o enviadas, todas las palabras del titular están en la descripción, en cualquier orden y sin importar mayúsculas ni tildes): sin categoría, `paraRevisar` true, motivo `"Transferencia a mi nombre"`.
- Si no: la **primera regla** cuya `contiene` (normalizada) está dentro de la descripción (normalizada) da la categoría; `paraRevisar` false, motivo `""`.
- Sin regla: categoría `""`, `paraRevisar` true, motivo `"Sin regla"`.
- Normalizar (`Textos.normalizar`): minúsculas, sin tildes ni diéresis (`ñ` → `n`), espacios repetidos a uno, sin espacios a los costados.

---

# T04b — Clasificar

## Objetivo
Completar **solo** `clasificar` y `clasificarTodos` de `Clasificador`, y el test de `ClasificadorTest`. `contraparte` y `esPropia` ya están hechos: usalos.

## Código que ya existe
- Campos `reglas` (List<Regla>) y `titular` (String) del clasificador.
- `Tipo.de(descripcion)`, `tipo.esReserva()`. `Textos.normalizar(String)`.
- `Clasificado(movimiento, tipo, contraparte, categoria, paraRevisar, motivo)`; `Regla(contiene, categoria)`.
- En los tests: `ReglasRepo.porDefecto()` (reglas iniciales), `Muestra.TITULAR` (`"Ana Laura Gomez"`), `Muestra.resumen().movimientos()` (14 movimientos, 2 son reservas).

## `clasificar(m)` (pasos en orden)
1. `Tipo tipo = Tipo.de(m.descripcion());`
2. `if (tipo.esReserva()) return Optional.empty();`
3. `String contra = contraparte(m.descripcion(), tipo);`
4. `boolean transferencia = tipo == Tipo.TRANSFERENCIA_RECIBIDA || tipo == Tipo.TRANSFERENCIA_ENVIADA;`
5. `if (transferencia && esPropia(m.descripcion(), titular)) return Optional.of(new Clasificado(m, tipo, contra, "", true, "Transferencia a mi nombre"));`
6. `String desc = Textos.normalizar(m.descripcion());`
7. `for (Regla r : reglas) { if (desc.contains(Textos.normalizar(r.contiene()))) return Optional.of(new Clasificado(m, tipo, contra, r.categoria(), false, "")); }`
8. `return Optional.of(new Clasificado(m, tipo, contra, "", true, "Sin regla"));`

## `clasificarTodos(movimientos)` (pasos en orden)
1. `List<Clasificado> out = new ArrayList<>();`
2. `for (Movimiento m : movimientos) { clasificar(m).ifPresent(out::add); }`
3. `return out;`

## Test (copiá estas aserciones tal cual)
- `clasificarMuestra`:
  - `Clasificador c = new Clasificador(ReglasRepo.porDefecto(), Muestra.TITULAR);`
  - `List<Clasificado> cs = c.clasificarTodos(Muestra.resumen().movimientos());`
  - `assertEquals(12, cs.size());`
  - `assertEquals("Rendimientos", cs.get(0).categoria());`
  - `assertEquals("Transferencia a mi nombre", cs.get(2).motivo());`
  - `assertEquals("Supermercado", cs.get(3).categoria());`

---

# Archivos actuales

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
        // TODO T04b
        throw new UnsupportedOperationException("T04b");
    }

    /** Los clasificados, en el mismo orden, sin las reservas. */
    public List<Clasificado> clasificarTodos(List<Movimiento> movimientos) {
        // TODO T04b
        throw new UnsupportedOperationException("T04b");
    }
}
```

```java
// src/test/java/com/finanzas/service/ClasificadorTest.java
package com.finanzas.service;

import static org.junit.jupiter.api.Assertions.*;

import com.finanzas.model.Clasificado;
import java.util.List;
import org.junit.jupiter.api.Test;

class ClasificadorTest {

    @Test
    void clasificarMuestra() {
        // TODO T04b
    }
}
```

Completá los `// TODO` que pide la tarea T04b. Devolvé **solo los métodos que completaste**, cada uno entero (firma y cuerpo), sin el resto de la clase: un bloque por archivo, con la ruta en la primera línea y después los métodos (y los imports nuevos, si agregaste). Archivos: src/main/java/com/finanzas/service/Clasificador.java, src/test/java/com/finanzas/service/ClasificadorTest.java.
