package com.finanzas.service;

import static org.junit.jupiter.api.Assertions.*;

import com.finanzas.model.Clasificado;
import java.util.List;
import org.junit.jupiter.api.Test;

class ClasificadorTest {

    @Test
    void clasificarMuestra() {
        Clasificador c = new Clasificador(ReglasRepo.porDefecto(), Muestra.TITULAR);
        List<Clasificado> cs = c.clasificarTodos(Muestra.resumen().movimientos());
        assertEquals(12, cs.size());
        assertEquals("Rendimientos", cs.get(0).categoria());
        assertEquals("Transferencia a mi nombre", cs.get(2).motivo());
        assertEquals("Supermercado", cs.get(3).categoria());
    }
}
