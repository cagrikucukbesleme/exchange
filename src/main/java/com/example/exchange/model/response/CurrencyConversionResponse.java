package com.example.exchange.model.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CurrencyConversionResponse {
    private String transactionId;
    private double convertedAmount;
    private String sourceCurrency;
    private String targetCurrency;
}
