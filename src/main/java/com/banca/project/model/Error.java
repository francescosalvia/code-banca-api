package com.banca.project.model;

import lombok.Data;

@Data
public class Error {
    private String code;
    private String description;
    private String params;
}
