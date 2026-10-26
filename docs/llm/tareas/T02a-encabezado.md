<!-- contrato: 1,2 | archivos: src/main/java/com/finanzas/service/LectorResumen.java, src/test/java/com/finanzas/service/LectorEncabezadoTest.java -->
# T02a — Lector: encabezado

## Objetivo
Completar **solo** `campo` y `leerEncabezado` de `LectorResumen`, y los tests de `LectorEncabezadoTest`. Los otros métodos los completa otra subtarea.

## Código que ya existe
- `Importes.parsear(String)` → `BigDecimal` ("$ -1.234,56" → -1234.56).
- `Muestra.lineas()` (en los tests): las líneas de un resumen inventado. Su titular es `"Ana Laura Gomez"`.

## `campo(lineas, nombre)` (pasos en orden)
1. `Pattern p = Pattern.compile(Pattern.quote(nombre) + ":\\s*\\$\\s*(-?[\\d.]+,\\d{2})");`
2. `for (String linea : lineas) { Matcher m = p.matcher(linea); if (m.find()) return Importes.parsear(m.group(1)); }`
3. `throw new IllegalArgumentException("Falta " + nombre + " en el resumen");`

## `leerEncabezado(lineas)` (pasos en orden)
1. `String titular = "";`
2. `for (int i = 0; i < lineas.size(); i++) { if (lineas.get(i).contains("RESUMEN DE CUENTA")) { for (int j = i + 1; j < lineas.size(); j++) { if (!lineas.get(j).isBlank()) { titular = lineas.get(j).trim(); break; } } break; } }`
3. `String periodo = "";`
4. `for (String linea : lineas) { int k = linea.indexOf("Periodo:"); if (k >= 0) { periodo = linea.substring(k + "Periodo:".length()).trim(); break; } }`
5. `return new Encabezado(titular, periodo, campo(lineas, "Saldo inicial"), campo(lineas, "Saldo final"), campo(lineas, "Entradas"), campo(lineas, "Salidas"));`

## Tests (copiá estas aserciones tal cual)
- `encabezado`: `Encabezado e = LectorResumen.leerEncabezado(Muestra.lineas());` y
  - `assertEquals("Ana Laura Gomez", e.titular());`
  - `assertEquals(new BigDecimal("2580.25"), e.saldoInicial());`
  - `assertEquals(new BigDecimal("-82396.18"), e.salidas());`
- `campoQueFalta`: `assertThrows(IllegalArgumentException.class, () -> LectorResumen.campo(java.util.List.of("hola"), "Saldo final"));`
