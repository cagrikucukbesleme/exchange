package com.example.exchange.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ConversionHistoryTransaction {
    @Id
    private String id;
    private String sourceCurrency;
    private String targetCurrency;
    private double amount;
    private double convertedAmount;
    private LocalDate date;
}
