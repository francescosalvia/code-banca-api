package com.banca.project.dto.response;

import lombok.Data;

@Data
public class PayloadSaldoResponse {

  private String date;
  private double balance;
  private double availableBalance;
  private String currency;
}
