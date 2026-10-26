<!-- modelo qwen/qwen3.5-9b, temperatura 0.2, max_tokens 5876, razonamiento no -->

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

## 5. Registro de importados
- Archivo de texto, una clave por línea: `idOperacion + "|" + valor.toPlainString()` (`"181588521871|17831.75"`). El ID solo no alcanza: una devolución usa el mismo ID que el pago.
- Se escribe de forma atómica: todo el contenido a `importados.txt.tmp` y después `Files.move(tmp, archivo, REPLACE_EXISTING, ATOMIC_MOVE)`.

---

# T05 — Registro de importados

## Objetivo
Completar `clave`, `leer`, `agregar` y `nuevos` de `Registro`, y los tests de `RegistroTest`.

## Código que ya existe
- Campo `archivo` (Path) del registro.
- `Clasificado` se lee con `c.movimiento()`; `Movimiento` con `m.idOperacion()` y `m.valor()`.
- En los tests: `Muestra.resumen().movimientos()`; `@TempDir Path dir` de JUnit (agregalo como campo del test).

## `clave(m)`
1. `return m.idOperacion() + "|" + m.valor().toPlainString();`

## `leer()` (pasos en orden)
1. `Set<String> out = new LinkedHashSet<>();`
2. `if (!Files.exists(archivo)) return out;`
3. `for (String linea : Files.readAllLines(archivo, StandardCharsets.UTF_8)) { if (!linea.isBlank()) out.add(linea.trim()); }`
4. `return out;`

## `agregar(claves)` (pasos en orden)
1. `Set<String> todas = leer();`
2. `todas.addAll(claves);`
3. `if (archivo.getParent() != null) Files.createDirectories(archivo.getParent());`
4. `Path tmp = archivo.resolveSibling(archivo.getFileName() + ".tmp");`
5. `StringBuilder sb = new StringBuilder(); for (String c : todas) sb.append(c).append('\n');`
6. `Files.writeString(tmp, sb.toString(), StandardCharsets.UTF_8);`
7. `Files.move(tmp, archivo, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);`

## `nuevos(clasificados)` (pasos en orden)
1. `Set<String> ya = leer();`
2. `List<Clasificado> out = new ArrayList<>();`
3. `for (Clasificado c : clasificados) { if (!ya.contains(clave(c.movimiento()))) out.add(c); }`
4. `return out;`

## Tests (copiá estas aserciones tal cual)
- `claveYAgregar`:
  - `assertEquals("180700000002|150000.00", Registro.clave(Muestra.resumen().movimientos().get(1)));`
  - `Registro r = new Registro(dir.resolve("sub/importados.txt"));`
  - `r.agregar(List.of("a|1.00", "b|2.00"));`
  - `r.agregar(List.of("b|2.00", "c|3.00"));`
  - `assertEquals("a|1.00\nb|2.00\nc|3.00\n", Files.readString(dir.resolve("sub/importados.txt")));`
- `nuevos`:
  - `Registro r = new Registro(dir.resolve("importados.txt"));`
  - `List<Clasificado> cs = new Clasificador(ReglasRepo.porDefecto(), Muestra.TITULAR).clasificarTodos(Muestra.resumen().movimientos());`
  - `r.agregar(List.of(Registro.clave(cs.get(0).movimiento())));`
  - `assertEquals(cs.size() - 1, r.nuevos(cs).size());`
- Los dos tests declaran `throws IOException`.

---

# Archivos actuales

```java
// src/main/java/com/finanzas/service/Registro.java
package com.finanzas.service;

import com.finanzas.model.Clasificado;
import com.finanzas.model.Movimiento;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/** Archivo de texto con una clave por línea de los movimientos ya importados, para no duplicarlos. */
public final class Registro {

    private final Path archivo;

    public Registro(Path archivo) {
        this.archivo = archivo;
    }

    /**
     * ID de operación + "|" + valor ("181588521871|17831.75").
     * El ID solo no alcanza: una devolución usa el mismo ID que el pago que devuelve.
     */
    public static String clave(Movimiento m) {
        // TODO T05
        throw new UnsupportedOperationException("T05");
    }

    /** Las claves guardadas, en orden; vacío si el archivo no existe. Las líneas en blanco se ignoran. */
    public Set<String> leer() throws IOException {
        // TODO T05
        throw new UnsupportedOperationException("T05");
    }

    /** Agrega las claves que falten y guarda de forma atómica (temporal + reemplazo). Crea las carpetas si no existen. */
    public void agregar(Collection<String> claves) throws IOException {
        // TODO T05
        throw new UnsupportedOperationException("T05");
    }

    /** Los clasificados cuya clave todavía no está en el registro, en el mismo orden. */
    public List<Clasificado> nuevos(List<Clasificado> clasificados) throws IOException {
        // TODO T05
        throw new UnsupportedOperationException("T05");
    }
}
```

```java
// src/test/java/com/finanzas/service/RegistroTest.java
package com.finanzas.service;

import static org.junit.jupiter.api.Assertions.*;

import com.finanzas.model.Clasificado;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.Test;

class RegistroTest {

    @Test
    void claveYAgregar() {
        // TODO T05
    }

    @Test
    void nuevos() {
        // TODO T05
    }
}
```

Completá los `// TODO` que pide la tarea T05. Devolvé estos archivos completos, cada uno en su bloque: src/main/java/com/finanzas/service/Registro.java, src/test/java/com/finanzas/service/RegistroTest.java.
