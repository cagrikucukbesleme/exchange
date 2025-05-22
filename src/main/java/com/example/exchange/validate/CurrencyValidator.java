package com.example.exchange.validate;

import java.util.Currency;
import java.util.Set;
import java.util.stream.Collectors;

public class CurrencyValidator {

    public static class CurrencyValidationException extends IllegalArgumentException {
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
            throw new CurrencyValidationException("Currency code cannot be null or empty");
        }

        String normalizedCode = currencyCode.toUpperCase();

        if (normalizedCode.length() != 3) {
            throw new CurrencyValidationException(
                    String.format("'%s' is not a valid currency code (must be 3 letters)", currencyCode)
            );
        }

        if (!AVAILABLE_CURRENCIES.contains(normalizedCode)) {
            throw new CurrencyValidationException(
                    String.format("'%s' is not a valid ISO currency code. Available currencies: %s",
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
                    String.format("Source and target currencies cannot be same (%s)", fromCurrency)
            );
        }
    }

}
