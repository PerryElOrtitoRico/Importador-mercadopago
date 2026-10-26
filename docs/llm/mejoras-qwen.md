# Mejoras en el uso de qwen y Gemma — FINANZAS

Una entrada por pedido: qué se pidió, qué salió, por qué y qué se cambia en el pedido siguiente. Las reglas confirmadas pasan a `../../../qwen-aprendizajes.md`.

## T01 — importes y textos (Gemma)
- Intento 1: 44 s, 2086 tokens de entrada, 785 de salida, `stop`. Ocultos 3/3 y tests propios en verde, firmas iguales.
- Subtarea mecánica con pasos de código: confirma Gemma para este tipo de tarea. Sin cambios para el pedido siguiente.

## T02a — encabezado (Gemma)
- Intento 1: 74 s, 2787 entrada, 1268 salida, `stop`. Ocultos 2/2. Gemma reescribió la constante `FILA` del esqueleto (de otra subtarea) y le sacó una barra (`\d` en vez de `\\d`): no compilaba. Lo restauró el orquestador copiando la línea del esqueleto (sin llamar al modelo; no cuenta como corrección).
- Cambiar: en la tarea, marcar las constantes del esqueleto como "no las toques" o decir que no cambie nada fuera de los métodos pedidos. Controlar con `git diff` las líneas fuera del método.

## T03 — verificador (Gemma)
- Intento 1: 53 s, 2106 entrada, 918 salida, `stop`. Ocultos 3/3, propios y firmas bien. Sin cambios.

## T04a — tipo y contraparte (Gemma)
- Intento 1: 75 s, 2628 entrada, 1223 salida, `stop`. Ocultos 3/3, firmas iguales. Sin cambios.

## T06 — exportador CSV (Gemma)
- Intento 1: 54 s, 2139 entrada, 878 salida, `stop`. Ocultos 4/4, firmas iguales. Sin cambios.

## T02b — movimientos (qwen, --metodos)
- Intento 1: 72 s, 3291 entrada, 764 salida, `stop`. Ocultos 4/4, firmas iguales. Sin cambios.

## T04b — clasificar (qwen, --metodos)
- Intento 1: 79 s, 2317 entrada, 875 salida, `stop`. Ocultos 3/3, firmas iguales. Sin cambios.

## T05 — registro (qwen)
- Intento 1: 95 s, 2211 entrada, 1008 salida, `stop`. Ocultos 4/4, firmas iguales. Sin cambios.

## T07 — reglas y pdftotext (qwen, --metodos)
- Intento 1: ~100 s, `stop`. Ocultos 3/3, código de producción bien al primer intento. Sus tests propios fallaban de dos maneras: (1) el injerto conserva la declaración del esqueleto, que no tenía `throws IOException`, y qwen no la agregó (el error de compilación venía del esqueleto de test, no del modelo); (2) su limpieza `Files.walk(dir).sorted()` borra el padre antes que los hijos (`DirectoryNotEmptyException`) y no usó `@TempDir` como pedía la tarea. Lo arregló el orquestador (`throws IOException` en los dos tests y `Comparator.reverseOrder()`), sin segundo pedido.
- Cambiar: el esqueleto de tests que usan archivos tiene que declarar `throws IOException`; pedir `@TempDir` en la tarea con el campo ya escrito en el esqueleto.
