package com.finanzas.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

class T05RegistroOcultoTest {
    @TempDir
    Path dir;

    @Test
    void leerSinArchivo() throws Exception {
        assertEquals(Set.of(), new Registro(dir.resolve("no/existe.txt")).leer());
    }

    @Test
    void ignoraLineasEnBlanco() throws Exception {
        Files.writeString(dir.resolve("r.txt"), "a\n\n b \n");
        assertEquals(List.of("a", "b"), List.copyOf(new Registro(dir.resolve("r.txt")).leer()));
    }

    @Test
    void sinTemporalAlTerminar() throws Exception {
        new Registro(dir.resolve("x/y/r.txt")).agregar(List.of("a|1.00"));
        assertTrue(Files.exists(dir.resolve("x/y/r.txt")));
        assertFalse(Files.exists(dir.resolve("x/y/r.txt.tmp")));
    }

    @Test
    void devolucionNoEsElPago() {
        assertEquals("182000000005|-1400.00", Registro.clave(Esperado.movimientos().get(4)));
        assertEquals("182000000005|1400.00", Registro.clave(Esperado.movimientos().get(5)));
    }
}
