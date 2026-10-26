<!-- contrato: 1,8 | archivos: src/main/java/com/finanzas/cli/Main.java, src/test/java/com/finanzas/cli/MainTest.java -->
# T08b — Línea de comandos

## Objetivo
Completar `ejecutar` e `informe` de `Main`, y los tests de `MainTest`.

## Código que ya existe
- `USO` (texto de uso).
- `Importador.textoDePdf(Path)` → `List<String>` (puede tirar `IOException`, `InterruptedException`); `Importador.importar(lineas, reglas, registro, carpetaSalida, simular)` → `Importador.Resultado`.
- `Importador.Resultado(int leidos, int reservas, int repetidos, List<Clasificado> exportados, List<String> errores, Path csv)`: se lee con `r.leidos()`, `r.exportados()`, `r.csv()`…
- `ReglasRepo.cargar(Path)` → `List<Regla>`. `Importes.aCsv(BigDecimal)` → `"150000.00"`.
- `Clasificado` se lee con `c.paraRevisar()`, `c.motivo()`, `c.movimiento().fecha()`, `c.movimiento().valor()`, `c.movimiento().descripcion()`.
- En los tests: `Muestra.TEXTO` (texto de un resumen inventado: 14 movimientos, 12 nuevos, 5 para revisar).

## `ejecutar(args, archivoReglas, registro, out, err)` (pasos en orden)
1. `if (args.length < 2 || !args[0].equals("importar")) { err.println(USO); return 2; }`
2. `Path archivo = Path.of(args[1]); Path salida = archivo.toAbsolutePath().getParent(); boolean simular = false;`
3. `for (int i = 2; i < args.length; i++) {`
4. `    if (args[i].equals("--simular")) { simular = true; }`
5. `    else if (args[i].equals("--salida") && i + 1 < args.length) { i++; salida = Path.of(args[i]); }`
6. `    else { err.println(USO); return 2; }`
7. `}`
8. `try {`
9. `    List<String> lineas = archivo.toString().endsWith(".txt") ? Files.readAllLines(archivo, StandardCharsets.UTF_8) : Importador.textoDePdf(archivo);`
10. `    List<Regla> reglas = ReglasRepo.cargar(archivoReglas);`
11. `    Importador.Resultado r = Importador.importar(lineas, reglas, registro, salida, simular);`
12. `    if (!r.errores().isEmpty()) { err.println("El resumen no cuadra; no se importó nada:"); for (String e : r.errores()) err.println("  - " + e); return 1; }`
13. `    out.print(informe(r, simular));`
14. `    return 0;`
15. `} catch (Exception e) { err.println("Error: " + e.getMessage()); return 1; }`

## `informe(r, simular)` (pasos en orden)
1. `long revisar = r.exportados().stream().filter(Clasificado::paraRevisar).count();`
2. `StringBuilder sb = new StringBuilder();`
3. `sb.append("Movimientos leídos: ").append(r.leidos()).append('\n');`
4. `sb.append("Reservas ignoradas: ").append(r.reservas()).append('\n');`
5. `sb.append("Ya importados antes: ").append(r.repetidos()).append('\n');`
6. `sb.append("Nuevos: ").append(r.exportados().size()).append(" (").append(revisar).append(" para revisar)").append('\n');`
7. `if (simular) sb.append("CSV: no se escribió (simulación)\n"); else if (r.csv() == null) sb.append("CSV: no hay movimientos nuevos\n"); else sb.append("CSV: ").append(r.csv()).append('\n');`
8. `if (revisar > 0) {`
9. `    sb.append("Para revisar:\n");`
10. `    for (Clasificado c : r.exportados()) { if (c.paraRevisar()) sb.append("  ").append(c.movimiento().fecha()).append("  ").append(Importes.aCsv(c.movimiento().valor())).append("  ").append(c.movimiento().descripcion()).append("  (").append(c.motivo()).append(")\n"); }`
11. `}`
12. `return sb.toString();`

## Tests (copiá estas aserciones tal cual; `@TempDir Path dir` como campo; los tests declaran `throws Exception`)
- `importa`:
  - `Path txt = dir.resolve("resumen.txt");`
  - `Files.writeString(txt, Muestra.TEXTO);`
  - `ByteArrayOutputStream salida = new ByteArrayOutputStream();`
  - `ByteArrayOutputStream errores = new ByteArrayOutputStream();`
  - `int codigo = Main.ejecutar(new String[] {"importar", txt.toString(), "--salida", dir.resolve("csv").toString()}, dir.resolve("reglas.json"), new Registro(dir.resolve("importados.txt")), new PrintStream(salida, true, StandardCharsets.UTF_8), new PrintStream(errores, true, StandardCharsets.UTF_8));`
  - `assertEquals(0, codigo);`
  - `assertTrue(salida.toString(StandardCharsets.UTF_8).contains("Nuevos: 12 (5 para revisar)"));`
  - `assertTrue(Files.exists(dir.resolve("csv/mercadopago_2026-10-01_2026-10-06.csv")));`
- `argumentosMal`:
  - `ByteArrayOutputStream errores = new ByteArrayOutputStream();`
  - `int codigo = Main.ejecutar(new String[] {"borrar"}, dir.resolve("reglas.json"), new Registro(dir.resolve("importados.txt")), new PrintStream(new ByteArrayOutputStream()), new PrintStream(errores, true, StandardCharsets.UTF_8));`
  - `assertEquals(2, codigo);`
  - `assertTrue(errores.toString(StandardCharsets.UTF_8).contains("Uso: finanzas importar"));`
