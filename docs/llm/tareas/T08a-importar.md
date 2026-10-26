<!-- contrato: 1,5,6,8 | archivos: src/main/java/com/finanzas/service/Importador.java, src/test/java/com/finanzas/service/ImportadorTest.java -->
# T08a — Importar

## Objetivo
Completar **solo** `nombreCsv` e `importar` de `Importador`, y el test de `ImportadorTest`. `textoDePdf` ya está hecho.

## Código que ya existe
- `Importador.Resultado(int leidos, int reservas, int repetidos, List<Clasificado> exportados, List<String> errores, Path csv)`.
- `LectorResumen.leer(List<String>)` → `Resumen`; `Verificador.verificar(Resumen)` → `List<String>` errores.
- `new Clasificador(reglas, titular).clasificarTodos(movimientos)` → `List<Clasificado>` sin reservas.
- `registro.nuevos(List<Clasificado>)`, `registro.agregar(Collection<String>)`, `Registro.clave(Movimiento)`.
- `ExportadorCsv.csv(List<Clasificado>)` → texto del CSV.
- En los tests: `Muestra.lineas()` (14 movimientos, 2 reservas, del 2026-10-01 al 2026-10-06), `ReglasRepo.porDefecto()`.

## `nombreCsv(resumen)` (pasos en orden)
1. `List<Movimiento> ms = resumen.movimientos();`
2. `return "mercadopago_" + ms.get(0).fecha() + "_" + ms.get(ms.size() - 1).fecha() + ".csv";`
(agregá `import com.finanzas.model.Movimiento;`)

## `importar(lineas, reglas, registro, carpetaSalida, simular)` (pasos en orden)
1. `Resumen r = LectorResumen.leer(lineas);`
2. `int leidos = r.movimientos().size();`
3. `List<String> errores = Verificador.verificar(r);`
4. `if (!errores.isEmpty()) return new Resultado(leidos, 0, 0, List.of(), errores, null);`
5. `List<Clasificado> clasificados = new Clasificador(reglas, r.encabezado().titular()).clasificarTodos(r.movimientos());`
6. `int reservas = leidos - clasificados.size();`
7. `List<Clasificado> nuevos = registro.nuevos(clasificados);`
8. `int repetidos = clasificados.size() - nuevos.size();`
9. `if (nuevos.isEmpty() || simular) return new Resultado(leidos, reservas, repetidos, nuevos, List.of(), null);`
10. `Files.createDirectories(carpetaSalida);`
11. `String nombre = nombreCsv(r); Path csv = carpetaSalida.resolve(nombre); int n = 2;`
12. `while (Files.exists(csv)) { csv = carpetaSalida.resolve(nombre.replace(".csv", "_" + n + ".csv")); n++; }`
13. `Files.writeString(csv, ExportadorCsv.csv(nuevos), StandardCharsets.UTF_8);`
14. `registro.agregar(nuevos.stream().map(c -> Registro.clave(c.movimiento())).toList());`
15. `return new Resultado(leidos, reservas, repetidos, nuevos, List.of(), csv);`

## Test (copiá estas aserciones tal cual; `@TempDir Path dir` como campo; declara `throws IOException`)
- `importaYNoRepite`:
  - `Registro registro = new Registro(dir.resolve("importados.txt"));`
  - `Importador.Resultado r1 = Importador.importar(Muestra.lineas(), ReglasRepo.porDefecto(), registro, dir.resolve("salida"), false);`
  - `assertEquals(14, r1.leidos());`
  - `assertEquals(2, r1.reservas());`
  - `assertEquals(12, r1.exportados().size());`
  - `assertEquals(dir.resolve("salida/mercadopago_2026-10-01_2026-10-06.csv"), r1.csv());`
  - `assertEquals(13, Files.readAllLines(r1.csv()).size());`
  - `Importador.Resultado r2 = Importador.importar(Muestra.lineas(), ReglasRepo.porDefecto(), registro, dir.resolve("salida"), false);`
  - `assertEquals(12, r2.repetidos());`
  - `assertNull(r2.csv());`
