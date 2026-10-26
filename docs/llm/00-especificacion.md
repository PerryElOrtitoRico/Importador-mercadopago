# FINANZAS — importador de resúmenes de Mercado Pago (contrato)

Programa de consola en Java 21. Lee el resumen de cuenta de Mercado Pago (PDF pasado a texto con `pdftotext -layout`), comprueba que cuadre, clasifica cada movimiento y escribe un CSV para importar en Actual Budget. Recuerda lo ya importado para no duplicar.

## 1. Reglas generales
- Paquetes: `com.finanzas.model` (datos), `com.finanzas.service` (lógica), `com.finanzas.cli` (línea de comandos).
- **Todo lo de `model` son records** y se leen con `x()`: `m.valor()`, `m.descripcion()`, `c.movimiento().fecha()`, `e.saldoInicial()`. No existen `getValor()` ni `getDescripcion()`.
- `Tipo` es un enum: `Tipo.PAGO_QR.prefijo()` es `"Pago con QR"`, `tipo.esReserva()`.
- Los importes son `BigDecimal` con escala 2. Para comparar importes se usa `compareTo(...) != 0`, nunca `equals`. Para mostrarlos, `toPlainString()`.
- Las salidas son negativas (como en el PDF): un pago de $ 2.900 tiene `valor` = -2900.00. `Encabezado.salidas()` también es negativo.
- Records del modelo:
  - `Movimiento(LocalDate fecha, String descripcion, String idOperacion, BigDecimal valor, BigDecimal saldo)`
  - `Encabezado(String titular, String periodo, BigDecimal saldoInicial, BigDecimal saldoFinal, BigDecimal entradas, BigDecimal salidas)`
  - `Resumen(Encabezado encabezado, List<Movimiento> movimientos)`
  - `Regla(String contiene, String categoria)`
  - `Clasificado(Movimiento movimiento, Tipo tipo, String contraparte, String categoria, boolean paraRevisar, String motivo)`

## 2. Formato del texto del resumen
El encabezado (primera página):
```
                                                                  RESUMEN DE CUENTA EN PESOS
                                                                              Ana Laura Gomez
                                             CVU: 0000003100000000000000 CUIT/ CUIL: 27000000000
                                                               Periodo: Del 1 al 6 de octubre de 2026

                                       Entradas: $ 166.401,38
Saldo inicial: $ 2.580,25                                                      Saldo final: $ 86.585,45
                                       Salidas: $ -82.396,18
```
- Importes: `$`, espacio opcional, signo `-` opcional, miles con `.`, decimales con `,` y siempre dos decimales.
- Movimientos: cada uno es un **bloque** de líneas separado de los demás por líneas vacías. El bloque tiene exactamente una **fila** (la línea con fecha, ID, valor y saldo, ver `LectorResumen.FILA`). La descripción puede estar en la fila, en la línea de arriba, en la de abajo, o repartida en las tres:
```
01-10-2026    Rendimientos                    1750900000001     $         1,38    $     2.581,63


              Transferencia recibida
01-10-2026                                    180700000002      $   150.000,00    $   152.581,63
              PEREZ JUAN CARLOS


              Transferencia recibida
03-10-2026                                    GOMEZ ANA         181500000006      $     5.000,00    $   134.681,63
              LAURA
```
  Descripciones: `"Rendimientos"`, `"Transferencia recibida PEREZ JUAN CARLOS"`, `"Transferencia recibida GOMEZ ANA LAURA"`: las partes de arriba, de la fila y de abajo, unidas con un espacio y sin espacios repetidos.
- Los bloques sin fila (títulos "ID de la / Fecha Descripción…", número de página `1/10`, pie) se saltean.

## 3. Verificación
Si algo no cuadra, el texto se leyó mal: no se importa nada. Los mensajes, exactamente así (importes con `toPlainString()`):
- `El resumen no tiene movimientos` (si no hay movimientos, es el único error).
- `Saldo distinto en <idOperacion>: calculado <x>, resumen <y>`: el saldo calculado (saldo inicial + valores hasta ese movimiento incluido) no es igual al saldo de la fila. El cálculo sigue con el saldo calculado, no con el de la fila.
- `Saldo final distinto: calculado <x>, resumen <y>`
- `Entradas distintas: suma <x>, resumen <y>`: suma de los valores positivos.
- `Salidas distintas: suma <x>, resumen <y>`: suma de los valores negativos (da negativa).

## 4. Clasificación
- Tipo: el primero de `Tipo` (en orden, sin `OTRO`) cuyo prefijo es el comienzo de la descripción; si ninguno, `OTRO`.
- Contraparte: la descripción sin el prefijo del tipo, con `trim()`. `"Transferencia enviada"` sola da `""`.
- **Reservas** (`RESERVA_GUARDADA`, `RESERVA_RETIRADA`): plata que se mueve dentro de la misma cuenta. No se exportan.
- **Transferencias a mi nombre** (recibidas o enviadas, todas las palabras del titular están en la descripción, en cualquier orden y sin importar mayúsculas ni tildes): sin categoría, `paraRevisar` true, motivo `"Transferencia a mi nombre"`.
- Si no: la **primera regla** cuya `contiene` (normalizada) está dentro de la descripción (normalizada) da la categoría; `paraRevisar` false, motivo `""`.
- Sin regla: categoría `""`, `paraRevisar` true, motivo `"Sin regla"`.
- Normalizar (`Textos.normalizar`): minúsculas, sin tildes ni diéresis (`ñ` → `n`), espacios repetidos a uno, sin espacios a los costados.

## 5. Registro de importados
- Archivo de texto, una clave por línea: `idOperacion + "|" + valor.toPlainString()` (`"181588521871|17831.75"`). El ID solo no alcanza: una devolución usa el mismo ID que el pago.
- Se escribe de forma atómica: todo el contenido a `importados.txt.tmp` y después `Files.move(tmp, archivo, REPLACE_EXISTING, ATOMIC_MOVE)`.

## 6. CSV para Actual Budget
- Cabecera `fecha,beneficiario,categoria,importe,notas`; líneas separadas por `\n`, la última también termina en `\n`.
- `fecha`: ISO (`2026-10-02`). `beneficiario`: la contraparte; si está vacía, el prefijo del tipo; si también está vacío (OTRO), la descripción. `categoria`: la del clasificado (puede ser vacía). `importe`: `Importes.aCsv(valor)` (`-1400.00`). `notas`: `"MP " + idOperacion`, y si es para revisar, además `" - REVISAR: " + motivo`.
- Un campo con coma, comillas o salto de línea va entre comillas dobles, y las comillas de adentro se duplican.

## 7. Reglas y pdftotext
- Reglas en `~/.config/finanzas/reglas.json`: una lista JSON de `{"contiene": "...", "categoria": "..."}`. Si no existe se crea con las reglas por defecto. Si existe y está mal escrito, error (no se pisa).
- El PDF se pasa a texto con `pdftotext -layout -enc UTF-8 <pdf> -` (salida estándar).

## 8. Importación y línea de comandos
- Uso: `finanzas importar <resumen.pdf|resumen.txt> [--salida <carpeta>] [--simular]`. Un `.txt` se lee directo (ya es la salida de pdftotext). Sin `--salida`, el CSV va a la carpeta del resumen.
- Nombre del CSV: `mercadopago_<fecha del primer movimiento>_<fecha del último>.csv` (`mercadopago_2026-10-01_2026-10-06.csv`). Si ya existe: `_2`, `_3`… antes de `.csv`.
- Se exportan solo los movimientos nuevos (no reservas, no ya importados). Con `--simular` no se escribe ni el CSV ni el registro.
- Códigos de salida: 0 bien, 1 el resumen no cuadra o falló algo, 2 argumentos mal.
