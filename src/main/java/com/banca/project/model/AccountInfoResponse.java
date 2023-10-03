package com.banca.project.model;

import com.banca.project.dto.response.PayloadAccountInfoResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@ToString
@SuperBuilder
public class AccountInfoResponse extends GenericResponse {
  private PayloadAccountInfoResponse payload;
}
