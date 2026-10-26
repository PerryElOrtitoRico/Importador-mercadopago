package com.finanzas.service;

import com.finanzas.model.Clasificado;
import com.finanzas.model.Movimiento;
import com.finanzas.model.Regla;
import com.finanzas.model.Tipo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/** Pone tipo, contraparte y categoría a cada movimiento. */
public final class Clasificador {

    private final List<Regla> reglas;
    private final String titular;

    /** {@code titular}: el nombre del encabezado, para reconocer las transferencias a mi nombre. */
    public Clasificador(List<Regla> reglas, String titular) {
        this.reglas = List.copyOf(reglas);
        this.titular = titular;
    }

    /** La descripción sin el prefijo del tipo, sin espacios a los costados. Para OTRO, la descripción entera (trim). */
    static String contraparte(String descripcion, Tipo tipo) {
        if (tipo == Tipo.OTRO) {
            return descripcion.trim();
        }
        return descripcion.substring(tipo.prefijo().length()).trim();
    }

    /** true si todas las palabras del titular aparecen como palabras de la descripción (normalizadas, en cualquier orden). */
    static boolean esPropia(String descripcion, String titular) {
        Set<String> palabras = new HashSet<>(Arrays.asList(Textos.normalizar(descripcion).split(" ")));
        for (String p : Textos.normalizar(titular).split(" ")) {
            if (!palabras.contains(p)) {
                return false;
            }
        }
        return true;
    }

    /** Vacío para las reservas; si no, el movimiento clasificado. */
    public Optional<Clasificado> clasificar(Movimiento m) {
        // lo completa otra subtarea (T04b): no lo cambies
        throw new UnsupportedOperationException("T04b");
    }

    /** Los clasificados, en el mismo orden, sin las reservas. */
    public List<Clasificado> clasificarTodos(List<Movimiento> movimientos) {
        // lo completa otra subtarea (T04b): no lo cambies
        throw new UnsupportedOperationException("T04b");
    }
}
