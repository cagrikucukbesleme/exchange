package com.example.exchange.utils;

import com.example.exchange.model.request.CurrencyConversionRequest;
import com.example.exchange.validate.CurrencyValidator;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.*;

import static com.example.exchange.validate.CurrencyValidator.validateCurrency;

public class CsvUtils {

    private static final CSVFormat CSV_FORMAT = CSVFormat.DEFAULT
            .withHeader("amount", "sourceCurrency", "targetCurrency")
            .withSkipHeaderRecord()
            .withTrim()
            .withIgnoreEmptyLines();

    public static List<CurrencyConversionRequest> parseCsv(MultipartFile file) throws IOException {
        try (InputStream inputStream = file.getInputStream();
             Reader reader = new InputStreamReader(inputStream);
             CSVParser parser = CSV_FORMAT.parse(reader)) {

            List<CurrencyConversionRequest> requests = new ArrayList<>();
            Set<String> seenCurrencies = new HashSet<>();

            for (CSVRecord record : parser) {
                try {
                    double amount = Double.parseDouble(record.get("amount").trim());
                    String source = record.get("sourceCurrency").trim().toUpperCase();
                    String target = record.get("targetCurrency").trim().toUpperCase();

                    if (amount <= 0) {
                        throw new IllegalArgumentException("Amount must be positive in row " + record.getRecordNumber());
                    }
                    if (source.length() != 3 || target.length() != 3) {
                        throw new CurrencyValidator.CurrencyValidationException("Currency codes must be 3 letters in row " + record.getRecordNumber());
                    }

                    CurrencyConversionRequest currencyConversionRequest= new CurrencyConversionRequest();
                    currencyConversionRequest.setAmount(amount);
                    currencyConversionRequest.setSourceCurrency(source);
                    currencyConversionRequest.setTargetCurrency(target);
                    requests.add(currencyConversionRequest);
                    seenCurrencies.add(source);
                    seenCurrencies.add(target);

                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Invalid number format in row " + record.getRecordNumber());
                } catch (CurrencyValidator.CurrencyValidationException e) {
                    throw new CurrencyValidator.CurrencyValidationException("Error:  " + e.getMessage());
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
                throw new IllegalArgumentException("Invalid currency code: " + currency);
            }
        }
    }
}
