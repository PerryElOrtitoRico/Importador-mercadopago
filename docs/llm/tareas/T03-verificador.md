<!-- contrato: 1,3 | archivos: src/main/java/com/finanzas/service/Verificador.java, src/test/java/com/finanzas/service/VerificadorTest.java -->
# T03 — Verificador

## Objetivo
Completar `Verificador.verificar` y los tests de `VerificadorTest`.

## Código que ya existe
- Records `Resumen(encabezado, movimientos)`, `Encabezado(titular, periodo, saldoInicial, saldoFinal, entradas, salidas)`, `Movimiento(fecha, descripcion, idOperacion, valor, saldo)`.
- En los tests: `Muestra.resumen()` (un resumen que cuadra; el movimiento 3 tiene ID `"181900000004"` y saldo 129681.63) y `Esperado.m(año, mes, día, descripción, id, valor, saldo)` que arma un `Movimiento`.

## `verificar(resumen)` (pasos en orden)
1. `List<String> errores = new ArrayList<>();`
2. `if (resumen.movimientos().isEmpty()) { errores.add("El resumen no tiene movimientos"); return errores; }`
3. `BigDecimal saldo = resumen.encabezado().saldoInicial(); BigDecimal entradas = BigDecimal.ZERO; BigDecimal salidas = BigDecimal.ZERO;`
4. `for (Movimiento m : resumen.movimientos()) {`
5. `    saldo = saldo.add(m.valor());`
6. `    if (saldo.compareTo(m.saldo()) != 0) errores.add("Saldo distinto en " + m.idOperacion() + ": calculado " + saldo.toPlainString() + ", resumen " + m.saldo().toPlainString());`
7. `    if (m.valor().signum() > 0) entradas = entradas.add(m.valor()); else salidas = salidas.add(m.valor());`
8. `}`
9. `if (saldo.compareTo(resumen.encabezado().saldoFinal()) != 0) errores.add("Saldo final distinto: calculado " + saldo.toPlainString() + ", resumen " + resumen.encabezado().saldoFinal().toPlainString());`
10. `if (entradas.compareTo(resumen.encabezado().entradas()) != 0) errores.add("Entradas distintas: suma " + entradas.toPlainString() + ", resumen " + resumen.encabezado().entradas().toPlainString());`
11. `if (salidas.compareTo(resumen.encabezado().salidas()) != 0) errores.add("Salidas distintas: suma " + salidas.toPlainString() + ", resumen " + resumen.encabezado().salidas().toPlainString());`
12. `return errores;`

## Tests (copiá estas aserciones tal cual)
- `cuadra`: `assertEquals(List.of(), Verificador.verificar(Muestra.resumen()));`
- `saldoDistinto`:
  - `List<Movimiento> ms = new ArrayList<>(Muestra.resumen().movimientos());`
  - `ms.set(3, Esperado.m(2026, 10, 2, "Pago con QR Autoservicio7030", "181900000004", "-2900.00", "129681.00"));`
  - `List<String> errores = Verificador.verificar(new Resumen(Muestra.resumen().encabezado(), ms));`
  - `assertEquals(List.of("Saldo distinto en 181900000004: calculado 129681.63, resumen 129681.00"), errores);`
