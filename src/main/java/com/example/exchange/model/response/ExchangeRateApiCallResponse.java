package com.example.exchange.model.response;

import lombok.Data;

import java.util.Map;

@Data
public class ExchangeRateApiCallResponse {
    private String base;
    private String date;
    private Map<String, Double> rates;
}
