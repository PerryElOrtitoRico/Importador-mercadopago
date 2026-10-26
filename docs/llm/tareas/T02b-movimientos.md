<!-- contrato: 1,2 | archivos: src/main/java/com/finanzas/service/LectorResumen.java, src/test/java/com/finanzas/service/LectorMovimientosTest.java -->
# T02b — Lector: movimientos

## Objetivo
Completar **solo** `bloques`, `leerMovimientos` y `leer` de `LectorResumen`, y los tests de `LectorMovimientosTest`. `campo` y `leerEncabezado` ya están hechos: no los cambies.

## Código que ya existe
- `FILA` (Pattern): grupos 1 fecha `dd-MM-yyyy`, 2 descripción de la fila (puede ser ""), 3 ID, 4 valor, 5 saldo. `FECHA` es el `DateTimeFormatter` de `dd-MM-yyyy`.
- `Importes.parsear(String)` → `BigDecimal`.
- `Muestra.lineas()` (en los tests): un resumen inventado con 14 movimientos.

## `bloques(lineas)` (pasos en orden)
1. `List<List<String>> out = new ArrayList<>(); List<String> actual = new ArrayList<>();`
2. `for (String linea : lineas) { if (linea.isBlank()) { if (!actual.isEmpty()) { out.add(actual); actual = new ArrayList<>(); } } else { actual.add(linea); } }`
3. `if (!actual.isEmpty()) out.add(actual);`
4. `return out;`

## `leerMovimientos(lineas)` (pasos en orden)
1. `List<Movimiento> out = new ArrayList<>();`
2. `for (List<String> bloque : bloques(lineas)) {`
3. `    int fila = -1;`
4. `    for (int i = 0; i < bloque.size(); i++) { if (FILA.matcher(bloque.get(i)).matches()) { if (fila >= 0) throw new IllegalStateException("Bloque con dos movimientos: " + bloque); fila = i; } }`
5. `    if (fila < 0) continue;`
6. `    Matcher m = FILA.matcher(bloque.get(fila)); m.matches();`
7. `    StringBuilder sb = new StringBuilder();`
8. `    for (int i = 0; i < fila; i++) sb.append(bloque.get(i).trim()).append(' ');`
9. `    sb.append(m.group(2).trim()).append(' ');`
10. `    for (int i = fila + 1; i < bloque.size(); i++) sb.append(bloque.get(i).trim()).append(' ');`
11. `    String descripcion = sb.toString().replaceAll("\\s+", " ").trim();`
12. `    out.add(new Movimiento(LocalDate.parse(m.group(1), FECHA), descripcion, m.group(3), Importes.parsear(m.group(4)), Importes.parsear(m.group(5))));`
13. `}`
14. `return out;`

## `leer(lineas)`
1. `return new Resumen(leerEncabezado(lineas), leerMovimientos(lineas));`

## Tests (copiá estas aserciones tal cual)
- `bloques`: `List<List<String>> b = LectorResumen.bloques(List.of("a", "", "  ", "b", "c", ""));` y `assertEquals(List.of(List.of("a"), List.of("b", "c")), b);`
- `movimientos`: `List<Movimiento> ms = LectorResumen.leerMovimientos(Muestra.lineas());` y
  - `assertEquals(14, ms.size());`
  - `assertEquals(LocalDate.of(2026, 10, 1), ms.get(0).fecha());`
  - `assertEquals("Transferencia recibida PEREZ JUAN CARLOS", ms.get(1).descripcion());`
  - `assertEquals("180700000002", ms.get(1).idOperacion());`
  - `assertEquals(new BigDecimal("-0.01"), ms.get(13).valor());`
