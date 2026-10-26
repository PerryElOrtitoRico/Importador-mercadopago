package com.finanzas.service;

import com.finanzas.model.Resumen;

import java.util.List;

/**
 * Resumen inventado con el mismo formato que deja {@code pdftotext -layout} con un resumen real de Mercado Pago.
 * Nombres, CVU e importes son de mentira. Cuadra: saldo inicial 2580.25, final 86585.45,
 * entradas 166401.38, salidas -82396.18, 14 movimientos (2 son reservas).
 */
public final class Muestra {

    public static final String TITULAR = "Ana Laura Gomez";

    public static final String TEXTO = """
                                                                  RESUMEN DE CUENTA EN PESOS
                                                                              Ana Laura Gomez
                                             CVU: 0000003100000000000000 CUIT/ CUIL: 27000000000
                                                               Periodo: Del 1 al 6 de octubre de 2026



                                       Entradas: $ 166.401,38
Saldo inicial: $ 2.580,25                                                      Saldo final: $ 86.585,45
                                       Salidas: $ -82.396,18




                                        DETALLE DE MOVIMIENTOS

                                              ID de la
Fecha         Descripción                                                    Valor                Saldo
                                              operación


01-10-2026    Rendimientos                    1750900000001     $         1,38    $     2.581,63


              Transferencia recibida
01-10-2026                                    180700000002      $   150.000,00    $   152.581,63
              PEREZ JUAN CARLOS


              Transferencia enviada Ana
01-10-2026                                    180800000003      $   -20.000,00    $   132.581,63
              Laura Gomez


              Pago con QR
02-10-2026                                    181900000004      $    -2.900,00    $   129.681,63
              Autoservicio7030


02-10-2026    Pago con QR Carrefour           182000000005      $    -1.400,00    $   128.281,63


              Devolución de dinero
02-10-2026                                    182000000005      $     1.400,00    $   129.681,63
              Carrefour






                                                                                                      1/10
                                              ID de la
Fecha        Descripción                      operación             Valor          Saldo


              Transferencia recibida
03-10-2026    GOMEZ ANA                       181500000006      $     5.000,00    $   134.681,63
              LAURA


03-10-2026    Dinero reservado HOGAR          1752000000007     $   -30.000,00    $   104.681,63


04-10-2026    Dinero retirado HOGAR           1752100000008     $    10.000,00    $   114.681,63


04-10-2026    Transferencia enviada           184900000009      $    -3.500,00    $   111.181,63


05-10-2026    Pago con QR ECOGAS              182600000010      $   -12.345,67    $    98.835,96


05-10-2026    Retiro de dinero Carrefour      182700000011      $    -8.000,00    $    90.835,96


              Transferencia enviada
05-10-2026                                    182000000012      $    -4.250,50    $    86.585,46
              Verdulería Doña Rosa


06-10-2026    Pago con QR La Esquina          182700000013      $        -0,01    $    86.585,45




Fecha de generación: 07-10-2026

Texto legal de pie de página.

                                                                                                                                   10/10

""";

    private Muestra() {
    }

    public static List<String> lineas() {
        return TEXTO.lines().toList();
    }

    /** El resumen leído a mano, para los tests que no dependen de LectorResumen. */
    public static Resumen resumen() {
        return Esperado.resumen();
    }
}
