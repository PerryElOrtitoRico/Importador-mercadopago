package com.finanzas.cli;

import com.finanzas.model.Clasificado;
import com.finanzas.model.Regla;
import com.finanzas.service.Importador;
import com.finanzas.service.Importes;
import com.finanzas.service.ReglasRepo;
import com.finanzas.service.Registro;
import com.finanzas.service.Rutas;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/** finanzas importar <resumen.pdf|resumen.txt> [--salida <carpeta>] [--simular] */
public final class Main {

    static final String USO = "Uso: finanzas importar <resumen.pdf|resumen.txt> [--salida <carpeta>] [--simular]";

    private Main() {
    }

    public static void main(String[] args) {
        System.exit(ejecutar(args, Rutas.reglas(), new Registro(Rutas.registro()), System.out, System.err));
    }

    /** Devuelve el código de salida: 0 bien, 1 el resumen no cuadra o falló algo, 2 argumentos mal. */
    static int ejecutar(String[] args, Path archivoReglas, Registro registro, PrintStream out, PrintStream err) {
        // TODO T08b
        throw new UnsupportedOperationException("T08b");
    }

    /** El informe que se muestra al terminar una importación sin errores. */
    static String informe(Importador.Resultado r, boolean simular) {
        // TODO T08b
        throw new UnsupportedOperationException("T08b");
    }
}
