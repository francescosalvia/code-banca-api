package com.banca.project.model;

import com.banca.project.model.Error;
import lombok.Data;

import java.util.ArrayList;

@Data
public class BadRequestResponse {

  private String status;
  private ArrayList<Error> errors;
  private Object payload;
}
