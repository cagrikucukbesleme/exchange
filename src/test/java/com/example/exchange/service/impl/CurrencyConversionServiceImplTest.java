package com.example.exchange.service.impl;

import com.example.exchange.model.request.CurrencyConversionRequest;
import com.example.exchange.model.response.CurrencyConversionResponse;
import com.example.exchange.model.response.CurrentExchangeRateResponse;
import com.example.exchange.repository.ConversionHistoryTransactionRepository;
import com.example.exchange.service.ExchangeRateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

@SpringBootTest
class CurrencyConversionServiceImplTest {

    @Mock
    private ExchangeRateService exchangeRateService;
    @Mock
    private ConversionHistoryTransactionRepository repository;
    private CurrencyConversionServiceImpl currencyConversionService;

    @BeforeEach
    void setup() {
        exchangeRateService = Mockito.mock(ExchangeRateService.class);
        repository = Mockito.mock(ConversionHistoryTransactionRepository.class);
        currencyConversionService = new CurrencyConversionServiceImpl(exchangeRateService, repository);

    }

    @Test
    void convertCurrency_ShouldReturnConvertedResponse() {
        CurrencyConversionRequest request = new CurrencyConversionRequest();
        request.setSourceCurrency("USD");
        request.setTargetCurrency("EUR");
        request.setAmount(100);

        CurrentExchangeRateResponse mockRate = new CurrentExchangeRateResponse();
        mockRate.setRate(0.9);

        Mockito.when(exchangeRateService.getExchangeRate("USD", "EUR"))
                .thenReturn(Mono.just(mockRate));

        Mockito.when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        CurrencyConversionResponse response = currencyConversionService.convertCurrency(request).block();

        assertNotNull(response);
        assertEquals("USD", response.getSourceCurrency());
        assertEquals("EUR", response.getTargetCurrency());
        assertEquals(90, response.getConvertedAmount(), 0.0001);
        assertNotNull(response.getTransactionId());

        Mockito.verify(repository, Mockito.times(1)).save(any());

    }

    @Test
    void bulkConversion_ShouldReturnListOfResponses() throws IOException {
        String csvContent = "sourceCurrency,targetCurrency,amount\n" +
                "USD,EUR,100\n" +
                "GBP,JPY,200\n";

        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "test.csv",
                "text/csv",
                csvContent.getBytes(StandardCharsets.UTF_8));

        List<CurrencyConversionRequest> requests = List.of(
                new CurrencyConversionRequest(100, "EUR", "USD"),
                new CurrencyConversionRequest(200, "JPY", "GBP")
        );

        try (var mockStatic = Mockito.mockStatic(com.example.exchange.utils.CsvUtils.class)) {
            mockStatic.when(() -> com.example.exchange.utils.CsvUtils.parseCsv(mockFile))
                    .thenReturn(requests);

            Mockito.when(exchangeRateService.getExchangeRate(anyString(), anyString()))
                    .thenReturn(Mono.just(new CurrentExchangeRateResponse("USD", "EUR", 0.9)));

            Mockito.when(exchangeRateService.getExchangeRate(anyString(), anyString()))
                    .thenReturn(Mono.just(new CurrentExchangeRateResponse("GBP", "JPY", 150)));

            Mockito.when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

            List<CurrencyConversionResponse> responses = currencyConversionService.bulkConversion(mockFile).block();

            assertNotNull(responses);
            assertEquals(2, responses.size());

            assertEquals("EUR", responses.get(0).getSourceCurrency());
            assertEquals("USD", responses.get(0).getTargetCurrency());
            assertEquals(15000, responses.get(0).getConvertedAmount(), 0.001);

            assertEquals("JPY", responses.get(1).getSourceCurrency());
            assertEquals("GBP", responses.get(1).getTargetCurrency());
            assertEquals(30000, responses.get(1).getConvertedAmount(), 0.001);

            Mockito.verify(repository, Mockito.times(2)).save(any());
        }
    }
}