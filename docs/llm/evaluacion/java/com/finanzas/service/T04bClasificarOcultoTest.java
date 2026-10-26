package com.finanzas.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.finanzas.model.Clasificado;
import com.finanzas.model.Regla;
import com.finanzas.model.Tipo;

import java.util.List;

class T04bClasificarOcultoTest {
    private final Clasificador c = new Clasificador(ReglasRepo.porDefecto(), Muestra.TITULAR);

    @Test
    void categoriasDeLaMuestra() {
        List<Clasificado> cs = c.clasificarTodos(Esperado.movimientos());
        assertEquals(List.of("Rendimientos", "", "", "Supermercado", "Supermercado", "Supermercado", "", "",
                "Servicios", "Efectivo", "Verdulería", ""), cs.stream().map(Clasificado::categoria).toList());
        assertEquals(5, cs.stream().filter(Clasificado::paraRevisar).count());
        assertEquals("Sin regla", cs.get(1).motivo());
        assertEquals("Transferencia a mi nombre", cs.get(6).motivo());
        assertEquals(Tipo.DEVOLUCION, cs.get(5).tipo());
        assertEquals("Carrefour", cs.get(5).contraparte());
        assertEquals("", cs.get(7).contraparte());
        assertEquals("", cs.get(3).motivo());
    }

    @Test
    void reservaVacia() {
        assertTrue(c.clasificar(Esperado.movimientos().get(7)).isEmpty());
    }

    @Test
    void ganaLaPrimeraRegla() {
        Clasificador otro = new Clasificador(List.of(new Regla("carrefour", "A"), new Regla("Pago con QR", "B")), "Nadie Nunca");
        assertEquals("A", otro.clasificar(Esperado.movimientos().get(4)).orElseThrow().categoria());
    }
}
