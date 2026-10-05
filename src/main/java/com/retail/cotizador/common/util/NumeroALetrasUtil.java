package com.retail.cotizador.common.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class NumeroALetrasUtil {

    private static final String[] UNIDADES = {
        "", "UN ", "DOS ", "TRES ", "CUATRO ", "CINCO ", "SEIS ", "SIETE ", "OCHO ", "NUEVE "
    };
    private static final String[] DECENAS = {
        "DIEZ ", "ONCE ", "DOCE ", "TRECE ", "CATORCE ", "QUINCE ", "DIECISEIS ",
        "DIECISIETE ", "DIECIOCHO ", "DIECINUEVE ", "VEINTE ", "VEINTIUN ", "VEINTIDOS ",
        "VEINTITRES ", "VEINTICUATRO ", "VEINTICINCO ", "VEINTISEIS ", "VEINTISIETE ",
        "VEINTIOCHO ", "VEINTINUEVE "
    };
    private static final String[] DIEZ_DECENAS = {
        "", "DIEZ ", "VEINTE ", "TREINTA ", "CUARENTA ", "CINCUENTA ", "SESENTA ",
        "SETENTA ", "OCHENTA ", "NOVENTA "
    };
    private static final String[] CENTENAS = {
        "", "CIENTO ", "DOSCIENTOS ", "TRESCIENTOS ", "CUATROCIENTOS ", "QUINIENTOS ",
        "SEISCIENTOS ", "SETECIENTOS ", "OCHOCIENTOS ", "NOVECIENTOS "
    };

    public static String convertir(BigDecimal cantidad) {
        if (cantidad == null) return "CERO DOLARES CON 00/100";
        BigDecimal cantidadPositiva = cantidad.setScale(2, RoundingMode.HALF_UP).abs();
        long parteEntera = cantidadPositiva.longValue();
        int centavos = cantidadPositiva.remainder(BigDecimal.ONE)
            .multiply(new BigDecimal(100)).intValue();

        String letrasParteEntera = convertirNumero(parteEntera);
        if (parteEntera == 0) letrasParteEntera = "CERO ";
        if (parteEntera == 100) letrasParteEntera = "CIEN ";

        return (letrasParteEntera.trim() + " DOLARES CON " +
            String.format("%02d", centavos) + "/100").toUpperCase();
    }

    private static String convertirNumero(long n) {
        if (n == 0) return "";
        if (n < 10) return UNIDADES[(int) n];
        if (n < 30) return DECENAS[(int) (n - 10)];
        if (n < 100) {
            int d = (int) (n / 10);
            int u = (int) (n % 10);
            return DIEZ_DECENAS[d] + (u > 0 ? "Y " + UNIDADES[u] : "");
        }
        if (n < 1000) {
            int c = (int) (n / 100);
            long resto = n % 100;
            if (n == 100) return "CIEN ";
            return CENTENAS[c] + convertirNumero(resto);
        }
        if (n < 1000000) {
            long miles = n / 1000;
            long resto = n % 1000;
            String textoMiles = (miles == 1) ? "MIL " : convertirNumero(miles) + "MIL ";
            return textoMiles + convertirNumero(resto);
        }
        long millones = n / 1000000;
        long resto = n % 1000000;
        String textoMillones = (millones == 1) ? "UN MILLON " : convertirNumero(millones) + "MILLONES ";
        return textoMillones + convertirNumero(resto);
    }
}
