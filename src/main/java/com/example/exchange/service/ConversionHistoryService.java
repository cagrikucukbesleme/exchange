package com.example.exchange.service;

import com.example.exchange.model.response.CurrencyConversionResponse;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

public interface ConversionHistoryService {

    Mono<List<CurrencyConversionResponse>> getAllConversionHistory();

    Mono<List<CurrencyConversionResponse>> getConversionHistoryByDate(LocalDate date);
}
