package com.example.exchange.service.impl;

import com.example.exchange.constant.Constants;
import com.example.exchange.model.ConversionHistoryTransaction;
import com.example.exchange.model.response.CurrencyConversionResponse;
import com.example.exchange.repository.ConversionHistoryTransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
class ConversionHistoryServiceImplTest {

    private ConversionHistoryTransactionRepository repository;
    private ConversionHistoryServiceImpl service;


    @BeforeEach
    void setup() {
        repository = mock(ConversionHistoryTransactionRepository.class);
        service = new ConversionHistoryServiceImpl(repository);
    }

    @Test
    void getAllConversionHistory_ReturnsList() {
        ConversionHistoryTransaction transaction = new ConversionHistoryTransaction(
                "tx123", "EUR", "USD",100 , 90.0, LocalDate.now()
        );
        when(repository.findAll()).thenReturn(List.of(transaction));

        Mono<List<CurrencyConversionResponse>> resultMono = service.getAllConversionHistory();
        List<CurrencyConversionResponse> result = resultMono.block();

        assertNotNull(result);
        assertEquals(1, result.size());
        CurrencyConversionResponse resp = result.get(0);
        assertEquals("tx123", resp.getTransactionId());
        assertEquals(90.0, resp.getConvertedAmount());
        assertEquals("EUR", resp.getSourceCurrency());
        assertEquals("USD", resp.getTargetCurrency());
    }

    @Test
    void getAllConversionHistory_EmptyList_ThrowsNotFound() {
        when(repository.findAll()).thenReturn(List.of());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.getAllConversionHistory().block());

        assertEquals(Constants.NO_CONVERSION_HISTORY_RECORDS_WERE_FOUND, ex.getReason());
    }

    @Test
    void getConversionHistoryByDate_ReturnsList() {
        LocalDate date = LocalDate.of(2025, 5, 24);
        ConversionHistoryTransaction transaction = new ConversionHistoryTransaction(
                "tx123", "EUR", "USD", 1000, 90.0, date
        );
        when(repository.findByDate(date)).thenReturn(List.of(transaction));

        Mono<List<CurrencyConversionResponse>> resultMono = service.getConversionHistoryByDate(date);
        List<CurrencyConversionResponse> result = resultMono.block();

        assertNotNull(result);
        assertEquals(1, result.size());
        CurrencyConversionResponse resp = result.get(0);
        assertEquals("tx123", resp.getTransactionId());
        assertEquals(90.0, resp.getConvertedAmount());
        assertEquals("EUR", resp.getSourceCurrency());
        assertEquals("USD", resp.getTargetCurrency());
    }

    @Test
    void getConversionHistoryByDate_EmptyList_ThrowsNotFound() {
        LocalDate date = LocalDate.of(2025, 5, 24);
        when(repository.findByDate(date)).thenReturn(List.of());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.getConversionHistoryByDate(date).block());

        assertTrue(ex.getReason().contains(Constants.NO_CONVERSION_HISTORY_RECORDS_WERE_FOUND_FOR_THE_SPECIFIED_DATE));
        assertTrue(ex.getReason().contains(date.toString()));
    }
}