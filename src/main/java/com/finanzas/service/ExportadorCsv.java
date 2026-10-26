package com.finanzas.service;

import com.finanzas.model.Clasificado;
import com.finanzas.model.Movimiento;

import java.util.List;

/** CSV para importar en Actual Budget: fecha,beneficiario,categoria,importe,notas. */
public final class ExportadorCsv {

    public static final String CABECERA = "fecha,beneficiario,categoria,importe,notas";

    private ExportadorCsv() {
    }

    /** Cabecera y una línea por movimiento, separadas por "\n" y terminadas en "\n". */
    public static String csv(List<Clasificado> clasificados) {
        // TODO T06
        throw new UnsupportedOperationException("T06");
    }

    /** Una línea sin el "\n" final. */
    static String linea(Clasificado c) {
        // TODO T06
        throw new UnsupportedOperationException("T06");
    }

    /** Entre comillas (y las comillas internas dobladas) si tiene coma, comillas o salto de línea; si no, tal cual. */
    static String campo(String texto) {
        // TODO T06
        throw new UnsupportedOperationException("T06");
    }
}
