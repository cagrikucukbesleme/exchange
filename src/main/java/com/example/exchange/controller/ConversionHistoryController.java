package com.example.exchange.controller;

import com.example.exchange.model.response.CurrencyConversionResponse;
import com.example.exchange.service.ConversionHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/v1/conversion-history")
public class ConversionHistoryController {

    private final ConversionHistoryService conversionHistoryService;

    public ConversionHistoryController(ConversionHistoryService conversionHistoryService) {
        this.conversionHistoryService = conversionHistoryService;
    }

    @GetMapping("/all")
    @Operation(summary = "All Transactions History", description = "Returns All Currency Transactions History")
    public Mono<List<CurrencyConversionResponse>> getAllConversionHistory() {
        return conversionHistoryService.getAllConversionHistory();
    }

    @GetMapping("/by-date")
    @Operation(summary = "Transaction History By Date ", description = "Returns Currency Transactions History according to Date" +
            "Date Formats must be one of them \"dd/MM/yyyy\"\n" +
            " \"MM/dd/yyyy\"\n" +
            " \"yyyyMMdd\"\n" +
            " \"dd-MMM-yyyy\"")
    public Mono<List<CurrencyConversionResponse>> getConversionHistoryByDate(
            @RequestParam(required = false) String date) {
        return conversionHistoryService.getConversionHistoryByDate(date);
    }
}
