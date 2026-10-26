package com.finanzas.service;

import static org.junit.jupiter.api.Assertions.*;

import com.finanzas.model.Clasificado;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.Test;

class RegistroTest {

    @TempDir Path dir;

    @Test
    void claveYAgregar() throws IOException {
        assertEquals("180700000002|150000.00", Registro.clave(Muestra.resumen().movimientos().get(1)));
        Registro r = new Registro(dir.resolve("sub/importados.txt"));
        r.agregar(List.of("a|1.00", "b|2.00"));
        r.agregar(List.of("b|2.00", "c|3.00"));
        assertEquals("a|1.00\nb|2.00\nc|3.00\n", Files.readString(dir.resolve("sub/importados.txt")));
    }

    @Test
    void nuevos() throws IOException {
        Registro r = new Registro(dir.resolve("importados.txt"));
        List<Clasificado> cs = new Clasificador(ReglasRepo.porDefecto(), Muestra.TITULAR).clasificarTodos(Muestra.resumen().movimientos());
        r.agregar(List.of(Registro.clave(cs.get(0).movimiento())));
        assertEquals(cs.size() - 1, r.nuevos(cs).size());
    }
}
