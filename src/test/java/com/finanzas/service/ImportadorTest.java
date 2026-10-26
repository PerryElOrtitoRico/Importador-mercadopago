package com.finanzas.service;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.Test;

class ImportadorTest {

    @TempDir
    Path dir;

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
