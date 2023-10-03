package com.banca.project.exception;

import lombok.Getter;

@Getter
public class EndDateNotCorrectlyException extends BadRequestException {
  public EndDateNotCorrectlyException(String message, String code) {
    super(message, code);
    this.code = code;
  }
}
