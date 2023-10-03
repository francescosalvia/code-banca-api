package com.banca.project.exception;

import lombok.Getter;

@Getter
public class DateNotCorrectlyException extends BadRequestException {
  public DateNotCorrectlyException(String message, String code) {
    super(message, code);
    this.code = code;
  }
}
