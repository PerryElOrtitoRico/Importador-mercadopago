<!-- modelo qwen/qwen3.5-9b, temperatura 0.2, max_tokens 5214, razonamiento no -->

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

## 7. Reglas y pdftotext
- Reglas en `~/.config/finanzas/reglas.json`: una lista JSON de `{"contiene": "...", "categoria": "..."}`. Si no existe se crea con las reglas por defecto. Si existe y está mal escrito, error (no se pisa).
- El PDF se pasa a texto con `pdftotext -layout -enc UTF-8 <pdf> -` (salida estándar).

---

# T07 — Reglas en JSON y pdftotext

## Objetivo
Completar `ReglasRepo.cargar`, **solo** `Importador.textoDePdf` (los otros métodos de `Importador` los completa otra subtarea) y los tests de `ReglasRepoTest`.

## Código que ya existe
- En `ReglasRepo`: `GSON` (Gson con formato lindo), `LISTA` (el `Type` de `List<Regla>`), `porDefecto()`.

## `ReglasRepo.cargar(archivo)` (pasos en orden)
1. `if (!Files.exists(archivo)) {`
2. `    if (archivo.getParent() != null) Files.createDirectories(archivo.getParent());`
3. `    List<Regla> porDefecto = porDefecto();`
4. `    Files.writeString(archivo, GSON.toJson(porDefecto, LISTA), StandardCharsets.UTF_8);`
5. `    return porDefecto;`
6. `}`
7. `String json = Files.readString(archivo, StandardCharsets.UTF_8);`
8. `try {`
9. `    List<Regla> reglas = GSON.fromJson(json, LISTA);`
10. `    if (reglas == null) throw new JsonParseException("archivo vacío");`
11. `    return List.copyOf(reglas);`
12. `} catch (JsonParseException e) {`
13. `    throw new IllegalStateException("No se pudo leer " + archivo + ": " + e.getMessage(), e);`
14. `}`

## `Importador.textoDePdf(pdf)` (cuerpo entero)
```java
        Process p = new ProcessBuilder("pdftotext", "-layout", "-enc", "UTF-8", pdf.toString(), "-")
                .redirectError(ProcessBuilder.Redirect.DISCARD)
                .start();
        String texto = new String(p.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        int codigo = p.waitFor();
        if (codigo != 0) {
            throw new IOException("pdftotext terminó con código " + codigo + " para " + pdf);
        }
        return texto.lines().toList();
```

## Tests (copiá estas aserciones tal cual; `@TempDir Path dir` como campo; los tests declaran `throws IOException`)
- `creaYLee`:
  - `Path archivo = dir.resolve("config/reglas.json");`
  - `assertEquals(ReglasRepo.porDefecto(), ReglasRepo.cargar(archivo));`
  - `assertTrue(Files.exists(archivo));`
  - `Files.writeString(archivo, "[{\"contiene\": \"Kiosco\", \"categoria\": \"Golosinas\"}]");`
  - `assertEquals(List.of(new Regla("Kiosco", "Golosinas")), ReglasRepo.cargar(archivo));`
- `mal`:
  - `Path archivo = dir.resolve("reglas.json");`
  - `Files.writeString(archivo, "[{");`
  - `assertThrows(IllegalStateException.class, () -> ReglasRepo.cargar(archivo));`
  - `assertEquals("[{", Files.readString(archivo));`

---

# Archivos actuales

```java
// src/main/java/com/finanzas/service/ReglasRepo.java
package com.finanzas.service;

import com.finanzas.model.Regla;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/** Reglas de categorías en un JSON editable a mano. */
public final class ReglasRepo {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private static final Type LISTA = new TypeToken<List<Regla>>() { }.getType();

    private ReglasRepo() {
    }

    /**
     * Las reglas iniciales, armadas con los comercios del resumen de octubre de 2026.
     * Van en orden: gana la primera que coincide ("Retiro de dinero Carrefour" es efectivo, no supermercado).
     */
    public static List<Regla> porDefecto() {
        return List.of(
                new Regla("Retiro de dinero", "Efectivo"),
                new Regla("Rendimientos", "Rendimientos"),
                new Regla("Carrefour", "Supermercado"),
                new Regla("Autoservicio7030", "Supermercado"),
                new Regla("Verdulería", "Verdulería"),
                new Regla("ECOGAS", "Servicios"),
                new Regla("EPEC", "Servicios"),
                new Regla("Universidad Nacional", "Facultad"),
                new Regla("Grafica", "Facultad"));
    }

    /**
     * Lee las reglas. Si el archivo no existe, lo crea con {@link #porDefecto()} y devuelve esas.
     * Si existe pero no se puede leer, IllegalStateException (no se pisa: son las reglas del usuario).
     */
    public static List<Regla> cargar(Path archivo) throws IOException {
        // TODO T07
        throw new UnsupportedOperationException("T07");
    }
}
```

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
        // TODO T07
        throw new UnsupportedOperationException("T07");
    }

    /** "mercadopago_2026-10-01_2026-10-24.csv" con la fecha del primer y del último movimiento. */
    static String nombreCsv(Resumen resumen) {
        // lo completa otra subtarea (T08a): no lo cambies
        throw new UnsupportedOperationException("T08a");
    }

    /** Importa el texto de un resumen. Con {@code simular} no escribe el CSV ni el registro. */
    public static Resultado importar(List<String> lineas, List<Regla> reglas, Registro registro,
                                     Path carpetaSalida, boolean simular) throws IOException {
        // lo completa otra subtarea (T08a): no lo cambies
        throw new UnsupportedOperationException("T08a");
    }
}
```

```java
// src/test/java/com/finanzas/service/ReglasRepoTest.java
package com.finanzas.service;

import static org.junit.jupiter.api.Assertions.*;

import com.finanzas.model.Regla;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.Test;

class ReglasRepoTest {

    @Test
    void creaYLee() {
        // TODO T07
    }

    @Test
    void mal() {
        // TODO T07
    }
}
```

Completá los `// TODO` que pide la tarea T07. Devolvé **solo los métodos que completaste**, cada uno entero (firma y cuerpo), sin el resto de la clase: un bloque por archivo, con la ruta en la primera línea y después los métodos (y los imports nuevos, si agregaste). Archivos: src/main/java/com/finanzas/service/ReglasRepo.java, src/main/java/com/finanzas/service/Importador.java, src/test/java/com/finanzas/service/ReglasRepoTest.java.
