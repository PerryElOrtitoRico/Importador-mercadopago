package com.finanzas.service;

import com.finanzas.model.Clasificado;
import com.finanzas.model.Movimiento;
import com.finanzas.model.Tipo;

import java.util.List;

/** CSV para importar en Actual Budget: fecha,beneficiario,categoria,importe,notas. */
public final class ExportadorCsv {

    public static final String CABECERA = "fecha,beneficiario,categoria,importe,notas";

    private ExportadorCsv() {
    }

    /** Cabecera y una línea por movimiento, separadas por "\n" y terminadas en "\n". */
    public static String csv(List<Clasificado> clasificados) {
        StringBuilder sb = new StringBuilder(CABECERA).append('\n');
        for (Clasificado c : clasificados) sb.append(linea(c)).append('\n');
        return sb.toString();
    }

    /** Una línea sin el "\n" final. */
    static String linea(Clasificado c) {
        Movimiento m = c.movimiento();
        String beneficiario = c.contraparte();
        if (beneficiario.isEmpty()) beneficiario = c.tipo().prefijo();
        if (beneficiario.isEmpty()) beneficiario = m.descripcion();
        String notas = "MP " + m.idOperacion();
        if (c.paraRevisar()) notas = notas + " - REVISAR: " + c.motivo();
        return String.join(",", m.fecha().toString(), campo(beneficiario), campo(c.categoria()), Importes.aCsv(m.valor()), campo(notas));
    }

    /** Entre comillas (y las comillas internas dobladas) si tiene coma, comillas o salto de línea; si no, tal cual. */
    static String campo(String texto) {
        if (texto.contains(",") || texto.contains("\"") || texto.contains("\n")) return "\"" + texto.replace("\"", "\"\"") + "\"";
        return texto;
    }
}
