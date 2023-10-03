package com.banca.project.dto.response;

import com.banca.project.model.TypeEnumeration;
import lombok.Data;

@Data
public class PayloadTransactionsResponse {

  private String transactionId;
  private String operationId;
  private String accountingDate;
  private String valueDate;
  private TypeEnumeration type;
  private double amount;
  private String currency;
  private String description;
}
