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
        Tipo tipo = Tipo.de(m.descripcion());
        if (tipo.esReserva()) return Optional.empty();
        String contra = contraparte(m.descripcion(), tipo);
        boolean transferencia = tipo == Tipo.TRANSFERENCIA_RECIBIDA || tipo == Tipo.TRANSFERENCIA_ENVIADA;
        if (transferencia && esPropia(m.descripcion(), titular)) return Optional.of(new Clasificado(m, tipo, contra, "", true, "Transferencia a mi nombre"));
        String desc = Textos.normalizar(m.descripcion());
        for (Regla r : reglas) {
            if (desc.contains(Textos.normalizar(r.contiene()))) return Optional.of(new Clasificado(m, tipo, contra, r.categoria(), false, ""));
        }
        return Optional.of(new Clasificado(m, tipo, contra, "", true, "Sin regla"));
    }

    /** Los clasificados, en el mismo orden, sin las reservas. */
    public List<Clasificado> clasificarTodos(List<Movimiento> movimientos) {
        List<Clasificado> out = new ArrayList<>();
        for (Movimiento m : movimientos) {
            clasificar(m).ifPresent(out::add);
        }
        return out;
    }
}
