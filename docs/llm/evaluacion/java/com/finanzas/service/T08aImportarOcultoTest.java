package com.finanzas.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

class T08aImportarOcultoTest {
    @TempDir
    Path dir;

    @Test
    void nombre() {
        assertEquals("mercadopago_2026-10-01_2026-10-06.csv", Importador.nombreCsv(Esperado.resumen()));
    }

    @Test
    void simularNoEscribe() throws Exception {
        Registro reg = new Registro(dir.resolve("importados.txt"));
        Importador.Resultado r = Importador.importar(Muestra.lineas(), ReglasRepo.porDefecto(), reg, dir.resolve("salida"), true);
        assertEquals(12, r.exportados().size());
        assertNull(r.csv());
        assertFalse(Files.exists(dir.resolve("importados.txt")));
        assertFalse(Files.exists(dir.resolve("salida")));
    }

    @Test
    void noCuadraNoEscribe() throws Exception {
        List<String> mal = Muestra.TEXTO.replace("Saldo final: $ 86.585,45", "Saldo final: $ 86.585,40").lines().toList();
        Importador.Resultado r = Importador.importar(mal, ReglasRepo.porDefecto(), new Registro(dir.resolve("importados.txt")), dir.resolve("salida"), false);
        assertEquals(List.of("Saldo final distinto: calculado 86585.45, resumen 86585.40"), r.errores());
        assertNull(r.csv());
        assertFalse(Files.exists(dir.resolve("salida")));
        assertFalse(Files.exists(dir.resolve("importados.txt")));
    }

    @Test
    void noPisaUnCsvQueYaEsta() throws Exception {
        Files.createDirectories(dir.resolve("salida"));
        Files.writeString(dir.resolve("salida/mercadopago_2026-10-01_2026-10-06.csv"), "mío");
        Importador.Resultado r = Importador.importar(Muestra.lineas(), ReglasRepo.porDefecto(), new Registro(dir.resolve("importados.txt")), dir.resolve("salida"), false);
        assertEquals(dir.resolve("salida/mercadopago_2026-10-01_2026-10-06_2.csv"), r.csv());
        assertEquals("mío", Files.readString(dir.resolve("salida/mercadopago_2026-10-01_2026-10-06.csv")));
        assertEquals("2026-10-01,Rendimientos,Rendimientos,1.38,MP 1750900000001", Files.readAllLines(r.csv()).get(1));
        assertEquals(12, new Registro(dir.resolve("importados.txt")).leer().size());
    }
}
