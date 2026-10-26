# FINANZAS programado con qwen y Gemma

Claude (Opus) escribió la especificación (`00-especificacion.md`), los esqueletos, las tareas (`tareas/`) y los tests ocultos (`evaluacion/`). Antes de mandarlas, comprobó que los pasos de cada tarea, copiados tal cual, pasan todos los tests (60/60). Un subagente (Sonnet) lleva el ciclo con los modelos locales.

- `pedir.py <tarea> <qwen|gemma> <intento> [--metodos] [--solo archivo] [--error log] [--razonar low|medium]`: manda el pedido a LM Studio y aplica la respuesta. Pedidos y respuestas quedan en `registros/`.
- `evaluar.sh T0x`: tests propios, firmas contra `evaluacion/firmas/` y tests ocultos de la subtarea.
- `aceptar.sh T0x archivos…`: borra los `// TODO T0x` que quedaron.
- `mejoras-qwen.md`: retrospectiva por pedido.

## Planilla
| Subtarea | Modelo | Ocultos | Correcciones | Quién |
|---|---|---|---|---|
