package com.example.exchange.service.impl;

import com.example.exchange.constant.Constants;
import com.example.exchange.model.response.CurrentExchangeRateResponse;
import com.example.exchange.model.response.ExchangeRateApiCallResponse;
import com.example.exchange.service.ExchangeRateService;
import com.example.exchange.validate.CurrencyValidator;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static com.example.exchange.constant.Constants.*;
import static com.example.exchange.utils.CurrencyUtils.normalizeCurrencyCode;
import static com.example.exchange.validate.CurrencyValidator.validateCurrencies;

@Service
public class ExchangeRateServiceImpl implements ExchangeRateService {

    private final WebClient webClient;

    public ExchangeRateServiceImpl(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl(Constants.API_FRANKFURTER_DEV_V1).build();
    }

    @Override
    public Mono<CurrentExchangeRateResponse> getExchangeRate(String fromCurrency, String toCurrency) {
        try {
            fromCurrency = normalizeCurrencyCode(fromCurrency);
            toCurrency = normalizeCurrencyCode(toCurrency);
            validateCurrencies(fromCurrency, toCurrency);
        }catch (CurrencyValidator.CurrencyValidationException e) {
            throw new CurrencyValidator.CurrencyValidationException(ERROR + e.getMessage());
        }

        Mono<ExchangeRateApiCallResponse> currentExchangeRateResponseMono = getExchangeRateFromExternalResource(
                fromCurrency, toCurrency);

        ExchangeRateApiCallResponse exchangeRateApiCallResponse = currentExchangeRateResponseMono.block();

        assert exchangeRateApiCallResponse != null;

        return Mono.just(new CurrentExchangeRateResponse(fromCurrency,toCurrency,
                exchangeRateApiCallResponse.getRates().get(toCurrency)));
    }

    private Mono<ExchangeRateApiCallResponse> getExchangeRateFromExternalResource(String fromCurrency, String toCurrency) {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(LATEST)
                        .queryParam(BASE, fromCurrency)
                        .queryParam(SYMBOLS, toCurrency)
                        .build())
                .retrieve()
                .bodyToMono(ExchangeRateApiCallResponse.class);
    }
}
