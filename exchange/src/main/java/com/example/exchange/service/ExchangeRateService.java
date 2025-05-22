package com.example.exchange.service;

import com.example.exchange.model.response.CurrentExchangeRateResponse;
import reactor.core.publisher.Mono;

public interface ExchangeRateService {

    Mono<CurrentExchangeRateResponse> getExchangeRate(String fromCurrency, String toCurrency);

}
