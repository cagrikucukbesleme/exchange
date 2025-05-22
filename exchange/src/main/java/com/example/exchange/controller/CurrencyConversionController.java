package com.example.exchange.controller;

import com.example.exchange.model.request.CurrencyConversionRequest;
import com.example.exchange.model.response.CurrencyConversionResponse;
import com.example.exchange.service.CurrencyConversionService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/v1/currency-conversion")
public class CurrencyConversionController {

    private final CurrencyConversionService currencyConversionService;

    public CurrencyConversionController(CurrencyConversionService currencyConversionService) {
        this.currencyConversionService = currencyConversionService;
    }

    @PostMapping("/exchange")
    @Operation(summary = "Exchange Currencies", description = "Returns Currencies Exchange Rate according to amount")
    public Mono<CurrencyConversionResponse> getRate(@RequestBody CurrencyConversionRequest request) {
        return currencyConversionService.convertCurrency(request);
    }

    @PostMapping("/bulk")
    @Operation(summary = "Bulk Exchange Currencies",
            description = "Returns Currencies Exchange Rate, upload a csv file for bulk currency conversion  uploading file\n" +
            "key: \"file\"\n" +
            "value \"....csv\" "
    )
    public Mono<List<CurrencyConversionResponse>> bulkConversion(@RequestPart("file") MultipartFile file) throws IOException {
        return currencyConversionService.bulkConversion(file);
    }

}
