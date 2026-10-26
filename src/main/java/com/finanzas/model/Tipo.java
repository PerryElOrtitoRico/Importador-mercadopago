package com.finanzas.model;

/** Tipo de movimiento según cómo empieza la descripción. */
public enum Tipo {
    RENDIMIENTOS("Rendimientos"),
    TRANSFERENCIA_RECIBIDA("Transferencia recibida"),
    TRANSFERENCIA_ENVIADA("Transferencia enviada"),
    PAGO_QR("Pago con QR"),
    DEVOLUCION("Devolución de dinero"),
    RETIRO("Retiro de dinero"),
    RESERVA_GUARDADA("Dinero reservado"),
    RESERVA_RETIRADA("Dinero retirado"),
    OTRO("");

    private final String prefijo;

    Tipo(String prefijo) {
        this.prefijo = prefijo;
    }

    public String prefijo() {
        return prefijo;
    }

    /** Las reservas de Mercado Pago se mueven dentro de la misma cuenta: no son gastos ni ingresos. */
    public boolean esReserva() {
        return this == RESERVA_GUARDADA || this == RESERVA_RETIRADA;
    }

    /** El primer tipo (en el orden de arriba, sin OTRO) cuyo prefijo es el comienzo de la descripción; si ninguno, OTRO. */
    public static Tipo de(String descripcion) {
        for (Tipo t : values()) {
            if (t != OTRO && descripcion.startsWith(t.prefijo())) {
                return t;
            }
        }
        return OTRO;
    }
}
