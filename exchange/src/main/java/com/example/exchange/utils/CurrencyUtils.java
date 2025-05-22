package com.example.exchange.utils;

import java.util.Locale;

public class CurrencyUtils {
    public static String normalizeCurrencyCode(String currencyCode) {
        if (currencyCode == null || currencyCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Currency code cannot be null or empty");
        }
        return currencyCode.trim().toUpperCase(Locale.ROOT);
    }
}
