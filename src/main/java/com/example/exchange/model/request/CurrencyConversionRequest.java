package com.example.exchange.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CurrencyConversionRequest {
    private double amount;
    private String sourceCurrency;
    private String targetCurrency;
}
