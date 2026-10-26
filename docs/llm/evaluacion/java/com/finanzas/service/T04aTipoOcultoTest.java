package com.finanzas.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.finanzas.model.Tipo;

class T04aTipoOcultoTest {
    @Test
    void todosLosTipos() {
        assertEquals(Tipo.TRANSFERENCIA_RECIBIDA, Tipo.de("Transferencia recibida PEREZ"));
        assertEquals(Tipo.TRANSFERENCIA_ENVIADA, Tipo.de("Transferencia enviada"));
        assertEquals(Tipo.PAGO_QR, Tipo.de("Pago con QR ECOGAS"));
        assertEquals(Tipo.DEVOLUCION, Tipo.de("Devolución de dinero Carrefour"));
        assertEquals(Tipo.RETIRO, Tipo.de("Retiro de dinero Carrefour"));
        assertEquals(Tipo.RESERVA_GUARDADA, Tipo.de("Dinero reservado Gastos"));
        assertEquals(Tipo.OTRO, Tipo.de(""));
    }

    @Test
    void contraparteCasos() {
        assertEquals("Algo raro", Clasificador.contraparte(" Algo raro ", Tipo.OTRO));
        assertEquals("PEREZ JUAN", Clasificador.contraparte("Transferencia recibida  PEREZ JUAN", Tipo.TRANSFERENCIA_RECIBIDA));
    }

    @Test
    void propiaPorPalabras() {
        assertFalse(Clasificador.esPropia("Transferencia enviada Anabela Gomez Laura", "Ana Laura Gomez"));
        assertTrue(Clasificador.esPropia("Transferencia recibida GÓMEZ ANA LAURA", "Ana Laura Gomez"));
    }
}
