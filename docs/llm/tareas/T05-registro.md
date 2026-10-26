<!-- contrato: 1,5 | archivos: src/main/java/com/finanzas/service/Registro.java, src/test/java/com/finanzas/service/RegistroTest.java -->
# T05 — Registro de importados

## Objetivo
Completar `clave`, `leer`, `agregar` y `nuevos` de `Registro`, y los tests de `RegistroTest`.

## Código que ya existe
- Campo `archivo` (Path) del registro.
- `Clasificado` se lee con `c.movimiento()`; `Movimiento` con `m.idOperacion()` y `m.valor()`.
- En los tests: `Muestra.resumen().movimientos()`; `@TempDir Path dir` de JUnit (agregalo como campo del test).

## `clave(m)`
1. `return m.idOperacion() + "|" + m.valor().toPlainString();`

## `leer()` (pasos en orden)
1. `Set<String> out = new LinkedHashSet<>();`
2. `if (!Files.exists(archivo)) return out;`
3. `for (String linea : Files.readAllLines(archivo, StandardCharsets.UTF_8)) { if (!linea.isBlank()) out.add(linea.trim()); }`
4. `return out;`

## `agregar(claves)` (pasos en orden)
1. `Set<String> todas = leer();`
2. `todas.addAll(claves);`
3. `if (archivo.getParent() != null) Files.createDirectories(archivo.getParent());`
4. `Path tmp = archivo.resolveSibling(archivo.getFileName() + ".tmp");`
5. `StringBuilder sb = new StringBuilder(); for (String c : todas) sb.append(c).append('\n');`
6. `Files.writeString(tmp, sb.toString(), StandardCharsets.UTF_8);`
7. `Files.move(tmp, archivo, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);`

## `nuevos(clasificados)` (pasos en orden)
1. `Set<String> ya = leer();`
2. `List<Clasificado> out = new ArrayList<>();`
3. `for (Clasificado c : clasificados) { if (!ya.contains(clave(c.movimiento()))) out.add(c); }`
4. `return out;`

## Tests (copiá estas aserciones tal cual)
- `claveYAgregar`:
  - `assertEquals("180700000002|150000.00", Registro.clave(Muestra.resumen().movimientos().get(1)));`
  - `Registro r = new Registro(dir.resolve("sub/importados.txt"));`
  - `r.agregar(List.of("a|1.00", "b|2.00"));`
  - `r.agregar(List.of("b|2.00", "c|3.00"));`
  - `assertEquals("a|1.00\nb|2.00\nc|3.00\n", Files.readString(dir.resolve("sub/importados.txt")));`
- `nuevos`:
  - `Registro r = new Registro(dir.resolve("importados.txt"));`
  - `List<Clasificado> cs = new Clasificador(ReglasRepo.porDefecto(), Muestra.TITULAR).clasificarTodos(Muestra.resumen().movimientos());`
  - `r.agregar(List.of(Registro.clave(cs.get(0).movimiento())));`
  - `assertEquals(cs.size() - 1, r.nuevos(cs).size());`
- Los dos tests declaran `throws IOException`.
