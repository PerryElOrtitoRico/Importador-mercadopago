<!-- contrato: 1,6 | archivos: src/main/java/com/finanzas/service/ExportadorCsv.java, src/test/java/com/finanzas/service/ExportadorCsvTest.java -->
# T06 — CSV para Actual Budget

## Objetivo
Completar `csv`, `linea` y `campo` de `ExportadorCsv`, y los tests de `ExportadorCsvTest`.

## Código que ya existe
- `CABECERA` = `"fecha,beneficiario,categoria,importe,notas"`.
- `Clasificado(movimiento, tipo, contraparte, categoria, paraRevisar, motivo)`; `Movimiento(fecha, descripcion, idOperacion, valor, saldo)`; `tipo.prefijo()`.
- `Importes.aCsv(BigDecimal)` → `"-1400.00"`.
- En los tests: `Esperado.m(año, mes, día, descripción, id, valor, saldo)` arma un `Movimiento`.

## `campo(texto)` (pasos en orden)
1. `if (texto.contains(",") || texto.contains("\"") || texto.contains("\n")) return "\"" + texto.replace("\"", "\"\"") + "\"";`
2. `return texto;`

## `linea(c)` (pasos en orden)
1. `Movimiento m = c.movimiento();`
2. `String beneficiario = c.contraparte();`
3. `if (beneficiario.isEmpty()) beneficiario = c.tipo().prefijo();`
4. `if (beneficiario.isEmpty()) beneficiario = m.descripcion();`
5. `String notas = "MP " + m.idOperacion();`
6. `if (c.paraRevisar()) notas = notas + " - REVISAR: " + c.motivo();`
7. `return String.join(",", m.fecha().toString(), campo(beneficiario), campo(c.categoria()), Importes.aCsv(m.valor()), campo(notas));`

## `csv(clasificados)` (pasos en orden)
1. `StringBuilder sb = new StringBuilder(CABECERA).append('\n');`
2. `for (Clasificado c : clasificados) sb.append(linea(c)).append('\n');`
3. `return sb.toString();`

## Tests (copiá estas aserciones tal cual)
- `campo`:
  - `assertEquals("hola", ExportadorCsv.campo("hola"));`
  - `assertEquals("\"a,b\"", ExportadorCsv.campo("a,b"));`
- `linea`:
  - `Clasificado c = new Clasificado(Esperado.m(2026, 10, 2, "Pago con QR Carrefour", "182000000005", "-1400.00", "128281.63"), Tipo.PAGO_QR, "Carrefour", "Supermercado", false, "");`
  - `assertEquals("2026-10-02,Carrefour,Supermercado,-1400.00,MP 182000000005", ExportadorCsv.linea(c));`
  - `assertEquals(ExportadorCsv.CABECERA + "\n" + ExportadorCsv.linea(c) + "\n", ExportadorCsv.csv(List.of(c)));`
