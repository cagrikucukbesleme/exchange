package com.example.exchange.service.impl;

import com.example.exchange.model.ConversionHistoryTransaction;
import com.example.exchange.model.request.CurrencyConversionRequest;
import com.example.exchange.model.response.CurrencyConversionResponse;
import com.example.exchange.model.response.CurrentExchangeRateResponse;
import com.example.exchange.repository.ConversionHistoryTransactionRepository;
import com.example.exchange.service.CurrencyConversionService;
import com.example.exchange.service.ExchangeRateService;
import com.example.exchange.utils.CsvUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
public class CurrencyConversionServiceImpl implements CurrencyConversionService {

    private final ExchangeRateService exchangeRateService;
    private final ConversionHistoryTransactionRepository repository;

    @Override
    public Mono<CurrencyConversionResponse> convertCurrency(CurrencyConversionRequest request) {

        return Mono.just(conversionCurrency(request));
    }

    @Override
    public Mono<List<CurrencyConversionResponse>> bulkConversion(MultipartFile file) throws IOException {

        List<CurrencyConversionRequest> requests = CsvUtils.parseCsv(file);
        List<CurrencyConversionResponse> currencyConversionResponseList = requests.stream()
                .map(this::conversionCurrency)
                .collect(Collectors.toList());
        return Mono.just(currencyConversionResponseList);

    }

    private CurrencyConversionResponse conversionCurrency(CurrencyConversionRequest request){

        CurrentExchangeRateResponse currentExchangeRateResponse = exchangeRateService.getExchangeRate(
                request.getSourceCurrency(), request.getTargetCurrency()).block();

        assert currentExchangeRateResponse != null;
        double rate = currentExchangeRateResponse.getRate();
        double convertedAmount = request.getAmount() * rate;

        String transactionId = UUID.randomUUID().toString();

        repository.save(new ConversionHistoryTransaction(transactionId,request.getSourceCurrency(),
                request.getTargetCurrency(),request.getAmount(),convertedAmount,LocalDate.now()));

        return CurrencyConversionResponse.builder()
                .transactionId(transactionId)
                .convertedAmount(convertedAmount)
                .sourceCurrency(request.getSourceCurrency())
                .targetCurrency(request.getTargetCurrency())
                .build();
    }

}


