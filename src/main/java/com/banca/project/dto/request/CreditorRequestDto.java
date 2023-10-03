package com.banca.project.dto.request;

import com.banca.project.model.Account;

public class CreditorRequestDto {

  String name;
  Account account;
  String description;
  Double amount;
  String currency;
  String executionDate;
}
