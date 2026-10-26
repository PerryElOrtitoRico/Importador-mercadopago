<!-- contrato: 1,4 | archivos: src/main/java/com/finanzas/service/Importes.java, src/main/java/com/finanzas/service/Textos.java, src/test/java/com/finanzas/service/ImportesTest.java -->
# T01 — Importes y textos

## Objetivo
Completar `Importes.parsear`, `Importes.aCsv`, `Textos.normalizar` y los tres tests de `ImportesTest`.

## `Importes.parsear(texto)` (pasos en orden)
1. `if (texto == null || texto.isBlank()) throw new IllegalArgumentException("Importe vacío");`
2. `String s = texto.replace("$", "").replace(" ", "").replace(".", "").replace(",", ".");`
3. `return new BigDecimal(s).setScale(2, RoundingMode.HALF_UP);`

## `Importes.aCsv(valor)`
1. `return valor.setScale(2, RoundingMode.HALF_UP).toPlainString();`

## `Textos.normalizar(texto)` (pasos en orden)
1. `if (texto == null) return "";`
2. `String sinTildes = Normalizer.normalize(texto, Normalizer.Form.NFD).replaceAll("\\p{M}", "");`
3. `return sinTildes.toLowerCase().replaceAll("\\s+", " ").trim();`

## Tests (copiá estas aserciones tal cual)
- `parsear`:
  - `assertEquals(new BigDecimal("-1234.56"), Importes.parsear("$ -1.234,56"));`
  - `assertEquals(new BigDecimal("500000.00"), Importes.parsear("500.000,00"));`
  - `assertThrows(IllegalArgumentException.class, () -> Importes.parsear(" "));`
- `aCsv`:
  - `assertEquals("-1234.50", Importes.aCsv(new BigDecimal("-1234.5")));`
  - `assertEquals("1000.00", Importes.aCsv(new BigDecimal("1000")));`
- `normalizar`:
  - `assertEquals("verduleria dona", Textos.normalizar("  Verdulería   Doña "));`
  - `assertEquals("", Textos.normalizar(null));`
