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
        if (args.length < 2 || !args[0].equals("importar")) {
            err.println(USO);
            return 2;
        }

        Path archivo = Path.of(args[1]);
        Path salida = archivo.toAbsolutePath().getParent();
        boolean simular = false;

        for (int i = 2; i < args.length; i++) {
            if (args[i].equals("--simular")) {
                simular = true;
            } else if (args[i].equals("--salida") && i + 1 < args.length) {
                i++;
                salida = Path.of(args[i]);
            } else {
                err.println(USO);
                return 2;
            }
        }

        try {
            List<String> lineas = archivo.toString().endsWith(".txt") ? Files.readAllLines(archivo, StandardCharsets.UTF_8) : Importador.textoDePdf(archivo);
            List<Regla> reglas = ReglasRepo.cargar(archivoReglas);
            Importador.Resultado r = Importador.importar(lineas, reglas, registro, salida, simular);

            if (!r.errores().isEmpty()) {
                err.println("El resumen no cuadra; no se importó nada:");
                for (String e : r.errores()) {
                    err.println("  - " + e);
                }
                return 1;
            }

            out.print(informe(r, simular));
            return 0;
        } catch (Exception e) {
            err.println("Error: " + e.getMessage());
            return 1;
        }
    }

    /** El informe que se muestra al terminar una importación sin errores. */
    static String informe(Importador.Resultado r, boolean simular) {
        long revisar = r.exportados().stream().filter(Clasificado::paraRevisar).count();
        StringBuilder sb = new StringBuilder();

        sb.append("Movimientos leídos: ").append(r.leidos()).append('\n');
        sb.append("Reservas ignoradas: ").append(r.reservas()).append('\n');
        sb.append("Ya importados antes: ").append(r.repetidos()).append('\n');
        sb.append("Nuevos: ").append(r.exportados().size()).append(" (").append(revisar).append(" para revisar)").append('\n');

        if (simular) {
            sb.append("CSV: no se escribió (simulación)\n");
        } else if (r.csv() == null) {
            sb.append("CSV: no hay movimientos nuevos\n");
        } else {
            sb.append("CSV: ").append(r.csv()).append('\n');
        }

        if (revisar > 0) {
            sb.append("Para revisar:\n");
            for (Clasificado c : r.exportados()) {
                if (c.paraRevisar()) {
                    sb.append("  ").append(c.movimiento().fecha()).append("  ").append(Importes.aCsv(c.movimiento().valor())).append("  ").append(c.movimiento().descripcion()).append("  (").append(c.motivo()).append(")\n");
                }
            }
        }
        return sb.toString();
    }
}
