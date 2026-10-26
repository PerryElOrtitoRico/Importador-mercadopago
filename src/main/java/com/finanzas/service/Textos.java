package com.finanzas.service;

import java.text.Normalizer;

/** Texto normalizado para comparar descripciones sin importar mayúsculas, tildes ni espacios. */
public final class Textos {

    private Textos() {
    }

    /** "  Verdulería   Doña " → "verduleria dona". null → "". */
    public static String normalizar(String texto) {
        // TODO T01
        throw new UnsupportedOperationException("T01");
    }
}
