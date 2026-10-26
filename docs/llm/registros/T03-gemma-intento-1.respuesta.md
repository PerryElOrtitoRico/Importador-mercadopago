```java
// src/main/java/com/finanzas/service/Verificador.java
package com.finanzas.service;

import com.finanzas.model.Movimiento;
import com.finanzas.model.Resumen;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/** Comprueba que el resumen leído cuadre: si algo no cuadra, se leyó mal y no se importa nada. */
public final class Verificador {

    private Verificador() {
    }

    /** Lista de errores en castellano; vacía si todo cuadra. */
    public static List<String> verificar(Resumen resumen) {
        List<String> errores = new ArrayList<>();
        if (resumen.movimientos().isEmpty()) {
            errores.add("El resumen no tiene movimientos");
            return errores;
        }

        BigDecimal saldo = resumen.encabezado().saldoInicial();
        BigDecimal entradas = BigDecimal.ZERO;
        BigDecimal salidas = BigDecimal.ZERO;

        for (Movimiento m : resumen.movimientos()) {
            saldo = saldo.add(m.valor());
            if (saldo.compareTo(m.saldo()) != 0) {
                errores.add("Saldo distinto en " + m.idOperacion() + ": calculado " + saldo.toPlainString() + ", resumen " + m.saldo().toPlainString());
            }
            if (m.valor().signum() > 0) {
                entradas = entradas.add(m.valor());
            } else {
                salidas = salidas.add(m.valor());
            }
        }

        if (saldo.compareTo(resumen.encabezado().saldoFinal()) != 0) {
            errores.add("Saldo final distinto: calculado " + saldo.toPlainString() + ", resumen " + resumen.encabezado().saldoFinal().toPlainString());
        }

        if (entradas.compareTo(resumen.encabezado().entradas()) != 0) {
            errores.add("Entradas distintas: suma " + entradas.toPlainString() + ", resumen " + resumen.encabezado().entradas().toPlainString());
        }

        if (salidas.compareTo(resumen.encabezado().salidas()) != 0) {
            errores.add("Salidas distintas: suma " + salidas.toPlainString() + ", resumen " + resumen.encabezado().salidas().toPlainString());
        }

        return errores;
    }
}
```

```java
// src/test/java/com/finanzas/service/VerificadorTest.java
package com.finanzas.service;

import static org.junit.jupiter.api.Assertions.*;

import com.finanzas.model.Movimiento;
import com.finanzas.model.Resumen;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class VerificadorTest {

    @Test
    void cuadra() {
        assertEquals(List.of(), Verificador.verificar(Muestra.resumen()));
    }

    @Test
    void saldoDistinto() {
        List<Movimiento> ms = new ArrayList<>(Muestra.resumen().movimientos());
        ms.set(3, Esperado.m(2026, 10, 2, "Pago con QR Autoservicio7030", "181900000004", "-2900.00", "129681.00"));
        List<String> errores = Verificador.verificar(new Resumen(Muestra.resumen().encabezado(), ms));
        assertEquals(List.of("Saldo distinto en 181900000004: calculado 129681.63, resumen 129681.00"), errores);
    }
}
```

<!-- 53 s · entrada 2106 · salida 918 · finish_reason stop -->
