package com.banca.project.exception;

public class GetInfoAccountNullException extends BadRequestException {
  public GetInfoAccountNullException(String message, String code) {
    super(message, code);
  }
}
