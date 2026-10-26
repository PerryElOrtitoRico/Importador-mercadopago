<!-- contrato: 1,4 | archivos: src/main/java/com/finanzas/service/Clasificador.java, src/test/java/com/finanzas/service/ClasificadorTest.java -->
# T04b — Clasificar

## Objetivo
Completar **solo** `clasificar` y `clasificarTodos` de `Clasificador`, y el test de `ClasificadorTest`. `contraparte` y `esPropia` ya están hechos: usalos.

## Código que ya existe
- Campos `reglas` (List<Regla>) y `titular` (String) del clasificador.
- `Tipo.de(descripcion)`, `tipo.esReserva()`. `Textos.normalizar(String)`.
- `Clasificado(movimiento, tipo, contraparte, categoria, paraRevisar, motivo)`; `Regla(contiene, categoria)`.
- En los tests: `ReglasRepo.porDefecto()` (reglas iniciales), `Muestra.TITULAR` (`"Ana Laura Gomez"`), `Muestra.resumen().movimientos()` (14 movimientos, 2 son reservas).

## `clasificar(m)` (pasos en orden)
1. `Tipo tipo = Tipo.de(m.descripcion());`
2. `if (tipo.esReserva()) return Optional.empty();`
3. `String contra = contraparte(m.descripcion(), tipo);`
4. `boolean transferencia = tipo == Tipo.TRANSFERENCIA_RECIBIDA || tipo == Tipo.TRANSFERENCIA_ENVIADA;`
5. `if (transferencia && esPropia(m.descripcion(), titular)) return Optional.of(new Clasificado(m, tipo, contra, "", true, "Transferencia a mi nombre"));`
6. `String desc = Textos.normalizar(m.descripcion());`
7. `for (Regla r : reglas) { if (desc.contains(Textos.normalizar(r.contiene()))) return Optional.of(new Clasificado(m, tipo, contra, r.categoria(), false, "")); }`
8. `return Optional.of(new Clasificado(m, tipo, contra, "", true, "Sin regla"));`

## `clasificarTodos(movimientos)` (pasos en orden)
1. `List<Clasificado> out = new ArrayList<>();`
2. `for (Movimiento m : movimientos) { clasificar(m).ifPresent(out::add); }`
3. `return out;`

## Test (copiá estas aserciones tal cual)
- `clasificarMuestra`:
  - `Clasificador c = new Clasificador(ReglasRepo.porDefecto(), Muestra.TITULAR);`
  - `List<Clasificado> cs = c.clasificarTodos(Muestra.resumen().movimientos());`
  - `assertEquals(12, cs.size());`
  - `assertEquals("Rendimientos", cs.get(0).categoria());`
  - `assertEquals("Transferencia a mi nombre", cs.get(2).motivo());`
  - `assertEquals("Supermercado", cs.get(3).categoria());`
