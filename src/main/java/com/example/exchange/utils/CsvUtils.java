package com.example.exchange.utils;

import com.example.exchange.model.request.CurrencyConversionRequest;
import com.example.exchange.validate.CurrencyValidator;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.*;

import static com.example.exchange.constant.Constants.*;
import static com.example.exchange.validate.CurrencyValidator.validateCurrency;

public class CsvUtils {



    public static List<CurrencyConversionRequest> parseCsv(MultipartFile file) throws IOException {
        try (InputStream inputStream = file.getInputStream();
             Reader reader = new InputStreamReader(inputStream);
             CSVParser parser = CSV_FORMAT.parse(reader)) {

            List<CurrencyConversionRequest> requests = new ArrayList<>();
            Set<String> seenCurrencies = new HashSet<>();

            for (CSVRecord record : parser) {
                try {
                    double amount = Double.parseDouble(record.get(AMOUNT).trim());
                    String source = record.get(SOURCE_CURRENCY).trim().toUpperCase();
                    String target = record.get(TARGET_CURRENCY).trim().toUpperCase();

                    if (amount <= 0) {
                        throw new IllegalArgumentException(AMOUNT_MUST_BE_POSITIVE_IN_ROW + record.getRecordNumber());
                    }
                    if (source.length() != 3 || target.length() != 3) {
                        throw new CurrencyValidator.CurrencyValidationException(CURRENCY_CODES_MUST_BE_3_LETTERS_IN_ROW +
                                record.getRecordNumber());
                    }
                    requests.add(new CurrencyConversionRequest(amount,source,target));
                    seenCurrencies.add(source);
                    seenCurrencies.add(target);

                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException(INVALID_NUMBER_FORMAT_IN_ROW + record.getRecordNumber());
                } catch (CurrencyValidator.CurrencyValidationException e) {
                    throw new CurrencyValidator.CurrencyValidationException(ERROR + e.getMessage());
                }
            }

            validateAllCurrencies(seenCurrencies);

            return requests;
        }
    }

    private static void validateAllCurrencies(Set<String> currencies) {
        for (String currency : currencies) {
            try {
                validateCurrency(currency);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException(INVALID_CURRENCY_CODE + currency);
            }
        }
    }
}
