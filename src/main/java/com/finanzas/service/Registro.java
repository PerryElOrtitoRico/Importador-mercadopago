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
        // TODO T05
        throw new UnsupportedOperationException("T05");
    }

    /** Las claves guardadas, en orden; vacío si el archivo no existe. Las líneas en blanco se ignoran. */
    public Set<String> leer() throws IOException {
        // TODO T05
        throw new UnsupportedOperationException("T05");
    }

    /** Agrega las claves que falten y guarda de forma atómica (temporal + reemplazo). Crea las carpetas si no existen. */
    public void agregar(Collection<String> claves) throws IOException {
        // TODO T05
        throw new UnsupportedOperationException("T05");
    }

    /** Los clasificados cuya clave todavía no está en el registro, en el mismo orden. */
    public List<Clasificado> nuevos(List<Clasificado> clasificados) throws IOException {
        // TODO T05
        throw new UnsupportedOperationException("T05");
    }
}
