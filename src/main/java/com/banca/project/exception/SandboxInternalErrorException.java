package com.banca.project.exception;

public class SandboxInternalErrorException extends InternalServerErrorException {
  public SandboxInternalErrorException(String message, String code) {
    super(message, code);
  }
}
