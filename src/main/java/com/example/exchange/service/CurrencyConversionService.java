package com.example.exchange.service;

import com.example.exchange.model.request.CurrencyConversionRequest;
import com.example.exchange.model.response.CurrencyConversionResponse;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.List;

public interface CurrencyConversionService  {

    Mono<CurrencyConversionResponse> convertCurrency(CurrencyConversionRequest currencyConversionRequest);

    Mono<List<CurrencyConversionResponse>> bulkConversion(MultipartFile file) throws IOException;

}
