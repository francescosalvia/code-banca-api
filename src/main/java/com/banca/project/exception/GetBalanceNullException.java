package com.banca.project.exception;

public class GetBalanceNullException extends BadRequestException {
  public GetBalanceNullException(String message, String code) {
    super(message, code);
  }
}
