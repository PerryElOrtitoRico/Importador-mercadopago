package com.finanzas.cli;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.finanzas.service.Importador;
import com.finanzas.service.Muestra;
import com.finanzas.service.Registro;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

class T08bMainOcultoTest {
    @TempDir
    Path dir;

    private final ByteArrayOutputStream out = new ByteArrayOutputStream();
    private final ByteArrayOutputStream err = new ByteArrayOutputStream();

    private int correr(String... args) {
        return Main.ejecutar(args, dir.resolve("reglas.json"), new Registro(dir.resolve("importados.txt")),
                new PrintStream(out, true, StandardCharsets.UTF_8), new PrintStream(err, true, StandardCharsets.UTF_8));
    }

    @Test
    void simular() throws Exception {
        Path txt = dir.resolve("resumen.txt");
        Files.writeString(txt, Muestra.TEXTO);
        assertEquals(0, correr("importar", txt.toString(), "--simular"));
        String s = out.toString(StandardCharsets.UTF_8);
        assertTrue(s.contains("CSV: no se escribió (simulación)\n"));
        assertTrue(s.contains("Para revisar:\n"));
        assertTrue(s.contains("  2026-10-01  150000.00  Transferencia recibida PEREZ JUAN CARLOS  (Sin regla)\n"));
        assertFalse(Files.exists(dir.resolve("importados.txt")));
        assertTrue(Files.exists(dir.resolve("reglas.json")));
    }

    @Test
    void segundaVez() throws Exception {
        Path txt = dir.resolve("resumen.txt");
        Files.writeString(txt, Muestra.TEXTO);
        assertEquals(0, correr("importar", txt.toString()));
        assertTrue(Files.exists(dir.resolve("mercadopago_2026-10-01_2026-10-06.csv")));
        out.reset();
        assertEquals(0, correr("importar", txt.toString()));
        assertTrue(out.toString(StandardCharsets.UTF_8).contains("Ya importados antes: 12\n"));
        assertTrue(out.toString(StandardCharsets.UTF_8).contains("CSV: no hay movimientos nuevos\n"));
    }

    @Test
    void noCuadra() throws Exception {
        Path txt = dir.resolve("resumen.txt");
        Files.writeString(txt, Muestra.TEXTO.replace("Saldo final: $ 86.585,45", "Saldo final: $ 86.585,40"));
        assertEquals(1, correr("importar", txt.toString()));
        assertTrue(err.toString(StandardCharsets.UTF_8).contains("no cuadra"));
    }

    @Test
    void argumentos() {
        assertEquals(2, correr("importar", "x.txt", "--salida"));
        assertEquals(2, correr("importar", "x.txt", "--otra"));
        assertEquals(1, correr("importar", dir.resolve("no.pdf").toString()));
        assertTrue(err.toString(StandardCharsets.UTF_8).contains("Error:"));
    }

    @Test
    void informeSinNuevos() {
        Importador.Resultado r = new Importador.Resultado(3, 1, 2, List.of(), List.of(), null);
        assertEquals("Movimientos leídos: 3\nReservas ignoradas: 1\nYa importados antes: 2\nNuevos: 0 (0 para revisar)\nCSV: no hay movimientos nuevos\n",
                Main.informe(r, false));
    }
}
