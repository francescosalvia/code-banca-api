package com.banca.project.exception;

public class GetBalanceNullException extends InternalServerErrorException {
  public GetBalanceNullException(String message, String code) {
    super(message, code);
  }
}
