<!-- contrato: 1,4 | archivos: src/main/java/com/finanzas/model/Tipo.java, src/main/java/com/finanzas/service/Clasificador.java, src/test/java/com/finanzas/service/TipoContraparteTest.java -->
# T04a — Tipo, contraparte y transferencias propias

## Objetivo
Completar `Tipo.de`, y **solo** `contraparte` y `esPropia` de `Clasificador`, y los tests de `TipoContraparteTest`. `clasificar` y `clasificarTodos` los completa otra subtarea.

## Código que ya existe
- `Tipo.values()` en orden; `t.prefijo()`; `OTRO` tiene prefijo `""`.
- `Textos.normalizar(String)`: minúsculas, sin tildes, espacios simples ("  GOMEZ  Aná " → "gomez ana").

## `Tipo.de(descripcion)` (pasos en orden)
1. `for (Tipo t : values()) { if (t != OTRO && descripcion.startsWith(t.prefijo())) return t; }`
2. `return OTRO;`

## `Clasificador.contraparte(descripcion, tipo)` (pasos en orden)
1. `if (tipo == Tipo.OTRO) return descripcion.trim();`
2. `return descripcion.substring(tipo.prefijo().length()).trim();`

## `Clasificador.esPropia(descripcion, titular)` (pasos en orden)
1. `Set<String> palabras = new HashSet<>(Arrays.asList(Textos.normalizar(descripcion).split(" ")));`
2. `for (String p : Textos.normalizar(titular).split(" ")) { if (!palabras.contains(p)) return false; }`
3. `return true;`

## Tests (copiá estas aserciones tal cual)
- `tipos`:
  - `assertEquals(Tipo.RENDIMIENTOS, Tipo.de("Rendimientos"));`
  - `assertEquals(Tipo.RESERVA_RETIRADA, Tipo.de("Dinero retirado HOGAR"));`
  - `assertEquals(Tipo.OTRO, Tipo.de("Compra rara"));`
- `contraparte`:
  - `assertEquals("Carrefour", Clasificador.contraparte("Pago con QR Carrefour", Tipo.PAGO_QR));`
  - `assertEquals("", Clasificador.contraparte("Transferencia enviada", Tipo.TRANSFERENCIA_ENVIADA));`
- `propia`:
  - `assertTrue(Clasificador.esPropia("Transferencia recibida GOMEZ ANA LAURA", "Ana Laura Gomez"));`
  - `assertFalse(Clasificador.esPropia("Transferencia enviada Ana Laura Perez", "Ana Laura Gomez"));`
