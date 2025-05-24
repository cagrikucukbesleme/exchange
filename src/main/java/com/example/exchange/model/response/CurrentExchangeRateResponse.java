package com.example.exchange.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CurrentExchangeRateResponse {
    private String from;
    private String to;
    private double rate;
}
