package com.example.exchange.service.impl;

import com.example.exchange.model.ConversionHistoryTransaction;
import com.example.exchange.model.request.CurrencyConversionRequest;
import com.example.exchange.model.response.CurrencyConversionResponse;
import com.example.exchange.model.response.CurrentExchangeRateResponse;
import com.example.exchange.repository.ConversionHistoryTransactionRepository;
import com.example.exchange.service.CurrencyConversionService;
import com.example.exchange.service.ExchangeRateService;
import com.example.exchange.utils.CsvUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CurrencyConversionServiceImpl implements CurrencyConversionService {

    private final ExchangeRateService exchangeRateService;
    private final ConversionHistoryTransactionRepository repository;

    public CurrencyConversionServiceImpl(ExchangeRateService exchangeRateService,
                                         ConversionHistoryTransactionRepository conversionHistoryTransactionRepository) {
        this.exchangeRateService = exchangeRateService;
        this.repository = conversionHistoryTransactionRepository;
    }

    @Override
    public Mono<CurrencyConversionResponse> convertCurrency(CurrencyConversionRequest request) {

        return Mono.just(conversionCurrency(request));
    }

    @Override
    public Mono<List<CurrencyConversionResponse>> bulkConversion(MultipartFile file) throws IOException {

        List<CurrencyConversionRequest> requests = CsvUtils.parseCsv(file);
        List<CurrencyConversionResponse> collect1 = requests.stream()
                .map(this::conversionCurrency)
                .collect(Collectors.toList());
        return Mono.just(collect1);

    }

    private CurrencyConversionResponse conversionCurrency(CurrencyConversionRequest request){
        CurrentExchangeRateResponse currentExchangeRateResponse = exchangeRateService.getExchangeRate(
                request.getSourceCurrency(), request.getTargetCurrency()).block();
        assert currentExchangeRateResponse != null;
        double rate = currentExchangeRateResponse.getRate();
        double convertedAmount = request.getAmount() * rate;
        String transactionId = UUID.randomUUID().toString();
        transactionsLogSave(request, transactionId, convertedAmount);
        return mapCurrencyConversionResponse(request, convertedAmount, transactionId);
    }

    private static CurrencyConversionResponse mapCurrencyConversionResponse(CurrencyConversionRequest request,
                                                                            double convertedAmount, String transactionId) {
        CurrencyConversionResponse currencyConversionResponse = new CurrencyConversionResponse();
        currencyConversionResponse.setTargetCurrency(request.getTargetCurrency());
        currencyConversionResponse.setSourceCurrency(request.getSourceCurrency());
        currencyConversionResponse.setConvertedAmount(convertedAmount);
        currencyConversionResponse.setTransactionId(transactionId);
        return currencyConversionResponse;
    }

    private void transactionsLogSave(CurrencyConversionRequest request, String transactionId, double convertedAmount) {
        ConversionHistoryTransaction conversionHistoryTransaction = new ConversionHistoryTransaction();
        conversionHistoryTransaction.setId(transactionId);
        conversionHistoryTransaction.setDate(LocalDate.now());
        conversionHistoryTransaction.setAmount(request.getAmount());
        conversionHistoryTransaction.setConvertedAmount(convertedAmount);
        conversionHistoryTransaction.setSourceCurrency(request.getSourceCurrency());
        conversionHistoryTransaction.setTargetCurrency(request.getTargetCurrency());
        repository.save(conversionHistoryTransaction);
    }

}


