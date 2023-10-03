package com.banca.project.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@ToString
@SuperBuilder
public class TransactionsBankResponse extends GenericResponse {
  private ResponseTransactions payload;
}
