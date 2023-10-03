package com.banca.project.mapper;

import com.banca.project.entity.Transaction;
import com.banca.project.dto.response.PayloadTransactionsResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

  @Mapping(source = "type.enumeration", target = "enumeration")
  @Mapping(source = "type.value", target = "value")
  Transaction toTransaction(PayloadTransactionsResponse payloadTransactionsResponse);

  List<Transaction> toTransactionList(
      List<PayloadTransactionsResponse> payloadTransactionsResponse);
}
