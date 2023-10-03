package com.banca.project.exception;

public class GetInfoAccountNullException extends InternalServerErrorException {
  public GetInfoAccountNullException(String message, String code) {
    super(message, code);
  }
}
