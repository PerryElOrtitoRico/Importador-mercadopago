<!-- modelo qwen/qwen3.5-9b, temperatura 0.2, max_tokens 5153, razonamiento no -->

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

## 5. Registro de importados
- Archivo de texto, una clave por línea: `idOperacion + "|" + valor.toPlainString()` (`"181588521871|17831.75"`). El ID solo no alcanza: una devolución usa el mismo ID que el pago.
- Se escribe de forma atómica: todo el contenido a `importados.txt.tmp` y después `Files.move(tmp, archivo, REPLACE_EXISTING, ATOMIC_MOVE)`.

## 6. CSV para Actual Budget
- Cabecera `fecha,beneficiario,categoria,importe,notas`; líneas separadas por `\n`, la última también termina en `\n`.
- `fecha`: ISO (`2026-10-02`). `beneficiario`: la contraparte; si está vacía, el prefijo del tipo; si también está vacío (OTRO), la descripción. `categoria`: la del clasificado (puede ser vacía). `importe`: `Importes.aCsv(valor)` (`-1400.00`). `notas`: `"MP " + idOperacion`, y si es para revisar, además `" - REVISAR: " + motivo`.
- Un campo con coma, comillas o salto de línea va entre comillas dobles, y las comillas de adentro se duplican.

## 8. Importación y línea de comandos
- Uso: `finanzas importar <resumen.pdf|resumen.txt> [--salida <carpeta>] [--simular]`. Un `.txt` se lee directo (ya es la salida de pdftotext). Sin `--salida`, el CSV va a la carpeta del resumen.
- Nombre del CSV: `mercadopago_<fecha del primer movimiento>_<fecha del último>.csv` (`mercadopago_2026-10-01_2026-10-06.csv`). Si ya existe: `_2`, `_3`… antes de `.csv`.
- Se exportan solo los movimientos nuevos (no reservas, no ya importados). Con `--simular` no se escribe ni el CSV ni el registro.
- Códigos de salida: 0 bien, 1 el resumen no cuadra o falló algo, 2 argumentos mal.

---

# T08a — Importar

## Objetivo
Completar **solo** `nombreCsv` e `importar` de `Importador`, y el test de `ImportadorTest`. `textoDePdf` ya está hecho.

## Código que ya existe
- `Importador.Resultado(int leidos, int reservas, int repetidos, List<Clasificado> exportados, List<String> errores, Path csv)`.
- `LectorResumen.leer(List<String>)` → `Resumen`; `Verificador.verificar(Resumen)` → `List<String>` errores.
- `new Clasificador(reglas, titular).clasificarTodos(movimientos)` → `List<Clasificado>` sin reservas.
- `registro.nuevos(List<Clasificado>)`, `registro.agregar(Collection<String>)`, `Registro.clave(Movimiento)`.
- `ExportadorCsv.csv(List<Clasificado>)` → texto del CSV.
- En los tests: `Muestra.lineas()` (14 movimientos, 2 reservas, del 2026-10-01 al 2026-10-06), `ReglasRepo.porDefecto()`.

## `nombreCsv(resumen)` (pasos en orden)
1. `List<Movimiento> ms = resumen.movimientos();`
2. `return "mercadopago_" + ms.get(0).fecha() + "_" + ms.get(ms.size() - 1).fecha() + ".csv";`
(agregá `import com.finanzas.model.Movimiento;`)

## `importar(lineas, reglas, registro, carpetaSalida, simular)` (pasos en orden)
1. `Resumen r = LectorResumen.leer(lineas);`
2. `int leidos = r.movimientos().size();`
3. `List<String> errores = Verificador.verificar(r);`
4. `if (!errores.isEmpty()) return new Resultado(leidos, 0, 0, List.of(), errores, null);`
5. `List<Clasificado> clasificados = new Clasificador(reglas, r.encabezado().titular()).clasificarTodos(r.movimientos());`
6. `int reservas = leidos - clasificados.size();`
7. `List<Clasificado> nuevos = registro.nuevos(clasificados);`
8. `int repetidos = clasificados.size() - nuevos.size();`
9. `if (nuevos.isEmpty() || simular) return new Resultado(leidos, reservas, repetidos, nuevos, List.of(), null);`
10. `Files.createDirectories(carpetaSalida);`
11. `String nombre = nombreCsv(r); Path csv = carpetaSalida.resolve(nombre); int n = 2;`
12. `while (Files.exists(csv)) { csv = carpetaSalida.resolve(nombre.replace(".csv", "_" + n + ".csv")); n++; }`
13. `Files.writeString(csv, ExportadorCsv.csv(nuevos), StandardCharsets.UTF_8);`
14. `registro.agregar(nuevos.stream().map(c -> Registro.clave(c.movimiento())).toList());`
15. `return new Resultado(leidos, reservas, repetidos, nuevos, List.of(), csv);`

## Test (copiá estas aserciones tal cual; `@TempDir Path dir` como campo; declara `throws IOException`)
- `importaYNoRepite`:
  - `Registro registro = new Registro(dir.resolve("importados.txt"));`
  - `Importador.Resultado r1 = Importador.importar(Muestra.lineas(), ReglasRepo.porDefecto(), registro, dir.resolve("salida"), false);`
  - `assertEquals(14, r1.leidos());`
  - `assertEquals(2, r1.reservas());`
  - `assertEquals(12, r1.exportados().size());`
  - `assertEquals(dir.resolve("salida/mercadopago_2026-10-01_2026-10-06.csv"), r1.csv());`
  - `assertEquals(13, Files.readAllLines(r1.csv()).size());`
  - `Importador.Resultado r2 = Importador.importar(Muestra.lineas(), ReglasRepo.porDefecto(), registro, dir.resolve("salida"), false);`
  - `assertEquals(12, r2.repetidos());`
  - `assertNull(r2.csv());`

---

# Archivos actuales

```java
// src/main/java/com/finanzas/service/Importador.java
package com.finanzas.service;

import com.finanzas.model.Clasificado;
import com.finanzas.model.Regla;
import com.finanzas.model.Resumen;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/** Junta todo: texto → resumen → verificación → clasificación → sin repetidos → CSV y registro. */
public final class Importador {

    /**
     * Qué pasó en una importación. {@code errores} no vacío: el resumen no cuadra y no se escribió nada.
     * {@code csv} es null si no se escribió archivo (errores, nada nuevo o simulación).
     */
    public record Resultado(int leidos, int reservas, int repetidos, List<Clasificado> exportados,
                            List<String> errores, Path csv) {
    }

    private Importador() {
    }

    /** Corre {@code pdftotext -layout <pdf> -} y devuelve sus líneas. Si termina con error → IOException. */
    public static List<String> textoDePdf(Path pdf) throws IOException, InterruptedException {
        Process p = new ProcessBuilder("pdftotext", "-layout", "-enc", "UTF-8", pdf.toString(), "-")
                .redirectError(ProcessBuilder.Redirect.DISCARD)
                .start();
        String texto = new String(p.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        int codigo = p.waitFor();
        if (codigo != 0) {
            throw new IOException("pdftotext terminó con código " + codigo + " para " + pdf);
        }
        return texto.lines().toList();
    }

    /** "mercadopago_2026-10-01_2026-10-24.csv" con la fecha del primer y del último movimiento. */
    static String nombreCsv(Resumen resumen) {
        // TODO T08a
        throw new UnsupportedOperationException("T08a");
    }

    /** Importa el texto de un resumen. Con {@code simular} no escribe el CSV ni el registro. */
    public static Resultado importar(List<String> lineas, List<Regla> reglas, Registro registro,
                                     Path carpetaSalida, boolean simular) throws IOException {
        // TODO T08a
        throw new UnsupportedOperationException("T08a");
    }
}
```

```java
// src/test/java/com/finanzas/service/ImportadorTest.java
package com.finanzas.service;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.Test;

class ImportadorTest {

    @Test
    void importaYNoRepite() {
        // TODO T08a
    }
}
```

Completá los `// TODO` que pide la tarea T08a. Devolvé **solo los métodos que completaste**, cada uno entero (firma y cuerpo), sin el resto de la clase: un bloque por archivo, con la ruta en la primera línea y después los métodos (y los imports nuevos, si agregaste). Archivos: src/main/java/com/finanzas/service/Importador.java, src/test/java/com/finanzas/service/ImportadorTest.java.
