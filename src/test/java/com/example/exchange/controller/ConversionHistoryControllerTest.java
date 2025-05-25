package com.example.exchange.controller;

import com.example.exchange.model.response.CurrencyConversionResponse;
import com.example.exchange.service.ConversionHistoryService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ConversionHistoryControllerTest {

    private ConversionHistoryService conversionHistoryService;
    private ConversionHistoryController controller;
    private WebTestClient webTestClient;

    @BeforeEach
    void setup() {
        conversionHistoryService = mock(ConversionHistoryService.class);
        controller = new ConversionHistoryController(conversionHistoryService);
        webTestClient = WebTestClient.bindToController(controller).build();
    }
    @Test
    void getAllConversionHistory() {

        CurrencyConversionResponse response = new CurrencyConversionResponse();
        response.setConvertedAmount(123);
        response.setTargetCurrency("asd");
        response.setSourceCurrency("dsa");
        when(conversionHistoryService.getAllConversionHistory())
                .thenReturn(Mono.just(List.of(response)));


        webTestClient.get().uri("/v1/conversion-history/all")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(CurrencyConversionResponse.class)
                .hasSize(1);

    }


    @Test
    void getConversionHistoryByDate() {
        CurrencyConversionResponse response = new CurrencyConversionResponse();
        response.setConvertedAmount(123);
        response.setTargetCurrency("USD");
        response.setSourceCurrency("EUR");

        LocalDate testDate = LocalDate.of(2025, 5, 24);

        when(conversionHistoryService.getConversionHistoryByDate(testDate))
                .thenReturn(Mono.just(List.of(response)));

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/conversion-history/by-date")
                        .queryParam("date", "24/05/2025")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(CurrencyConversionResponse.class)
                .hasSize(1)
                .value(list -> {
                    CurrencyConversionResponse item = list.get(0);
                    assert item.getConvertedAmount() == 123;
                    assert "USD".equals(item.getTargetCurrency());
                    assert "EUR".equals(item.getSourceCurrency());
                });
    }
}