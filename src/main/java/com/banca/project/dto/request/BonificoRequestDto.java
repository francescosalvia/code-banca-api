package com.banca.project.dto.request;

import com.banca.project.model.Account;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class BonificoRequestDto {

  @NotBlank(message = "'name' is mandatory")
  String name;

  Account account;

  @NotBlank(message = "'description' is mandatory")
  String description;

  @NotNull(message = "'amount' is mandatory")
  @Min(1)
  Double amount;

  @NotBlank(message = "'currency' is mandatory")
  String currency;

  @JsonFormat(pattern = "yyyy-MM-dd")
  @NotBlank(message = "'executionDate' is mandatory")
  String executionDate;
}
