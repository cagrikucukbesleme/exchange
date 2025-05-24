package com.example.exchange.service.impl;

import com.example.exchange.model.response.CurrentExchangeRateResponse;
import com.example.exchange.model.response.ExchangeRateApiCallResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class ExchangeRateServiceImplTest {

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec uriSpec;

    @Mock
    private WebClient.RequestHeadersSpec headersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    private ExchangeRateServiceImpl service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        service = new ExchangeRateServiceImpl(webClient);
    }

    @Test
    void testGetExchangeRate() {

        ExchangeRateApiCallResponse apiResponse = new ExchangeRateApiCallResponse();
        apiResponse.setRates(Map.of("EUR", 0.85));

        when(webClient.get()).thenReturn(uriSpec);
        when(uriSpec.uri(any(java.util.function.Function.class))).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(ExchangeRateApiCallResponse.class)).thenReturn(Mono.just(apiResponse));

        CurrentExchangeRateResponse response = service.getExchangeRate("USD", "EUR").block();

        assertNotNull(response);
        assertEquals("USD", response.getFrom());
        assertEquals("EUR", response.getTo());
        assertEquals(0.85, response.getRate());
    }
}