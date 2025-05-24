package com.example.exchange.constant;

import org.apache.commons.csv.CSVFormat;

public final class Constants {

    private Constants() {}

    public static final String API_FRANKFURTER_DEV_V1 = "https://api.frankfurter.dev/v1";

    public static final String NO_CONVERSION_HISTORY_RECORDS_WERE_FOUND =
            "No conversion history records were found.";

    public static final String INVALID_DATE_FORMAT_PLEASE_FOLLOW_DEFINITIONS_INSTRUCTIONS =
            "Invalid date format, Please follow definitions instructions. Date: ";

    public static final String NO_CONVERSION_HISTORY_RECORDS_WERE_FOUND_FOR_THE_SPECIFIED_DATE =
            "No conversion history records were found for the specified date: ";

    public static final String AMOUNT = "amount";
    public static final String SOURCE_CURRENCY = "sourceCurrency";
    public static final String TARGET_CURRENCY = "targetCurrency";

    public static final CSVFormat CSV_FORMAT = CSVFormat.DEFAULT
            .withHeader(AMOUNT, SOURCE_CURRENCY, TARGET_CURRENCY)
            .withSkipHeaderRecord()
            .withTrim()
            .withIgnoreEmptyLines();
    public static final String AMOUNT_MUST_BE_POSITIVE_IN_ROW = "Amount must be positive in row ";
    public static final String CURRENCY_CODES_MUST_BE_3_LETTERS_IN_ROW = "Currency codes must be 3 letters in row ";
    public static final String INVALID_NUMBER_FORMAT_IN_ROW = "Invalid number format in row ";
    public static final String ERROR = "Error:  ";
    public static final String INVALID_CURRENCY_CODE = "Invalid currency code: ";

    public static final String LATEST = "/latest";
    public static final String BASE = "base";
    public static final String SYMBOLS = "symbols";

    public static final String CURRENCY_CODE_CANNOT_BE_NULL_OR_EMPTY = "Currency code cannot be null or empty";
    public static final String S_IS_NOT_A_VALID_CURRENCY_CODE_MUST_BE_3_LETTERS = "'%s' is not a valid currency code (must be 3 letters)";
    public static final String S_IS_NOT_A_VALID_ISO_CURRENCY_CODE_AVAILABLE_CURRENCIES_S = "'%s' is not a valid ISO currency code. Available currencies: %s";
    public static final String SOURCE_AND_TARGET_CURRENCIES_CANNOT_BE_SAME_S = "Source and target currencies cannot be same (%s)";

}
