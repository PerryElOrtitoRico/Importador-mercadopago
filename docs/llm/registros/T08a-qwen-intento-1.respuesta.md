```java
// src/main/java/com/finanzas/service/Importador.java
package com.finanzas.service;

import com.finanzas.model.Clasificado;
import com.finanzas.model.Movimiento;
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
        List<Movimiento> ms = resumen.movimientos();
        return "mercadopago_" + ms.get(0).fecha() + "_" + ms.get(ms.size() - 1).fecha() + ".csv";
    }

    /** Importa el texto de un resumen. Con {@code simular} no escribe el CSV ni el registro. */
    public static Resultado importar(List<String> lineas, List<Regla> reglas, Registro registro,
                                     Path carpetaSalida, boolean simular) throws IOException {
        Resumen r = LectorResumen.leer(lineas);
        int leidos = r.movimientos().size();
        List<String> errores = Verificador.verificar(r);
        if (!errores.isEmpty()) return new Resultado(leidos, 0, 0, List.of(), errores, null);
        List<Clasificado> clasificados = new Clasificador(reglas, r.encabezado().titular()).clasificarTodos(r.movimientos());
        int reservas = leidos - clasificados.size();
        List<Clasificado> nuevos = registro.nuevos(clasificados);
        int repetidos = clasificados.size() - nuevos.size();
        if (nuevos.isEmpty() || simular) return new Resultado(leidos, reservas, repetidos, nuevos, List.of(), null);
        Files.createDirectories(carpetaSalida);
        String nombre = nombreCsv(r); Path csv = carpetaSalida.resolve(nombre); int n = 2;
        while (Files.exists(csv)) { csv = carpetaSalida.resolve(nombre.replace(".csv", "_" + n + ".csv")); n++; }
        Files.writeString(csv, ExportadorCsv.csv(nuevos), StandardCharsets.UTF_8);
        registro.agregar(nuevos.stream().map(c -> Registro.clave(c.movimiento())).toList());
        return new Resultado(leidos, reservas, repetidos, nuevos, List.of(), csv);
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

    @TempDir Path dir;

    @Test
    void importaYNoRepite() throws IOException {
        Registro registro = new Registro(dir.resolve("importados.txt"));
        Importador.Resultado r1 = Importador.importar(Muestra.lineas(), ReglasRepo.porDefecto(), registro, dir.resolve("salida"), false);
        assertEquals(14, r1.leidos());
        assertEquals(2, r1.reservas());
        assertEquals(12, r1.exportados().size());
        assertEquals(dir.resolve("salida/mercadopago_2026-10-01_2026-10-06.csv"), r1.csv());
        assertEquals(13, Files.readAllLines(r1.csv()).size());
        Importador.Resultado r2 = Importador.importar(Muestra.lineas(), ReglasRepo.porDefecto(), registro, dir.resolve("salida"), false);
        assertEquals(12, r2.repetidos());
        assertNull(r2.csv());
    }
}
```

<!-- 108 s · entrada 2958 · salida 1197 · finish_reason stop -->
