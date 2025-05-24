package com.example.exchange.controller;

import com.example.exchange.model.request.CurrencyConversionRequest;
import com.example.exchange.model.response.CurrencyConversionResponse;
import com.example.exchange.service.ConversionHistoryService;
import com.example.exchange.service.CurrencyConversionService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.mock;

@SpringBootTest
@AllArgsConstructor
@RequiredArgsConstructor
class CurrencyConversionControllerTest {

    private CurrencyConversionService currencyConversionService = mock(CurrencyConversionService.class);

    private CurrencyConversionController controller = new CurrencyConversionController(currencyConversionService);

    private WebTestClient webTestClient = WebTestClient.bindToController(controller).build();

    @Test
    void testGetRate() {
        CurrencyConversionRequest request = new CurrencyConversionRequest();
        request.setAmount(100);
        request.setSourceCurrency("USD");
        request.setTargetCurrency("EUR");

        CurrencyConversionResponse response = new CurrencyConversionResponse();
        response.setConvertedAmount(90);
        response.setSourceCurrency("USD");
        response.setTargetCurrency("EUR");

        Mockito.when(currencyConversionService.convertCurrency(request))
                .thenReturn(Mono.just(response));

        webTestClient.post()
                .uri("/v1/currency/convert")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CurrencyConversionResponse.class)
                .value(res -> {
                    assert res.getConvertedAmount() == 90;
                    assert "USD".equals(res.getSourceCurrency());
                    assert "EUR".equals(res.getTargetCurrency());
                });
    }

    @Test
    void testBulkConversion() throws IOException {
        List<CurrencyConversionResponse> responseList = getCurrencyConversionResponses();

        Mockito.when(currencyConversionService.bulkConversion(Mockito.any()))
                .thenReturn(Mono.just(responseList));

        ByteArrayResource resource = new ByteArrayResource("USD,EUR,100\nGBP,JPY,200".getBytes()) {
            @Override
            public String getFilename() {
                return "test.csv";
            }
        };

        MultiValueMap<String, Object> multipartData = new LinkedMultiValueMap<>();
        multipartData.add("file", resource);

        WebTestClient.ResponseSpec exchange = webTestClient.post()
                .uri("/v1/currency/bulk-convert")
                .body(BodyInserters.fromMultipartData(multipartData))
                .exchange();
        assertNotNull(exchange);
    }

    private static List<CurrencyConversionResponse> getCurrencyConversionResponses() {
        CurrencyConversionResponse response1 = new CurrencyConversionResponse();
        response1.setSourceCurrency("USD");
        response1.setTargetCurrency("EUR");
        response1.setConvertedAmount(90);

        CurrencyConversionResponse response2 = new CurrencyConversionResponse();
        response2.setSourceCurrency("GBP");
        response2.setTargetCurrency("JPY");
        response2.setConvertedAmount(30000);

        return List.of(response1, response2);
    }
}