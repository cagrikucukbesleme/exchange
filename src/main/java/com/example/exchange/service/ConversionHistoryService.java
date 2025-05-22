package com.example.exchange.service;

import com.example.exchange.model.response.CurrencyConversionResponse;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ConversionHistoryService {
    Mono<List<CurrencyConversionResponse>> getAllConversionHistory();

    Mono<List<CurrencyConversionResponse>> getConversionHistoryByDate(String date);
}
