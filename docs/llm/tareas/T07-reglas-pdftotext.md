<!-- contrato: 1,7 | archivos: src/main/java/com/finanzas/service/ReglasRepo.java, src/main/java/com/finanzas/service/Importador.java, src/test/java/com/finanzas/service/ReglasRepoTest.java -->
# T07 — Reglas en JSON y pdftotext

## Objetivo
Completar `ReglasRepo.cargar`, **solo** `Importador.textoDePdf` (los otros métodos de `Importador` los completa otra subtarea) y los tests de `ReglasRepoTest`.

## Código que ya existe
- En `ReglasRepo`: `GSON` (Gson con formato lindo), `LISTA` (el `Type` de `List<Regla>`), `porDefecto()`.

## `ReglasRepo.cargar(archivo)` (pasos en orden)
1. `if (!Files.exists(archivo)) {`
2. `    if (archivo.getParent() != null) Files.createDirectories(archivo.getParent());`
3. `    List<Regla> porDefecto = porDefecto();`
4. `    Files.writeString(archivo, GSON.toJson(porDefecto, LISTA), StandardCharsets.UTF_8);`
5. `    return porDefecto;`
6. `}`
7. `String json = Files.readString(archivo, StandardCharsets.UTF_8);`
8. `try {`
9. `    List<Regla> reglas = GSON.fromJson(json, LISTA);`
10. `    if (reglas == null) throw new JsonParseException("archivo vacío");`
11. `    return List.copyOf(reglas);`
12. `} catch (JsonParseException e) {`
13. `    throw new IllegalStateException("No se pudo leer " + archivo + ": " + e.getMessage(), e);`
14. `}`

## `Importador.textoDePdf(pdf)` (cuerpo entero)
```java
        Process p = new ProcessBuilder("pdftotext", "-layout", "-enc", "UTF-8", pdf.toString(), "-")
                .redirectError(ProcessBuilder.Redirect.DISCARD)
                .start();
        String texto = new String(p.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        int codigo = p.waitFor();
        if (codigo != 0) {
            throw new IOException("pdftotext terminó con código " + codigo + " para " + pdf);
        }
        return texto.lines().toList();
```

## Tests (copiá estas aserciones tal cual; `@TempDir Path dir` como campo; los tests declaran `throws IOException`)
- `creaYLee`:
  - `Path archivo = dir.resolve("config/reglas.json");`
  - `assertEquals(ReglasRepo.porDefecto(), ReglasRepo.cargar(archivo));`
  - `assertTrue(Files.exists(archivo));`
  - `Files.writeString(archivo, "[{\"contiene\": \"Kiosco\", \"categoria\": \"Golosinas\"}]");`
  - `assertEquals(List.of(new Regla("Kiosco", "Golosinas")), ReglasRepo.cargar(archivo));`
- `mal`:
  - `Path archivo = dir.resolve("reglas.json");`
  - `Files.writeString(archivo, "[{");`
  - `assertThrows(IllegalStateException.class, () -> ReglasRepo.cargar(archivo));`
  - `assertEquals("[{", Files.readString(archivo));`
