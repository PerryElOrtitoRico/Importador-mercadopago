```java
// src/main/java/com/finanzas/service/LectorResumen.java
    public static Resumen leer(List<String> lineas) {
        return new Resumen(leerEncabezado(lineas), leerMovimientos(lineas));
    }

    /** Grupos de líneas no vacías separados por una o más líneas vacías (o solo con espacios). */
    static List<List<String>> bloques(List<String> lineas) {
        List<List<String>> out = new ArrayList<>();
        List<String> actual = new ArrayList<>();
        for (String linea : lineas) {
            if (linea.isBlank()) {
                if (!actual.isEmpty()) {
                    out.add(actual);
                    actual = new ArrayList<>();
                }
            } else {
                actual.add(linea);
            }
        }
        if (!actual.isEmpty()) out.add(actual);
        return out;
    }

    /** Un movimiento por cada bloque que tiene una fila; los bloques sin fila (títulos, páginas) se saltean. */
    public static List<Movimiento> leerMovimientos(List<String> lineas) {
        List<Movimiento> out = new ArrayList<>();
        for (List<String> bloque : bloques(lineas)) {
            int fila = -1;
            for (int i = 0; i < bloque.size(); i++) {
                if (FILA.matcher(bloque.get(i)).matches()) {
                    if (fila >= 0) throw new IllegalStateException("Bloque con dos movimientos: " + bloque);
                    fila = i;
                }
            }
            if (fila < 0) continue;
            Matcher m = FILA.matcher(bloque.get(fila));
            m.matches();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < fila; i++) sb.append(bloque.get(i).trim()).append(' ');
            sb.append(m.group(2).trim()).append(' ');
            for (int i = fila + 1; i < bloque.size(); i++) sb.append(bloque.get(i).trim()).append(' ');
            String descripcion = sb.toString().replaceAll("\\s+", " ").trim();
            out.add(new Movimiento(LocalDate.parse(m.group(1), FECHA), descripcion, m.group(3), Importes.parsear(m.group(4)), Importes.parsear(m.group(5))));
        }
        return out;
    }
```

```java
// src/test/java/com/finanzas/service/LectorMovimientosTest.java
    @Test
    void bloques() {
        List<List<String>> b = LectorResumen.bloques(List.of("a", "", "  ", "b", "c", ""));
        assertEquals(List.of(List.of("a"), List.of("b", "c")), b);
    }

    @Test
    void movimientos() {
        List<Movimiento> ms = LectorResumen.leerMovimientos(Muestra.lineas());
        assertEquals(14, ms.size());
        assertEquals(LocalDate.of(2026, 10, 1), ms.get(0).fecha());
        assertEquals("Transferencia recibida PEREZ JUAN CARLOS", ms.get(1).descripcion());
        assertEquals("180700000002", ms.get(1).idOperacion());
        assertEquals(new BigDecimal("-0.01"), ms.get(13).valor());
    }
```

<!-- 72 s · entrada 3291 · salida 764 · finish_reason stop -->
