package com.banca.project.exception;

import lombok.Getter;

@Getter
public abstract class BadRequestException extends Exception {

  protected String code;

  public BadRequestException(Exception e, String code) {
    super(e);
    this.code = code;
  }

  public BadRequestException(Exception e, String message, String code) {
    super(message, e);
    this.code = code;
  }

  public BadRequestException(String message, String code) {
    super(message);
    this.code = code;
  }
}
