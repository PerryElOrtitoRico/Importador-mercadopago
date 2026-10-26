package com.finanzas.cli;

import static org.junit.jupiter.api.Assertions.*;

import com.finanzas.service.Muestra;
import com.finanzas.service.Registro;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.Test;

class MainTest {

    @Test
    void importa() throws Exception {
        Path dir = Files.createTempDirectory("test_dir");
        Path txt = dir.resolve("resumen.txt");
        Files.writeString(txt, Muestra.TEXTO);
        ByteArrayOutputStream salida = new ByteArrayOutputStream();
        ByteArrayOutputStream errores = new ByteArrayOutputStream();
        Path csvDir = dir.resolve("csv");
        Files.createDirectories(csvDir);

        int codigo = Main.ejecutar(new String[] {"importar", txt.toString(), "--salida", csvDir.toString()}, dir.resolve("reglas.json"), new Registro(dir.resolve("importados.txt")), new PrintStream(salida, true, StandardCharsets.UTF_8), new PrintStream(errores, true, StandardCharsets.UTF_8));
        assertEquals(0, codigo);
        assertTrue(salida.toString(StandardCharsets.UTF_8).contains("Nuevos: 12 (5 para revisar)"));
        assertTrue(Files.exists(csvDir.resolve("mercadopago_2026-10-01_2026-10-06.csv")));
    }

    @Test
    void argumentosMal() throws Exception {
        Path dir = Files.createTempDirectory("test_dir");
        ByteArrayOutputStream errores = new ByteArrayOutputStream();
        int codigo = Main.ejecutar(new String[] {"borrar"}, dir.resolve("reglas.json"), new Registro(dir.resolve("importados.txt")), new PrintStream(new ByteArrayOutputStream()), new PrintStream(errores, true, StandardCharsets.UTF_8));
        assertEquals(2, codigo);
        assertTrue(errores.toString(StandardCharsets.UTF_8).contains("Uso: finanzas importar"));
    }
}
