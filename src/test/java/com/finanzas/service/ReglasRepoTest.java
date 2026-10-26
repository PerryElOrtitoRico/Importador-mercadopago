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
            Files.walk(dir).sorted(java.util.Comparator.reverseOrder()).forEach(p -> {
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
            Files.walk(dir).sorted(java.util.Comparator.reverseOrder()).forEach(p -> {
                try {
                    Files.deleteIfExists(p);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }
}
