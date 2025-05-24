package com.example.exchange.controller;

import com.example.exchange.model.response.CurrentExchangeRateResponse;
import com.example.exchange.service.ConversionHistoryService;
import com.example.exchange.service.ExchangeRateService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@SpringBootTest
@AllArgsConstructor
@RequiredArgsConstructor
class ExchangeRateControllerTest {

    private ExchangeRateService exchangeRateService = mock(ExchangeRateService.class);

    private ExchangeRateController controller = new ExchangeRateController(exchangeRateService);

    private WebTestClient webTestClient = WebTestClient.bindToController(controller).build();


    @Test
    void getExchangeRate_ValidCurrencies_ReturnsExchangeRate() {
        String from = "usd";
        String to = "eur";

        CurrentExchangeRateResponse mockResponse = new CurrentExchangeRateResponse();
        mockResponse.setFrom("USD");
        mockResponse.setTo("EUR");
        mockResponse.setRate(0.85);

        Mockito.when(exchangeRateService.getExchangeRate("USD", "EUR"))
                .thenReturn(Mono.just(mockResponse));

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/exchange-rate")
                        .queryParam("from", from)
                        .queryParam("to", to)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(CurrentExchangeRateResponse.class)
                .value(response -> {
                    assert response.getFrom().equals("USD");
                    assert response.getTo().equals("EUR");
                    assert response.getRate() == 0.85;
                });
    }
}
