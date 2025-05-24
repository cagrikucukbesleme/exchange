package com.example.exchange.utils;

import java.util.Locale;

import static com.example.exchange.constant.Constants.CURRENCY_CODE_CANNOT_BE_NULL_OR_EMPTY;

public class CurrencyUtils {

    public static String normalizeCurrencyCode(String currencyCode) {
        if (currencyCode == null || currencyCode.trim().isEmpty()) {
            throw new IllegalArgumentException(CURRENCY_CODE_CANNOT_BE_NULL_OR_EMPTY);
        }
        return currencyCode.trim().toUpperCase(Locale.ROOT);
    }
}
