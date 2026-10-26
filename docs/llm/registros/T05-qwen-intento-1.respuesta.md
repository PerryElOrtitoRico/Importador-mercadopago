```java
// src/main/java/com/finanzas/service/Registro.java
package com.finanzas.service;

import com.finanzas.model.Clasificado;
import com.finanzas.model.Movimiento;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/** Archivo de texto con una clave por línea de los movimientos ya importados, para no duplicarlos. */
public final class Registro {

    private final Path archivo;

    public Registro(Path archivo) {
        this.archivo = archivo;
    }

    /**
     * ID de operación + "|" + valor ("181588521871|17831.75").
     * El ID solo no alcanza: una devolución usa el mismo ID que el pago que devuelve.
     */
    public static String clave(Movimiento m) {
        return m.idOperacion() + "|" + m.valor().toPlainString();
    }

    /** Las claves guardadas, en orden; vacío si el archivo no existe. Las líneas en blanco se ignoran. */
    public Set<String> leer() throws IOException {
        Set<String> out = new LinkedHashSet<>();
        if (!Files.exists(archivo)) return out;
        for (String linea : Files.readAllLines(archivo, StandardCharsets.UTF_8)) {
            if (!linea.isBlank()) out.add(linea.trim());
        }
        return out;
    }

    /** Agrega las claves que falten y guarda de forma atómica (temporal + reemplazo). Crea las carpetas si no existen. */
    public void agregar(Collection<String> claves) throws IOException {
        Set<String> todas = leer();
        todas.addAll(claves);
        if (archivo.getParent() != null) Files.createDirectories(archivo.getParent());
        Path tmp = archivo.resolveSibling(archivo.getFileName() + ".tmp");
        StringBuilder sb = new StringBuilder();
        for (String c : todas) sb.append(c).append('\n');
        Files.writeString(tmp, sb.toString(), StandardCharsets.UTF_8);
        Files.move(tmp, archivo, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
    }

    /** Los clasificados cuya clave todavía no está en el registro, en el mismo orden. */
    public List<Clasificado> nuevos(List<Clasificado> clasificados) throws IOException {
        Set<String> ya = leer();
        List<Clasificado> out = new ArrayList<>();
        for (Clasificado c : clasificados) {
            if (!ya.contains(clave(c.movimiento()))) out.add(c);
        }
        return out;
    }
}
```

```java
// src/test/java/com/finanzas/service/RegistroTest.java
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
```

<!-- 95 s · entrada 2211 · salida 1008 · finish_reason stop -->
