package com.finanzas.model;

import java.util.List;

/** Un resumen de cuenta leído: encabezado y movimientos en el orden del PDF. */
public record Resumen(Encabezado encabezado, List<Movimiento> movimientos) {
}
