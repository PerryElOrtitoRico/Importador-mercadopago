package com.finanzas.model;

/**
 * Un movimiento con su tipo, contraparte y categoría.
 * {@code paraRevisar} es true cuando no tiene categoría y la pongo a mano; {@code motivo} dice por qué ("" si no).
 */
public record Clasificado(Movimiento movimiento, Tipo tipo, String contraparte, String categoria,
                          boolean paraRevisar, String motivo) {
}
