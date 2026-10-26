package com.finanzas.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.finanzas.model.Clasificado;
import com.finanzas.model.Tipo;

import java.util.List;

class T06CsvOcultoTest {
    @Test
    void comillas() {
        assertEquals("\"dijo \"\"no\"\"\"", ExportadorCsv.campo("dijo \"no\""));
        assertEquals("\"Ahylo S.a., SRL\"", ExportadorCsv.campo("Ahylo S.a., SRL"));
    }

    @Test
    void beneficiarioPorDefecto() {
        Clasificado r = new Clasificado(Esperado.movimientos().get(0), Tipo.RENDIMIENTOS, "", "Rendimientos", false, "");
        assertEquals("2026-10-01,Rendimientos,Rendimientos,1.38,MP 1750900000001", ExportadorCsv.linea(r));
    }

    @Test
    void paraRevisar() {
        Clasificado t = new Clasificado(Esperado.movimientos().get(9), Tipo.TRANSFERENCIA_ENVIADA, "", "", true, "Sin regla");
        assertEquals("2026-10-04,Transferencia enviada,,-3500.00,MP 184900000009 - REVISAR: Sin regla", ExportadorCsv.linea(t));
    }

    @Test
    void vacio() {
        assertEquals("fecha,beneficiario,categoria,importe,notas\n", ExportadorCsv.csv(List.of()));
    }
}
