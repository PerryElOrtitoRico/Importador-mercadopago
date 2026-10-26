package com.finanzas.service;

import static org.junit.jupiter.api.Assertions.*;

import com.finanzas.model.Tipo;
import org.junit.jupiter.api.Test;

class TipoContraparteTest {

    @Test
    void tipos() {
        assertEquals(Tipo.RENDIMIENTOS, Tipo.de("Rendimientos"));
        assertEquals(Tipo.RESERVA_RETIRADA, Tipo.de("Dinero retirado HOGAR"));
        assertEquals(Tipo.OTRO, Tipo.de("Compra rara"));
    }

    @Test
    void contraparte() {
        assertEquals("Carrefour", Clasificador.contraparte("Pago con QR Carrefour", Tipo.PAGO_QR));
        assertEquals("", Clasificador.contraparte("Transferencia enviada", Tipo.TRANSFERENCIA_ENVIADA));
    }

    @Test
    void propia() {
        assertTrue(Clasificador.esPropia("Transferencia recibida GOMEZ ANA LAURA", "Ana Laura Gomez"));
        assertFalse(Clasificador.esPropia("Transferencia enviada Ana Laura Perez", "Ana Laura Gomez"));
    }
}
