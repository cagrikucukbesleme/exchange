package com.example.exchange.controller;

import com.example.exchange.model.response.CurrentExchangeRateResponse;
import com.example.exchange.service.ExchangeRateService;
import com.example.exchange.validate.CurrencyValidator;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import static com.example.exchange.utils.CurrencyUtils.normalizeCurrencyCode;
import static com.example.exchange.validate.CurrencyValidator.validateCurrencies;


@RestController
@RequestMapping("/v1/exchange-rate")
public class ExchangeRateController {
    private final ExchangeRateService exchangeRateService;


    public ExchangeRateController(ExchangeRateService exchangeRateService) {
        this.exchangeRateService = exchangeRateService;
    }

    @GetMapping
    @Operation(summary = "Exchange Rate", description = "Returns Exchange Rate between 2 currency")
    public Mono<CurrentExchangeRateResponse> getRate(@RequestParam String from, @RequestParam String to ) {
        try {
            from= normalizeCurrencyCode(from);
            to= normalizeCurrencyCode(to);
            validateCurrencies(from, to);
        }catch (CurrencyValidator.CurrencyValidationException e) {
            throw new CurrencyValidator.CurrencyValidationException("Error: " + e.getMessage());
        }
        return exchangeRateService.getExchangeRate(from, to);
    }
}
