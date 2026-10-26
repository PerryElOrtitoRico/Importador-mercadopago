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
