package com.example.exchange.validate;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Currency;
import java.util.Set;
import java.util.stream.Collectors;

import static com.example.exchange.constant.Constants.*;

public class CurrencyValidator {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public static class CurrencyValidationException extends RuntimeException {
        public CurrencyValidationException(String message) {
            super(message);
        }
    }

    private static final Set<String> AVAILABLE_CURRENCIES =
            Currency.getAvailableCurrencies()
                    .stream()
                    .map(Currency::getCurrencyCode)
                    .collect(Collectors.toSet());

    public static void validateCurrency(String currencyCode) {
        if (currencyCode == null || currencyCode.trim().isEmpty()) {
            throw new CurrencyValidationException(CURRENCY_CODE_CANNOT_BE_NULL_OR_EMPTY);
        }

        String normalizedCode = currencyCode.toUpperCase();

        if (normalizedCode.length() != 3) {
            throw new CurrencyValidationException(
                    String.format(S_IS_NOT_A_VALID_CURRENCY_CODE_MUST_BE_3_LETTERS, currencyCode)
            );
        }

        if (!AVAILABLE_CURRENCIES.contains(normalizedCode)) {
            throw new CurrencyValidationException(
                    String.format(S_IS_NOT_A_VALID_ISO_CURRENCY_CODE_AVAILABLE_CURRENCIES_S,
                            currencyCode,
                            String.join(", ", AVAILABLE_CURRENCIES))
            );
        }
    }

    public static void validateCurrencies(String fromCurrency, String toCurrency) {
        validateCurrency(fromCurrency);
        validateCurrency(toCurrency);

        if (fromCurrency.equalsIgnoreCase(toCurrency)) {
            throw new CurrencyValidationException(
                    String.format(SOURCE_AND_TARGET_CURRENCIES_CANNOT_BE_SAME_S, fromCurrency)
            );
        }
    }

}
