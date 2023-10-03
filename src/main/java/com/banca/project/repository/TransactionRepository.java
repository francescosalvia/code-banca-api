package com.banca.project.repository;

import com.banca.project.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {

  Optional<Transaction> findByTransactionId(String transactionId);
}
