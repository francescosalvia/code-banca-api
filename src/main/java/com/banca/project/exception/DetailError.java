package com.banca.project.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DetailError {
  private final String field;
  private final String message;
}
