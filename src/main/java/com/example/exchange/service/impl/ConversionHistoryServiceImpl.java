package com.example.exchange.service.impl;

import com.example.exchange.constant.Constants;
import com.example.exchange.model.ConversionHistoryTransaction;
import com.example.exchange.model.response.CurrencyConversionResponse;
import com.example.exchange.repository.ConversionHistoryTransactionRepository;
import com.example.exchange.service.ConversionHistoryService;
import com.example.exchange.utils.DateUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ConversionHistoryServiceImpl implements ConversionHistoryService {

    private final ConversionHistoryTransactionRepository repository;

    public ConversionHistoryServiceImpl(ConversionHistoryTransactionRepository repository) {
        this.repository = repository;
    }

    @Override
    public Mono<List<CurrencyConversionResponse>> getAllConversionHistory() {
        List<ConversionHistoryTransaction> conversionHistoryTransactions = repository.findAll();
        checkDbResponse(conversionHistoryTransactions, Constants.NO_CONVERSION_HISTORY_RECORDS_WERE_FOUND);

        return getResponseAsMonoList(conversionHistoryTransactions);
    }


    @Override
    public Mono<List<CurrencyConversionResponse>> getConversionHistoryByDate(String date) {
        LocalDate localDate= DateUtils.parseStringToLocalDate(date);
        List<ConversionHistoryTransaction> conversionHistoryTransactions = repository.findByDate(localDate);

        checkDbResponse(conversionHistoryTransactions,
                Constants.NO_CONVERSION_HISTORY_RECORDS_WERE_FOUND_FOR_THE_SPECIFIED_DATE + localDate);

        return getResponseAsMonoList(conversionHistoryTransactions);
    }


    private static Mono<List<CurrencyConversionResponse>> getResponseAsMonoList(
            List<ConversionHistoryTransaction> conversionHistoryTransactions) {
        List<CurrencyConversionResponse> currencyConversionResponses = conversionHistoryTransactions.stream()
                .map(transaction -> new CurrencyConversionResponse(
                        transaction.getId(),
                        transaction.getConvertedAmount(),
                        transaction.getSourceCurrency(),
                        transaction.getTargetCurrency()))
                .collect(Collectors.toList());

        return Mono.just(currencyConversionResponses);
    }

    private static void checkDbResponse(List<ConversionHistoryTransaction> conversionHistoryTransactions, String errorMessage) {
        if (conversionHistoryTransactions.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    errorMessage);
        }
    }

}
