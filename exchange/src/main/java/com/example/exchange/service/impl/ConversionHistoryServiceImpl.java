package com.example.exchange.service.impl;

import com.example.exchange.model.ConversionHistoryTransaction;
import com.example.exchange.model.response.CurrencyConversionResponse;
import com.example.exchange.repository.ConversionHistoryTransactionRepository;
import com.example.exchange.service.ConversionHistoryService;
import com.example.exchange.utils.DateUtils;
import org.springframework.stereotype.Service;
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
        List<CurrencyConversionResponse> currencyConversionResponses = conversionHistoryTransactions.stream()
                .map(this::mapToCurrencyConversionResponse)
                .collect(Collectors.toList());

        return Mono.just(currencyConversionResponses);
    }

    @Override
    public Mono<List<CurrencyConversionResponse>> getConversionHistoryByDate(String date) {
        LocalDate localDate= DateUtils.parseStringToLocalDate(date);
        List<ConversionHistoryTransaction> conversionHistoryTransactions = repository.findAll();
        List<CurrencyConversionResponse> currencyConversionResponses= conversionHistoryTransactions.stream()
                .filter(transaction -> transaction.getDate() != null && transaction.getDate().equals(localDate))
                .map(this::mapToCurrencyConversionResponse)
                .collect(Collectors.toList());
        return Mono.just(currencyConversionResponses);
    }

    private CurrencyConversionResponse mapToCurrencyConversionResponse(ConversionHistoryTransaction transaction) {
        CurrencyConversionResponse currencyConversionResponse= new CurrencyConversionResponse();
        currencyConversionResponse.setSourceCurrency(transaction.getSourceCurrency());
        currencyConversionResponse.setTransactionId(transaction.getId());
        currencyConversionResponse.setTargetCurrency(transaction.getTargetCurrency());
        currencyConversionResponse.setConvertedAmount(transaction.getConvertedAmount());
        return currencyConversionResponse;
    }

}
