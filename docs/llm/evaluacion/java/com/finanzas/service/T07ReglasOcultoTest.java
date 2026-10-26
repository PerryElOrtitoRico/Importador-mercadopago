package com.finanzas.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

class T07ReglasOcultoTest {
    @TempDir
    Path dir;

    @Test
    void archivoCreadoEsLegibleYConTildes() throws Exception {
        Path a = dir.resolve("a/b/reglas.json");
        ReglasRepo.cargar(a);
        assertTrue(Files.readString(a).contains("Verdulería"));
        assertEquals(ReglasRepo.porDefecto(), ReglasRepo.cargar(a));
    }

    @Test
    void vacioEsError() throws Exception {
        Path a = dir.resolve("reglas.json");
        Files.writeString(a, "");
        assertThrows(IllegalStateException.class, () -> ReglasRepo.cargar(a));
    }

    @Test
    void pdfQueNoExiste() {
        assertThrows(IOException.class, () -> Importador.textoDePdf(dir.resolve("no-existe.pdf")));
    }
}
