package com.example.exchange.repository;

import com.example.exchange.model.ConversionHistoryTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ConversionHistoryTransactionRepository extends JpaRepository<ConversionHistoryTransaction, String> {
    List<ConversionHistoryTransaction> findByDate(LocalDate date);
}
