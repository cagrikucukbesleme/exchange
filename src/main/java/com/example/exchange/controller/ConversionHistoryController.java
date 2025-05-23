package com.example.exchange.controller;

import com.example.exchange.model.response.CurrencyConversionResponse;
import com.example.exchange.service.ConversionHistoryService;
import com.example.exchange.utils.DateUtils;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/v1/conversion-history")
@AllArgsConstructor
public class ConversionHistoryController {

    private final ConversionHistoryService conversionHistoryService;

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
        LocalDate localDate= DateUtils.parseStringToLocalDate(date);
        return conversionHistoryService.getConversionHistoryByDate(localDate);
    }
}
