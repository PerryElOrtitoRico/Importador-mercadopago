package com.finanzas.service;

import com.finanzas.model.Movimiento;
import com.finanzas.model.Resumen;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/** Comprueba que el resumen leído cuadre: si algo no cuadra, se leyó mal y no se importa nada. */
public final class Verificador {

    private Verificador() {
    }

    /** Lista de errores en castellano; vacía si todo cuadra. */
    public static List<String> verificar(Resumen resumen) {
        // TODO T03
        throw new UnsupportedOperationException("T03");
    }
}
