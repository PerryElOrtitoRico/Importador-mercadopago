package com.finanzas.model;

/** Si la descripción del movimiento contiene {@code contiene} (sin mayúsculas ni tildes), va a {@code categoria}. */
public record Regla(String contiene, String categoria) {
}
