package com.banca.project.exception;

import lombok.Getter;

@Getter
public class CustomerIdNotCorrectlyException extends BadRequestException {
  public CustomerIdNotCorrectlyException(String message, String code) {
    super(message, code);
    this.code = code;
  }
}
