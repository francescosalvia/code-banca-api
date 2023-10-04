package com.banca.project.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class Creditor {
  @NotBlank(message = "'name' is mandatory")
  String name;

  @NotNull(message = "'account' is mandatory")
  Account account;
}
