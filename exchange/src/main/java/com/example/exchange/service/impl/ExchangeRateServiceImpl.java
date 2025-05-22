package com.example.exchange.service.impl;

import com.example.exchange.model.response.CurrentExchangeRateResponse;
import com.example.exchange.model.response.ExchangeRateApiCallResponse;
import com.example.exchange.service.ExchangeRateService;
import com.example.exchange.validate.CurrencyValidator;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static com.example.exchange.utils.CurrencyUtils.normalizeCurrencyCode;
import static com.example.exchange.validate.CurrencyValidator.validateCurrencies;

@Service
public class ExchangeRateServiceImpl implements ExchangeRateService {

    private final WebClient webClient;

    public ExchangeRateServiceImpl(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://api.frankfurter.dev/v1").build();
    }

    @Override
    public Mono<CurrentExchangeRateResponse> getExchangeRate(String fromCurrency, String toCurrency) {
        try {
            fromCurrency = normalizeCurrencyCode(fromCurrency);
            toCurrency = normalizeCurrencyCode(toCurrency);
            validateCurrencies(fromCurrency, toCurrency);
        }catch (CurrencyValidator.CurrencyValidationException e) {
            throw new CurrencyValidator.CurrencyValidationException("Error:  " + e.getMessage());
        }

        Mono<ExchangeRateApiCallResponse> currentExchangeRateResponseMono = getExchangeRateFromExternalResource(
                fromCurrency, toCurrency);

        ExchangeRateApiCallResponse exchangeRateApiCallResponse = currentExchangeRateResponseMono.block();

        assert exchangeRateApiCallResponse != null;

        return Mono.just(getCurrentExchangeRateResponse(fromCurrency, toCurrency, exchangeRateApiCallResponse));
    }

    private Mono<ExchangeRateApiCallResponse> getExchangeRateFromExternalResource(String fromCurrency, String toCurrency) {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/latest")
                        .queryParam("base", fromCurrency)
                        .queryParam("symbols", toCurrency)
                        .build())
                .retrieve()
                .bodyToMono(ExchangeRateApiCallResponse.class);
    }

    private static CurrentExchangeRateResponse getCurrentExchangeRateResponse(String fromCurrency, String toCurrency,
                                                                              ExchangeRateApiCallResponse exchangeRateApiCallResponse) {
        CurrentExchangeRateResponse currentExchangeRateResponse= new CurrentExchangeRateResponse();
        Double rate = exchangeRateApiCallResponse.getRates().get(toCurrency);
        currentExchangeRateResponse.setRate(rate);
        currentExchangeRateResponse.setFrom(fromCurrency);
        currentExchangeRateResponse.setTo(toCurrency);
        return currentExchangeRateResponse;
    }
}
