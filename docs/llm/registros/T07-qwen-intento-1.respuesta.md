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
        if (!Files.exists(archivo)) {
            if (archivo.getParent() != null) Files.createDirectories(archivo.getParent());
            List<Regla> porDefecto = porDefecto();
            Files.writeString(archivo, GSON.toJson(porDefecto, LISTA), StandardCharsets.UTF_8);
            return porDefecto;
        }
        String json = Files.readString(archivo, StandardCharsets.UTF_8);
        try {
            List<Regla> reglas = GSON.fromJson(json, LISTA);
            if (reglas == null) throw new JsonParseException("archivo vacío");
            return List.copyOf(reglas);
        } catch (JsonParseException e) {
            throw new IllegalStateException("No se pudo leer " + archivo + ": " + e.getMessage(), e);
        }
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
    void creaYLee() throws IOException {
        Path dir = Files.createTempDirectory("reglas");
        try {
            Path archivo = dir.resolve("config/reglas.json");
            assertEquals(ReglasRepo.porDefecto(), ReglasRepo.cargar(archivo));
            assertTrue(Files.exists(archivo));
            Files.writeString(archivo, "[{\"contiene\": \"Kiosco\", \"categoria\": \"Golosinas\"}]");
            assertEquals(List.of(new Regla("Kiosco", "Golosinas")), ReglasRepo.cargar(archivo));
        } finally {
            Files.walk(dir).sorted().forEach(p -> {
                try {
                    Files.deleteIfExists(p);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    @Test
    void mal() throws IOException {
        Path dir = Files.createTempDirectory("reglas");
        try {
            Path archivo = dir.resolve("reglas.json");
            Files.writeString(archivo, "[{");
            assertThrows(IllegalStateException.class, () -> ReglasRepo.cargar(archivo));
            assertEquals("[{", Files.readString(archivo));
        } finally {
            Files.walk(dir).sorted().forEach(p -> {
                try {
                    Files.deleteIfExists(p);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }
}
```

<!-- 196 s · entrada 2739 · salida 1655 · finish_reason stop -->
