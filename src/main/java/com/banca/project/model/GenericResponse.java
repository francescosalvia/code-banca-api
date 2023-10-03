package com.banca.project.model;

import lombok.Data;
import lombok.experimental.SuperBuilder;

import javax.persistence.MappedSuperclass;
import java.util.ArrayList;

@MappedSuperclass
@Data
@SuperBuilder
public class GenericResponse {

  private String status;
  private ArrayList<String> error;
}
