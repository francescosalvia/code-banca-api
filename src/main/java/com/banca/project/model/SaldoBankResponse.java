package com.banca.project.model;

import com.banca.project.dto.response.PayloadSaldoResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@ToString
@SuperBuilder
public class SaldoBankResponse extends GenericResponse {
  private PayloadSaldoResponse payload;
}
