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
