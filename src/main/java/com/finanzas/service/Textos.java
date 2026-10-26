package com.finanzas.service;

import java.text.Normalizer;

/** Texto normalizado para comparar descripciones sin importar mayúsculas, tildes ni espacios. */
public final class Textos {

    private Textos() {
    }

    /** "  Verdulería   Doña " → "verduleria dona". null → "". */
    public static String normalizar(String texto) {
        if (texto == null) {
            return "";
        }
        String sinTildes = Normalizer.normalize(texto, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        return sinTildes.toLowerCase().replaceAll("\\s+", " ").trim();
    }
}
