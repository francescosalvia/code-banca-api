package com.banca.project.model;

import lombok.Data;

@Data
public class Creditor {
  String name;
  Account account;
}
